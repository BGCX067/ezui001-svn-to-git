package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.vstore.appserver.model.HomeAdvertisement;
import cn.vstore.appserver.service.ResultCode.CommonCode;
/** 
 * @author Raymond 
 * @version 创建时间：2012-6-7 上午1:26:25 
 * 类说明 
 */
@Service("HomeService")
public class HomeService {
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private ConstantService constantService;
    
    /**
     * AdvertisementList : 取得首页焦点图 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<HomeAdvertisement>> homeAdvertisements(HomeService self,BigDecimal storeId) {
        try {
            // 用来存储广告图的List
            List<HomeAdvertisement> homeAdvertisements = new ArrayList<HomeAdvertisement>();
            // 参数map
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("storeId", storeId);
            //声明首页应用集合
            @SuppressWarnings("unchecked")
            List<HomeAdvertisement> homeAdvertisementList;
            homeAdvertisementList = sqlMapClientTemplate.queryForList("HomeAdvertisement.getHomeAdvertisement", map);
            //循环存储首页广告图
            //for (HomeAdvertisement homeAdvertisement : homeAdvertisementList) {
  /*          	 if (!(homeAdvertisement.getId()<0)){
            		   	 if (StringUtils.isNotBlank(homeAdvertisement.getPicturePath()) 
                			 && homeAdvertisement.getPicturePath().charAt(0) == '/')
                	 	homeAdvertisement.setPicturePath("http://www.51vapp.com/image"+ homeAdvertisement.getPicturePath());	
                	 homeAdvertisements.add(homeAdvertisement);
                }*/
           // }
            return new ServiceResult<List<HomeAdvertisement>>(CommonCode.SUCCESS, homeAdvertisementList);
        } catch (Throwable e) {
            return new ServiceResult<List<HomeAdvertisement>>(CommonCode.SERVICE_FAIL, null, e);
        }

    }

}
