package cn.vstore.appserver.service;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.PaymentSession;

/**
 * @version $Id: PaymentSessionService.java 7437 2011-03-03 06:26:58Z yellow $
 */
@Service
public class PaymentSessionService {

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    /**
     * 取得IPAY_SESSION
     */
    public PaymentSession getPaymentSession(String token, String imei, String iccid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("iccid", iccid);
        map.put("imei", imei);
        map.put("token", token);
        return (PaymentSession) sqlMapClientTemplate.queryForObject(
                "PaymentSession.getPaymentSession", map);
    }
}