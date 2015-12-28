package cn.com.vapk.vstore.client.installapp;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

public class DownloadAppService extends Service {
	private final Logger L = Logger.getLogger(DownloadAppService.class);
	private final int THREAD_MAX_SIZE = 2;
	private DownloadAppNotifier downloadAppNotifier;
	private Map<DownloadAppInfo, DownloadAppThread> dlAppThreadMap;
	private static Map<String, DownloadAppInfo> daiMap;
	private DownloadAppObserver dlAppObserver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static boolean isInDownload(String pkg) {
		if (daiMap == null)
			return false;
		try {
			return daiMap.containsKey(pkg);
		} catch (Exception e) { // thread safe
			return false;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		downloadAppNotifier = new DownloadAppNotifier(this);
		dlAppThreadMap = Collections
				.synchronizedMap(new HashMap<DownloadAppInfo, DownloadAppThread>(
						THREAD_MAX_SIZE));
		daiMap = Collections
				.synchronizedMap(new HashMap<String, DownloadAppInfo>());
		dlAppObserver = new DownloadAppObserver() {
			@Override
			void updateInBackground(DownloadAppInfo dai) {
				updateDownloadAppInfo(dai);
			}
		};
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent == null) {
			download();
			return;
		}
		Bundle extras = intent.getExtras();
		String pkg = extras.getString(ActionController.EXTRA_PACKAGE_NAME);
		String categoryId = extras
				.getString(ActionController.EXTRA_CATEGORY_ID);
		String appTitle = extras.getString(ActionController.EXTRA_APP_TITLE);
		int version = extras.getInt(ActionController.EXTRA_APP_VERSION);
		if (LangUtils.isBlankAny(pkg, categoryId, appTitle) > -1 || version < 1) {
			return;
		}

		String vlogId = extras.getString(ActionController.EXTRA_V_LOG_ID);
		DownloadAppInfo dai = new DownloadAppInfo(pkg, categoryId, appTitle,
				version, vlogId);
		dai.observer = dlAppObserver;
		synchronized (daiMap) {
			if (daiMap.containsKey(dai.pkg))
				return;
			daiMap.put(dai.pkg, dai);
		}

		download();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		daiMap = null;
	}

	private void download() {
		if (dlAppThreadMap.size() >= THREAD_MAX_SIZE)
			return;

		DownloadAppThread dlAppThread = null;
		synchronized (daiMap) {
			if (!daiMap.isEmpty()) {
				Collection<DownloadAppInfo> values = daiMap.values();
				DownloadAppInfo[] dais = values
						.toArray(new DownloadAppInfo[values.size()]);
				for (DownloadAppInfo dai : dais) {
					if (DownloadAppInfo.Status.PENDING != dai.getStatus())
						continue;
					if (dlAppThreadMap.containsKey(dai))
						continue;
					dlAppThread = new DownloadAppThread(this, dai);
					break;
				}
			}
		}

		if (dlAppThread == null) {
			if (dlAppThreadMap.isEmpty() && daiMap.isEmpty())
				stopSelf();
			return;
		}

		dlAppThreadMap.put(dlAppThread.dai, dlAppThread);
		dlAppThread.start();
	}

	private void updateDownloadAppInfo(DownloadAppInfo dai) {
		downloadAppNotifier.notify(dai);
		if (DownloadAppInfo.Status.FINISHED == dai.getStatus()) {
			daiMap.remove(dai.pkg);
			dlAppThreadMap.remove(dai);

			if (!LangUtils.isBlank(dai.downloadId)) {
				if (Logger.DEBUG)
					L.d("Save Download Id, pkg: " + dai.pkg + ", downloadId"
							+ dai.downloadId);
				DownloadStatus.insertOrUpdateDownloadId(
						getApplicationContext(), dai.pkg, dai.downloadId);
			}

			sendInstallLog(dai);

			if (DownloadAppInfo.Result.SUCCESS == dai.getResult()) {
				AppInstallLogReceiver.putInstallApk(dai.pkg, dai.apk);
				installApp(dai.apk, dai.contentType);
			}

			download();
		}
	}

	private void installApp(File file, String contentType) {
		try {
			if (Logger.DEBUG)
				L.d("install apk file: " + file);

			if (!file.exists()) {
				if (Logger.DEBUG)
					throw new RuntimeException("file not exist");
				return;
			}

			if (LangUtils.isBlank(contentType))
				contentType = "application/vnd.android.package-archive";

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), contentType);
			startActivity(intent);
		} catch (Exception e) {
			if (Logger.ERROR)
				L.e("install apk file exception: ", e);
		}
	}

	private void sendInstallLog(DownloadAppInfo dai) {
		int failCode;
		switch (dai.getResult()) {
		case FAIL_DEVICE_UPSUPPORT:
			failCode = InstallLog.FAIL_CODE_NETWORK_OR_FILE_WRITE;
			break;
		case FAIL_CONNECT_TO_SERVER:
			failCode = InstallLog.FAIL_CODE_NETWORK;
			break;
		case FAIL_SD_UNMOUNT:
			failCode = InstallLog.FAIL_CODE_NETWORK_OR_FILE_WRITE;
			break;
		case FAIL_FILE_WRITE:
			failCode = InstallLog.FAIL_CODE_NETWORK_OR_FILE_WRITE;
			break;
		case FAIL_FILE_WRITE_OR_SERVER_CONNECT:
			failCode = InstallLog.FAIL_CODE_NETWORK_OR_FILE_WRITE;
			break;
		default:
			return;
		}
		if (Logger.DEBUG)
			L.d("insert Install Log, pkg: " + dai.pkg + ", downloadId: "
					+ dai.downloadId + ", failCode: " + failCode);

		InstallLog.insertInstallLog(this, dai.pkg, dai.version + "",
				InstallLog.EventTypeValue.DOWN_FAIL, dai.downloadId, failCode,
				0);

		ActionController.sendInstallLog(this);
	}

}