package cn.vstore.appserver.service;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.StoreClientBehaviorLog;

/**
 * @version $Id$
 */
@Service("StoreClientBehaviorLogService")
public class StoreClientBehaviorLogService {
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
	
	public StoreClientBehaviorLog getStoreClientBehaviorLogBy(String userId, String iccid, String imei, String appId, Integer versionId, Date openTime){
		HashMap<String , Object> hm = new HashMap<String, Object>();
		hm.put("userId", userId);
    	hm.put("iccid", iccid);
    	hm.put("imei", imei);
    	hm.put("appId", appId);
    	hm.put("versionId", versionId);
    	hm.put("openTime", openTime);
    	return (StoreClientBehaviorLog)sqlMapClientTemplate.queryForObject("store_client_behavior_log.getStoreClientBehavioLogBy",hm);
	}
	
	public Integer insert(StoreClientBehaviorLog storeClientBehaviorLog){
		return (Integer)sqlMapClientTemplate.insert("store_client_behavior_log.ibatorgenerated_insert",storeClientBehaviorLog);
	}

}
