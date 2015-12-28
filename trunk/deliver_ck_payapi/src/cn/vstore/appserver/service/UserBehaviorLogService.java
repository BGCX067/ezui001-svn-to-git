package cn.vstore.appserver.service;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.UserBehaviorLog;

/**
 * @version $Id$
 */
@Service("UserBehaviorLog")
public class UserBehaviorLogService {
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
	
	public UserBehaviorLog getUserBehaviorLogBy(String userId, String iccid, String imei, String appId, Integer versionId, Date openTime, Long storeId){
		HashMap<String , Object> hm = new HashMap<String, Object>();
		hm.put("userId", userId);
    	hm.put("iccid", iccid);
    	hm.put("imei", imei);
    	hm.put("appId", appId);
    	hm.put("versionId", versionId);
    	hm.put("openTime", openTime);
    	hm.put("storeId", storeId);
    	return (UserBehaviorLog)sqlMapClientTemplate.queryForObject("user_behavior_log.getUserBehaviorLogBy",hm);
	}
	
	public Long insert(UserBehaviorLog userBehaviorLog){
		return (Long)sqlMapClientTemplate.insert("user_behavior_log.ibatorgenerated_insert", userBehaviorLog);
	}

}
