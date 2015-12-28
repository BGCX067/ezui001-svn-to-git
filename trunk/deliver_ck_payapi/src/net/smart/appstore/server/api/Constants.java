package net.smart.appstore.server.api;

import java.io.Reader;
import java.sql.Clob;

public class Constants {

	public final static int STATUS_DOWNLOAD_SUCCESS=0;
	public final static int STATUS_DOWNLOAD_FAIL=1;
	public final static int STATUS_INSTALLED=2;
	public final static int STATUS_UNINSTALL=4;
	public final static int DEFAULT_BUFFER_SIZE=1024 * 4;
	public final static int FEATURE_MAX_NUMBER=15;
	public final static int APPLIST_MAX_NUMBER=10;
	public final static int LICENSE_CREATE_FROM_TEMPLATE=1;
	public final static int LICENSE_CREATE_FROM_TEST_VERSION_CODE=2;
	public final static int LICENSE_CREATE_FROM_CHARGE=11;
	public final static String SUBSCRIBE_NOT_SEND_SMS="2";

	public static String clobToString(Clob in){
		StringBuffer ret=new StringBuffer();
		try{
			Reader reader=in.getCharacterStream();
			int i=-1;
			char[] buf=new char[DEFAULT_BUFFER_SIZE];
			while ( (i=reader.read(buf))!=-1)
			{
				ret.append(buf,0,i);
			}
			}catch(Exception e){}
		return ret.toString();
	}
	public static String getDownloadArea(int count){
		String times = "";
		if (count < 101) {
		 times = "<100";
		} else if (count > 100 && count < 501) {
		 times = "101~500";
		} else if (count > 500 && count < 1001) {
		 times = "501~1000";
		} else if (count > 1000 && count < 5001) {
		 times = "1000~5000";
		} else if (count > 5000 && count < 10001) {
		 times = "5001~10000";
		} else if (count > 10000 && count < 25001) {
		 times = "10001~25000";
		} else {
		 times = ">25000";
		}
		return times;
	}


//	0=沒有付款 1=付款中 2=付款成功 3=付款失敗 4=訂單取消 5=一次性付費的反安裝或者月租取消

	//IPAY_USER_PAYMENT_LOG 內的 status 參數
	public static final int STATUS_PAYMENT_LOG = 0;          //沒有付款
	public static final int STATUS_PAYMENT_TEMP_SUCCESS = 1; //付款中
	public static final int STATUS_PAYMENT_SUCCESS = 2;      //付款成功
	public static final int STATUS_PAYMENT_FAIL = 3;         //付款失敗
	public static final int STATUS_PAYMENT_CANCEL = 4;       //訂單取消
	public static final int STATUS_PAYMENT_FAILURE = 5;      //一次性付費的反安裝或者月租取消
	public static final int STATUS_PAYMENT_TEST_SUCCESS = 6; //測試用假付款成功
	public static final int STATUS_PAYMENT_TEST_CANCEL = 7;  //測試用假訂單取消

	//USER_SUBSCRIBE 和  USER_SUBSCRIBE_LOG 的訂閱或取消的status
	public static final int USER_SUBSCRIBE_OPEN = 0;         //訂閱已付款
	public static final int USER_SUBSCRIBE_CANCELED = 1;     //取消訂閱
	public static final int USER_SUBSCRIBE_DOWN_NOT_PAY = 2; //下載但未付款

	//付款方式
	public static final int PAY_METHOD_EMPTY = 0;             //尚未設定     E:Empty, not been set yet
	public static final int PAY_METHOD_TELECOM_BILLING = 1;   //電信帳單     B:Phone Bill
	public static final int PAY_METHOD_CREDIT_CARD = 2;       //信用卡          C:Credit Card
	public static final int PAY_METHOD_VIRTUAL_ACCOUNT = 3;   //虛擬帳號     V:Virtual Account
	public static final int PAY_METHOD_APP_PRICE_ZERO = 9;	  //0元App，免進 iPay 畫面付款

	//付款方式 Ipay
	public static enum paymentType{
		E   //尚未設定     E:Empty, not been set yet
		,B  //電信帳單     B:Phone Bill
		,C  //信用卡          C:Credit Card
		,V  //虛擬帳號     V:Virtual Account
	}

	//PRICE_TYPE
	public static final int PRICE_TYPE_FREE = 0;  //免費
	public static final int PRICE_TYPE_ONCE = 1;  //計次
	public static final int PRICE_TYPE_MONTH = 2; //月租
	public static final int PRICE_INNAPP_PURCHASE = 3; //InAppPurchase
	public static final int PRICE_INNAPP_CAR = 4; //INNAPP購物車

	//Test payment code number
	public static final String PAY_CODE_TEST_SUCCESS="TEST00000";  //Test payment code 成功

	//Ipay code number
	public static final String IPAY_CODE_SUCCESS="E00000000";  //Ipay code 成功

	//CSP code number
	public static final String CSP_CODE_SUCCESS="00000000";  //CSP code 成功
	public static final String CSP_CODE_FAIL_CREATE_IPAY="02049999";
	public static final String CSP_CODE_FAIL_NO_EMAIL="02040005";
	public static final String CSP_FAIL_CODE_LOGIN_INVALID_PASSWORD="02040000";
	public static final String CSP_FAIL_CODE_LOGIN_INVALID_USER="02040001";
	public static final String CSP_FAIL_CODE_LOGIN_INVALID_USERID="02040002";
	public static final String CSP_FAIL_CODE_LOGIN_SYSTEM_ERROR="02049900";
	public static final String CSP_STATUS_SUBSCRIBED="I"; //用戶已經訂閱服務
	public static final String CSP_SERVICE_SUBSCRIBED="04000003";  //service已被註冊，但視為成功

	//Ipay action code
	public static final String IPAY_ADD = "A"; //Ipay code 新增
	public static final String IPAY_DELETE = "D"; //Ipay code 刪除
	public static final String IPAY_UPDATE = "U"; //Ipay code 更新

	//是否為SIM用戶
	public static final String IS_SIM_TRUE = "1"; //true 是SIM用戶
	public static final String IS_SIM_FALSE = "0"; //false 不是SIM用戶

	//Ipay 付款狀態
	public static final String IPAY_PAY_SUCCSESS = "D"; //付款成功

	//0元service Id
	public static final String ZERO_SERVICE_ID = "CSOTR29899";

	//The VersionCode for CP's testing app.
	public static final int APP_TEST_VRESION_CODE = -65536;


	/**
	 * error code
	 * STI10000   <----成功
	 * STI20000   <----自家系統錯誤
	 * STI30000   <----外部系統錯誤
	 * STI40000   <----未知錯誤
	*/
	public static final String ERROR_CODE_NOT_FOUND = "STI20001";
	public static final String ERROR_CODE_VERIFICATION_FAIL = "STI20002";
	public static final String ERROR_CODE_REPEAT_FAIL = "STI20003";
	public static final String ERROR_CODE_SYSTEM_ERROE = "STI20004";
	public static final String ERROR_CODE_PARAMETER_FAIL = "STI20005";
	public static final String ERROR_CODE_AMOUNT_FAIL = "STI20006";

	// Tomed out
	public static final String ERROR_CODE_CP_FAIL = "STI30100";
	// response status code != 200
	public static final String ERROR_CODE_CP_RESPONSE_FAIL = "STI30101";
	// 金額超過5000 或 總額與細項相加後不相等
	public static final String ERROR_CODE_CP_AMOUNT_FAIL = "STI30102";
	public static final String ERROR_CODE_CP_XML_FAIL = "STI30103";

	public static final String ERROR_CODE_IPAY_OR_CSP_FAIL = "STI30200";

	//Timeout設定
	public static final int CP_PAYMENT_API_TIMED_OUT = 45000;
}
