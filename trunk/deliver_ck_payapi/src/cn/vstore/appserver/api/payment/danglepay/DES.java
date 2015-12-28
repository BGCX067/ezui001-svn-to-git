package cn.vstore.appserver.api.payment.danglepay;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * DES加密的，文件中共有两个方法,加密、解密
 */
public class DES {
    private String Algorithm="DES";

    private KeyGenerator keygen;

    private SecretKey deskey;

    private Cipher c;

    private byte[] cipherByte;

    /**
     * 初始化 DES 实例
     */
    public DES() {
        init();
    }

    public void init() {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
            keygen=KeyGenerator.getInstance(Algorithm);
            deskey=keygen.generateKey();
            c=Cipher.getInstance(Algorithm);
        } catch(NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch(NoSuchPaddingException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 对 String 进行加密
     * @param str 要加密的数据
     * @return 返回加密后的 byte 数组
     */
    public byte[] createEncryptor(String str) {
        try {
            c.init(Cipher.ENCRYPT_MODE, deskey);
            cipherByte=c.doFinal(str.getBytes());
        } catch(java.security.InvalidKeyException ex) {
            ex.printStackTrace();
        } catch(javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch(javax.crypto.IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
        return cipherByte;
    }

    /**
     * 对 Byte 数组进行解密
     * @param buff 要解密的数据
     * @return 返回加密后的 String
     */
    public String createDecryptor(byte[] buff) {
        try {
            c.init(Cipher.DECRYPT_MODE, deskey);
            cipherByte=c.doFinal(buff);
        } catch(java.security.InvalidKeyException ex) {
            ex.printStackTrace();
        } catch(javax.crypto.BadPaddingException ex) {
            ex.printStackTrace();
        } catch(javax.crypto.IllegalBlockSizeException ex) {
            ex.printStackTrace();
        }
        return(new String(cipherByte));
    }

    /**
     * 已知密钥的情况下加密
     */
    public static String encode(String str, String key) throws Exception {

//        SecureRandom sr=new SecureRandom();
        byte[] rawKey=(new sun.misc.BASE64Decoder()).decodeBuffer(key); //Base64.decode(key);
        IvParameterSpec sr=new IvParameterSpec(rawKey);
        DESKeySpec dks=new DESKeySpec(rawKey);
        SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("DES");
        SecretKey secretKey=keyFactory.generateSecret(dks);

        javax.crypto.Cipher cipher=Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

        byte data[]=str.getBytes("UTF8");
        byte encryptedData[]=cipher.doFinal(data);
        return(new sun.misc.BASE64Encoder()).encodeBuffer(encryptedData);

    }

    /**
     * 已知密钥的情况下解密
     */
    public static String decode(String str, String key) throws Exception {
//        SecureRandom sr=new SecureRandom();
        byte[] rawKey=(new sun.misc.BASE64Decoder()).decodeBuffer(key); //Base64.decode(key);
        IvParameterSpec sr=new IvParameterSpec(rawKey);
        DESKeySpec dks=new DESKeySpec(rawKey);
        SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("DES");
        SecretKey secretKey=keyFactory.generateSecret(dks);
        Cipher cipher=Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        byte encryptedData[]=(new sun.misc.BASE64Decoder()).decodeBuffer(str); //Base64.decode(str);
        byte decryptedData[]=cipher.doFinal(encryptedData);
        return new String(decryptedData, "UTF8");
    }

    public static void main(String[] args) {
        DES des=new DES();
        System.out.println("Test1...");
        try {
            String en=des.encode("100@07773194573437965@323548109530138171", "x7VhhchMp0Y=");
            System.out.print(en);
            String de=des.decode(en, "x7VhhchMp0Y=");
            System.out.println(de);
        } catch(Exception e) {
            System.out.println("Test1 Error!");
        }
    }
}

