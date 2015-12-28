package cn.vstore.appserver.api.payment.unionpay;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/*
 * relevancy MerchantTestActivity's Submit method
 * author: guoqiang
 */

public class XmlHttpConnection implements Serializable {
	private static final long serialVersionUID = -8576553457942740493L;
	private final int RESPCODE_SUCCESS = 200;
	private String URL;
	private String recvMsg;
	private HttpURLConnection urlCon;
	private int errCode;
	private String errMsg;
	InputStream in;
	public XmlHttpConnection(String url, int timeOut) {
		this(url, timeOut + "");
	}

	public XmlHttpConnection(String url, String timeOut) {
		this.URL = url;
		System.setProperty("sun.net.client.defaultConnectTimeout", timeOut);
		System.setProperty("sun.net.client.defaultReadTimeout", timeOut);
	}

	private boolean open() {
		try {
			//敦阳测试用proxy，可联外网时务必移除
//			Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("172.20.1.68", 8080));
//			urlCon = (HttpURLConnection) new URL(URL).openConnection(proxy);
			urlCon = (HttpURLConnection) new URL(URL).openConnection();
			return true;
		}catch (Exception e) {
			errCode = -11;
			errMsg = "";
		}
		return false;
	}
	/**
	 * 
	 * @param msgStr
	 *            msg String
	 * @param msgEncoding
	 *            sending encoding
	 * @return boolean
	 */
	public boolean sendMsg(String msgStr) {
		if (!open())
			return false;

		OutputStream os =null;
		InputStream is=null;
		try {
			try{
				urlCon.setRequestMethod("POST");
				urlCon.setRequestProperty("content-type", "text/plain");
				urlCon.setDoOutput(true);
				urlCon.setDoInput(true);
				os =urlCon.getOutputStream();
//				os.write(msgStr.getBytes("utf-8"));
//				os.flush();
				OutputStreamWriter writer = new OutputStreamWriter(os);
				writer.write(URLEncoder.encode(msgStr, "utf-8"));
				writer.flush();
			}catch (Exception e) {
				e.printStackTrace();
				errCode = -21;
				errMsg = "";
				return false;
			}
			try {
				int respCode = urlCon.getResponseCode();
				if (RESPCODE_SUCCESS != respCode) {
					errCode = -31;
					errMsg="httpState=[" + respCode + "]";	
				} 
				is=urlCon.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
				StringBuilder responseBuilder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line);
				}
				recvMsg=URLDecoder.decode(responseBuilder.toString(),"utf-8");
				
				in = StringToInputStream(recvMsg);
				
				
				

				
			} catch (Exception e) {
				errCode = -31;
				errMsg = "";
				return false;
			}  
		}
		finally {
			close(is);
			close(os);
			urlCon.disconnect();
			urlCon = null;
		}
		return true;
	}
	private void close(InputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (Exception e) {
		}
		stream=null;
	}

	private  void close(OutputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (Exception e) {
		}
		stream=null;
	}

	public int getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public InputStream getRecvMsg() {
		
		
		return in;
	}
	public String getReMeg() {
		
		
		return recvMsg;
	}
	InputStream StringToInputStream (String recvMsg){
		
		ByteArrayInputStream stream = new ByteArrayInputStream(recvMsg.getBytes());
		
		
		return stream;
	}
	
}
