package cn.vstore.appserver.api.payment.shenzhoufu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.codec.digest.DigestUtils;

public class SZFTest {
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		String orderId = "20121115-160355-000000000001";//request.getParameter("orderId");
		String payMoney = "500";//request.getParameter("payMoney");
		String cardMoney = "500";//request.getParameter("cardMoney");
	    String sn = "07361019478333421";//request.getParameter("sn");
	    String password = "12344123";//request.getParameter("password");

	    String szfurl = "http://pay3.shenzhoufu.com/interface/version3/serverconnszx/entry-noxml.aspx";   //神州行充值卡服务器直连接口入口
	    String returnUrl = "http://211.144.39.6:8080/api/integrate/szfpay/receiver"; //服务器返回地址

	    String version = "3"; //接口版本号
	    String merId = "160355"; //商户ID
	    String merUserName = "bjvtion";  //商户用户 名
	    String merUserMail = "zhangd@vtion.com.cn";  //商户用户 Email
	    String privateField = "bjvtiontoshenzhoufu"; //商户私有数据
	    String verifyType = "1";//MD5 校验
	    String desKey = "ba5hPT39SZE=";
	    String privateKey = "vtionbj";
	    String cardTypeCombine = "0"; //0：移动 1：联通 2：电信
	    String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(cardMoney, sn, password, desKey);   //充值卡加密信息
	    String combineString = version + merId + payMoney + orderId + returnUrl + cardInfo + privateField + verifyType + privateKey;
	    String md5String = DigestUtils.md5Hex(combineString); //md5加密串
	    System.out.println("cardInfo===="+cardInfo);
	    System.out.println("md5加密前拼窜：" + combineString);
	    //构造 url 请求数据
	    String urlRequestData = szfurl + "?version=" + version
	            + "&merId=" + merId
	            + "&payMoney=" + payMoney
	            + "&orderId=" + orderId
	            + "&returnUrl=" + returnUrl
	            + "&cardInfo=" + URLEncoder.encode(cardInfo, "utf-8")
	            + "&merUserName=" + merUserName
	            + "&merUserMail=" + merUserMail
	            + "&privateField=" + privateField
	            + "&verifyType=" + verifyType
	            + "&cardTypeCombine=" + cardTypeCombine
	            + "&md5String=" + md5String
	            + "&signString=";
	    System.out.println("构造 url 请求数据：" + urlRequestData);
	    //服务器建立连接
	    HttpURLConnection httpConnection;
	    URL url;
	    int code;
	    int szfResponseCode;
	    try {
	        url = new URL(urlRequestData);
	        httpConnection = (HttpURLConnection) url.openConnection();
//	        httpConnection.setRequestMethod("GET");
	        httpConnection.setRequestMethod("POST");
	        httpConnection.setDoOutput(true);
	        httpConnection.setDoInput(true);
	        code = httpConnection.getResponseCode();
	        System.out.println("连接神州付服务器：" + szfurl + "，HTTP响应代码：" + code);
	        if (code == HttpURLConnection.HTTP_OK) {
	            try {
//	                String strCurrentLine = "";
	                BufferedReader reader = null;
	                reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF-8"));
	                //当正确响应时处理数据
	                szfResponseCode = httpConnection.getHeaderFieldInt("szfResponseCode", 0);
	                System.out.println("连接神州付服务器：" + szfurl + "，SZF响应代码：" + szfResponseCode);
	                //数据通过校验
	                if (szfResponseCode == 200) {
	                    System.out.println("<html>\n" +
	                            "<head><title></title>\n" +
	                            "</head>\n" +
	                            "<body>\n" +
	                            "支付处理中,请稍候...\n" +
	                            "</body>\n" +
	                            "</html>");
	                }
	            } catch (IOException e) {
	                System.out.println("连接神州付服务器：" + szfurl + "异常，e=" + e);
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("连接神州付服务器：" + szfurl + "异常，e=" + e);
	    }
	}
	
}
