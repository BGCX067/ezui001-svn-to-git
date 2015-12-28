package cn.vstore.appserver.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

public class DSAUtil {
	public static String sign(String s, String privkey){
		String r = null;
		PrivateKey myprikey = null;
		byte[] bs = Base64Coder.decode(privkey.toCharArray());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bs);
		try {
			myprikey = KeyFactory.getInstance("DSA").generatePrivate(spec);
			Signature signet = Signature.getInstance("DSA");
			signet.initSign(myprikey);
			byte[] b = s.getBytes();
			signet.update(b,0,b.length);
			byte[] signed = signet.sign(); 
			r  =new String(Base64Coder.encode(signed));
		} catch (Throwable e) {
		}
		return r;
	}
	
	public String splits(){
	    	long times = System.currentTimeMillis();
	    	String strTime = String.valueOf(times);
			return strTime.substring(strTime.length()-8);
	}

}
