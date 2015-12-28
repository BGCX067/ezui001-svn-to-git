package cn.vstore.appserver.util;

import java.util.Random;
import java.util.UUID;

public class OrderNoUtil {

	public static String genRandomNum(int strlen) {
		int count = 0;
		char str[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < strlen) {
			int i = Math.abs(r.nextInt(10));
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	// 神州付订单号生成
	public static String getOrderNoInfo() {
		StringBuffer sBuffer = new StringBuffer();
		UUID uuid = UUID.randomUUID();
		String uuids = uuid.toString();
		String uuidss = uuids.substring(0,8);
		long times = System.currentTimeMillis();
		String strTime = String.valueOf(times);
		String strs = strTime.substring(strTime.length() - 3);
		String randStr = genRandomNum(3);
		sBuffer = sBuffer.append(uuidss).append(strs).append(randStr);
		return sBuffer.toString();
	}

	public static void main(String[] args) {
//		UUID uuid = UUID.randomUUID();
//		System.out.println(uuid);
//		System.out.println(System.currentTimeMillis());
//		String uuids = uuid.toString();
//		long times = System.currentTimeMillis();
//		String strTime = String.valueOf(times);
//		String strs = strTime.substring(strTime.length() - 6);
//		System.out.println(uuids.substring(0, 8) + strs);
//		
//		System.out.println(genRandomNum(3));
		System.out.println(getOrderNoInfo());
	}
}
