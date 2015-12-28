package cn.vstore.appserver.service;

import org.apache.commons.lang.builder.ToStringBuilder;

public final class ResultCode {
    public static final String CLIENT_MSG_PREFIX = "retmsg.";
    public static final String SYSTEM_MSG_PREFIX = "sysretmsg.";

    public static final class CommonCode {
        public static final String model = "00"; // IN_BLANK_LIST
        public static final ResultCode SUCCESS = new ResultCode(model, "01");//成功
        public static final ResultCode SERVICE_FAIL = new ResultCode(model, "02");//Service錯誤
        public static final ResultCode NEW_CLIENT = new ResultCode(model, "03");//有新版本的Client
        public static final ResultCode PARAMETER_ERROR = new ResultCode(model, "04");//http request parameter 錯誤
        public static final ResultCode TOKEN_PARAMETER_ERROR = new ResultCode(model, "05");//Token http request parameter 錯誤
        public static final ResultCode NO_APP_SEARCH = new ResultCode(model, "99");//Token http request parameter 錯誤
        public static final ResultCode INVALID_TOKEN = new ResultCode(model, "10");//Token無效
        public static final ResultCode NOT_IN_BLANK_LIST = new ResultCode(model, "11");//不再白名單內
        public static final ResultCode NOT_IN_NETWORK_SEGMENT = new ResultCode(model, "12");//不再網段內
        public static final ResultCode TIME_ZONE_48HOURS_ERROR = new ResultCode(model, "13");//手機端的時間必須跟server端48小時內誤差
    }

    public static final class RegisterCode {
        public static final String model = "22";
        public static final ResultCode USER_ALREADY_EXISTED = new ResultCode(model, "01");//已經有此使用者
        public static final ResultCode NICKNAME_DUPLICATE = new ResultCode(model, "02");//已經有此暱稱
        
        public static final ResultCode INVALID_USER_ID = new ResultCode(model, "03");//未輸入帳號或者不是合法的email格式
        public static final ResultCode INVALID_ACCOUNT_LENGTH = new ResultCode(model, "04");//帳號長度必須小於100個字母
        public static final ResultCode INVALID_PASSWORD = new ResultCode(model, "05");//密碼長度必須在6~個字之間
        public static final ResultCode INVALID_NICKNAME = new ResultCode(model, "06");//未輸入昵稱或者長度超過20個字
        public static final ResultCode INVALID_SIGNATURE = new ResultCode(model, "07");//个性签名超过10个字
    }
    
    public static final class Web_RegisterCode {
        public static final String model = "29";
        public static final ResultCode USER_ALREADY_EXISTED = new ResultCode(model, "01");//已經有此使用者
        public static final ResultCode NICKNAME_DUPLICATE = new ResultCode(model, "02");//已經有此暱稱
        
        public static final ResultCode INVALID_USER_ID = new ResultCode(model, "03");//未輸入帳號或者不是合法的email格式
        public static final ResultCode INVALID_ACCOUNT_LENGTH = new ResultCode(model, "04");//帳號長度必須小於100個字母
        public static final ResultCode INVALID_PASSWORD = new ResultCode(model, "05");//密碼長度必須在6~個字之間
        public static final ResultCode INVALID_NICKNAME = new ResultCode(model, "06");//未輸入昵稱或者長度超過20個字
        public static final ResultCode INVALID_SIGNATURE = new ResultCode(model, "07");//个性签名超过10个字
        
    }
    
    public static final class LoginCode {
        public static final String model = "01";
        public static final ResultCode LOGIN_INVALID_PWD = new ResultCode(model, "01");//密碼錯誤
        public static final ResultCode LOGIN_INVALID_ACCOUNT = new ResultCode(model, "02");//该帐号已经停用
        public static final ResultCode LOGIN_NO_ACCOUNT = new ResultCode(model, "03");//无此帐号
        public static final ResultCode NO_PROSUMER_ACCOUNT = new ResultCode(model, "04");//请完整填写一般使用者的资料。
    }
    
//    public static final class Web_LoginCode {
//        public static final String model = "28";
//        public static final ResultCode LOGIN_INVALID_PWD = new ResultCode(model, "01");//密碼錯誤
//        public static final ResultCode LOGIN_INVALID_ACCOUNT = new ResultCode(model, "02");//该帐号已经停用
//        public static final ResultCode LOGIN_NO_ACCOUNT = new ResultCode(model, "03");//无此帐号
//    }

    public static final class AutoLoginCode {
        public static final String model = "02";
        public static final ResultCode AUTO_LOGIN_FAIL = new ResultCode(model, "01");//自動登入失敗
    }

    public static final class IpLoginCode {
        public static final String model = "03";
        public static final ResultCode LOGIN_FAIL = new ResultCode(model, "01");//無法登入該系統
    }

    public static final class FeatureAppsCode {
        public static final String model = "04";
    }

    public static final class CategoryListCode {
        public static final String model = "05";
    }

    public static final class AppOfCategoryCode {
        public static final String model = "06";
    }

    public static final class AppDetailCode {
        public static final String model = "07";
        public static final ResultCode NO_APP = new ResultCode(model, "01");//無此Application
    }

    public static final class CommentsOfAppCode {
        public static final String model = "08";
    }

    public static final class ReportAppCode {
        public static final String model = "09";
        public static final ResultCode NO_APP = new ResultCode(model, "01");//無此Application
        public static final ResultCode REPORT_CONTENT_ERRORS = new ResultCode(model, "02");//檢舉內容錯誤
    }

    public static final class RateAppCode {
        public static final String model = "10";
        public static final ResultCode NO_APP = new ResultCode(model, "01");//無此Application
        public static final ResultCode RATINF_SCPRE_ERROR = new ResultCode(model, "02");//評分分數錯誤
        public static final ResultCode COMMENT_TOO_LANG = new ResultCode(model, "03");//評論內容太長
        public static final ResultCode NEED_LOGIN = new ResultCode(model, "04");//需登入后才可评论
        public static final ResultCode NEED_DOWNLOAD = new ResultCode(model, "05");//需下载后才可评论
    }

    public static final class SearchAppsCode {
        public static final String model = "11";
    }

    public static final class CpAppsCode {
        public static final String model = "12";
        public static final ResultCode NO_CP = new ResultCode(model, "01");//無此開發商
    }

    public static final class MyDownLoadCode {
        public static final String model = "13";
    }

    public static final class ClientUsageLogCode {
        public static final String model = "14";
    }

    public static final class OverPaymentQuotaCode {
        public static final String model = "15";
        public static final ResultCode NO_APP = new ResultCode(model, "01");//無此Application
    }

    public static final class CheckPayStatusCode {
        public static final String model = "16";
        public static final ResultCode NO_APP = new ResultCode(model, "01");//無此應用程式
        public static final ResultCode NO_PAID = new ResultCode(model, "02");//無付款資訊
        public static final ResultCode NO_SERVICE_ID = new ResultCode(model, "03");//無ServiceId
    }

    public static final class DoUnsubscribeCode {
        public static final String model = "17";
    }

    public static final class CheckNewClientCode {
        public static final String model = "18";
        public static final ResultCode NO_NEW_CLIENT_CODE = new ResultCode(model, "01");//查無最新版本
    }

    public static final class DownloadClientCode {
        public static final String model = "19";
        public static final ResultCode NO_CLIENT_APK = new ResultCode(model, "01");//無此應用程式
    }

    public static final class CheckMyAppsVersionCode {
        public static final String model = "20";
    }

    public static final class DownloadAppCode {
        public static final String model = "21";
        public static final ResultCode INVALID_AUTH = new ResultCode(model, "01");//無此權限
        public static final ResultCode NO_APP = new ResultCode(model, "02");//無此Application
    }

    public static final class CheckAppStoreCode {
        public static final String model = "26";
        public static final ResultCode NO_APP = new ResultCode(model, "01");//無此Application
    }
    
    public static final class GetSdkAppInfoCode{
    	public static final String model = "27";
    	public static final ResultCode No_APP = new ResultCode(model, "01");//無此app
    }
    
    public static final class SendLogCode {
        public static final String model = "35";
        public static final ResultCode TOKEN_IS_NULL = new ResultCode(model, "01");//token為空
        public static final ResultCode IMEI_IS_NULL = new ResultCode(model, "02");//Imei為空
        public static final ResultCode DVC_IS_NULL = new ResultCode(model, "03");//dvc為空
        public static final ResultCode LOG_IS_NULL = new ResultCode(model, "04");//log為空
        public static final ResultCode ICCID_IS_NULL = new ResultCode(model, "05");//Iccid為空
        public static final ResultCode INVALID_USER = new ResultCode(model, "06");//不合法使用者
        
    }
    
    public static final class CspCode {
        public static final String model = "98";
        public static final ResultCode CSP_SUCCESS = new ResultCode(model, "00");//系統無回應
        public static final ResultCode CSP_NO_FEEDBACK = new ResultCode(model, "01");//系統無回應
        public static final ResultCode INVALID_PASSWORD = new ResultCode(model, "02");//驗證無效密碼
        public static final ResultCode INVALID_USER = new ResultCode(model, "03");//驗證無效使用者
        public static final ResultCode INVALID_USERID = new ResultCode(model, "04");//驗證無效使用者ID
        public static final ResultCode CSP_PAYMENT_FAIL = new ResultCode(model, "05");//付款失敗
        public static final ResultCode UNSUBSCRIBE_FAIL = new ResultCode(model, "06");//取消訂閱失敗
    }

    public static final class IpayCode {
        public static final String model = "99";
        public static final ResultCode IPAY_NO_FEEDBACK = new ResultCode(model, "01");//系統無回應
        public static final ResultCode IPAY_PAYMENT_FAIL = new ResultCode(model, "02");//付款失敗
    }
    
    public static final class ForgetPwdCode {
        public static final String model = "24";
        public static final ResultCode USER_NOT_EXISTED = new ResultCode(model, "01");//帳號(email)不存在
        public static final ResultCode UPDATE_FAIL = new ResultCode(model, "02");//更新失敗
    }
    
    public static final class ResetPwdCode {
        public static final String model = "25";
        public static final ResultCode PWD_IS_NULL = new ResultCode(model, "01");//密碼是NULL或空白
        public static final ResultCode TOKEN_IS_NULL = new ResultCode(model, "02");//resetToken是NULL或空白
        public static final ResultCode UPDATE_FAIL = new ResultCode(model, "03");//更新失敗
        public static final ResultCode TIMEOUT = new ResultCode(model, "04");//時間逾時
        public static final ResultCode USER_NOT_EXISTED = new ResultCode(model, "05");//帳號(email)不存在
    }
    
    public static final class RecommenderCode{
    	public static final String model = "31";
    	public static final ResultCode No_RECOMMENDER = new ResultCode(model, "01");//不存在的達人
    }

    public static final class GetLicenseCode{
    	public static final String model = "34";
    	public static final ResultCode No_LICENSE = new ResultCode(model, "01");//無合法的license
    }
    
    public static final class SubstoreOkOrderCode{
    	public static final String model = "36";
    	public static final ResultCode EXISTED_ORDER = new ResultCode(model, "01"); //此訂單已经存在
    	public static final ResultCode INVALID_PKG = new ResultCode(model, "02"); //無效的app
    }
    
    public static final class SubstoreCancelOrderCode{
    	public static final String model = "37";
    	public static final ResultCode INVALID_ORDER = new ResultCode(model, "01"); //無效的訂單
    	public static final ResultCode HAVE_REFUND_HISTORY = new ResultCode(model, "02"); //已經申請退款或退款已經核准
    	public static final ResultCode CANCEL_REJECT = new ResultCode(model, "03"); //非月租型，超過3天不可退款
    }
    
    public static final class GetOrderStatus{
    	public static final String model = "44";
    	public static final ResultCode INVALID_ORDER = new ResultCode(model, "01"); //無效的訂單
    	public static final ResultCode INVALID_PKG = new ResultCode(model, "02"); //無效的app
    }
    public static final class CancelOrderCode{
    	public static final String model = "45";
    	public static final ResultCode INVALID_ORDER_NO = new ResultCode(model, "01"); //無效的訂單
    	public static final ResultCode HAVE_REFUND_HISTORY = new ResultCode(model, "02"); //已經申請退款或退款已經核准
    	public static final ResultCode CANCEL_REJECT = new ResultCode(model, "03"); //非月租型，超過3天不可退款
    }
    public static final class GetPePayOrderCode{
    	public static final String model = "46";
    	public static final ResultCode USER_IN_PAY_PROCESS = new ResultCode(model, "01"); //用戶已在購買流程中
    	public static final ResultCode USER_ALREADY_BUY = new ResultCode(model, "02"); //用戶已購買
    }
    
    public final ResultCode bindSource(ResultCode source) {
        ResultCode bindedResult = new ResultCode(this);
        bindedResult.source = source;
        return bindedResult;
    }

    private final String model;
    private final String code;
    private final String completeCode;
    private ResultCode source;

    private ResultCode(ResultCode resultCode) {
        this.model = resultCode.model;
        this.code = resultCode.code;
        this.source = resultCode.source;
        this.completeCode = model + code;
    }

    private ResultCode(String model, String code) {
        this.model = model;
        this.code = code;
        this.completeCode = model + code;
    }

    private ResultCode(String model, String code, ResultCode source) {
        this.model = model;
        this.code = code;
        this.source = source;
        this.completeCode = model + code;
    }

    public String getCompleteCode() {
        return completeCode;
    }

    public String getDeepSouceCompleteCode() {
        if (source != null) {
            return source.getDeepSouceCompleteCode();
        } else {
            return getCompleteCode();
        }
    }

    public String getClientMessageCodes() {
        return CLIENT_MSG_PREFIX + getCompleteCode();
    }

    public String getSystemMessageCodes() {
        return SYSTEM_MSG_PREFIX + getDeepSouceCompleteCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
