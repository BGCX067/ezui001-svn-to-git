package cn.com.vapk.vstore.client.installapp;

import java.util.Date;

import tw.com.sti.store.api.util.LangUtils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DownloadStatus implements BaseColumns {

	static final String AUTHORITY = "cn.com.vapk.vstore.client.provider.DownloadStatus";
	static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/downloadStatuss");

	static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.fet.android.appstore.client.downloadStatus";
	static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.fet.android.appstore.client.downloadStatus";
	static final String DEFAULT_SORT_ORDER = "";

	static final String COLUMN_ID = BaseColumns._ID;
	static final String COLUMN_DOWNLOAD_ID = "downloadId";
	static final String COLUMN_PKGNAME = "pkgName";
	// public static final String COLUMN_VERSION = "version";
	static final String COLUMN_DOWNLOAD_TIME = "downloadTime";

	// public static final String COLUMN_STATUS = "status";

	private DownloadStatus() {
	}

	public static String getDownloadId(Context ctx, String pkgName) {
		if (pkgName == null)
			return null;

		Cursor cur = ctx.getContentResolver().query(CONTENT_URI, null,
				COLUMN_PKGNAME + "=?", new String[] { pkgName }, null);

		try {
			cur.moveToFirst();
			if (cur.isAfterLast()) {
				return null;
			}
			int Idx = cur.getColumnIndex(COLUMN_DOWNLOAD_ID);
			String downloadId = cur.getString(Idx);
			return downloadId;
		} finally {
			cur.close();
		}
	}

	public static void insertOrUpdateDownloadId(Context ctx, String pkgName,
			String downloadId) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_DOWNLOAD_TIME,
				LangUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		values.put(DownloadStatus.COLUMN_DOWNLOAD_ID, downloadId);
		ContentResolver cr = ctx.getContentResolver();
		int rows = cr.update(CONTENT_URI, values, COLUMN_PKGNAME + "=?",
				new String[] { pkgName });

		if (rows < 1) {
			values.put(DownloadStatus.COLUMN_PKGNAME, pkgName);
			cr.insert(DownloadStatus.CONTENT_URI, values);
		}
	}
}
