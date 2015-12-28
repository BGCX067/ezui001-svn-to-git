package cn.vstore.appserver.api.payment.unionpay;
/*
 * relevancy MerchantTestActivity
 * author: guoqiang
 */
public class XmlDefinition {

	

	public static String SubmitOrder(boolean isTest, String merchantName,
			String merchantId, String merchantOrderId,
			String merchantOrderTime, String merchantOrderAmt,
			String merchantOrderDesc, String transTimeout, String backEndUrl,
			String sign, String merchant_public_cer) {

		String SubmitOrder = "<?xml version="+"'1.0' "
				+ "encoding=" + "'UTF-8' " + "?>"+"<upomp  application="+"'SubmitOrder.Req' " +"version="+ "'1.0.0'"+">"
				
				+ "<merchantName>"
				+ merchantName
				+ "</merchantName>"
				
				+ "<merchantId>"
				+ merchantId
				+ "</merchantId>"
				
				+ "<merchantOrderId>"
				+ merchantOrderId
				+ "</merchantOrderId>"
				
				+ "<merchantOrderTime>"
				+ merchantOrderTime
				+ "</merchantOrderTime>"
				
				+ "<merchantOrderAmt>"
				+ merchantOrderAmt
				+ "</merchantOrderAmt>"
				
				+ "<merchantOrderDesc>"
				+ merchantOrderDesc
				+ "</merchantOrderDesc>"
				
				+ "<transTimeout>"
				+ transTimeout
				+ "</transTimeout>"
				
				+ "<backEndUrl>"
				+ backEndUrl
				+ "</backEndUrl>"
				
				+ "<sign>"
				+ sign
				+ "</sign>"
				
				+ "<merchantPublicCert>"
				+ merchant_public_cer
				+ "</merchantPublicCert>"+ "</upomp>";

		return SubmitOrder;
	}

	public static String LanchPay(String manchertId, String merchantOrderId,
			String merchantOrderTime, String sign, String url) {

		String lanchpay ="<?xml version=" + "'1.0' "
				+ "encoding=" + "'UTF-8' " + "?>" + "<upomp  application="+"'LanchPay.Req' "+"version="+ "'1.0.0' "+">"				
				+ "<merchantId>"
				+ manchertId
				+ "</merchantId>"
				
				+ "<merchantOrderId>"
				+ merchantOrderId
				+ "</merchantOrderId>"
				
				+ "<merchantOrderTime>"
				+ merchantOrderTime
				+ "</merchantOrderTime>"
				
				+ "<backEndUrl>"
				+ url
				+ "</backEndUrl>"
				
				+ "<sign>"
				+ sign
				+ "</sign>"
				+ "</upomp>";

		return lanchpay;

	}

	// 生成原串
	public static String CreateOriginalSign7(String merchantName,
			String merchantId, String merchantOrderId,
			String merchantOrderTime, String merchantOrderAmt,
			String merchantOrderDesc, String transTimeout) {
		StringBuilder os = new StringBuilder();
		os.append("merchantName=").append(merchantName).append("&merchantId=")
				.append(merchantId).append("&merchantOrderId=")
				.append(merchantOrderId).append("&merchantOrderTime=")
				.append(merchantOrderTime).append("&merchantOrderAmt=")
				.append(merchantOrderAmt).append("&merchantOrderDesc=")
				.append(merchantOrderDesc).append("&transTimeout=")
				.append(transTimeout);

		return os.toString();
	}
	
	public static String CreateOriginalSign3(String merchantId, String merchantOrderId,String merchantOrderTime) {
		StringBuilder os3 = new StringBuilder();
		os3.append("merchantId=")
				.append(merchantId).append("&merchantOrderId=")
				.append(merchantOrderId).append("&merchantOrderTime=")
				.append(merchantOrderTime);

		return os3.toString();
	}

}