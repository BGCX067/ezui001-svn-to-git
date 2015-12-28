package cn.com.vapk.vstore.client.installapp;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import tw.com.sti.store.api.android.util.Logger;

import cn.com.vapk.vstore.client.util.Constants;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class InstallLogProvider extends ContentProvider {
	private static final Logger L=Logger.getLogger(InstallLogProvider.class);
	private static HashMap<String, String> sProjectionMap;
	private static final int INSTALL_LOG = 1;
	private static final int INSTALL_LOG_ID = 2;
	private static final UriMatcher sUriMatcher;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(InstallLog.AUTHORITY, "logs", INSTALL_LOG);
		sUriMatcher.addURI(InstallLog.AUTHORITY, "logs/#", INSTALL_LOG_ID);

		sProjectionMap = new HashMap<String, String>();
		sProjectionMap.put(InstallLog._ID, InstallLog._ID);
		sProjectionMap.put(InstallLog.COLUMN_TOKEN, InstallLog.COLUMN_TOKEN);
		sProjectionMap.put(InstallLog.COLUMN_ICCID, InstallLog.COLUMN_ICCID);
		sProjectionMap
				.put(InstallLog.COLUMN_USER_ID, InstallLog.COLUMN_USER_ID);
		sProjectionMap
				.put(InstallLog.COLUMN_PACKAGE, InstallLog.COLUMN_PACKAGE);
		sProjectionMap
				.put(InstallLog.COLUMN_VERSION, InstallLog.COLUMN_VERSION);
		sProjectionMap.put(InstallLog.COLUMN_PHONE_NUMBER,
				InstallLog.COLUMN_PHONE_NUMBER);
		sProjectionMap.put(InstallLog.COLUMN_DONW_TYPE,
				InstallLog.COLUMN_DONW_TYPE);
		sProjectionMap.put(InstallLog.COLUMN_EVENT_DATETIME,
				InstallLog.COLUMN_EVENT_DATETIME);
		sProjectionMap.put(InstallLog.COLUMN_EVENT_TYPE,
				InstallLog.COLUMN_EVENT_TYPE);
		sProjectionMap.put(InstallLog.COLUMN_DOWNLOAD_ID,
				InstallLog.COLUMN_DOWNLOAD_ID);
		sProjectionMap.put(InstallLog.COLUMN_FAIL_CODE,
				InstallLog.COLUMN_FAIL_CODE);
		sProjectionMap.put(InstallLog.COLUMN_REASON_ID,
				InstallLog.COLUMN_REASON_ID);
		sProjectionMap.put(InstallLog.COLUMN_VERSION_CHECK,
				InstallLog.COLUMN_VERSION_CHECK);
	}

	private static final String DATABASE_NAME = Constants.CLIENT_APP_NAME+".install_log.db";
	private static final int DATABASE_VERSION = 19;
	private static final int DATABASE_VERSION_FOR_VERSION_CHECK = 18;
	private static final String TABLE_NAME = "install_log";
	private DatabaseHelper mOpenHelper;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (Logger.DEBUG)
				L.d("InstallLogProvider Finishing Constructing DatabaseHelper");
		}

		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			SQLiteDatabase db = super.getWritableDatabase();
			db.setMaximumSize(Constants.DATABASE_MAX_SIZE);
			return super.getWritableDatabase();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("Create Table {0}(");
			sqlBuilder.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
			sqlBuilder.append("Token TEXT,");
			sqlBuilder.append("UserId TEXT NOT NULL,");
			sqlBuilder.append("ICCID TEXT,");
			sqlBuilder.append("PackageName TEXT NOT NULL,");
			sqlBuilder.append("Version TEXT NOT NULL,");
			sqlBuilder.append("VersionCheck TEXT,");
			sqlBuilder.append("PhoneNumber TEXT,");
			sqlBuilder.append("DOWNLOADID TEXT,");
			sqlBuilder.append("FAIL_CODE INTEGER,");
			sqlBuilder.append("REASON_ID INTEGER,");
			sqlBuilder.append("DownType TEXT NOT NULL,");
			sqlBuilder.append("EventTime DATETIME NOT NULL,");
			sqlBuilder.append("EventType INTEGER NOT NULL)");

			String sql = MessageFormat
					.format(sqlBuilder.toString(), TABLE_NAME);

			if (Logger.DEBUG)
				L.d("InstallLogProvider In onCreate : SQL=" + sql);

			try {
				db.setMaximumSize(Constants.DATABASE_MAX_SIZE);
				db.execSQL(sql);
			} catch (SQLException e) {
				L.e("InstallLogProvider Unable to create database table, sql="
						+ sql, e);
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (Logger.DEBUG)
				L.d("InstallLogProvider In onUpgrade table : oldZVersion: "
						+ oldVersion + ", newVersion: " + newVersion);

			if (oldVersion <= DATABASE_VERSION_FOR_VERSION_CHECK) {
				String sql = "ALTER TABLE " + TABLE_NAME + " ADD "
						+ InstallLog.COLUMN_VERSION_CHECK + " TEXT";
				if (Logger.DEBUG)
					L.d("InstallLogProvider In onUpgrade table : SQL=" + sql);
				try {
					db.execSQL(sql);
				} catch (SQLException e) {
					L.e("InstallLogProvider Unable to upgrade database table, sql="
							+ sql, e);
				}
			}
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case INSTALL_LOG:
			return InstallLog.CONTENT_TYPE;

		case INSTALL_LOG_ID:
			return InstallLog.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case INSTALL_LOG:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(sProjectionMap);
			break;

		case INSTALL_LOG_ID:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(sProjectionMap);
			qb.appendWhere(InstallLog._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sUriMatcher.match(uri) != INSTALL_LOG) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(TABLE_NAME, null, values);

		if (rowId > 0) {
			Uri idUri = ContentUris.withAppendedId(InstallLog.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(idUri, null);
			return idUri;
		}

		if (Logger.DEBUG)
			throw new SQLException("Failed to insert row into " + uri);
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case INSTALL_LOG:
			count = db.delete(TABLE_NAME, selection, selectionArgs);
			break;

		case INSTALL_LOG_ID:
			String _id = uri.getPathSegments().get(1);
			selection = !TextUtils.isEmpty(selection) ? " AND (" + selection
					+ ')' : "";
			count = db.delete(TABLE_NAME, InstallLog._ID + "=" + _id
					+ selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if (Logger.DEBUG)
			throw new RuntimeException("unsupport");
		return 0;
	}

	/**
	 * return LogMessage of appinstalllog record ,print out the record and do
	 * NOT move cursor to next
	 * 
	 * @param cursor
	 */
	public static AppInstallLog getInstallLogMessage(Cursor cursor) {

		AppInstallLog appInstallLog = null;
		int iccidIdx = cursor.getColumnIndex(InstallLog.COLUMN_ICCID);
		int pkgidIdx = cursor.getColumnIndex(InstallLog.COLUMN_PACKAGE);
		int userIdIdx = cursor.getColumnIndex(InstallLog.COLUMN_USER_ID);
		int installTimeIdx = cursor
				.getColumnIndex(InstallLog.COLUMN_EVENT_DATETIME);
		int tokenIdx = cursor.getColumnIndex(InstallLog.COLUMN_TOKEN);
		int downTypeIdx = cursor.getColumnIndex(InstallLog.COLUMN_DONW_TYPE);
		int eventTypeIdx = cursor.getColumnIndex(InstallLog.COLUMN_EVENT_TYPE);
		int phoneIdx = cursor.getColumnIndex(InstallLog.COLUMN_PHONE_NUMBER);
		int versionIdx = cursor.getColumnIndex(InstallLog.COLUMN_VERSION);
		int downloadIdx = cursor.getColumnIndex(InstallLog.COLUMN_DOWNLOAD_ID);
		int failIdx = cursor.getColumnIndex(InstallLog.COLUMN_FAIL_CODE);
		int reasonIdx = cursor.getColumnIndex(InstallLog.COLUMN_REASON_ID);
		int versionCheckIdx = cursor
				.getColumnIndex(InstallLog.COLUMN_VERSION_CHECK);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			appInstallLog = new AppInstallLog(cursor.getString(downTypeIdx),
					cursor.getString(iccidIdx), cursor.getInt(eventTypeIdx),
					sdf.parse(cursor.getString(installTimeIdx)),
					cursor.getString(phoneIdx), cursor.getString(pkgidIdx),
					cursor.getString(userIdIdx), cursor.getString(tokenIdx),
					Integer.parseInt(cursor.getString(versionIdx)),
					cursor.getString(downloadIdx), Integer.parseInt(cursor
							.getString(failIdx)), Integer.parseInt(cursor
							.getString(reasonIdx)),
					cursor.getString(versionCheckIdx));
		} catch (Exception e) {
			if (Logger.ERROR)
				L.e("new AppInstallLog fail.", e);
		}

		return appInstallLog;

	}

	// public static String getLastInstallOkDownloadId(Context ctxt,String
	// pkgName,String eventType){
	// String downloadId = "0";
	//
	// Cursor cur = ctxt.getContentResolver().query(InstalllogCONTENT_URI,null ,
	// InstalllogCOLUMN_PACKAGE+"=? and " + InstalllogCOLUMN_EVENT_TYPE+"=?" ,
	// new String[]{pkgName,eventType} , InstalllogCOLUMN_DOWNLOAD_ID +
	// " desc");
	// cur.moveToFirst();
	// if(cur.isAfterLast()){
	// return null;
	// }
	// int downIdx = cur.getColumnIndex(InstalllogCOLUMN_DOWNLOAD_ID);
	// downloadId = cur.getString(downIdx);
	// cur.close();
	// return downloadId;
	// }

}
