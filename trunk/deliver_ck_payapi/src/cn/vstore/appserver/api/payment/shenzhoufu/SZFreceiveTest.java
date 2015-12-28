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

public class SZFreceiveTest {
	
	public void getPayInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{

	    String version = request.getParameter("version");            //获取神州付消费接口的版本号
	    String merId = request.getParameter("merId");        //获取商户ID
	    String payMoney = request.getParameter("payMoney");    //获取消费金额
	    String orderId = request.getParameter("orderId");        //获取商户订单号
	    String payResult = request.getParameter("payResult");    //获取交易结果,1 成功 0 失败
	    String privateField = request.getParameter("privateField");        //获取商户私有数据
	    String payDetails = request.getParameter("payDetails"); //获取消费详情
	    String returnMd5String = request.getParameter("md5String");        //获取MD5加密串
	    String signString = request.getParameter("signString");            //神州付证书签名
	    String cardMoney = request.getParameter("cardMoney");            //卡面额
	    String privateKey = "123456";

	    System.out.println("version=" + version + "<br/>");
	    System.out.println("merId=" + merId + "<br/>");
	    System.out.println("payMoney=" + payMoney + "<br/>");
	    System.out.println("orderId=" + orderId + "<br/>");
	    System.out.println("payResult=" + payResult + "<br/>");
	    System.out.println("privateField=" + privateField + "<br/>");
	    System.out.println("returnMd5String=" + returnMd5String + "<br/>");
	    System.out.println("signString=" + signString + "<br/>");
	    System.out.println("cardMoney=" + cardMoney + "<br/>");

	    ///生成加密串,注意顺序  下面的if else判断如果采用返回模式1请用不加竖线的，如果是返回模式2请用加竖线的
	    String combineString;
	    if (cardMoney != null) {
	        combineString = version + "|" + merId + "|" + payMoney + "|" + cardMoney + "|" + orderId + "|" + payResult + "|" + privateField + "|" + payDetails + "|" + privateKey;
	    } else {
	        combineString = version + merId + payMoney + orderId + payResult + privateField + payDetails + privateKey;
	    }
	    //System.out.println("神州付网关返回数据：combineString=" + combineString);
//	    Md5 md5 = new Md5();
	    
	    String md5String = DigestUtils.md5Hex(combineString);
	    //（1）进行 MD5 校验
	    String result;
	    if (md5String.equals(returnMd5String)) {
	        //System.out.print("MD5验证成功！");
	        //用于服务器地址返回时,回复神州付消费平台:
	            if ("1".equals(payResult)) {
	                //消费成功
	                result = "消费成功";
	                System.out.print("消费成功......" + "<br/>");
	                //todo 商户处理网站业务逻辑代码.
	            } else {
	                //消费失败
	                result = "消费失败";
	                System.out.print("消费失败....." + "<br/>");
	                //todo 商户处理网站业务逻辑代码.
	                
	            }
	        } 
	        else {
	            result = "证书验签失败";
	            System.out.print("证书验签失败！" + "<br/>");
	        }
	    response.getWriter().write(result + ", orderId=" + orderId);
	}
}
