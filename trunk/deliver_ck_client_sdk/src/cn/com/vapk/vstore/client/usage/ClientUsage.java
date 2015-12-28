package cn.com.vapk.vstore.client.usage;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.telephony.TelephonyManager;

final class ClientUsage implements BaseColumns {
	private static final Logger L = Logger.getLogger(ClientUsage.class);

	private static final String AUTHORITY = "cn.com.vapk.vstore.client.provider.ClientUsageProvider";
	static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/client_usage_logs");

	static final String COLUMN_UID = "uid";
	static final String COLUMN_USER_ID = "userid";
	static final String COLUMN_ICCID = "iccid";
	static final String COLUMN_IMEI = "imei";
	static final String COLUMN_IMSI = "imsi";
	static final String COLUMN_MSISDN = "msisdn";
	static final String COLUMN_CLIENT_VERSION = "version";
	static final String COLUMN_TIME = "time";
	static final String COLUMN_DEVICE_MODEL_NAME = "dvc";
	static final String COLUMN_IS_FIRST_TIME = "isfirsttime";

	final long id;
	final String uid, userId, iccid, imei, imsi, msisdn, version, dvc, isFirstTime;
	final long time;

	private ClientUsage(long id, String uid, String userId, String iccid,
			String imei, String imsi, String msisdn, String version, long time,
			String dvc, String isFirstTime) {
		this.id = id;
		this.uid = uid;
		this.userId = userId;
		this.iccid = iccid;
		this.imei = imei;
		this.imsi = imsi;
		this.msisdn = msisdn;
		this.version = version;
		this.time = time;
		this.dvc = dvc;
		this.isFirstTime = isFirstTime;
	}

	final static ClientUsage query(Context ctx) {
		Cursor c = null;
		try {
			c = ctx.getContentResolver().query(CONTENT_URI, null, null, null,
					null);

			if (c.moveToNext()) {
				long id = c.getLong(c.getColumnIndex(ClientUsage._ID));
				String l_uid = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_UID));
				String l_userId = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_USER_ID));
				String l_cver = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_CLIENT_VERSION));
				long l_time = c.getLong(c
						.getColumnIndex(ClientUsage.COLUMN_TIME));
				String l_iccid = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_ICCID));
				String l_imei = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_IMEI));
				String l_dvc = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_DEVICE_MODEL_NAME));
				String l_imsi = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_IMSI));
				String l_msisdn = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_MSISDN));
				String l_isFirstTime = c.getString(c
						.getColumnIndex(ClientUsage.COLUMN_IS_FIRST_TIME));

				ClientUsage clientUsage = new ClientUsage(id, l_uid, l_userId,
						l_iccid, l_imei, l_imsi, l_msisdn, l_cver, l_time, l_dvc, l_isFirstTime);
				return clientUsage;
			}
			return null;
		} finally {
			if (c != null)
				c.close();
		}
	}

	final static boolean insert(Context ctx, String uid, String userId, String l_isFirstTime) {
		TelephonyManager teleMgr = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		String l_uid, l_userId, l_ver, l_dvc, l_imei, l_iccid, l_imsi, l_msisdn;
		long l_time;

		try {
			PackageInfo pi = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			l_ver = "" + pi.versionCode;
		} catch (NameNotFoundException e) {
			l_ver = "";
		}

		l_imei = teleMgr.getDeviceId() == null ? "" : teleMgr.getDeviceId();
		l_iccid = teleMgr.getSimSerialNumber() == null ? "" : teleMgr
				.getSimSerialNumber();
		l_dvc = Build.MODEL == null ? "" : Build.MODEL;
		l_uid = uid == null ? "" : uid;
		l_userId = userId == null ? "" : userId;
		l_time = System.currentTimeMillis();
		l_imsi = teleMgr.getSubscriberId() == null ? "" : teleMgr
				.getSubscriberId();
		l_msisdn = teleMgr.getLine1Number() == null ? "" : teleMgr
				.getLine1Number();

		ContentValues values = new ContentValues();
		values.put(COLUMN_IMEI, l_imei);
		values.put(COLUMN_ICCID, l_iccid);
		values.put(COLUMN_CLIENT_VERSION, l_ver);
		values.put(COLUMN_DEVICE_MODEL_NAME, l_dvc);
		values.put(COLUMN_IMSI, l_imsi);
		values.put(COLUMN_MSISDN, l_msisdn);
		// extra value with intent
		values.put(COLUMN_UID, l_uid);
		values.put(COLUMN_USER_ID, l_userId);
		values.put(COLUMN_TIME, l_time);
		values.put(COLUMN_IS_FIRST_TIME, l_isFirstTime);

//		if (LangUtils.isBlankAny(l_uid, l_userId) < 0) {
			ContentResolver cr = ctx.getContentResolver();
			Uri insertRow = cr.insert(ClientUsage.CONTENT_URI, values);
			if (Logger.DEBUG) {
				L.d("insertRow: " + insertRow);
				L.d("values: " + values);
			}
			return true;
//		}
//
//		if (Logger.DEBUG) {
//			L.d("l_uid or l_userId is null.");
//		}
//		return false;
	}

	final static boolean delete(Context ctx, long id) {
		int deleteRows = ctx.getContentResolver().delete(CONTENT_URI,
				ClientUsage._ID + "=?", new String[] { String.valueOf(id) });

		if (deleteRows == 0)
			return false;
		else
			return true;
	}

}