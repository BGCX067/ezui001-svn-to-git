package cn.vstore.appserver.util;

public class URLUtil {
	
	//商户测试地址-飞流
	public static final String TEST_FL_VTION_POST_SP = "http://nuclearc.fullpowers.org/store/verifyProductFlyCallback";
	//商户正是地址
	public static final String FL_VTION_POST_SP = "http://42.120.60.44/store/verifyProductFlyCallback";
	
	//商户测试地址—当乐
	public static final String TEST_DL_VTION_POST_SP = "http://nuclearc.fullpowers.org/store/verifyProductFlyCallback";
	
	public static final String DL_VTION_POST_SP = "http://42.120.60.44/store/verifyProductFlyCallback";
	
	
	/**
	 * 飞流支付订单接口
	 */
	public static final String FEILIU_ORDER_PAY_URL = "http://pay.feiliu.com/order/billinterface";
	
	public static final String FLGPAY_INFO = "<flpay><partner>100043</partner><seller>flpay@vtion.com.cn</seller></flpay>";
	public static final String DLGPAY_INFO = "<dlpay><partner>100382</partner><seller>danglepay@vtion.com.cn</seller></dlpay>";
	
	
	/**
	 * 神州付信息
	 */
	//神州付订单支付接口URL
	public static final String SZF_ORDER_PAY_URL="http://pay3.shenzhoufu.com/interface/version3/serverconnszx/entry-noxml.aspx";
	public static String SZF_VERSION = "3"; //接口版本号
	public static String SZF_MERID = "160355"; //商户ID
	public static String SZF_MERUSERNAME = "bjvtion";  //商户用户 名
	public static String SZF_MERUSERMAIL = "zhangd@vtion.com.cn";  //商户用户 Email
	public static String SZF_PRIVATEFIELD = "bjvtiontoshenzhoufu"; //商户私有数据
	public static String SZF_VERIFYTYPE = "1";//MD5 校验
	public static String SZF_DESKEY = "ba5hPT39SZE=";
	public static String SZF_PRIVATEKEY = "vtionbj";
	public static String SZF_RETURNURL="http://124.207.138.152:28080/payapi/api/integrate/szfpay/receiver";
	//"http://211.144.39.6:8080/payapi/api/integrate/szfpay/receiver";
}
