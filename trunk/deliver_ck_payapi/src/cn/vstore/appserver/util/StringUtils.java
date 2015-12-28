package cn.vstore.appserver.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils extends org.apache.commons.lang.StringUtils {

    public static final int isBlankAny(String... str) {
        for (int i = 0; i < str.length; i++) {
            if (isBlank(str[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public static String getDataStr(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        return timestamp;
    }
    
    public static String subStr(String respStr){
    	return respStr.substring(respStr.indexOf("<Response>"), respStr.length());
    }
    public static void main(String[] args) {
		System.out.println(getDataStr());
	}
}
