package cn.com.vapk.vstore.client.installapp;

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

public class DownloadStatusProvider extends ContentProvider {
	private static final Logger L=Logger.getLogger(DownloadStatusProvider.class);
	private static final String DATABASE_NAME = Constants.CLIENT_APP_NAME+".downloadStatus.db";
	private static final int DATABASE_VERSION = 6;
	private static final String TABLE_NAME = "downloadStatus";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			SQLiteDatabase db = super.getWritableDatabase();
			db.setMaximumSize(Constants.DATABASE_MAX_SIZE);
			return super.getWritableDatabase();
		}

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (Logger.DEBUG)
				L.d("Finishing Constructing DatabaseHelper");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("Create Table {0}(");
			sqlBuilder.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
			sqlBuilder.append("pkgName TEXT NOT NULL,");
			// sqlBuilder.append("version TEXT ,");
			sqlBuilder.append("downloadId TEXT,");
			sqlBuilder.append("downloadTime DATETIME NOT NULL)");
			// sqlBuilder.append("status TEXT )");

			String sql = MessageFormat
					.format(sqlBuilder.toString(), TABLE_NAME);

			if (Logger.DEBUG)
				L.d("In onCreate : SQL" + sql);

			try {
				db.setMaximumSize(Constants.DATABASE_MAX_SIZE);
				db.execSQL(sql);
			} catch (SQLException e) {
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
				L.e("Unable to drop database table, sql=" + sql, e);
			}

			if (Logger.DEBUG)
				L.d("Finished Dropping table : SQL" + sql);
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		Context context = getContext();
		mOpenHelper = new DatabaseHelper(context);

		if (mOpenHelper.getWritableDatabase() == null) {
			if (Logger.DEBUG)
				throw new RuntimeException("on Create Fail");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return mOpenHelper.getReadableDatabase().query(TABLE_NAME, projection,
				selection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = this.mOpenHelper.getWritableDatabase().insert(TABLE_NAME,
				null, values);

		if (rowId > 0) {
			Uri idUri = ContentUris.withAppendedId(DownloadStatus.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(idUri, null);
			return idUri;
		}

		if (Logger.DEBUG)
			throw new SQLException("Failed to insert row into " + uri);
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return (int) mOpenHelper.getWritableDatabase().update(TABLE_NAME,
				values, selection, selectionArgs);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return mOpenHelper.getWritableDatabase().delete(TABLE_NAME, selection,
				selectionArgs);
	}

}
