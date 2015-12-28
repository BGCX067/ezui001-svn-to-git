package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.modeler.FeatureInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.meetup.memcached.MemcachedClient;

import cn.vstore.appserver.model.CommentsApp;
import cn.vstore.appserver.model.FeatureApplication;
import cn.vstore.appserver.model.GamePayInformation;
import cn.vstore.appserver.model.UserCollect;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MemberCacheUtil;


@Service("CollectService")
public class CollectService {

	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Autowired
    private ConstantService constantService;
	
	public Long insert(UserCollect userCollect){
		 return (Long)sqlMapClientTemplate.insert("userCollect.insertCollectSelective",userCollect);
	}
	
	public int deleteByUserIdAndAppId(String userUid, String appId){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("userid", userUid);
        map.put("appid", appId);
		 return sqlMapClientTemplate.delete("userCollect.deleteByUserIdAndAppId",map);
	}
	
	public UserCollect selectByUserIdAndAppId(String userUid, String appId){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("userid", userUid);
        map.put("appid", appId);
        return (UserCollect)sqlMapClientTemplate.queryForObject("userCollect.selectCollectByUseridAndPkg", map);
	}
	
	@Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<FeatureApplication>> collectApps(String userId,BigDecimal storeId){
		//实例memcatched 
        new MemberCacheUtil(); 
        //实例memcache
        MemcachedClient mcc = new MemcachedClient();
        try{
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("userId", userId);
	        map.put("storeId", storeId);
	        
	        List<FeatureApplication> collectApps;
	        
	        collectApps = sqlMapClientTemplate.queryForList("userCollect.selectCollectAppList", map);
	        
	        
	        // 取得hostpath
            String hostPath = constantService.getHostPath();
	        for (int i = 0; i < collectApps.size(); i++) {
	        	FeatureApplication featureApp = collectApps.get(i);
	        	if (StringUtils.isNotBlank(featureApp.getIcon()) && featureApp.getIcon().charAt(0) == '/')
	        		featureApp.setIcon(hostPath + featureApp.getIcon());
	            	//去掉所有空格
	            	if(!"".equals(featureApp)&&featureApp.getVersionName() != null){
	            		featureApp.setVersionName(featureApp.getVersionName().replace(" ",""));
	            }
			}
	        return new ServiceResult<List<FeatureApplication>>(CommonCode.SUCCESS, collectApps);
        }catch (Exception e) {
        	return new ServiceResult<List<FeatureApplication>>(CommonCode.SERVICE_FAIL, null, e);
		}
	}
	
	@Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<CommentsApp>> commentApps(String userId,BigDecimal storeId){
        try{
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("userId", userId);
	        map.put("storeId", storeId);
	        
	        List<CommentsApp> commentsApps;
	        commentsApps = sqlMapClientTemplate.queryForList("userCollect.selectUserCommentApps", map);
	        
	        // 取得hostpath
            String hostPath = constantService.getHostPath();
	        for (int i = 0; i < commentsApps.size(); i++) {
	        	CommentsApp commentsApp = commentsApps.get(i);
	        	if (StringUtils.isNotBlank(commentsApp.getIcon()) && commentsApp.getIcon().charAt(0) == '/')
	        		commentsApp.setIcon(hostPath + commentsApp.getIcon());
	            	//去掉所有空格
	            	if(!"".equals(commentsApp) && commentsApp.getVersionName() != null){
	            		commentsApp.setVersionName(commentsApp.getVersionName().replace(" ",""));
	            }
			}
	        return new ServiceResult<List<CommentsApp>>(CommonCode.SUCCESS, commentsApps);
        }catch (Exception e) {
        	return new ServiceResult<List<CommentsApp>>(CommonCode.SERVICE_FAIL, null, e);
		}
	}
}