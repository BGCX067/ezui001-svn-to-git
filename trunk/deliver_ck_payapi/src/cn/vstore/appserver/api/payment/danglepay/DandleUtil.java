package cn.vstore.appserver.api.payment.danglepay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import cn.vstore.appserver.form.payment.SelectGamePayForm;

public class DandleUtil {
	
	public static final String CHART_CODE = "UTF-8";
	public static final String MERCHANT_ID = "382";
	public static final String GAME_ID = "1";
	public static final String SERVER_ID = "1";
	
	public static final String MERCHANT_KEY = "sr8fnmXt";
	public static final String DES_KEY = "c3I4Zm5tWHQ=";
	
	
	/**
	 * 获取加密字符串
	 * @param selectGamePayForm
	 * @return
	 */
	public static String getMD5Strs(SelectGamePayForm selectGamePayForm){
		String postData = null;
		StringBuilder sb=new StringBuilder();
		String cardNo = selectGamePayForm.getCardNo();
		String cardPwd = selectGamePayForm.getCardPwd();
//		String md5No = null;
//		String md5Pwd = null;
		try {
			cardNo = DES.encode(cardNo, DES_KEY).replaceAll("\r\n", ""); // 对卡号进行 DES 加密
			cardPwd = DES.encode(cardPwd, DES_KEY).replaceAll("\r\n", ""); // 对密码进行 DES 加密
			// 拼接MD5加密数据
	        sb.append("mid=").append(MERCHANT_ID);
	        sb.append("&gid=").append(GAME_ID);
	        sb.append("&sid=").append(SERVER_ID);
	        sb.append("&uif=").append(selectGamePayForm.getUserID());
	        sb.append("&utp=").append(selectGamePayForm.getPayUserId());
	        sb.append("&uip=").append(selectGamePayForm.getUserIpAddr());
	        sb.append("&eif=").append(selectGamePayForm.getMerPriv());
	        sb.append("&cardno=").append(cardNo);// 在MD5时是不对卡号和密码进行UTF-8编码的，
	        sb.append("&cardpwd=").append(cardPwd);
	        sb.append("&amount=").append(selectGamePayForm.getAmount());
	        sb.append("&timestamp=").append(selectGamePayForm.getOrderTime());
	        sb.append("&merchantkey=").append(MERCHANT_KEY);
	        
	        System.out.println("需要加密的拼接字符串===="+sb.toString());
	        String verstr = MD5.MD5Encode(sb.toString()).toLowerCase();
	        System.out.println("拼接字符串加密后为===="+verstr);
	        
	        // 拼提交订单数据
	        sb=new StringBuilder();
	        sb.append("mid=").append(MERCHANT_ID);
	        sb.append("&gid=").append(GAME_ID);
	        sb.append("&sid=").append(SERVER_ID);
	        sb.append("&uif=").append(URLEncoder.encode(selectGamePayForm.getUserID(), CHART_CODE));
	        sb.append("&utp=").append(selectGamePayForm.getPayUserId());
	        sb.append("&uip=").append(selectGamePayForm.getUserIpAddr());
	        sb.append("&eif=").append(selectGamePayForm.getMerPriv());
			sb.append("&cardno=").append(URLEncoder.encode(cardNo, CHART_CODE));
	        sb.append("&cardpwd=").append(URLEncoder.encode(cardPwd, CHART_CODE));
	        sb.append("&amount=").append(selectGamePayForm.getAmount());
	        sb.append("&timestamp=").append(selectGamePayForm.getOrderTime());
	        sb.append("&verstring=").append(verstr);
	        postData = sb.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postData;
	}
	
	/**
	 * 根据当乐支付文档组装参数规则
	 * @param selectGamePayForm
	 * @return
	 */
	public static String getMD5Str(SelectGamePayForm selectGamePayForm){
		String postData = null;
		StringBuilder sb = new StringBuilder();
		try {
			String cardNo = selectGamePayForm.getCardNo();
			String cardPwd = selectGamePayForm.getCardPwd();
			cardNo = DES.encode(cardNo, DES_KEY).replaceAll("\r\n", ""); // 对卡号进行 DES 加密
			cardPwd = DES.encode(cardPwd, DES_KEY).replaceAll("\r\n", ""); // 对密码进行 DES 加密
			// 拼接MD5加密数据
	        sb.append("mid=").append("382");
	        sb.append("&gid=").append("1");
	        sb.append("&sid=").append("1");
	        sb.append("&uif=").append(selectGamePayForm.getUserID());
	        sb.append("&utp=").append(selectGamePayForm.getPayUserId());
	        sb.append("&uip=").append(selectGamePayForm.getUserIpAddr());
	        sb.append("&eif=").append(selectGamePayForm.getMerPriv());
	        sb.append("&cardno=").append(cardNo);// 在MD5时是不对卡号和密码进行UTF-8编码的，
	        sb.append("&cardpwd=").append(cardPwd);
	        sb.append("&amount=").append(selectGamePayForm.getAmount());
	        sb.append("&timestamp=").append(selectGamePayForm.getOrderTime());
	        sb.append("&merchantkey=").append(MERCHANT_KEY);
	        
	        System.out.println("需要加密的拼接字符串===="+sb.toString());
	        String verstr = MD5.MD5Encode(sb.toString()).toLowerCase();
	        System.out.println("拼接字符串加密后为===="+verstr);
	        
	        // 拼提交订单数据
	        sb=new StringBuilder();
	        sb.append("mid=").append("382");
	        sb.append("&gid=").append("1");
	        sb.append("&sid=").append("1");
	        sb.append("&uif=").append(URLEncoder.encode(selectGamePayForm.getUserID(), CHART_CODE));
	        sb.append("&utp=").append(selectGamePayForm.getPayUserId());
	        sb.append("&uip=").append(selectGamePayForm.getUserIpAddr());
	        sb.append("&eif=").append(selectGamePayForm.getMerPriv());
			sb.append("&cardno=").append(URLEncoder.encode(cardNo, CHART_CODE));
	        sb.append("&cardpwd=").append(URLEncoder.encode(cardPwd, CHART_CODE));
	        sb.append("&amount=").append(selectGamePayForm.getAmount());
	        sb.append("&timestamp=").append(selectGamePayForm.getOrderTime());
	        sb.append("&verstring=").append(verstr);
	        postData = sb.toString();
	        System.out.println("需要提交的订单数据==="+postData);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postData;
	}
	
	
	
	/**
	 * 获取并解析当乐返回的订单信息
	 * @param res
	 * @return
	 */
	public static Map<String,String> retOrderInfo(String res){
		String []tmp=res.trim().split("&");
        Map<String,String> resMap = new HashMap<String,String>();
        for(String t:tmp){
            //System.out.println(t);
            String [] tt =t.split("=");
            if(tt.length==2){
                resMap.put(tt[0], tt[1]);
            }
        }
        System.out.println("提交当乐订单返回字符串解析后的result为==="+resMap.get("result")+"====非1即为失败"); // 当result=1时提交订单成功，否则是失败的
//        if("1".equals(resMap.get("result"))){
//            System.out.println("提交订单状态===="+resMap.get("msg")); // 提交订单状态说明
//        }
        return resMap; 
	}
	
	/**
	 * post请求
	 * @param urlStr
	 * @param postData
	 * @param charSet
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String urlStr, String postData, String charSet) throws Exception {
        PrintWriter writer=null;
        HttpURLConnection httpConn=null;
        String res=null;
        try {
            byte[] data=postData.getBytes(charSet);
            URL url=new URL(urlStr);
            httpConn=(HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Content-Length", String.valueOf(data.length));
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//jdk1.4换成这个,连接超时
            // System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //jdk1.4换成这个,读操作超时
            httpConn.setConnectTimeout(30000);// jdk 1.5换成这个,连接超时
            httpConn.setReadTimeout(30000);// jdk 1.5换成这个,读操作超时
            httpConn.connect();
            OutputStream os=httpConn.getOutputStream();
            os.write(data);
            // 获得响应状态
            int responseCode=httpConn.getResponseCode();
            if(HttpURLConnection.HTTP_OK == responseCode) {
                byte[] buffer=new byte[1024];
                int len=-1;
                InputStream is=httpConn.getInputStream();
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                while((len=is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                res=bos.toString(charSet);
                is.close();
            } else {
                System.err.println(urlStr + " Response Code:" + responseCode);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            System.err.println(urlStr);
        } finally {
            if(null != writer) {
                writer.close();
            }
            if(null != httpConn) {
                httpConn.disconnect();
            }
        }
        return res;
    }
	
	public static void main(String[] args) throws Exception {
		System.out.println(DES.encode("12345", DandleUtil.DES_KEY));
		System.out.println(DES.decode("EIw+YC6jb3k=", DandleUtil.DES_KEY));
	}
}
