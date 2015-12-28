package cn.vstore.appserver.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sti.security.util.Base64Coder;


public class LicenseFactory {
	private static final Logger log = LoggerFactory.getLogger(LicenseFactory.class);
	private static final String PRIVATE_KEY = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUFkegJy8hJP35m7XPOGwD9PlRudg=";
	public static class SignedLicense{
		private String pkgId;
		private String data;
		private String sign;
		private BigDecimal id;
		
		private License license;
		
		public BigDecimal getId() {
			return id;
		}
		public void setId(BigDecimal id) {
			this.id = id;
		}
		/**
		 * 取得
		 * @return the pkgId 
		 */
		public String getPkgId() {
			return pkgId;
		}
		/**
		 * 設定
		 * @param pkgId 
		 */
		public void setPkgId(String pkgId) {
			this.pkgId = pkgId;
		}
		/**
		 * 取得
		 * @return the data 
		 */
		public String getData() {
			return license.toXml();
		}
//		public String getData() {
//			return data;
//		}
		/**
		 * 設定
		 * @param data 
		 */
		public void setData(String data) {
			this.data = data;
		}
		/**
		 * 取得
		 * @return the sign 
		 */
		public String getSign() {
			return sign;
		}
		/**
		 * 設定
		 * @param sign 
		 */
		public void setSign(String sign) {
			this.sign = sign;
		}
//		/**
//		 * 取得
//		 * @return the licenseType 
//		 */
//		public int getLicenseType() {
//			return licenseType;
//		}
//		/**
//		 * 設定
//		 * @param licenseType 
//		 */
//		public void setLicenseType(int licenseType) {
//			this.licenseType = licenseType;
//		}
//		/**
//		 * 取得
//		 * @return the durationEnd 
//		 */
//		public Date getDurationEnd() {
//			return durationEnd;
//		}
//		/**
//		 * 設定
//		 * @param durationEnd 
//		 */
//		public void setDurationEnd(Date durationEnd) {
//			this.durationEnd = durationEnd;
//		}
//		/**
//		 * 取得
//		 * @return the durationStart 
//		 */
//		public Date getDurationStart() {
//			return durationStart;
//		}
//		/**
//		 * 設定
//		 * @param durationStart 
//		 */
//		public void setDurationStart(Date durationStart) {
//			this.durationStart = durationStart;
//		}
//		public int getVersion() {
//			return version;
//		}
//		public void setVersion(int version) {
//			this.version = version;
//		}
//		public String getStore() {
//			return store;
//		}
//		public void setStore(String store) {
//			this.store = store;
//		}
//		public String getImei() {
//			return imei;
//		}
//		public void setImei(String imei) {
//			this.imei = imei;
//		}
		public void setLicense(License license) {
			this.license = license;
		}
		
	}
	/**
	 * 
	 * TODO : Write down business purpose
	 * Purpose:
	 * @author xiandong
	 * @param lis
	 * @return
	 */
	public static SignedLicense genLicense(final License lis){
		SignedLicense r=null;
		if(lis==null) return r;
		if(lis.getAppPackageId()==null) return r;
		try{
			byte[] b=lis.toXml().getBytes();
			
			PrivateKey myprikey=null;
			byte[] bs = Base64Coder.decode(PRIVATE_KEY.toCharArray());
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bs);
			try {
				myprikey = KeyFactory.getInstance("DSA").generatePrivate(spec);
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
				return r;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return r;
			}
			if(myprikey==null) return r;
	
			Signature signet=Signature.getInstance("DSA");
			signet.initSign(myprikey);
			signet.update(b,0,b.length);
			byte[] signed=signet.sign(); //
	
			r=new SignedLicense();
			r.setData(new String(Base64Coder.encode(b)));
			r.setSign(new String(Base64Coder.encode(signed)));
			r.setPkgId(lis.getAppPackageId());
//			r.setLicenseType(lis.getLicenseType());
//			r.setVersion(lis.getVersion());
//			r.setDurationEnd(lis.getDurationEnd());
//			r.setDurationStart(lis.getDurationStart());
//			r.setStore(lis.getStore());
//			r.setImei(lis.getIMEI());
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return r;
	}
	public static License parseLicense(byte[] data) {

		try {
			InputStream byteArrayInputStream = new ByteArrayInputStream(data);
			SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser parser = factory.newSAXParser();
			LicneseHandler handler = new LicneseHandler();
			parser.parse(byteArrayInputStream, handler);
			License license = handler.getLicenses().get(0);

			return license;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
}
