package cn.com.vapk.vstore.client.update;

import java.util.Calendar;

import tw.com.sti.store.api.android.util.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

final public class CheckClientVersionReceiver extends BroadcastReceiver {

	private final Logger L = Logger.getLogger(CheckClientVersionReceiver.class);
	private static final String PREF_NAME = "cn.com.vapk.vstore.client.update.pref.CHECK_CLIENT_VERSION_RECEIVER";
	private static final String PREF_KEY_CHECK_TIME = "check_time";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (Logger.DEBUG)
			L.d("onReceive action: " + action);

		if (ActionController.ACTION_CHECK_CLIENT_VERSION.equals(action)) {
			int checkTime = getCheckTime(ctx);
			if (checkTime != -1)
				return;
			checkTime = initCheckTime(ctx);
			ActionController.alarmCheckClientVersion(ctx,
					getAlarmTime(checkTime));
			return;
		}

		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			int checkTime = getCheckTime(ctx);
			if (checkTime == -1)
				checkTime = initCheckTime(ctx);
			ActionController.alarmCheckClientVersion(ctx,
					getAlarmTime(checkTime));
			return;
		}

		if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			Uri uri = intent.getData();
			if (uri == null)
				return;
			String pkg = uri.toString().replace("package:", "");
			if (!ctx.getPackageName().equals(pkg))
				return;

			int checkTime = getCheckTime(ctx);
			if (checkTime == -1)
				checkTime = initCheckTime(ctx);
			ActionController.alarmCheckClientVersion(ctx,
					getAlarmTime(checkTime));

			return;
		}
	}

	public static int getCheckTime(Context ctx) {
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		int checkTime = pref.getInt(PREF_KEY_CHECK_TIME, -1);
		return checkTime;
	}

	private int initCheckTime(Context ctx) {
		final int checkTime = (int) (Math.random() * 86400000);
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(PREF_KEY_CHECK_TIME, checkTime);
		edit.commit();
		if (Logger.DEBUG)
			L.d("initCheckTime " + checkTime);

		return checkTime;
	}

	private long getAlarmTime(int checkTime) {
		Calendar today = Calendar.getInstance();
		today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
				today.get(Calendar.DATE), 0, 0, 0);
		today.set(Calendar.MILLISECOND, 0);
		long alarmTime = today.getTimeInMillis() + checkTime;
		if (alarmTime < System.currentTimeMillis())
			alarmTime += 86400000;
		return alarmTime;
	}
}