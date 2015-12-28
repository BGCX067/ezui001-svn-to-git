package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.UserInstallLog;

/**
 * @version $Id$
 */
@Service("UserInstallLogService")
public class UserInstallLogService {
	
	 @Autowired
	 private SqlMapClientTemplate sqlMapClientTemplate;
	 
	 public UserInstallLog getUserInstallLog(UserInstallLog userInstallLog){
		 return (UserInstallLog)sqlMapClientTemplate.queryForObject("user_install_log.getUserInstallLog",userInstallLog);
	 }
	 
	 public Long insert(UserInstallLog userInstallLog){
		 return (Long)sqlMapClientTemplate.insert("user_install_log.insertUserInstallLog",userInstallLog);
	 }
	 
	 public Integer deleteBy(String appId, String userId, String imei, int installStatus, Long downloadId,Long storeId){
		 HashMap<String, Object> hm = new HashMap<String, Object>();
		 hm.put("appId", appId);
		 hm.put("userId", userId);
		 hm.put("imei", imei);
		 hm.put("installStatus", installStatus);
		 hm.put("downloadId", downloadId);
		 hm.put("storeId", storeId);
		 return sqlMapClientTemplate.delete("user_install_log.deleteBy", hm);
	 }

}
