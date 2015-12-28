package cn.com.vapk.vstore.client.installapp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import tw.com.sti.security.util.Base64Coder;

import tw.com.sti.clientsdk.LicenseTool;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;

//import cn.com.vapk.vstore.client.contentprovider.SdkAppTerminatNotificationProvider;
import cn.com.vapk.vstore.client.installapp.InstallLog.EventTypeValue;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInstallLogReceiver extends BroadcastReceiver {

	private final Logger L = Logger.getLogger(AppInstallLogReceiver.class);
	private final static HashMap<String, String> INSTALL_PKG_NAMES_AND_FILE_PATH = new HashMap<String, String>();
	public final static Map<String, Integer> UNINSTALL_REASON_ID = new HashMap<String, Integer>();

	@Override
	public void onReceive(Context context, Intent intent) {

		// 判別是安裝或移除
		InstallLog.EventTypeValue eventType = null;
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
			eventType = EventTypeValue.INSTALL_SUCCESS;
		} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
			if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false))
				return;
			eventType = EventTypeValue.UNINSTALL_SUCCESS;
		} else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
			eventType = EventTypeValue.INSTALL_SUCCESS;
		} else {
			return;
		}
		if (Logger.DEBUG)
			L.d("action: " + action);

		String pkgName = intent.getData().toString().replace("package:", "");
		if (Logger.DEBUG)
			L.d("pkgName: " + pkgName);
		String version = "";
		// 反安裝是抓手機端最後一筆安裝成功的 download ID
		String downloadId = DownloadStatus.getDownloadId(context, pkgName);
		if (Logger.DEBUG)
			L.d("downloadId: " + downloadId);
		int resonId = 0;

		switch (eventType) {
		case INSTALL_SUCCESS:
			if (!INSTALL_PKG_NAMES_AND_FILE_PATH.containsKey(pkgName)) {
				if (Logger.DEBUG)
					L.d("INSTALL_PKG_NAMES_AND_FILE_PATH not contain for pkg: "
							+ downloadId);
				return;
			}
			try {
				PackageInfo pi = context.getPackageManager().getPackageInfo(
						pkgName, PackageManager.GET_ACTIVITIES);
				version = "" + pi.versionCode;
				removeInstallApk(pkgName);
				// 安裝成功是抓從 Server 端傳來的download ID
			} catch (Exception e) {
				if (Logger.ERROR)
					L.e("INSTALL_SUCCESS", e);
			}
			break;
		case UNINSTALL_SUCCESS:
//			SdkAppTerminatNotificationProvider.cancelStopNotiAgain(context,
//					pkgName);
			LicenseTool.deleteAllLicense(context, pkgName);
			if (UNINSTALL_REASON_ID.get(pkgName) != null) {
				resonId = (Integer) UNINSTALL_REASON_ID.get(pkgName);
				UNINSTALL_REASON_ID.remove(pkgName);
				if (Logger.DEBUG)
					L.d("resonId: " + resonId);
			}
			version = "0";
			break;
		}

		String versionCheck = Base64Coder.encodeString(version);
		if (LangUtils.isBlank(downloadId))
			downloadId = "0";
		if (LangUtils.isBlank(version))
			version = "0";

		ContentValues values = InstallLog.createContentValue(context, pkgName,
				version, eventType, downloadId, 0, resonId);
		values.put(InstallLog.COLUMN_VERSION_CHECK, versionCheck);
		if (Logger.DEBUG)
			L.d("insert Install Log, pkgName: " + pkgName + ", version: "
					+ version + ", downloadId: " + downloadId + ", eventType: "
					+ eventType + ", failCode: " + 0 + ", resonId: " + resonId
					+ ", versionCheck: " + versionCheck);
		context.getContentResolver().insert(InstallLog.CONTENT_URI, values);
		ActionController.sendInstallLog(context);
	}

	static void putInstallApk(String pkgName, File apk) {
		if (pkgName == null)
			return;
		INSTALL_PKG_NAMES_AND_FILE_PATH.put(pkgName,
				apk != null ? apk.getAbsolutePath() : "");
	}

	private void removeInstallApk(String pkgName) {
		String filePath = INSTALL_PKG_NAMES_AND_FILE_PATH.get(pkgName);
		if (!LangUtils.isBlank(filePath)) {
			try {
				File file = new File(filePath);
				if (file.exists()) {
					if (Logger.DEBUG)
						L.d("delete Install Apk: " + filePath);
					file.delete();
				}
			} catch (Exception e) {
				if (Logger.ERROR)
					L.e("removeInstallApk", e);
			}
		}
		INSTALL_PKG_NAMES_AND_FILE_PATH.remove(pkgName);
	}

}
