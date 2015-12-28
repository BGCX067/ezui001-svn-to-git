package cn.vstore.appserver.service;

import java.util.Calendar;

/**
 * @version $Id: Unsubscribe.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class Unsubscribe {

    /**
     * 取消訂閱成功 CPS: 00000000, 04230002
     */
    public static final String SUCCESS_UNSUBSCRIBE = "1";
    /**
     * CPS SDK 發生錯誤
     */
    public static final String CSP_FAIL = "2";
    /**
     * CPS SDK 回傳其他值
     */
    public static final String CSP_RETURN_OTHER_CODE = "3";
    /**
     * 未訂閱、已取消訂閱、或在USER_SUBSCRIBE找不到
     */
    public static final String UNSUBSCRIBEED_NOTSUBSCRIBE = "4";

    public String status;
    public Calendar endTime;

    Unsubscribe() {
    }
}
