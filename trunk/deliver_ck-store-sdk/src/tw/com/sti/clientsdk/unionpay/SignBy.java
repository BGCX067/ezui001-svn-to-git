package tw.com.sti.clientsdk.unionpay;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;





public class SignBy {	

    /**
	 * 生成签名
	 */
	public String createSign(String  original_string,String alias,String password,InputStream PrivateSign){

		try {
			
			byte [] signsMD5 = MD5(original_string);
			
			byte [] signsRSA = rsaEncode(signsMD5,alias,password,PrivateSign);
			
			
			return new String(Base64.encode(signsRSA));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
    
	
	/**
	 * 
	 * @param md5
	 * @return
	 */
	private  byte[] MD5(String src) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(src.getBytes("utf-8"));
			byte[] digest = messageDigest.digest();
			return digest;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}
	/**
	 * BASE64编码
	 * @param bstr
	 * @return
	 */
//	private String BASE64encode(byte[] bstr){
//		String s = new sun.misc.BASE64Encoder().encode(bstr).replaceAll("\r\n", "");
//		s = s.replaceAll("\n", "");
//		return s;
//	}

	/**
	 * 
	 * @param signsRSA
	 * @param isTest
	 * @return
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	private byte[] rsaEncode(byte[] signsRSA,String alias,String pwd,InputStream dataSign) {
		
		
		// 以PKCS2类型打开密钥库
		try {

			KeyStore store = KeyStore.getInstance("PKCS12");
			InputStream inStream = dataSign;
			store.load(inStream, pwd.toCharArray());			
			inStream.close();
			// 从密钥库中提取密钥
			PrivateKey pKey = (PrivateKey)store.getKey(alias, pwd.toCharArray());
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pKey);
			
			
			return cipher.doFinal(signsRSA);
			
			
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		
		return null;
	}


}
