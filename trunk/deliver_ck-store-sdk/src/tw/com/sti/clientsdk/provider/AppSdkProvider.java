package tw.com.sti.clientsdk.provider;

import java.text.MessageFormat;
import java.util.HashSet;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.android.util.NetworkUtils;
import tw.com.sti.store.api.vo.CheckAppStoreRet;
import tw.com.sti.store.api.vo.SdkAppInfoRet;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public abstract class AppSdkProvider extends ContentProvider {
	private static final Logger L=Logger.getLogger(AppSdkProvider.class);
	private static final UriMatcher sUriMatcher;
	
	private static final int LICENSE = 1;
	private static final int APP_STRORE_LIST = 2;
	private static final int APP_SDK_PROCESS = 3;
	private static final int APP_PAYMENT_INFO = 4;
	private static final int APP_SELF_PURCHASE_LICENSE = 10;
	private static final int SUPPORT_APP_SELF_PURCHASE = 11;
	private static final int SUPPORT_SERVER_PRODUCT_PURCHASE = 12;
	private static final int USER_UID = 13;
	
	private String authority;
	private DatabaseHelper licenseDbHelper;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	}
	protected abstract Configuration getConfiguration();
	private String getAuthority(){
		return authority;
	}
	protected AppSdkProvider(){
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return licenseDbHelper.getWritableDatabase().delete(TABLE_NAME, selection,
				selectionArgs);
	}
	@Override
	public String getType(Uri uri) {
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = this.licenseDbHelper.getWritableDatabase().insert(TABLE_NAME,
				null, values);
		if (rowId > 0) {
			return ContentUris.withAppendedId(Uri.parse("content://" + authority+ "/app/license"), rowId);
		} else {
			return null;
		}
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return licenseDbHelper.getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);
	}
	@Override
	public boolean onCreate() {
		licenseDbHelper = new DatabaseHelper(getContext(), "sdk.applicense.db");
		authority = this.getContext().getPackageName() + ".sdk.provider.StoreProvider";
		sUriMatcher.addURI(getAuthority(), "app/license", LICENSE);
		sUriMatcher.addURI(getAuthority(), "store/list", APP_STRORE_LIST);
		sUriMatcher.addURI(getAuthority(), "appsdk/process", APP_SDK_PROCESS);
		sUriMatcher.addURI(getAuthority(), "app/paymentInfo", APP_PAYMENT_INFO);
		//
		sUriMatcher.addURI(getAuthority(), "app/selfPurchaseLicense", APP_SELF_PURCHASE_LICENSE);
		sUriMatcher.addURI(getAuthority(), "Support/AppSelfPurchase", SUPPORT_APP_SELF_PURCHASE);
		sUriMatcher.addURI(getAuthority(), "Support/ServerProductPurchase", SUPPORT_SERVER_PRODUCT_PURCHASE);
		//
		sUriMatcher.addURI(getAuthority(), "user/uid", USER_UID);
		if (licenseDbHelper.getWritableDatabase() == null) {
			return false;
		} else {
			return true;
		}
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		try {
			switch (sUriMatcher.match(uri)) {
			case LICENSE:
				return readLicense(uri, projection, selection, selectionArgs, sortOrder);
			case APP_STRORE_LIST:
				return checkAppStore(uri, projection, selection, selectionArgs, sortOrder);
			case APP_SDK_PROCESS:
				return isSdkProcess(uri, projection, selection, selectionArgs, sortOrder);
			case APP_PAYMENT_INFO:
				return getAppPaymentInfo(uri, projection, selection, selectionArgs, sortOrder);
			case APP_SELF_PURCHASE_LICENSE:
				return appSelfPurchaseLicense(uri, projection, selection, selectionArgs, sortOrder);
			case SUPPORT_APP_SELF_PURCHASE:
//				return supportAppSelfPurchase(uri, projection, selection, selectionArgs, sortOrder);
				break;
			case SUPPORT_SERVER_PRODUCT_PURCHASE:
//				return supportServerProductPrucahse(uri, projection, selection, selectionArgs, sortOrder);
				break;
			case USER_UID:
				return getUserUid(uri, projection, selection, selectionArgs, sortOrder);
			}
		} catch (Throwable e) {
			L.e("query error",e);
		}
		return new MatrixCursor(new String[] { "nothing" });
	}
	
	private Cursor readLicense(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = licenseDbHelper.getReadableDatabase();
		String[] rmSdkVerArgs = new String[1];
		rmSdkVerArgs[0] = selectionArgs[0];
		Cursor c = db.query("Serials", new String[] { "data", "sign" }, "packageName=?", rmSdkVerArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		if(Logger.DEBUG)
			L.d("LicenseForReadProvider license size: " + c.getCount());
		return c;
	}
	
	private Cursor checkAppStore(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String[] columnNames = new String[] { "retcode", "id", "name", "clientDownloadUrl" };
		MatrixCursor cursor = new MatrixCursor(columnNames);
		if (!NetworkUtils.isNetworkOpen(getContext())) {
			cursor.addRow(new Object[]{"01","","No Network",""});
		}else{
			int version = Integer.parseInt(selectionArgs[1]);
			AndroidApiService apiService = AndroidApiService.getInstance(getContext(),getConfiguration());
			ApiInvoker<CheckAppStoreRet> apiInvoker = apiService.checkAppStoreList(selectionArgs[0], version);
			apiInvoker.invoke();
			
			if(apiInvoker.isSuccess()){
				if(apiInvoker.getRet()!=null&&apiInvoker.getRet().isSuccess()&&apiInvoker.getRet().getStores()!=null){
					for(int i=0; i < apiInvoker.getRet().getStores().length ; i++){
						Object[] columnValues = new Object[] { "00",apiInvoker.getRet().getStores()[i].getId(), apiInvoker.getRet().getStores()[i].getName(), apiInvoker.getRet().getStores()[i].getDownloadUrl()};
						cursor.addRow(columnValues);
					}
				}
			}else{
				cursor.addRow(new Object[]{"02","","No Network",""});
			}
		}
		return cursor;
	}
	
	private static final HashSet<String> SDK_CANCEL = new HashSet<String>();
	
	public static void addSdkCancel(String pkgname) {
		SDK_CANCEL.add(pkgname);
	}
	
	public static void removeSdkCancel(String pkgname) {
		SDK_CANCEL.remove(pkgname);
	}
	
	private Cursor isSdkProcess(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// 目前只有查詢SDK Cancel
		final String[] columnNames = new String[] { "sdk_cancel" };
		String pkgId = selectionArgs[0];
		MatrixCursor cursor = new MatrixCursor(columnNames);
		Object[] columnValues = new Object[columnNames.length];
		if (SDK_CANCEL.contains(pkgId)) {
			columnValues[0] = 1;
			removeSdkCancel(pkgId);
		} else {
			columnValues[0] = 0;
		}
		cursor.addRow(columnValues);
		return cursor;
	}
	
	private Cursor getAppPaymentInfo(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if(Logger.DEBUG)
			L.d("PackagePaymentProvider query");
		if (selectionArgs == null || selectionArgs.length < 3) {
			if(Logger.DEBUG)
				L.d("PackagePaymentProvider selectionArgs: " + selectionArgs);
			return null;
		}

		String[] columnNames = new String[] { "pkgid", "pkgtitle", "iconurl", "provider", "rank", "price", 
//				"currency", 
				"pricetype", "version", "myPriceType", "paystatus", "monthlyEnddate", "myPrice", "logId","onUse" };
		MatrixCursor cursor = new MatrixCursor(columnNames);
		try{
			String pkgId = selectionArgs[0];
			int version = Integer.parseInt(selectionArgs[1]);
			boolean dolog = "y".equals(selectionArgs[2]);
			String firstTime = selectionArgs.length > 3 ? selectionArgs[3] : "0";
			AndroidApiService apiService = AndroidApiService.getInstance(getContext(),getConfiguration());
			ApiInvoker<SdkAppInfoRet> apiInvoker = apiService.getSdkAppInfo(pkgId, version, dolog?"1":"0", firstTime);
			apiInvoker.invoke();
			if(apiInvoker.isSuccess()){
				SdkAppInfoRet.SdkApp apk=apiInvoker.getRet().getApplication();
				if(Logger.DEBUG)
					L.d("PackagePaymentProvider Apk: " + apk);
		
				String[] columnValues = new String[columnNames.length];
				columnValues[0] = apk.getPkg();
				columnValues[1] = apk.getTitle();
				columnValues[2] = apk.getIcon();
				columnValues[3] = apk.getProvider();
				columnValues[4] = apk.getRating() + "";
				columnValues[5] = apk.getPrice() + "";
	//			columnValues[6] = apk.getCurrency();
				columnValues[6] = apk.getPriceType()==null?"":apk.getAppPriceType()+"";
				columnValues[7] = apk.getVersion() + "";
				columnValues[8] = apk.getPriceType()==null?"":apk.getAppPriceType()+"";
				columnValues[9] = apk.getPayStatus()==null?"":apk.getPaymentStatus()+"";
				columnValues[10] = apk.getSubscribeExpDate() == null ? "" : apk.getSubscribeExpDate();
				columnValues[11] = apk.getPrice() + "";
				columnValues[12] = apk.getLogId() != 0 ? ""+apk.getLogId() : null;
				columnValues[13] = apk.getOnUse()+"";
				cursor.addRow(columnValues);
		
				/*
				 * <pkgid>application package name</pkgid> <pkgtitle>package
				 * title</pkgtitle> <iconurl>package icon url</iconurl>
				 * <provider>開發者資訊<provider> <rank>0~10</rank> <price>數值型</price>
				 * <currency>NT</currency> <pricetype>數值型<pricetype>
				 * <version>（最新有效的）數值型</version> <myPriceType>數值型<myPriceType>
				 * <paystatus>0=沒有付款 1=付款中 2=付款成功 3=付款失敗<paystatus>
				 * <monthlyEnddate>月租型日期(字串類型年/月/日)<monthlyEnddate>
				 * <myPrice>數值型</myPrice> <appVersion> <pkgid>application package
				 * name</pkgid> <pkgtitle>package title</pkgtitle> <iconurl>package icon
				 * url</iconurl> <provider>開發者資訊<provider> <rank>0~10</rank>
				 * <price>數值型</price> <currency>NT</currency> <pricetype>數值型<pricetype>
				 * <version>（最新有效的）數值型</version> <myPriceType>數值型<myPriceType>
				 * <paystatus>0=沒有付款 1=付款中 2=付款成功 3=付款失敗<paystatus>
				 * <myPrice>數值型</myPrice> </appVersion>
				 */
			}
		}catch(Throwable e){
			L.e(e.getMessage(),e);
		}
		return cursor;
	}
	
	private Cursor appSelfPurchaseLicense(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		if (Logger.DEBUG)
			L.d("SdpProvider.appSelfPurchaseLicense: " + selectionArgs[0]);
		SQLiteDatabase db = licenseDbHelper.getReadableDatabase();
		Cursor cur = db.query("Serials", new String[] { "data", "sign" }, "packageName=?", new String[] { selectionArgs[0] }, null, null, sortOrder);
		cur.setNotificationUri(getContext().getContentResolver(), uri);
		return cur;
	}
	
	private static final long DATABASE_MAX_SIZE = 2 * 1024 * 1024;
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME = "Serials";
	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context,String dbName) {
			super(context, dbName, null, DATABASE_VERSION);
		}

		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			// SQLiteDatabase db = super.getWritableDatabase();
			// db.setMaximumSize(Constants.DATABASE_MAX_SIZE);
			return super.getWritableDatabase();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("Create Table {0}(");
			sqlBuilder.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
			sqlBuilder.append("serverGenId INTEGER  NOT NULL,");
			sqlBuilder.append("packageName TEXT NOT NULL,");
			sqlBuilder.append("data blob NOT NULL,");
			sqlBuilder.append("sign blob NOT NULL)");
			String sql = MessageFormat
					.format(sqlBuilder.toString(), TABLE_NAME);
			try {
				db.setMaximumSize(DATABASE_MAX_SIZE);
				db.execSQL(sql);
			} catch (SQLException e) {
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = MessageFormat.format("DROP TABLE IF EXISTS {0}",
					TABLE_NAME);
			try {
				db.execSQL(sql);
			} catch (SQLException e) {
			}
			onCreate(db);
		}

	}
	
	private Cursor getUserUid(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		final String[] columnNames = new String[] { "uid", "isLogin"};
		MatrixCursor cursor = new MatrixCursor(columnNames);
		try{
			AndroidApiService apiService = AndroidApiService.getInstance(getContext(), getConfiguration());
			String[] columnValues = new String[columnNames.length];
			columnValues[0] = apiService.getCredential(getContext()).getUid();
			columnValues[1] = String.valueOf(AndroidApiService.isLogin());
			L.d("current login uid="+columnValues[0]+"; is login="+columnValues[1]);
			cursor.addRow(columnValues);
		}catch(Throwable e){
			L.e(e.getMessage(),e);
		}
		return cursor;
	}
}
