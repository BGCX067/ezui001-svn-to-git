package cn.vstore.appserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.form.LogForm;
import cn.vstore.appserver.model.UserUninstallApkReason;


@Service("UserUninstallApkReasonService")
public class UserUninstallApkReasonService {
	
	protected static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
	
	//新增
	public long insert(UserUninstallApkReason userUninstallApkReason){
		return (Long)sqlMapClientTemplate.insert("user_uninstall_apk_reason.insert", userUninstallApkReason);
	}

}
