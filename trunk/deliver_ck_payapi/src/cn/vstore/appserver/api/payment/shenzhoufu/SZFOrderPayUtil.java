package cn.vstore.appserver.api.payment.shenzhoufu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import cn.vstore.appserver.form.payment.SelectOrderPayForm;
import cn.vstore.appserver.util.URLUtil;

public class SZFOrderPayUtil {
	
	/**
	 * 发送订单信息到神州付支付服务器
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	public int sendOrderInfoToSZF(SelectOrderPayForm selectOrderPayForm) throws UnsupportedEncodingException{
//		//生成订单号
//		String orderId = request.getParameter("orderId");//request.getParameter("orderId");"20121115-160355-000000000001"
//	    String payMoney = request.getParameter("payMoney");//request.getParameter("payMoney");"100";支付金额
//	    String cardMoney = request.getParameter("cardMoney");//request.getParameter("cardMoney");"10000";卡面额
//	    String sn = request.getParameter("sn");//request.getParameter("sn");"12";卡号
//	    String password = request.getParameter("password");//request.getParameter("password");"123456";卡密

//	    String szfurl = "http://pay3.shenzhoufu.com/interface/version3/serverconnszx/entry-noxml.aspx";//神州行充值卡服务器直连接口入口
//	    String returnUrl = "http://localhost/serverReceive.jsp"; //服务器返回地址
//
//	    String version = "3"; //接口版本号
//	    String merId = "160355"; //商户ID
//	    String merUserName = "bjvtion";  //商户用户 名
//	    String merUserMail = "zhangd@vtion.com.cn";  //商户用户 Email
//	    String privateField = "bjvtiontoshenzhoufu"; //商户私有数据
//	    String verifyType = "1";//MD5 校验
//	    String desKey = "ba5hPT39SZE=";
//	    String privateKey = "vtionbj";
//	    String cardTypeCombine = "0"; //0：移动 1：联通 2：电信
	    String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(selectOrderPayForm.getDenomination().toString(), selectOrderPayForm.getCardno(), selectOrderPayForm.getCardpwd(), URLUtil.SZF_DESKEY);   //充值卡加密信息
	    String combineString = selectOrderPayForm.getVersion() + selectOrderPayForm.getMerchantsid() + selectOrderPayForm.getAmount() + selectOrderPayForm.getOrderno() + URLUtil.SZF_RETURNURL + cardInfo + URLUtil.SZF_PRIVATEFIELD + selectOrderPayForm.getVerifytype() + URLUtil.SZF_PRIVATEKEY;
	    String md5String = DigestUtils.md5Hex(combineString); //md5加密串
	    System.out.println("cardInfo===="+cardInfo);
	    System.out.println("md5加密前拼窜：" + combineString);
	    //构造 url 请求数据
	    String urlRequestData = URLUtil.SZF_ORDER_PAY_URL + "?version=" + URLUtil.SZF_VERSION
	            + "&merId=" + URLUtil.SZF_MERID
	            + "&payMoney=" + selectOrderPayForm.getAmount()
	            + "&orderId=" + selectOrderPayForm.getOrderno()
	            + "&returnUrl=" + URLUtil.SZF_RETURNURL
	            + "&cardInfo=" + URLEncoder.encode(cardInfo, "utf-8")
	            + "&merUserName=" + URLUtil.SZF_MERUSERNAME
	            + "&merUserMail=" + URLUtil.SZF_MERUSERMAIL
	            + "&privateField=" + URLUtil.SZF_PRIVATEFIELD
	            + "&verifyType=" + URLUtil.SZF_VERIFYTYPE
	            + "&cardTypeCombine=" + selectOrderPayForm.getCardtype()
	            + "&md5String=" + md5String
	            + "&signString=";
	    System.out.println("构造 url 请求数据：" + urlRequestData);
	    //服务器建立连接
	    HttpURLConnection httpConnection;
	    URL url = null;
	    int code = 0;
	    int szfResponseCode=0;
	    try {
	        url = new URL(urlRequestData);
	        httpConnection = (HttpURLConnection) url.openConnection();
	        httpConnection.setRequestMethod("GET");
//	        httpConnection.setRequestMethod("POST");
	        httpConnection.setDoOutput(true);
	        httpConnection.setDoInput(true);
	        code = httpConnection.getResponseCode();
	        System.out.println("连接神州付服务器：" + URLUtil.SZF_ORDER_PAY_URL + "，HTTP响应代码：" + code);
	        if (code == HttpURLConnection.HTTP_OK) {
	//	        String strCurrentLine = "";
		        BufferedReader reader;
		        reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "UTF-8"));
		        //当正确响应时处理数据
		        szfResponseCode = httpConnection.getHeaderFieldInt("szfResponseCode", 0);
		        System.out.println("连接神州付服务器：" + URLUtil.SZF_ORDER_PAY_URL + "，SZF响应代码：" + szfResponseCode);
		        //数据通过校验
	//	         if (szfResponseCode == 200) {
	//	         	System.out.println("<html>\n" +
	//	            "<head><title></title>\n" +
	//	            "</head>\n" +
	//	            "<body>\n" +
	//	            "支付处理中,请稍候...\n" +
	//	            "</body>\n" +
	//	            "</html>");
	//	        }
		        if(szfResponseCode == 200){
		        	szfResponseCode = 200;
		        }else{
		        	szfResponseCode=0;
		        }
	        }
	    } catch (IOException e) {
            System.out.println("连接神州付服务器：" + URLUtil.SZF_ORDER_PAY_URL + "异常，e=" + e);
        }catch (Exception e) {
	        System.out.println("连接神州付服务器：" + URLUtil.SZF_ORDER_PAY_URL + "异常，e=" + e);
	    }
	    return szfResponseCode;
	}
	
	
	public static void main(String[] args) {
		try {
			System.out.println(new SZFOrderPayUtil().sendOrderInfoToSZF(null));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
