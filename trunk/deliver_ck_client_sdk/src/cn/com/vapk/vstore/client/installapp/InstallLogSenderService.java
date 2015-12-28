package cn.com.vapk.vstore.client.installapp;

import java.util.ArrayList;
import java.util.List;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.CommonRet;

import cn.com.vapk.vstore.client.util.ConfigurationFactory;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class InstallLogSenderService extends Service {
	private static final Logger L=Logger.getLogger(InstallLogSenderService.class);
	private final int RETRIEVELIMIT = 10;

	public class LocalBinder extends Binder {
		InstallLogSenderService getService() {
			return InstallLogSenderService.this;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private Thread sendLogAfterLoginThread = null;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (Logger.DEBUG)
			L.d("onStart");
		if (intent == null) {
			if (Logger.DEBUG)
				L.d("intent == null");
			return;
		}
		if (sendLogAfterLoginThread == null
				|| !sendLogAfterLoginThread.isAlive()) {
			sendLogAfterLoginThread = new Thread(null, new MyTask(),
					"log sender service");
			sendLogAfterLoginThread.start();
		}
	}

	class MyTask implements Runnable {
		public MyTask() {
		}

		public void run() {
			sendInstallLogToClientServer();
		}
	};

	private void sendInstallLogToClientServer() {
		do {
			Uri uri = InstallLog.CONTENT_URI;
			Cursor cur = getContentResolver().query(uri, null, null, null,
					"_id ASC limit " + RETRIEVELIMIT);
			cur.moveToFirst();
			if (Logger.DEBUG)
				L.d("getCount():" + cur.getCount());
			if (cur.getCount() == 0) {
				cur.close();
				return;
			}
			List<AppInstallLog> logMessageList = new ArrayList<AppInstallLog>();
			List<String> ids = new ArrayList<String>();
			int idIdx = cur.getColumnIndex("_id");
			while (!cur.isAfterLast()) {
				AppInstallLog logMessage = InstallLogProvider
						.getInstallLogMessage(cur);
				if (logMessage != null) {
					logMessageList.add(logMessage);
					ids.add(cur.getString(idIdx));
				}
				cur.moveToNext();
			}
			cur.close();

			String where = this.getInString(ids);
			if (Logger.DEBUG)
				L.d("Sending Logs");
			ApiInvoker<CommonRet> apiInvoker = null;
			if (AndroidApiService.isLogin()) {
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.sendLogs(logMessageList);
				apiInvoker.invoke();
				if (Logger.DEBUG)
					L.d("Deleting Logs");
				deleteRecord(uri, apiInvoker.getRet(), where);
			}
			try {
				//2011-12-13 xiandong 此bug需告知vtion開發人員修正
				if (apiInvoker==null||apiInvoker.getRet()==null ||apiInvoker.getRet().isSuccess()) {
					if (Logger.DEBUG)
						L.d("sendInstallLogToServer sleep 10mins");
					Thread.sleep(600000);
				} else {
					if (Logger.DEBUG)
						L.d("sendInstallLogToServer sleep 1sec");
					Thread.sleep(1000);
				}
			} catch (Exception ex) {
				if (Logger.DEBUG)
					L.e(ex.getMessage(), ex);
			}
		} while (true);
	}

	/**
	 * delete record
	 * 
	 * @param uri
	 * @param ret
	 * @param cursor
	 * @param where
	 */
	private void deleteRecord(Uri uri,CommonRet ret, String where) {
		if (ret == null) {
			if (Logger.DEBUG)
				L.d("delete fail:no ret");
			return;
		}
		boolean retCode = ret==null?false:ret.isSuccess();
		if (retCode) {
			int numRowDeleted = this.getContentResolver().delete(uri, where,
					null);
			if (Logger.DEBUG)
				L.d("numRowDeleted :" + numRowDeleted);

		} else {
			if (Logger.DEBUG)
				L.d("delete fail,retCode = " + retCode);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (Logger.DEBUG)
			L.d("return binder " + intent.getAction());
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * get the IN where clause
	 * 
	 * @param ids
	 * @return
	 */
	private String getInString(List<String> ids) {
		StringBuilder sb = new StringBuilder();
		sb.append("_id IN (");
		int count = ids.size();
		for (int i = 0; i < count - 1; i++) {
			sb.append(ids.get(i) + ",");
		}
		if (count > 0) {
			sb.append(ids.get(count - 1));
		}
		sb.append(")");
		return sb.toString();
	}

}