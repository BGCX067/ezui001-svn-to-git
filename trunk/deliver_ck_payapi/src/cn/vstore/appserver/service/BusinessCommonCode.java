package cn.vstore.appserver.service;

/**
 * @version $Id$
 */
public class BusinessCommonCode {

    /**
     * CSP code number
     */
    public static final String CSP_CODE_SUCCESS = "00000000"; //CSP code 成功
    public static final String CSP_CODE_FAIL_CREATE_IPAY = "02049999";
    public static final String CSP_CODE_FAIL_NO_EMAIL = "02040005";
    public static final String CSP_FAIL_CODE_LOGIN_INVALID_PASSWORD = "02040000";
    public static final String CSP_FAIL_CODE_LOGIN_INVALID_USER = "02040001";
    public static final String CSP_FAIL_CODE_LOGIN_INVALID_USERID = "02040002";
    public static final String CSP_FAIL_CODE_LOGIN_SYSTEM_ERROR = "02049900";
    public static final String CSP_STATUS_SUBSCRIBED = "I"; //用戶已經訂閱服務
    public static final String CSP_SERVICE_SUBSCRIBED = "04000003"; //service已被註冊，但視為成功
    public static final String CSP_UNSUBSCRIBED = "04230002"; //已取消訂閱，但視為成功

    /**
     * Ipay code number
     */
    public static final String IPAY_CODE_SUCCESS = "E00000000"; //Ipay code 成功
    public static final String IPAY_PAY_SUCCSESS = "D"; //付款成功

    /**
     * 0元service Id
     */
    public static final String ZERO_SERVICE_ID = "CSOTR29899";

    public final static String SUBSCRIBE_NOT_SEND_SMS = "2";

    /**
     * IPAY or CSP的錯誤
     */
    public static final String ERROR_CODE_IPAY_OR_CSP_FAIL = "STI30200";

    /**
     * USER_SUBSCRIBE 和 USER_SUBSCRIBE_LOG 的訂閱或取消的status
     */
    public static final int USER_SUBSCRIBE_OPEN = 0; //訂閱已付款
    public static final int USER_SUBSCRIBE_CANCELED = 1; //取消訂閱
    public static final int USER_SUBSCRIBE_DOWN_NOT_PAY = 2; //下載但未付款

    /**
     * 是否過到期日，到期：1，未到期：2
     */
    public static final int ENDDATE = 1;
    public static final int NOT_ENDDATE = 2;

}
