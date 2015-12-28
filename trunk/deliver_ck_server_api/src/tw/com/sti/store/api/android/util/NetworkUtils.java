package tw.com.sti.store.api.android.util;

import java.io.BufferedReader;
import java.io.FileReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {

	/**
	 * 檢查網路是否有開啟
	 */
	public static boolean isNetworkOpen(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 取得 Mac-Address 
	 * 1. 先透過 SIS Lib 取得 Ethernet Mac-Address
	 * 2. 無法取得時再透過 SIS Lib 取得 Wifi Mac-Address
	 * 3. 無法使用 SIS Lib 時
	 * 4. 先取得 Ethernet Mac-Address 
	 * 5. 再取得 Wifi Mac-Address
	 * 
	 * @param activity
	 * @return
	 */
	public static String getDeviceMacAddress(Context context) {
		String macAddress = null;
//		try {
//			macAddress = getEthernetMacAddressFromSis();
//			if(macAddress == null) {
//				macAddress = getWifiMacAddressFromSis();
//			}
//		} catch(Throwable t) {
//			macAddress = null;
//		}
		
		if(macAddress == null || "".equals(macAddress)) {
			macAddress = getEthernetMacAddress();
			if(macAddress == null) {
				macAddress = getWifiMacAddress(context);
			}
			if(macAddress == null || "".equals(macAddress)) {
				macAddress = "0";
			}
		}
		return macAddress;
	}

	/**
	 * 取得 Ethernet Mac-Address
	 * @return
	 */
	private static String getEthernetMacAddress() {
		String macAddress = null;
		try {
			macAddress = loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return macAddress;
	}
	
//	/**
//	 * 從 SIS Lib 取得 Ethernet Mac-Address
//	 * @return
//	 */
//	private static String getEthernetMacAddressFromSis() {
//		return android.net.NetworkUtils.getHwAddress("eth0");
//	}
	
	/**
	 * 取得 Wifi Mac-Address
	 * @param activity
	 * @return
	 */
	private static String getWifiMacAddress(Context context) {
		String macAddress = null;
		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if (wifiInfo != null) {
				macAddress = wifiInfo.getMacAddress();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return macAddress;
	}
	
//	/**
//	 * 從 SIS Lib 取得 Wifi Mac-Address
//	 * @return
//	 */
//	private static String getWifiMacAddressFromSis() {
//		return android.net.NetworkUtils.getHwAddress("wlan0");
//	}
	
	/**
	 * 讀取檔案
	 * 
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String loadFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}
}
