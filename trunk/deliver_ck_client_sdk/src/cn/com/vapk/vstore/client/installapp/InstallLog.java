package cn.com.vapk.vstore.client.installapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.sti.store.api.android.AndroidApiService;

import cn.com.vapk.vstore.client.util.ConfigurationFactory;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.telephony.TelephonyManager;

final class InstallLog implements BaseColumns {

	static final String AUTHORITY = "cn.com.vapk.vstore.client.provider.InstallLog";

	static final Uri CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/logs");

	static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.fet.android.appstore.client.install.logs";

	static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.fet.android.appstore.client.install.logs";

	static final String DEFAULT_SORT_ORDER = "";

	static final String COLUMN_TOKEN = "Token";
	static final String COLUMN_USER_ID = "UserId";
	static final String COLUMN_ICCID = "ICCID";
	static final String COLUMN_PACKAGE = "PackageName";
	static final String COLUMN_VERSION = "Version";
	static final String COLUMN_VERSION_CHECK = "VersionCheck";
	static final String COLUMN_PHONE_NUMBER = "PhoneNumber";
	static final String COLUMN_DOWNLOAD_ID = "DOWNLOADID";
	static final String COLUMN_FAIL_CODE = "FAIL_CODE";
	static final String COLUMN_REASON_ID = "REASON_ID";

	/** 下載管道 */
	static final String COLUMN_DONW_TYPE = "DownType";
	/** 事件時間 */
	static final String COLUMN_EVENT_DATETIME = "EventTime";
	/** 事件類別 */
	static final String COLUMN_EVENT_TYPE = "EventType";

	static final int FAIL_CODE_NETWORK = 1;
	static final int FAIL_CODE_NETWORK_OR_FILE_WRITE = 2;

	/** 從手機下載 */
	private static final String VALUE_DONW_TYPE_MOBILE = "mobile";

	private InstallLog() {
	}

	static enum EventTypeValue {
		// /** 事件類別: 下載成功 */
		// DOWN_SUCCESS, //
		/** 事件類別: 下載失敗 */
		DOWN_FAIL, //
		/** 事件類別: 安裝成功 */
		INSTALL_SUCCESS, //
		// /** 事件類別: 安裝失敗 */
		// INSTALL_FAIL, //
		/** 事件類別: 移除成功 */
		UNINSTALL_SUCCESS;

		int getTypeValue() {
			switch (this) {
			// case DOWN_SUCCESS:
			// return -1;
			case DOWN_FAIL:
				return 1;
			case INSTALL_SUCCESS:
				return 2;
			case UNINSTALL_SUCCESS:
				return 4;
			}
			throw new IllegalArgumentException("no such EventType");
		}
	};

	static final ContentValues createContentValue(Context ctxt,
			String packageName, String version, EventTypeValue eventType,
			String downloadId, int failCode, int reasonId) {
		ContentValues values = new ContentValues();
		String userId = AndroidApiService.getInstance(ctxt,ConfigurationFactory.getInstance()).getCredential(ctxt).getUserId();
		values.put(COLUMN_USER_ID, userId);
		TelephonyManager telMgr = (TelephonyManager) ctxt
				.getSystemService(Context.TELEPHONY_SERVICE);
		values.put(COLUMN_ICCID, telMgr.getSimSerialNumber());
		values.put(COLUMN_PACKAGE, packageName);
		values.put(COLUMN_VERSION, version);
		values.put(COLUMN_FAIL_CODE, failCode);
		values.put(COLUMN_REASON_ID, reasonId);
		values.put(COLUMN_DOWNLOAD_ID, downloadId);
		values.put(COLUMN_PHONE_NUMBER, telMgr.getLine1Number());
		values.put(COLUMN_DONW_TYPE, VALUE_DONW_TYPE_MOBILE);
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(COLUMN_EVENT_DATETIME, fmt.format(new Date()));
		values.put(COLUMN_EVENT_TYPE, eventType.getTypeValue());
		return values;
	}

	static final Uri insertInstallLog(Context ctxt, String packageName,
			String version, EventTypeValue eventType, String downloadId,
			int failCode, int reasonId) {
		ContentValues values = createContentValue(ctxt, packageName,
				version.toString(), eventType, downloadId, failCode, 0);
		return ctxt.getContentResolver().insert(CONTENT_URI, values);
	}
}
