package cn.vstore.appserver.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.Account;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.service.ResultCode.CommonCode;

@Service("SendLogService")
public class SendLogService {

    private final static String APPID = "net.smart.appstore.client";

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Void> addClientUsageLog(SendLogService self, String store, String imei, String iccid,
        String token, String l_dvc, String l_iccid, String l_imei, Timestamp openTime,
        String l_uid, String l_userId, String l_cver, String l_msisd, String l_isFirstTime, String snum, String IP) {
        try {
            // 取得使用者資訊
//            Prosumer users = authenticationService.getProsumerByAccount(new Account(token, iccid, imei));
//            String userUid = users.getUserUid();

            // 檢查StoreClientBehaviorLog是否已有此筆log
            boolean isOwn = self.isPresenceClientBehaviorLog(l_userId, l_iccid, l_imei, store, l_cver, openTime);

            // 如果不存在則insert一筆新的資料到StoreClientBehaviorLog
            if (!isOwn) {
                try {
                	if(l_isFirstTime==null || l_isFirstTime.trim().equals("")){
                		l_isFirstTime = "0";
                	}
                    self.insertClientBehaviorLog(self, l_userId, l_iccid, l_imei, store, l_cver, openTime, l_dvc, l_uid, l_msisd, Double.parseDouble(l_isFirstTime), snum, IP);
                } catch (Throwable e) {
                    return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
                }
            }
            return new ServiceResult<Void>(CommonCode.SUCCESS);
        } catch (Throwable e) {
            return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * 取得ClientBehaviorLog中的LogUid
     */
    boolean isPresenceClientBehaviorLog(String userId, String iccid, String imei, String appId,
        String versionId, Timestamp openTime) {
        boolean isOwn = false;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("iccid", iccid);
        map.put("imei", imei);
        map.put("appId", appId);
        map.put("versionId", versionId);
        map.put("openTime", openTime);
        Integer logUidcount = (Integer) sqlMapClientTemplate.queryForObject("StoreClientBehaviorLog.getClientBehaviorLogUidCount", map);
        if ((logUidcount != null ? logUidcount.intValue() : 0) != 0) {
            isOwn = true;
        }
        return isOwn;
    }

    /**
     * 新增client usage log
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertClientBehaviorLog(SendLogService self, String userId, String iccid, String imei,
        String appId, String versionId, Timestamp openTime, String modelName, String userUid,
        String l_msisdn, Double l_isFirstTime, String snum, String IP) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("iccid", iccid);
        map.put("imei", imei);
        map.put("appId", appId);
        map.put("versionId", versionId);
        map.put("openTime", openTime);
        map.put("modelName", modelName);
        map.put("userUid", userUid);
        map.put("msisd", l_msisdn);
        map.put("isFirstTime", l_isFirstTime);
        map.put("snum", snum);
        map.put("IP", IP);
        sqlMapClientTemplate.insert("StoreClientBehaviorLog.insertClientBehaviorLog", map);
    }

    /**
     * 更新UserApkBehavior
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserApkBehavior(SendLogService self, String userId, String userUid, String appId,
        int lastPaymentMethod) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("userUid", userUid);
        map.put("appId", appId);
        map.put("lastPaymentMethod", new Integer(lastPaymentMethod));
        sqlMapClientTemplate.update("UserApkBehavior.updateUserApkBehavior", map);
    }
}