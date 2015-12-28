package cn.vstore.appserver.model;

/**
 * @version $Id: UserPayment.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class UserPayment {

    /**
     * 沒有付款(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_NO_PAID = 0;
    /**
     * 付款中(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_PROCESSING = 1;
    /**
     * 付款成功(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_SUCCESS = 2;
    /**
     * 付款失敗(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_FAIL = 3;
    /**
     * 訂單取消(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_CANCEL = 4;
    /**
     * 一次性付費的反安裝或者月租取消(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_UN_SUBSCRIBE = 5;
    /**
     * 測試用假付款成功(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_TEST_SUCCESS = 6;
    /**
     * 測試用假訂單取消(IPAY_USER_PAYMENT_LOG 內的 status 參數)
     */
    public static final int STATUS_PAYMENT_TEST_CANCEL = 7;

}
