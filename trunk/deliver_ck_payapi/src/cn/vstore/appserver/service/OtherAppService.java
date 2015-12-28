package cn.vstore.appserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.OtherAppUserParameter;


@Service("OtherAppService")
public class OtherAppService {
	
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	
	public Long insert(OtherAppUserParameter parameter){
		 return (Long)sqlMapClientTemplate.insert("otherAppStat.insertOpen",parameter);
	 }
}
