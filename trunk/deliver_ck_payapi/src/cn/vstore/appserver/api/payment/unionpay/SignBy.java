package cn.vstore.appserver.api.payment.unionpay;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;





public class SignBy {	
	public static String alias_test = "889ce7a52067a87f905c91f502c69644_d1cba47d-cbb1-4e29-9d77-8d1fe1b0dccd";
	public static String pwd_test = "898000000000002";
	public static String dataSign_test = "898000000000002.p12";
	
	public static String alias_online = "87e0c0c2e8a8f5c083d17d572506a35e_d1cba47d-cbb1-4e29-9d77-8d1fe1b0dccd";
	public static String pwd_online = "123456";
	public static String dataSign_online = "hanxin.p12";
    /**
	 * 生成签名
	 */
	public String createSign(String  original_string,String alias,String password,InputStream PrivateSign){

		try {
			
			byte [] signsMD5 = MD5(original_string);
			
			byte [] signsRSA = rsaEncode(signsMD5,alias,password,PrivateSign);
			
		//	byte [] signsRSA = rsaEncode(signsMD5,true);
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

	/**
	 * 
	 */
	private byte[] rsaEncode(byte[] msg, boolean isTest) {
		String alias, pwd, dataSign;
		if(isTest) {
			alias = alias_test;
			pwd = pwd_test;
			dataSign = dataSign_test;
		} else {
			alias = alias_online;
			pwd = pwd_online;
			dataSign = dataSign_online;
		}
		try {
			// 以PKCS2类型打开密钥库
			KeyStore store = KeyStore.getInstance("PKCS12");
			InputStream inStream = getClass().getClassLoader().getResourceAsStream(dataSign);
			store.load(inStream, pwd.toCharArray());
			inStream.close();
			// 从密钥库中提取密钥
			PrivateKey pKey = (PrivateKey) store.getKey(alias, pwd.toCharArray());
			// byte[] d = pKey.getEncoded();
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pKey);   
			return cipher.doFinal(msg);    
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 数据签名
	 * @param msg
	 * @throws Exception
	 */
	private byte[] rsa(byte[] msg, boolean isTest) throws Exception {
		String alias, pwd, dataSign;
		if(isTest) {
			alias = alias_test;
			pwd = pwd_test;
			dataSign = dataSign_test;
		} else {
			alias = alias_online;
			pwd = pwd_online;
			dataSign = dataSign_online;
		}
		// 以PKCS2类型打开密钥库
		KeyStore store = KeyStore.getInstance("PKCS12");
//		String rootPath = CoreProcess.getClass().getClassLoader().getResourceAsStream(dataSign);
		InputStream inStream = getClass().getClassLoader().getResourceAsStream(dataSign);
		store.load(inStream, pwd.toCharArray());
		inStream.close();
		// 从密钥库中提取密钥
		PrivateKey pKey = (PrivateKey) store.getKey(alias, pwd.toCharArray());
		byte[] d = pKey.getEncoded();
		// 取出数字证书对应的签名算法，初始化验证器
		X509Certificate cert = (X509Certificate) store.getCertificate(alias);
		String sigAlg = cert.getSigAlgName();
//		Signature signAlg = Signature.getInstance(sigAlg);
		Signature signAlg = Signature.getInstance("MD5withRSA");//先写死得
		signAlg.initSign(pKey);
		
		// 对message进行数字签名的到result
		signAlg.update(msg); 
		return signAlg.sign();
	}
}
