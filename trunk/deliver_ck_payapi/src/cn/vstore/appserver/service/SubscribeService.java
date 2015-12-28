package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.smart.appstore.server.api.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.Account;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.model.UserPayment;
import cn.vstore.appserver.model.UserSubscribe;
import cn.vstore.appserver.model.UserSubscribeLog;
import cn.vstore.appserver.service.ResultCode.CommonCode;

/**
 * @version $Id: SubscribeService.java 6932 2010-12-30 02:57:47Z yellow $
 */
@Service("SubscribeService")
public class SubscribeService {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeService.class);

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BlankListService blankListService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CspService cspService;

    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Unsubscribe> unsubscribe(SubscribeService self, String token,
        String iccid, String imei, String pkgId) {

        Unsubscribe unsubscribe = new Unsubscribe();

        try {
            // 取得使用者資訊
            Prosumer users = authenticationService.getProsumerByAccount(token);
            String userUid = users.getUserUid();
            String userId = users.getUserId();

            // 取得有效付款的ServiceId和endTime資訊
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", pkgId);
            map.put("userUid", userUid);
            UserSubscribe userSubscribe = (UserSubscribe) sqlMapClientTemplate.queryForObject("UserSubscribe.getUserSubcribeOfNotCancel", map);
            if (userSubscribe == null) {
                unsubscribe.status = Unsubscribe.UNSUBSCRIBEED_NOTSUBSCRIBE;
                return new ServiceResult<Unsubscribe>(CommonCode.SUCCESS, unsubscribe);
            }

            // 取得付費狀態
            PaymentInformation paymentInformation = null;
            boolean blankTest = blankListService.isMockPayment(blankListService, userId);
            if (blankTest) { // true:測試使用, false:實際使用)
                paymentInformation = paymentService.getSuccessPaidInfoByTest(paymentService, userUid, pkgId);
            } else {
                paymentInformation = paymentService.getSuccessPaidInfo(paymentService, userUid, pkgId);
            }

            if (paymentInformation == null) {
                unsubscribe.status = Unsubscribe.UNSUBSCRIBEED_NOTSUBSCRIBE;
                logger.warn("This data problem has been successfully subscribed, but no one paid a successful state ...  -- pkgId="
                        + pkgId + "; userUid=" + userUid);
                return new ServiceResult<Unsubscribe>(CommonCode.SUCCESS, unsubscribe);
            }

            // 計算到期日，計算一個月後
            Date begin = userSubscribe.getBeginTime();
            Calendar maturityCal = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            maturityCal.setTime(begin);
            maturityCal = operatorMaturity(maturityCal, today);

            // 電信帳單取消後，次日不能使用。信用卡取消後，一個月後不能使用。
            if (paymentInformation.getPaymentType() == Constants.PAY_METHOD_TELECOM_BILLING) {
                unsubscribe.endTime = today;
            } else if (paymentInformation.getPaymentType() == Constants.PAY_METHOD_CREDIT_CARD) {
                unsubscribe.endTime = maturityCal;
            }
            userSubscribe.setEndTime(new Timestamp(unsubscribe.endTime.getTime().getTime()));
            paymentInformation.setRightEndDate(new Timestamp(unsubscribe.endTime.getTime().getTime()));

            // 是否執行update 資料
            boolean isUpdateData = false;

            ServiceResult<Boolean> serviceResult = null;
            // 如果為假付費測試，不執行CSP的取消訂閲
            if (paymentInformation.getStatus() == Constants.STATUS_PAYMENT_TEST_SUCCESS) {
                isUpdateData = true;
            } else {
                // call CSP取消訂閲
                serviceResult = cspService.unsubscribe(userId, userSubscribe.getServiceId(), userUid);
                if (serviceResult != null)
                    isUpdateData = serviceResult.getData().booleanValue();
            }

            // Call CSP時，如果有錯誤訊息，則 update IPAY_USER_PAYMENT_LOG 錯誤內容
            if (serviceResult != null && serviceResult.getResult() != null) {
                if (serviceResult.getThrowable() != null) {
                    paymentInformation.setRetMsgWithThrowable(serviceResult.getThrowable());
                } else {
                    paymentInformation.setRetMsg(serviceResult.getDes() != null ? serviceResult.getDes()
                            : "DesubscribeAPServiceResponse is not return");
                }
                paymentInformation.setApiName("DesubscribeAPService");
                paymentInformation.setRetCode(serviceResult.getCode() != null ? serviceResult.getCode()
                        : BusinessCommonCode.ERROR_CODE_IPAY_OR_CSP_FAIL);
                // 更新IPAY_USER_PAYMENT_LOG錯誤訊息
                paymentService.updateIpayUserPaymentLogNoStatus(paymentService, paymentInformation);

                if (serviceResult.getCode() != null) {
                    unsubscribe.status = Unsubscribe.CSP_RETURN_OTHER_CODE;
                } else {
                    unsubscribe.status = Unsubscribe.CSP_FAIL;
                }
                return new ServiceResult<Unsubscribe>(CommonCode.SUCCESS.bindSource((serviceResult != null ? serviceResult.getResult()
                        : null)), unsubscribe, (serviceResult != null ? serviceResult.getThrowable()
                        : null));
            }

            // CSP取消訂閲成功
            if (isUpdateData) {
                try {
                    self.accessSuccessPayment(self, userUid, pkgId, userSubscribe, paymentInformation, blankTest);
                } catch (Throwable e) {
                    return new ServiceResult<Unsubscribe>(CommonCode.SERVICE_FAIL, unsubscribe, e);
                }
                unsubscribe.status = Unsubscribe.SUCCESS_UNSUBSCRIBE;
            }
        } catch (Throwable e) {
            return new ServiceResult<Unsubscribe>(CommonCode.SERVICE_FAIL, unsubscribe, e);
        }

        return new ServiceResult<Unsubscribe>(CommonCode.SUCCESS, unsubscribe);
    }

    /**
     * 計算到期日
     */
    private Calendar operatorMaturity(Calendar beginCal, Calendar nowCal2) {
        if (nowCal2.get(Calendar.YEAR) == beginCal.get(Calendar.YEAR)
                && nowCal2.get(Calendar.MONTH) == beginCal.get(Calendar.MONTH)
                && nowCal2.get(Calendar.DAY_OF_MONTH) == beginCal.get(Calendar.DAY_OF_MONTH)) {
            beginCal.add(Calendar.MONTH, 1);
        } else {
            if (nowCal2.get(Calendar.DAY_OF_MONTH) > beginCal.get(Calendar.DAY_OF_MONTH)) {
                beginCal.add(Calendar.MONTH, (nowCal2.get(Calendar.YEAR) - beginCal.get(Calendar.YEAR))
                        * 12 + (nowCal2.get(Calendar.MONTH) - beginCal.get(Calendar.MONTH)) + 1);
            } else {
                beginCal.add(Calendar.MONTH, (nowCal2.get(Calendar.YEAR) - beginCal.get(Calendar.YEAR))
                        * 12 + (nowCal2.get(Calendar.MONTH) - beginCal.get(Calendar.MONTH)));
            }
        }
        return beginCal;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void accessSuccessPayment(SubscribeService self, String userUid, String pkgId,
        UserSubscribe userSubscribe, PaymentInformation paymentInformation, boolean isTestBlankType) {

        userSubscribe.setIsCanceled(BusinessCommonCode.USER_SUBSCRIBE_CANCELED);
        userSubscribe.setUserUid(userUid);
        userSubscribe.setAppId(pkgId);

        // 更新USER_SUBSCRIBE中的IS_CANCELED/END_TIME/CANCEL_SRC_TYPE
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isCanceled", new Integer(BusinessCommonCode.USER_SUBSCRIBE_CANCELED));
        map.put("endTime", userSubscribe.getEndTime());
        map.put("subscribeId", new Long(userSubscribe.getSubscribeId()));
        sqlMapClientTemplate.update("UserSubscribe.updateUnSubcribe", map);
        // 更新USER_SUBSCRIBE_LOG中的CANCEL_TIME/END_TIME/CANCEL_SRC_TYPE
        map = new HashMap<String, Object>();
        map.put("endTime", userSubscribe.getEndTime());
        map.put("userUid", userSubscribe.getUserUid());
        map.put("pkgId", userSubscribe.getAppId());
        sqlMapClientTemplate.update("UserSubscribe.updateUnSubcribeLog", map);

        // 更新IPAY_USER_PAYMENT_LOG的status
        if (isTestBlankType) { // true:測試使用, false:實際使用)
            paymentInformation.setStatus(UserPayment.STATUS_PAYMENT_TEST_CANCEL);
            paymentService.updateFailurePaidLogByTest(paymentService, paymentInformation);
        } else {
            paymentInformation.setStatus(UserPayment.STATUS_PAYMENT_UN_SUBSCRIBE);
            paymentService.updateFailurePaidLog(paymentService, paymentInformation);
        }
    }

    /**
     * 取出IS_CANCELED/END_TIM
     */
    UserSubscribe getIsCanceledAndEndTime(String appId, String userUid, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("storeId", storeId);
        return (UserSubscribe) sqlMapClientTemplate.queryForObject("UserSubscribe.getIsCanceledAndEndTime", map);
    }

    /**
     * 取得Subscribe Id
     */
    long getSubcribeId() {
        return ((Long) sqlMapClientTemplate.queryForObject("UserSubscribe.getSubcribeId")).longValue();
    }

    /**
     * 更新Subscribe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSubcribe(SubscribeService self, UserSubscribe userSubscribe) {
        sqlMapClientTemplate.update("UserSubscribe.updateSubcribe", userSubscribe);
    }

    /**
     * 更新SubcribeId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSubcribeId(SubscribeService self, long subcribeId, UserSubscribe userSubscribe) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("subcribeId", new Long(subcribeId));
        map.put("userUid", userSubscribe.getUserUid());
        map.put("appId", userSubscribe.getAppId());
        sqlMapClientTemplate.update("UserSubscribe.updateSubcribeId", map);
    }

    /**
     * 更新USER_SUBSCRIBE中的IS_CANCELED/END_TIME=null
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIsCanceledWithUserUidAndAppId(SubscribeService self, int isCanceled, String userUid,
        String appId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isCanceled", new Integer(isCanceled));
        map.put("userUid", userUid);
        map.put("appId", appId);
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("UserSubscribe.updateIsCanceledWithUserUidAndAppId", map);
    }

    /**
     * 新增USER_SUBSCRIBE_LOG資訊For 下載APK時用
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserSubcribeForDownloadApk(SubscribeService self, String userId, String appId,
        Date endTime, int isCanceled, int versionId, int lastVersionId, String userUid, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("appId", appId);
        map.put("endTime", endTime);
        map.put("isCanceled", new Integer(isCanceled));
        map.put("versionId", new Integer(versionId));
        map.put("lastVersionId", new Integer(lastVersionId));
        map.put("userUid", userUid);
        map.put("storeId", storeId);
        sqlMapClientTemplate.insert("UserSubscribe.insertUserSubcribeForDownloadApk", map);
    }

    /**
     * 新增Subscribe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertSubcribe(SubscribeService self, UserSubscribe userSubscribe) {
        sqlMapClientTemplate.insert("UserSubscribe.insertSubcribe", userSubscribe);
    }

    /**
     * 新增Subscribe Log
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertSubcribeLog(SubscribeService self, UserSubscribeLog userSubscribeLog) {
        sqlMapClientTemplate.insert("UserSubscribe.insertSubcribeLog", userSubscribeLog);
    }

    /**
     * 更新LAST_VERSION_ID，條件為APP_ID/USER_UID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastVersionId(SubscribeService self, int lastVersionId, String appId, String userUid, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lastVersionId", Integer.valueOf(lastVersionId));
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("UserSubscribe.updateLastVersionId", map);
    }
}