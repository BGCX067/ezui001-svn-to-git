package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.FeatureApplication;
import cn.vstore.appserver.model.HomeAdvertisement;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MemberCacheUtil;

import com.meetup.memcached.MemcachedClient;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-7 下午8:15:25 
 * 类说明 
 */
@Service("GuessService")
public class GuessService {
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
    @Autowired
    private PaymentService paymentService;
    /**
     * guessYourLikes : 猜你喜歡 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<FeatureApplication>> guessYourLike(GuessService self,String imei,String token, boolean appfilter,String pkg,BigDecimal storeId) {
         try {
            // 用来存储猜你喜歡應用的集合
            List<FeatureApplication> guessYourLikes = new ArrayList<FeatureApplication>();
            // 参数map
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pkg", pkg);
            map.put("storeID",storeId);
            map.put("plots",10);
            //声明猜你喜歡集合
            @SuppressWarnings("unchecked")
            List<FeatureApplication> guessYourLikeList;
            List<String> appPkgNames = new ArrayList<String>();
            //实例memcatched 
            new MemberCacheUtil(); 
            //实例memcache
            MemcachedClient mcc = new MemcachedClient();
            if(mcc.get(storeId+pkg+"guessyourlike10")==null||"".equals(mcc.get(storeId+pkg+"guessyourlike10"))){
            	guessYourLikeList = sqlMapClientTemplate.queryForList("FeatureApps.getGuessYourLikes", map);
            	mcc.set(storeId+pkg+"guessyourlike10", guessYourLikeList,new Date(8*60*60*1000));
            }else{
            	guessYourLikeList=(List<FeatureApplication>)mcc.get(storeId+pkg+"guessyourlike10");
            }
            //如果同子分类中不足10条则查询上级分类的10条
            //&TODO
            //取得hostpath
            String hostPath = constantService.getHostPath();
            //循环存储猜你喜歡應用
            for (FeatureApplication guessYourLike : guessYourLikeList) {
                if ((guessYourLike.getPkg()!=null)){
                	if (StringUtils.isNotBlank(guessYourLike.getIcon()) && guessYourLike.getIcon().charAt(0) == '/')
                		guessYourLike.setIcon(hostPath + guessYourLike.getIcon());
                guessYourLikes.add(guessYourLike);
                appPkgNames.add(guessYourLike.getPkg());
                }
                
            }
            // 取得使用者資訊
            //&TODO
            if(!StringUtils.isBlank(token)&&imei!=null){
            	Prosumer users = authenticationService.getProsumerByAccount(token);
            	if(users!=null){
	            	String userUid = users.getUserUid();
	            	String userId = users.getUserId();
	            	List<PaymentInformation> paymentInformations = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
	            	for (FeatureApplication featureApplication : guessYourLikes) {
	            		// 存入PaymentInformationList使取得PayStatus
	            		featureApplication.setPaymentInformationList(paymentInformations);
	            	}
            	}
            }
            return new ServiceResult<List<FeatureApplication>>(CommonCode.SUCCESS, guessYourLikes);
        } catch (Throwable e) {
            return new ServiceResult<List<FeatureApplication>>(CommonCode.SERVICE_FAIL, null, e);
        }

    }

}
