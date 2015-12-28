package cn.com.vapk.vstore.client.update;

import java.util.Calendar;

import tw.com.sti.store.api.android.util.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

public class CheckAppsVersionReceiver extends BroadcastReceiver {
	private final Logger L = Logger.getLogger(CheckAppsVersionReceiver.class);
	private static final String PREF_NAME = "cn.com.vapk.vstore.client.update.pref.CHECK_APP_VERSION_RECEIVER";
	private static final String PREF_KEY_CHECK_APP_VERSION_TIME = "prefAppTime";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (Logger.DEBUG)
			L.d("onReceiver action: " + action);

		long triggerAtTime = getCheckAppsTime(ctx);
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			if (triggerAtTime == -1)
				return;
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			if (triggerAtTime == -1)
				return;
			Uri uri = intent.getData();
			if (uri == null)
				return;
			String pkg = uri.toString().replace("package:", "");
			if (!ctx.getPackageName().equals(pkg))
				return;
		} else if (ActionController.ACTION_CHECK_APPS_VERSION.equals(action)) {
			if (triggerAtTime != -1) {
				return;
			}
			intiCheckAppsTime(ctx);
		}
		ActionController.alarmCheckAppVersion(ctx, alarmCheckAppTime(ctx));
	}

	private void intiCheckAppsTime(Context ctx) {
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		int triggerAtTime = (int) (Math.random() * 86400000);
		editor.putInt(PREF_KEY_CHECK_APP_VERSION_TIME, triggerAtTime);
		editor.commit();

		if (Logger.DEBUG)
			L.d("intiCheckAppsTime: " + triggerAtTime);
	}

	public static int getCheckAppsTime(Context ctx) {
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		int checkAppTime = pref.getInt(PREF_KEY_CHECK_APP_VERSION_TIME, -1);
		return checkAppTime;
	}

	private long alarmCheckAppTime(Context ctx) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DATE), 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
		long triggerAtTime = c.getTimeInMillis() + getCheckAppsTime(ctx);
		if (triggerAtTime < System.currentTimeMillis())
			triggerAtTime += 86400000;

		return triggerAtTime;
	}
}