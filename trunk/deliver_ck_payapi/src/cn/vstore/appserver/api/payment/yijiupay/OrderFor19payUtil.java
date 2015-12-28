package cn.vstore.appserver.api.payment.yijiupay;

public class OrderFor19payUtil {
	// 生成原串
	public static String createOriginalSign(String version_id,
			String merchant_id, String order_date,
			String order_id, String amount,
			String currency, String cardnum1,String cardnum2,String pm_id,String pc_id,String key) {
		StringBuilder os = new StringBuilder();
		os.append("version_id=").append(version_id).append("&merchant_id=")
				.append(merchant_id).append("&order_date=")
				.append(order_date).append("&order_id=")
				.append(order_id).append("&amount=")
				.append(amount).append("&currency=")
				.append(currency).append("&cardnum1=")
				.append(cardnum1).append("&cardnum2=")
				.append(cardnum2).append("&pm_id=")
				.append(pm_id).append("&pc_id=")
				.append(pc_id).append("&merchant_key=")
				.append(key);
		return  KeyedDigestMD5.getKeyedDigest(os.toString(),"");
	}
	//生成查詢參數
	public static String createRequestParams(String version_id,
			String merchant_id, String order_date,
			String order_id, String amount,
			String currency, String cardnum1,String cardnum2,String pm_id,String pc_id,String verifystring,
			String order_pdesc,String user_name,String user_phone,
			String user_email,String order_pname,String select_amount,String notify_url)
	{
		StringBuilder os = new StringBuilder();
		os.append("version_id=").append(version_id).append("&merchant_id=")
				.append(merchant_id).append("&order_date=")
				.append(order_date).append("&order_id=")
				.append(order_id).append("&amount=")
				.append(amount).append("&currency=")
				.append(currency).append("&cardnum1=")
				.append(cardnum1).append("&cardnum2=")
				.append(cardnum2).append("&pm_id=")
				.append(pm_id).append("&pc_id=")
				.append(pc_id).append("&verifystring=")
				.append(verifystring).append("&order_pdesc=")
				.append(order_pdesc).append("&user_name=")
				.append(user_name).append("&user_phone=")
				.append(user_phone).append("&user_email=")
				.append(user_email).append("&order_pname=")
				.append(order_pname).append("&select_amount=")
				.append(select_amount).append("&notify_url=")
				.append(notify_url);
		return os.toString();
	}
	
}
