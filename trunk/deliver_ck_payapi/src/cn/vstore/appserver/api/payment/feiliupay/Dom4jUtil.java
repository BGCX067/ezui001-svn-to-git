package cn.vstore.appserver.api.payment.feiliupay;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.vstore.appserver.form.payment.SelectGamePayForm;
import cn.vstore.appserver.util.RsaMessage;

public class Dom4jUtil {
	public static Document getDomStrs(SelectGamePayForm selectGamePayForm, String verifyStr) {
		Document document = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("Request");
		document.setRootElement(root);
		Element orderId = root.addElement("OrderId");
		Element productId = root.addElement("ProductId");
		Element companyId = root.addElement("CompanyId");
		Element channelId = root.addElement("ChannelId");
		Element orderType = root.addElement("OrderType");
		Element denomination = root.addElement("Denomination");
		Element cardNo = root.addElement("CardNO");
		Element cardPwd = root.addElement("CardPwd");
		Element amount = root.addElement("Amount");
		Element merPriv = root.addElement("MerPriv");
		Element verifyString = root.addElement("VerifyString");

		orderId.setText(selectGamePayForm.getOrderNo());
		productId.setText("100016");//产品ID
		companyId.setText("100016");//公司ID
		channelId.setText("100043");//渠道ID
		orderType.setText(selectGamePayForm.getOrderType());
		denomination.setText(selectGamePayForm.getDenomination().toString());
		cardNo.setText(selectGamePayForm.getCardNo());
		cardPwd.setText(selectGamePayForm.getCardPwd());
		amount.setText(selectGamePayForm.getAmount());
		merPriv.setText(selectGamePayForm.getMerPriv());
		verifyString.setText(verifyStr);
		
		return document;
	}
	
	public static String getDomStr(SelectGamePayForm selectGamePayForm,
			String verifyStr) {
		Document document = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("Request");
		document.setRootElement(root);
		Element orderId = root.addElement("OrderId");
		Element productId = root.addElement("ProductId");
		Element companyId = root.addElement("CompanyId");
		Element channelId = root.addElement("ChannelId");
		Element orderType = root.addElement("OrderType");
		Element denomination = root.addElement("Denomination");
		Element cardNo = root.addElement("CardNO");
		Element cardPwd = root.addElement("CardPwd");
		Element amount = root.addElement("Amount");
		Element merPriv = root.addElement("MerPriv");
		Element verifyString = root.addElement("VerifyString");

		orderId.setText(selectGamePayForm.getOrderNo());
		productId.setText("100016");//产品ID
		companyId.setText("100016");//公司ID
		channelId.setText("100043");//渠道ID
		orderType.setText(selectGamePayForm.getOrderType());
		denomination.setText(selectGamePayForm.getDenomination().toString());
		cardNo.setText(selectGamePayForm.getCardNo());
		cardPwd.setText(selectGamePayForm.getCardPwd());
		amount.setText(selectGamePayForm.getAmount());
		merPriv.setText(selectGamePayForm.getMerPriv());
		verifyString.setText(verifyStr);
		return document.asXML();
	}

	/**
	 * 签章
	 * 
	 * @param selectGamePayForm
	 * @param orderId
	 * @return
	 */
	public static String getDSAStr(SelectGamePayForm selectGamePayForm) {
		String dsaStr = null;
		String allStr = selectGamePayForm.getOrderNo() + "|"
				+ "100016" + "|"
				+ "100016" + "|"
				+ "100043" + "|"
				+ selectGamePayForm.getOrderType() + "|"
				+ selectGamePayForm.getDenomination() + "|"
				+ selectGamePayForm.getCardNo() + "|"
				+ selectGamePayForm.getCardPwd() + "|"
				+ selectGamePayForm.getAmount() + "|"
				+ selectGamePayForm.getMerPriv();
		try {
			dsaStr = RsaMessage.getStrDSAInfo(allStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dsaStr;
	}

	/**
	 * 获取xml信息的节点
	 * @param xml
	 * @return
	 */
	public static Map<String, String> readStringXmlOut(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			// 将字符串转为XML
			Element rootElt = doc.getRootElement();
			// 获取根节点
			System.out.println("spancer返回的根节点：" + rootElt.getName());
			// 拿到根节点的名称
			String ret = rootElt.elementTextTrim("Ret");
			String retmsg = rootElt.elementText("RetMsg");
			map.put("Ret", ret);
			map.put("RetMsg", retmsg);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取订单成功与否信息
	 * @param xmlStr
	 * @return
	 */
	public static String getRetMsg(String xmlStr) {
		// String xmlString =
		// "<Response><Ret>0</Ret><RetMsg>success</RetMsg></Response>";
		Map<String, String> map = readStringXmlOut(xmlStr);
		String resultStr = map.get("Ret").toString();
		return resultStr;
	}

	/**
	 * USE
	 * 发送xml数据请求到server端
	 * @param url xml请求数据地址
	 * @param xmlString 发送的xml数据流
	 * @return null发送失败，否则返回响应内容
	 */
	public static String post(String url,String xmlString){
		//关闭合同谈判
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog"); 
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true"); 
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");
		
		HttpClient client = new HttpClient();
		PostMethod myPost = new PostMethod(url);
		client.getParams().setSoTimeout(300*1000);
		String responseString = null;
		try{
			myPost.setRequestEntity(new StringRequestEntity(xmlString,"text/xml","utf-8"));
			int statusCode = client.executeMethod(myPost);
			if(statusCode == HttpStatus.SC_OK){
				BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream());
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int count = 0;
				while((count = bis.read(bytes))!= -1){
					bos.write(bytes, 0, count);
				}
				byte[] strByte = bos.toByteArray();
				responseString = new String(strByte,0,strByte.length,"utf-8");
				bos.close();
				bis.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		myPost.releaseConnection();
		client.getHttpConnectionManager().closeIdleConnections(0);
		return responseString;
	}
	
	/**
	 * 获取飞流返回的支付结果信息
	 * @param xml
	 * @return
	 */
	public static Map<String, String> readFLStringXmlOut(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			// 将字符串转为XML
			Element rootElt = doc.getRootElement();
			// 获取根节点
			System.out.println("支付结果返回的xml数据根节点：" + rootElt.getName());
			// 拿到根节点的名称
			String flOrderId = rootElt.elementTextTrim("FLOrderId");
			String orderId = rootElt.elementText("OrderId");
			String productId = rootElt.elementTextTrim("ProductId");
			String cardNO = rootElt.elementText("CardNO");
			String amount = rootElt.elementTextTrim("Amount");
			String ret = rootElt.elementText("Ret");
			String cardStatus = rootElt.elementTextTrim("CardStatus");
			String merPriv = rootElt.elementText("MerPriv");
			String verifyString = rootElt.elementTextTrim("VerifyString");
			
			map.put("flOrderId", flOrderId);
			map.put("orderId", orderId);
			map.put("productId", productId);
			map.put("cardNO", cardNO);
			map.put("amount", amount);
			map.put("ret", ret);
			map.put("cardStatus", cardStatus);
			map.put("merPriv", merPriv);
			map.put("verifyString", verifyString);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 获取解码字符串后的数据
	 * @param rsaStrs已经解密的字符串
	 * @param rsaStr未解密的字符串(RSA字符串)
	 * @return
	 */
	public static Map<String, String> getRsaInfo(String rsaStrs,String rsaStr) {
		Map<String, String> map = new HashMap<String, String>();
//		rsaStr = "2323|1000|100016|2323323|100|1|101|feiliupay";
		try {
			//先替换“|”为“,”，不然会出现问题
			//然后再组成数组
			String splitStr = rsaStrs.replace("|", ",");
			String[] strs = splitStr.split(",");
			map.put("flOrderId", strs[0]);
			map.put("orderId", strs[1]);
			map.put("productId", strs[2]);
			map.put("cardNO", strs[3]);
			map.put("amount", strs[4]);
			map.put("ret", strs[5]);
			map.put("cardStatus", strs[6]);
			map.put("merPriv", strs[7]);
			map.put("verifyString", rsaStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static String reqStr(String ret, String orderId){
		StringBuffer stringBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        stringBuffer.append("<Request>");
        stringBuffer.append("<Ret>"+ret+"</Ret>");
        stringBuffer.append("<OrderNO>"+orderId+"</OrderNO>");
        stringBuffer.append("</Request>");
        return stringBuffer.toString();
	}
	
	/**
	 * 发送给请求客户端服务器
	 * @param url
	 * @param xmlString
	 * @return
	 */
	public static String postSpancer(String url,String xmlString){
		//关闭合同谈判
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog"); 
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true"); 
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");
		
		HttpClient client = new HttpClient();
		PostMethod myPost = new PostMethod(url);
		client.getParams().setSoTimeout(300*1000);
		String responseString = null;
		try{
			myPost.setRequestEntity(new StringRequestEntity(xmlString,"text/xml","utf-8"));
			int statusCode = client.executeMethod(myPost);
			if(statusCode == HttpStatus.SC_OK){
				BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream());
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int count = 0;
				while((count = bis.read(bytes))!= -1){
					bos.write(bytes, 0, count);
				}
				byte[] strByte = bos.toByteArray();
				responseString = new String(strByte,0,strByte.length,"utf-8");
				bos.close();
				bis.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		myPost.releaseConnection();
		client.getHttpConnectionManager().closeIdleConnections(0);
		return responseString;
	}
	
	public static void main(String[] args) throws IOException {
//		Map<String, String> map1 = new HashMap<String, String>();
//		map1.put("florder", "1");
//		map1.put("orderid", "2");
//		
//		Map<String, String> map2 = new HashMap<String, String>();
//		map2.put("florder", "1");
//		map2.put("orderid", "2");
//		
//		System.out.println(map1.equals(map2));
		
		String s1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Request><FLOrderId>134985382005</FLOrderId><OrderId>10001653662411</OrderId><ProductId>100016</ProductId><CardNO>100000123</CardNO><Amount>12</Amount><Ret>0</Ret><CardStatus>7</CardStatus><MerPriv>feiliuinterface</MerPriv><VerifyString>s8us/fe6DAT8bXu5xVd5CkOe51Sp1u91jWPfkbl/ZulCxW3ytZxbX8EaEX7WP6UV5/Ct2I5TkUm07VwfzK1DWYXG0YLXxN1z7n796l8LKzmo2SCJw3XrAlLN22yoduJcx7awyYFoKTRcQ8DAKBWOWWxntRiTBdcbxn58KbR6vcA=</VerifyString></Request>";
		Map<String, String> map1 = readFLStringXmlOut(s1);
		System.out.println("map1==="+map1.get("flOrderId"));
		
		String s2 = "s8us/fe6DAT8bXu5xVd5CkOe51Sp1u91jWPfkbl/ZulCxW3ytZxbX8EaEX7WP6UV5/Ct2I5TkUm07VwfzK1DWYXG0YLXxN1z7n796l8LKzmo2SCJw3XrAlLN22yoduJcx7awyYFoKTRcQ8DAKBWOWWxntRiTBdcbxn58KbR6vcA=";
		String s3 = RsaMessage.getStrDSADecInfo(s2);
		System.out.println("s3=="+s3);
		
		Map<String, String> map2 = getRsaInfo(s3, s2);
		System.out.println("map2==="+map2.get("flOrderId"));
		
		System.out.println(map1.equals(map2));
		
		
		Map<String, String> maps = new HashMap<String, String>();
		maps.put("", "df");
		maps.put("", "");
		
		@SuppressWarnings("rawtypes")
		Iterator iters = maps.keySet().iterator();
		while (iters.hasNext()) {
			String key = iters.next().toString(); // 拿到键
			String val = maps.get(key).toString(); // 拿到值
			System.out.println(key + "=" + val);
		}
	}
}
