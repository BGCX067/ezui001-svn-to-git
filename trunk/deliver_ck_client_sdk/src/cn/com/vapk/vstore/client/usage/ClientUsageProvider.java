package cn.com.vapk.vstore.client.usage;

import java.text.MessageFormat;

import tw.com.sti.store.api.android.util.Logger;

import cn.com.vapk.vstore.client.util.Constants;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class ClientUsageProvider extends ContentProvider {
	private static final Logger L = Logger.getLogger(ClientUsageProvider.class);

	private static final String DATABASE_NAME = Constants.CLIENT_APP_NAME+".client_usage_log.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "client_usage_log";
	private static final int DATA_MAX_SIZE = 2 * 1024 * 1024;

	static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (Logger.DEBUG)
				L.d("Finishing Constructing DatabaseHelper");
		}

		public synchronized SQLiteDatabase getWritableDatabase() {
			SQLiteDatabase db = super.getWritableDatabase();
			db.setMaximumSize(DATA_MAX_SIZE);
			return super.getWritableDatabase();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("Create Table {0}(");
			sqlBuilder.append(ClientUsage._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,");
			sqlBuilder.append("{1} TEXT NOT NULL,");
			sqlBuilder.append("{2} TEXT NOT NULL,");
			sqlBuilder.append("{3} TEXT NOT NULL,");
			sqlBuilder.append("{4} TEXT NOT NULL,");
			sqlBuilder.append("{5} TEXT NOT NULL,");
			sqlBuilder.append("{6} TEXT NOT NULL,");
			sqlBuilder.append("{7} LONG NOT NULL,");
			sqlBuilder.append("{8} TEXT NOT NULL,");
			sqlBuilder.append("{9} TEXT NOT NULL,");
			sqlBuilder.append("{10} TEXT NOT NULL)");

			String sql = MessageFormat.format(sqlBuilder.toString(),
					TABLE_NAME, ClientUsage.COLUMN_UID,
					ClientUsage.COLUMN_USER_ID, ClientUsage.COLUMN_ICCID,
					ClientUsage.COLUMN_IMEI, ClientUsage.COLUMN_IMSI,
					ClientUsage.COLUMN_CLIENT_VERSION, ClientUsage.COLUMN_TIME,
					ClientUsage.COLUMN_DEVICE_MODEL_NAME,
					ClientUsage.COLUMN_MSISDN, ClientUsage.COLUMN_IS_FIRST_TIME);

			if (Logger.DEBUG)
				L.d("In onCreate : SQL, " + sql);

			try {
				db.setMaximumSize(DATA_MAX_SIZE);
				db.execSQL(sql);
			} catch (SQLException e) {
				if (Logger.ERROR)
					L.e("Unable to create database table, sql=" + sql, e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = MessageFormat.format("DROP TABLE IF EXISTS {0}",
					TABLE_NAME);
			if (Logger.DEBUG)
				L.d("In onUpgrade table : SQL" + sql);
			try {
				db.execSQL(sql);
			} catch (SQLException e) {
				if (Logger.ERROR)
					L.e("Unable to drop database table, sql=" + sql, e);
			}

			if (Logger.DEBUG)
				L.d(DatabaseHelper.class.getName()
						+ ", Finished Dropping table : SQL" + sql);
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (Logger.DEBUG)
			L.d("Delete one item.");

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		try {
			int deleteRow = db.delete(TABLE_NAME, selection, selectionArgs);
			return deleteRow;
		} catch (SQLException e) {
			if (Logger.ERROR)
				L.e("Delete failed exception: " + e);
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (Logger.DEBUG)
			L.d("Insert one item.");

		if (!uri.equals(ClientUsage.CONTENT_URI)) {
			if (Logger.DEBUG)
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		if (values == null) {
			if (Logger.DEBUG)
				throw new IllegalArgumentException("Null values ");
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri insertUri = ContentUris.withAppendedId(ClientUsage.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(insertUri, null);
			return insertUri;
		}
		if (Logger.DEBUG)
			throw new SQLException("Fail to insert row into " + uri);

		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs,
				null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}