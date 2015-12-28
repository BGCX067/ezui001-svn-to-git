package cn.vstore.appserver.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	private static final Logger log = LoggerFactory.getLogger(Util.class);
	//Parser String to Integer
	public static int parseInt(String value){
		int intValue = 0;
		try{
			intValue = Integer.parseInt(value.trim());
		}catch(Exception e){}
		return intValue;
	}

	//Parser String to Double
	public static double parseDouble(String value){
		double doubleValue = 0.0;
		try{
			doubleValue = Double.parseDouble(value.trim());
		}catch(Exception e){}
		return doubleValue;
	}

	//MD5加密2次
	public static String encoderByMd5ByTwoTimes(String str) throws NoSuchAlgorithmException,UnsupportedEncodingException{
        MessageDigest md5=MessageDigest.getInstance("MD5");
        String newstr=new String(Base64.encodeBase64(md5.digest(md5.digest(str.getBytes("utf-8")))),"UTF-8");
        return newstr;
	}


	//取得隱藏的信用卡號
	public static String getHideCreditCard(String value){
		String firstCardNo = value.substring(0, 4);
		String endCardNo = value.substring(value.length()-4);
		return firstCardNo + "********" + endCardNo;
	}

	//MD5(取得 Token)
	public static String getMD5(String userId, String iccid, String imei, Logger log, Date now){
		String token = null;
		try{
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        digest.reset();
	        digest.update((userId+iccid+imei+now.getTime()).getBytes());
	        byte[] hash = digest.digest();
	        StringBuffer result = new StringBuffer(hash.length * 2);
	        for (int i = 0; i < hash.length; i++) {
	          if (((int) hash[i] & 0xff) < 0x10) {
	            result.append("0");
	          }
	          result.append(Long.toString((int) hash[i] & 0xff, 16));
	        }
	        token=result.toString();
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return token;
	}


//	//檢查是否符合網段內
//	public static boolean isBlankIp(String ip){
//		boolean ret=false;
//		String ips = Configuration.getInstance().getApiIpBlankList();
//		String[] ipArray=ips.split("\\,");
//		for(int i=0;i<ipArray.length;i++){
//			String wholeIp = NetUtil.getSubnet(ipArray[i]);
//			String[] wholeIpArray = wholeIp.split("/");
//			if(NetUtil.isInSubnet(ip, wholeIpArray[0], NetUtil.getMask(new Integer(wholeIpArray[1])))){
//				ret = true;
//			}
//		}
//		log.debug("is blank IP? "+ip+" in "+ips+" == "+ret);
//		return ret;
//	}

	/**
	 * 取得客戶端IP
	 * @param request : HttpServletRequest
	 * @return String : IP
	 */
	public static String getIpAddr(HttpServletRequest request) {
//       String ip = request.getHeader("x-forwarded-for");
//       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//           ip = request.getHeader("Proxy-Client-IP");
//       }
//       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//           ip = request.getHeader("WL-Proxy-Client-IP");
//       }
//       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
    	   String ip = request.getRemoteAddr();
//       }
       if(StringUtils.isNotBlank(ip))log.info("Current user IP : " + ip);
       return ip;
	}

	public static String getLanguageById(String langId){
		if (langId == null) {
			return "";
		}
		try{
			ResourceBundle resourceBundle = ResourceBundle.getBundle("applicationMessage",Locale.CHINESE);
			StringBuilder sb = new StringBuilder();
			String[] value = langId.replaceAll("\\s+", "").split(",");
			for (int i = 0; i < value.length; i++) {
				if(StringUtils.isNotBlank(value[i])){
					try{
						String valueString = resourceBundle.getString("lang." + value[i]);
						if(i > 0){
							sb.append(", ");
						}
						sb.append(valueString);
					}catch(Exception e){
						log.warn("supportLangs can not find the corresponding language : key is lang." + value[i]);
					}
				}
			}
			return sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return "";
		}
//				langId=langId.trim();
//				if(langId.equalsIgnoreCase("1")){
//					ret="中文";
//				}else if(langId.equalsIgnoreCase("2")){
//					ret="英文";
//				}else if(langId.equalsIgnoreCase("3")){
//					ret="中/英文";
//				}
	}

	//計算到期日
	public static Calendar operatorMaturity(Calendar beginCal,Calendar nowCal2){
		if(nowCal2.get(Calendar.YEAR)==beginCal.get(Calendar.YEAR)&&nowCal2.get(Calendar.MONTH)==beginCal.get(Calendar.MONTH)&&nowCal2.get(Calendar.DAY_OF_MONTH)==beginCal.get(Calendar.DAY_OF_MONTH)){
    		beginCal.add(Calendar.MONTH,1);
    	}else{
        	if(nowCal2.get(Calendar.DAY_OF_MONTH)>beginCal.get(Calendar.DAY_OF_MONTH)){
        		beginCal.add(Calendar.MONTH, (nowCal2.get(Calendar.YEAR)-beginCal.get(Calendar.YEAR))*12+(nowCal2.get(Calendar.MONTH)-beginCal.get(Calendar.MONTH))+1);
        	}else{
        		beginCal.add(Calendar.MONTH, (nowCal2.get(Calendar.YEAR)-beginCal.get(Calendar.YEAR))*12+(nowCal2.get(Calendar.MONTH)-beginCal.get(Calendar.MONTH)));
        	}
    	}
		return beginCal;
	}

}
