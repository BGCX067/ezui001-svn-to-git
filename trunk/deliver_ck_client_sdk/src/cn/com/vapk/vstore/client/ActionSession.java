package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import cn.com.vapk.vstore.client.R;

public class ActionSession {

	private static final Logger L = Logger.getLogger(ActionSession.class);

	private static final String PREF_NAME = "cn.com.vapk.vstore.client.pref.ACTION_SESSION";
	private static final String PREF_KEY_SUBSCRIBER_ID = "subscriber_id";
	private static final String PREF_KEY_AUTO_LOGIN_ENABLE = "auto_login_enable";
	private static final String PREF_KEY_IP_LOGIN_ENABLE = "ip_login_enable";
	private static final String PREF_KEY_APP_FILTER_SETTING_ENABLE = "app_filter_setting_enable";

	private static ActionSession instance;
	private boolean ipLoginEnable;
	private boolean autoLoginEnable;
	private boolean appFilterSettingEnable;

	private ActionSession(Context ctx) {
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		autoLoginEnable = pref.getBoolean(PREF_KEY_AUTO_LOGIN_ENABLE, false);
		ipLoginEnable = pref.getBoolean(PREF_KEY_IP_LOGIN_ENABLE, true);
		appFilterSettingEnable = pref.getBoolean(
				PREF_KEY_APP_FILTER_SETTING_ENABLE, false);
		if (!autoLoginEnable) {
			return;
		}
		String pref_subscriberId = pref.getString(PREF_KEY_SUBSCRIBER_ID, "");
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String subscriberId = tm.getSubscriberId() == null ? "" : tm
				.getSubscriberId();
		if (!subscriberId.equals(pref_subscriberId)) {
			if (Logger.DEBUG)
				L.d("Change SIM card.");
			autoLoginEnable = false;
			Editor editor = pref.edit();
			editor.putBoolean(PREF_KEY_AUTO_LOGIN_ENABLE, false);
			editor.commit();
		}
	}

	@Override
	public String toString() {
		return "ipLoginEnable: " + ipLoginEnable + ", autoLoginEnable: "
				+ autoLoginEnable;
	}

	private final static synchronized ActionSession getSession(Context ctx) {
		if (instance == null)
			instance = new ActionSession(ctx);
		return instance;
	}

	static final boolean isAutoLoginEnable(Context ctx) {
		return getSession(ctx).autoLoginEnable;
	}

	static final void disableAutoLogin(Context ctx) {
		setAutoLogin(ctx, false);
	}

	static final void enableAutoLogin(Context ctx) {
		setAutoLogin(ctx, true);
	}

	private final static void setAutoLogin(Context ctx, boolean enable) {
		if (getSession(ctx).autoLoginEnable == enable)
			return;
		getSession(ctx).autoLoginEnable = enable;
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(PREF_KEY_AUTO_LOGIN_ENABLE, enable);
		if (enable) {
			TelephonyManager tm = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			String subscriberId = tm.getSubscriberId() == null ? "" : tm
					.getSubscriberId();
			editor.putString(PREF_KEY_SUBSCRIBER_ID, subscriberId);
		}
		editor.commit();
	}

	static final boolean isIPLoginEnable(Context ctx) {
		return getSession(ctx).ipLoginEnable;
	}

	static final void disableIPLogin(Context ctx) {
		if (!getSession(ctx).ipLoginEnable)
			return;
		getSession(ctx).ipLoginEnable = false;
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(PREF_KEY_IP_LOGIN_ENABLE, false);
		editor.commit();
	}

	static final boolean isAppFilterSettingEnable(Context ctx) {
		return getSession(ctx).appFilterSettingEnable;
	}

	static final void disableAppFilterSetting(Context ctx) {
		setAppFilterSettingEnable(ctx, false);
	}

	static final void enableAppFilterSetting(Context ctx) {
		setAppFilterSettingEnable(ctx, true);
	}

	private final static void setAppFilterSettingEnable(Context ctx,
			boolean enable) {
		if (getSession(ctx).appFilterSettingEnable == enable)
			return;

		if (Logger.DEBUG)
			L.d("setAppFilterSettingEnable: " + enable);

		getSession(ctx).appFilterSettingEnable = enable;
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(PREF_KEY_APP_FILTER_SETTING_ENABLE, enable);
		editor.commit();
	}

	/* EULA */

	public static final boolean isAgreeEula(Context ctx, String userId) {
		if (LangUtils.isBlank(userId))
			return false;
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return pref.getBoolean(getAgreeEulaKey(userId), false);
	}

	static final void agreeEula(Context ctx, String userId) {
		if (LangUtils.isBlank(userId))
			return;
		SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putBoolean(getAgreeEulaKey(userId), true);
		editor.commit();
	}

	private static final String getAgreeEulaKey(String userId) {
		return "eula_user_id_" + userId;
	}

}
