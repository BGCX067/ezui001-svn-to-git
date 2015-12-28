package cn.vstore.appserver.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

@Service("IpayUserPaymentLogService")
public class IpayUserPaymentLogService {
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
	
	public Integer getStatus(String appId, String userId, String imei, Long storeId){
		HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("appId", appId);
    	hm.put("userId",userId);
    	hm.put("imei",imei);
    	hm.put("storeId",storeId);
    	Integer i = (Integer)sqlMapClientTemplate.queryForObject("ipay_user_payment_log.getStatus",hm);
    	return i==null ? 0 : i;
	}

}
