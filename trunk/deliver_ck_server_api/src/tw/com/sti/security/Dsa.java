package tw.com.sti.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import tw.com.sti.security.util.Base64Coder;

public class Dsa {
	public static void makeDSAKey() throws Exception{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
	    kpg.initialize(1024, new SecureRandom());
	    KeyPair dsaKeyPair = kpg.generateKeyPair();
	    String privatekey=new String(Base64Coder.encode(dsaKeyPair.getPrivate().getEncoded()));
	    System.out.println("private key="+privatekey);
	    String pubkey=new String(Base64Coder.encode(dsaKeyPair.getPublic().getEncoded()));
	    System.out.println("public key="+pubkey);
	}
	public static String sign(String s,String privkey){
		String r=null;
		PrivateKey myprikey=null;
		byte[] bs = Base64Coder.decode(privkey.toCharArray());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bs);
		try {
			myprikey = KeyFactory.getInstance("DSA").generatePrivate(spec);
			Signature signet=Signature.getInstance("DSA");
			signet.initSign(myprikey);
			byte[] b=s.getBytes();
			signet.update(b,0,b.length);
			byte[] signed=signet.sign(); 
			r=new String(Base64Coder.encode(signed));
		} catch (Throwable e) {
		}
		return r;
	}
	public static boolean verify(String s,String signature,String pubkey){
		boolean r=false;
		PublicKey publickey=null;
		byte[] bs = Base64Coder.decode(pubkey.toCharArray());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(bs);
		try {		
            publickey = KeyFactory.getInstance("DSA").generatePublic(spec);
			Signature signet=Signature.getInstance("DSA");
			signet.initVerify(publickey);
			signet.update(s.getBytes());
			r=signet.verify(Base64Coder.decode(signature));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return r;
	}
	public static void main(String[] args){
		try {
			makeDSAKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(sign("fasfdasf","MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUO7t/zgv3VYaek0FS1wZptHS8AJY="));
		System.out.println(verify("fasfdasf","MCwCFFs4mJgbzmgR+OrV3NyBiryFb/ltAhRGKu3QNM/Xtwa1uUKYqHplWXXfuQ==","MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAGET4xGi0Ln2XxH019fpoN3onL3VgHnSXJKWVPbnJXgAetTIsEooYStKN1/NcEM3skD+LgAVyzFbaZWGKX8Wl6asiJZiP+1DC927Foq6TDMOKGxnB7FjYFSaTuOCr9UlBhGkq/QYto1XPB16UNtIKr+LF0m5cEYKKyUmdl+5B2f4="));
	}
}
