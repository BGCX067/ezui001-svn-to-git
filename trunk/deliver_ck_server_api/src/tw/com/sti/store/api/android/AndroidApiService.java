package tw.com.sti.store.api.android;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import tw.com.sti.security.Dsa;
import tw.com.sti.store.api.ApiDataParseHandler;
import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.ApiService.AppsType;
import tw.com.sti.store.api.ApiUrl;
import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.android.util.NetworkUtils;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.AppVersionsRet;
import tw.com.sti.store.api.vo.ApplicationRet;
import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.CPAppsRet;
import tw.com.sti.store.api.vo.CategoriesRet;
import tw.com.sti.store.api.vo.CategoryAppsRet;
import tw.com.sti.store.api.vo.CheckAppStoreRet;
import tw.com.sti.store.api.vo.CheckClientVersionRet;
import tw.com.sti.store.api.vo.CheckPayStatusRet;
import tw.com.sti.store.api.vo.CommentsRet;
import tw.com.sti.store.api.vo.CommonRet;
import tw.com.sti.store.api.vo.Credential;
import tw.com.sti.store.api.vo.IPLoginRet;
import tw.com.sti.store.api.vo.LicenseInfoRet;
import tw.com.sti.store.api.vo.LogMessage;
import tw.com.sti.store.api.vo.LoginRet;
import tw.com.sti.store.api.vo.OrderRet;
import tw.com.sti.store.api.vo.OverPaymentQuotaRet;
import tw.com.sti.store.api.vo.PePayOrderRet;
import tw.com.sti.store.api.vo.SdkAppInfoRet;
import tw.com.sti.store.api.vo.UPayConfig;
import tw.com.sti.store.api.vo.UnsubscribeRet;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class AndroidApiService {

	private Configuration config;
	private ApiUrl apiUrl;
	private static final Logger L = Logger.getLogger(AndroidApiService.class);

	private static final String PREF_NAME = "cn.com.vapk.vstore.client.pref.API_SERVICE";
	private static final String PREF_KEY_TOKEN = "token";
	private static final String PREF_KEY_USER_ID = "user_id";
	private static final String PREF_KEY_UID = "uid";
	private static final String PREF_KEY_SUBSCRIBER_ID = "subscriber_id";
	private static final String PREF_KEY_SIM_SERIAL_NUMBER = "sim_serial_number";
	private static final String PREF_KEY_APP_FILTER = "app_filter";
	private static boolean isLogin = false;

	private static AndroidApiService service;
	private String storeId;
	private String sdkVer;
	private String sdkRel;
	private String clientVer;
	private String deviceId;
	private String macAddress;
	private String subscriberId;
	private String simSerialNumber;
//	private String token;
	private String wpx;
	private String hpx;
//	private String userId;
//	private String uid;
	private int appFilter;
	private Credential credential;
	

	private AndroidApiService(Context context,Configuration config) {
		this.config=config;
		this.apiUrl=new ApiUrl(config);
		if (Logger.DEBUG)
			L.d("new ApiService()");

		sdkVer = Build.VERSION.SDK;
		sdkRel = Build.VERSION.RELEASE;
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
			storeId=pi.packageName;
			clientVer = "" + pi.versionCode;
		} catch (NameNotFoundException e) {
		}
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId() == null ? "0" : tm.getDeviceId();
		macAddress = NetworkUtils.getDeviceMacAddress(context);
		subscriberId = tm.getSubscriberId() == null ? "0" : tm
				.getSubscriberId();
		simSerialNumber = tm.getSimSerialNumber() == null ? "0" : tm
				.getSimSerialNumber();

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		try {
			Class<Display> cls = Display.class;
			Method method = cls.getMethod("getRotation");
			Object retobj = method.invoke(display);
			int rotation = Integer.parseInt(retobj.toString());
			if (Surface.ROTATION_0 == rotation
					|| Surface.ROTATION_180 == rotation) {
				wpx = "" + display.getWidth();
				hpx = "" + display.getHeight();
			} else {
				wpx = "" + display.getHeight();
				hpx = "" + display.getWidth();
			}
		} catch (Exception e) {
			if (display.getOrientation() == 1) {
				wpx = "" + display.getHeight();
				hpx = "" + display.getWidth();
			} else {
				wpx = "" + display.getWidth();
				hpx = "" + display.getHeight();
			}
		}

		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
//		token = pref.getString(PREF_KEY_TOKEN, "");
//		uid = pref.getString(PREF_KEY_UID, "");
//		userId = pref.getString(PREF_KEY_USER_ID, "");
		appFilter = pref.getInt(PREF_KEY_APP_FILTER, 0);
		// ipLoginEnable = pref.getBoolean(PREF_KEY_IP_LOGIN_ENABLE, true);

		// 換過SIM卡，清除資料
		String pref_subscriberId = pref.getString(PREF_KEY_SUBSCRIBER_ID, "0");
		String pref_simSerialNumber = pref.getString(
				PREF_KEY_SIM_SERIAL_NUMBER, "0");
		if (!subscriberId.equals(pref_subscriberId)
				|| !simSerialNumber.equals(pref_simSerialNumber)) {
			if (Logger.DEBUG)
				L.d("Change SIM card.");
			cleanCredential(context);
		}
		this.getCredential(context);
	}

	public static synchronized AndroidApiService getInstance(Context context,Configuration config) {
		if (service != null)
			return service;
		service = new AndroidApiService(context,config);
		return service;
	}

//	public String getToken() {
//		return token;
//	}
//
//	public String getUserId() {
//		return userId;
//	}
//
//	public String getUID() {
//		return uid;
//	}

	public int getAppFilter() {
		return appFilter;
	}
	public Credential getCredential(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		String token = pref.getString(PREF_KEY_TOKEN, "");
		String uid = pref.getString(PREF_KEY_UID, "");
		String userId = pref.getString(PREF_KEY_USER_ID, "");

		credential= new Credential(token,uid,userId);
		return credential;
	}
	public void saveAppFilter(Context context, int filter) {
		this.appFilter = filter;
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putInt(PREF_KEY_APP_FILTER, filter);
		edit.commit();
	}

	public void saveCredential(Context context, Credential credential) {
		if (Logger.DEBUG)
			L.d("save Credential.");
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putString(PREF_KEY_TOKEN, credential.getToken());
		edit.putString(PREF_KEY_UID, credential.getUid());
		edit.putString(PREF_KEY_USER_ID, credential.getUserId());
		edit.putString(PREF_KEY_SUBSCRIBER_ID, subscriberId);
		edit.putString(PREF_KEY_SIM_SERIAL_NUMBER, simSerialNumber);
		edit.commit();
//		this.token = credential.getToken();
//		this.userId = credential.getUserId();
//		this.uid = credential.getUid();
		this.credential = credential;
		isLogin = true;
	}

	public void cleanCredential(Context context) {
		if (Logger.DEBUG)
			L.d("clean Credential.");
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.remove(PREF_KEY_TOKEN);
		edit.remove(PREF_KEY_UID);
		edit.remove(PREF_KEY_SUBSCRIBER_ID);
		edit.remove(PREF_KEY_SIM_SERIAL_NUMBER);
		edit.commit();
//		this.token = "";
//		this.uid = "";
		this.credential = new Credential("", "", "");
		isLogin = false;
	}

	private List<NameValuePair> createRequestParams() {
		return createRequestParams(null, null, true);
	}
	public List<NameValuePair> createRequestParams(String[] paramNames,
			String[] paramValues, boolean withToken) {
		return createRequestParams(paramNames, paramValues, withToken, null);
	}
	public List<NameValuePair> createRequestParams(String[] paramNames,
			String[] paramValues, boolean withToken, Integer appfilter) {
		return createRequestParams(paramNames, paramValues, withToken, appfilter, null, null);
	}
	public List<NameValuePair> createRequestParams(String[] paramNames,
			String[] paramValues, boolean withToken, Integer appfilter, String userId, String pwd) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("store", storeId));
		nvps.add(new BasicNameValuePair("aver", sdkVer));
		nvps.add(new BasicNameValuePair("arel", sdkRel));
		nvps.add(new BasicNameValuePair("cver", clientVer));
		nvps.add(new BasicNameValuePair("lang", Locale.getDefault().toString()));
		TimeZone tz = TimeZone.getDefault();
		nvps.add(new BasicNameValuePair("tzid", tz.getID()));
		nvps.add(new BasicNameValuePair("tzrawoffset", "" + tz.getRawOffset()));
		String time=""+System.currentTimeMillis();
		nvps.add(new BasicNameValuePair("time", time));
		String vstring=storeId+"|"+time;
		if(userId!=null&&pwd!=null){
        	vstring=storeId+"|"+time+"|"+userId+"|"+pwd;
        }else if(withToken&&this.credential.getToken()!=null&&this.credential.getToken().length()>0){
        	vstring=storeId+"|"+time+"|"+this.credential.getToken();
        }else if(deviceId!=null&&subscriberId!=null&&simSerialNumber!=null){
        	vstring=storeId+"|"+time+"|"+deviceId+"|"+subscriberId+"|"+simSerialNumber;
        }
    	L.d("vstring="+vstring);
        String vsign = "";
        try {
        	vsign = Dsa.sign(vstring,this.config.getApiPrivkey());
        } catch (Exception e) {
        	
        }
        nvps.add(new BasicNameValuePair("vsign", vsign));
		nvps.add(new BasicNameValuePair("imei", deviceId));
		nvps.add(new BasicNameValuePair("mac", macAddress));
		nvps.add(new BasicNameValuePair("imsi", subscriberId));
		nvps.add(new BasicNameValuePair("iccid", simSerialNumber));
		nvps.add(new BasicNameValuePair("dvc", Build.MODEL));
		nvps.add(new BasicNameValuePair("wpx", wpx));
		nvps.add(new BasicNameValuePair("hpx", hpx));
		if(appfilter==null){
			//使用預設值appFilter(全域變數)
			nvps.add(new BasicNameValuePair("appfilter", "" + appFilter));
		}else{
			//使用傳入參數appfilter
			nvps.add(new BasicNameValuePair("appfilter", "" + appfilter));
		}
		
		//加入渠道号码
		if(this.config.getSnum()!=null && this.config.getSnum().length()>0){
			nvps.add(new BasicNameValuePair("snum", this.config.getSnum()));
		}
		
		if (withToken&&this.credential.getToken()!=null&&this.credential.getToken().length()>0) {
			nvps.add(new BasicNameValuePair("token", this.credential.getToken()));
		}

		if (paramNames == null || paramValues == null) {
			return nvps;
		}

		if (paramNames.length != paramValues.length) {
			throw new IndexOutOfBoundsException(
					"paramNames.length != paramValues.length");
		}

		int count = paramNames.length;
		for (int i = 0; i < count; i++) {
			nvps.add(new BasicNameValuePair(paramNames[i], paramValues[i]));
		}

		return nvps;
	}
	/**
	 * Login(01): 登入
	 */
	public ApiInvoker<LoginRet> login(String userId, String password) {
		String[] paramNames = new String[] { "userId", "pwd" };
		String[] paramValues = new String[] { "" + userId, "" + password };
		String url = apiUrl.getLoginUrl();
		ApiDataParseHandler<LoginRet> handler = ApiDataParseHandler.LOGIN_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				false, null ,userId,password);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<LoginRet>(this.config,handler, url, nvps);
	}

	/**
	 * Register:註冊 
	 */
	public ApiInvoker<LoginRet> register(String userId, String password,String nickName,String signature) {
		String[] paramNames = new String[] { "userId", "pwd", "nickname", "signature" };
		String[] paramValues = new String[] { "" + userId, "" + password, "" + nickName, "" + signature};
		String url = apiUrl.getRegisterUrl();
		ApiDataParseHandler<LoginRet> handler = ApiDataParseHandler.LOGIN_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				false, null ,userId,password);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<LoginRet>(this.config,handler, url, nvps);
	}
	
	/**
	 * Auto Login(02): 自動登入
	 */
	public ApiInvoker<LoginRet> autoLogin() {
		String url = apiUrl.getAutoLoginUrl();
		ApiDataParseHandler<LoginRet> handler = ApiDataParseHandler.LOGIN_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams();
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<LoginRet>(this.config,handler, url, nvps);
	}

	/**
	 * IP Login(03): IP登入
	 */
	public ApiInvoker<IPLoginRet> IPLogin(String ip) {
		String[] paramNames = new String[] { "ip" };
		String[] paramValues = new String[] { "" + ip };
		String url = apiUrl.getIPLoginUrl();
		ApiDataParseHandler<IPLoginRet> handler = ApiDataParseHandler.IP_LOGIN_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				false);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<IPLoginRet>(this.config,handler, url, nvps);
	}

	/**
	 * Feature Applications(04): Feature Applications
	 */
	public ApiInvoker<AppsRet> featureApps(Integer appfilter) {
		String url = apiUrl.getFeatureAppsUrl();
		ApiDataParseHandler<AppsRet> handler = ApiDataParseHandler.APPS_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(null, null, true, appfilter);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<AppsRet>(this.config,handler, url, nvps);
	}

	/**
	 * Application Category List(05): 應用程式類別清單
	 */
	public ApiInvoker<CategoriesRet> categories(String categoryId) {
		String url = apiUrl.getCategoryUrl();
		ApiDataParseHandler<CategoriesRet> handler = ApiDataParseHandler.CATEGORIES_PARSE_HANDLER;
		String[] paramNames = null;
		String[] paramValues = null;
		if (categoryId != null) {
			paramNames = new String[] { "pid" };
			paramValues = new String[] {categoryId };
		}
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues, true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CategoriesRet>(this.config,handler, url, nvps);
	}

	/**
	 * Applications of Category(06): 某一類別的應用程式清單
	 */
	public ApiInvoker<CategoryAppsRet> categoryApps(String categoryId,
			AppsType appsType, Integer appfilter, int page, Integer pSize) {
		String url = apiUrl.getCategoryAppsUrl(categoryId, appsType, page);
		ApiDataParseHandler<CategoryAppsRet> handler = ApiDataParseHandler.CATEGORY_APPS_RET_PARSE_HANDLER;
		String[] paramNames = null;
		String[] paramValues = null;
		if (pSize != null) {
			paramNames = new String[] { "psize" };
			paramValues = new String[] { pSize.toString() };
		}
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues, true, appfilter);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CategoryAppsRet>(this.config,handler, url, nvps);
	}

	/**
	 * Application Detail(07): 應用程式詳細內容
	 */
	public ApiInvoker<ApplicationRet> application(String packageName) {
		String url = apiUrl.getApplicationDetailUrl(packageName);
		ApiDataParseHandler<ApplicationRet> handler = ApiDataParseHandler.APPLICATION_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams();
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<ApplicationRet>(this.config,handler, url, nvps);
	}

	/**
	 * Comments of Application(08): 應用程式評論清單
	 */
	public ApiInvoker<CommentsRet> appComments(String packageName, int page, Integer pSize) {
		String url = apiUrl.getApplicationCommentsUrl(packageName, page);
		ApiDataParseHandler<CommentsRet> handler = ApiDataParseHandler.COMMENTS_RET_PARSE_HANDLER;
		String[] paramNames = null;
		String[] paramValues = null;
		if (pSize != null) {
			paramNames = new String[] { "psize" };
			paramValues = new String[] { pSize.toString() };
		}
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues, true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CommentsRet>(this.config,handler, url, nvps);
	}

	/**
	 * Report Application(09): 檢舉Application
	 */
	public ApiInvoker<CommonRet> reportApp(String packageName,
			Integer appVersion, int reportId) {
		String[] paramNames;
		String[] paramValues;
		if (appVersion != null) {
			paramNames = new String[] { "lappv", "reportId" };
			paramValues = new String[] { String.valueOf(appVersion),
					String.valueOf(reportId) };
		} else {
			paramNames = new String[] { "reportId" };
			paramValues = new String[] { String.valueOf(reportId) };
		}
		String url = apiUrl.getApplicationReportUrl(packageName);
		ApiDataParseHandler<CommonRet> handler = ApiDataParseHandler.COMMON_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CommonRet>(this.config,handler, url, nvps);
	}

	/**
	 * Rate Application(10): 評論應用程式
	 */
	public ApiInvoker<CommonRet> rateApp(String packageName,
			Integer appVersion, int rating, String comment) {
		String[] paramNames;
		String[] paramValues;
		if (appVersion != null) {
			paramNames = new String[] { "lappv", "rating", "comment" };
			paramValues = new String[] { String.valueOf(appVersion),
					String.valueOf(rating), "" + comment };
		} else {
			paramNames = new String[] { "rating", "comment" };
			paramValues = new String[] { String.valueOf(rating), "" + comment };
		}
		String url = apiUrl.getApplicationRateUrl(packageName);
		ApiDataParseHandler<CommonRet> handler = ApiDataParseHandler.COMMON_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CommonRet>(this.config,handler, url, nvps);
	}

	/**
	 * Search Apps(11): 搜尋某一類別的應用程式
	 */
	public ApiInvoker<AppsRet> searchApps(String query, AppsType appType, Integer appfilter, int page, Integer pSize) {
		String[] paramNames = null;
		String[] paramValues = null;
		if (pSize != null) {
			paramNames = new String[] { "query", "psize" };
			paramValues = new String[] { "" + query, pSize.toString() };
		}else{
			paramNames = new String[] { "query" };
			paramValues = new String[] { "" + query };
		}
		
		String url = apiUrl.getSearchAppsUrl(appType, page);
		ApiDataParseHandler<AppsRet> handler = ApiDataParseHandler.APPS_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues, true, appfilter);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<AppsRet>(this.config,handler, url, nvps);
	}

	/**
	 * CP’s Apps(12): 開發者某一類別的應用程式
	 */
	public ApiInvoker<CPAppsRet> cpApps(String cpId, AppsType appsType, Integer appfilter, int page, Integer pSize) {
		String url = apiUrl.getCPAppsUrl(cpId, appsType, page);
		ApiDataParseHandler<CPAppsRet> handler = ApiDataParseHandler.CP_APPS_RET_PARSE_HANDLER;
		String[] paramNames = null;
		String[] paramValues = null;
		if (pSize != null) {
			paramNames = new String[] { "psize" };
			paramValues = new String[] { pSize.toString() };
		}
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues, true, appfilter);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CPAppsRet>(this.config,handler, url, nvps);
	}

	/**
	 * My Download ( Free, Paid, All )(13): 我的下載App清單 免費：Free, InApp購買, InApp購物車
	 * 付費：計次, 月租, InApp購買
	 */
	public ApiInvoker<AppsRet> myDownloadApps(AppsType appsType, int page) {
		String url = apiUrl.getMyDownloadAppsUrl(appsType, page);
		ApiDataParseHandler<AppsRet> handler = ApiDataParseHandler.APPS_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams();
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<AppsRet>(this.config,handler, url, nvps);
	}

	/**
	 * Send Client Usage Log (14): Client 被開啟的紀錄
	 */
	public ApiInvoker<CommonRet> clientUsage(String l_uid, String l_userId,
			String l_iccid, String l_imei, String l_cver, long l_time,
			String l_dvc, String l_imsi, String l_msisdn, String l_isFirstTime) {
		String[] paramNames = new String[] { "l_uid", "l_userId", "l_iccid",
				"l_imei", "l_cver", "l_time", "l_dvc", "l_imsi", "l_msisdn" , "l_isFirstTime"};
		String[] paramValues = new String[] { l_uid, l_userId, l_iccid, l_imei,
				l_cver, String.valueOf(l_time), l_dvc, l_imsi, l_msisdn, l_isFirstTime };
		String url = apiUrl.getClientUsageUrl();
		ApiDataParseHandler<CommonRet> handler = ApiDataParseHandler.COMMON_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CommonRet>(this.config,handler, url, nvps);
	}

	/**
	 * Is Over Payment Quota?(15): 當月付款總金額是否超過
	 */
	public ApiInvoker<OverPaymentQuotaRet> overPaymentQuota(String packageName) {
		String url = apiUrl.getOverPaymentQuotaUrl(packageName);
		ApiDataParseHandler<OverPaymentQuotaRet> handler = ApiDataParseHandler.OVER_PAYMENT_QUOTA_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams();
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<OverPaymentQuotaRet>(this.config,handler, url, nvps);
	}

	/**
	 * Check Pay Status(16): 檢查付款狀態
	 */
	public ApiInvoker<CheckPayStatusRet> checkPayStatus(String packageName,
			Integer lappv) {
		String url = apiUrl.getCheckPayStatusUrl(packageName);
		ApiDataParseHandler<CheckPayStatusRet> handler = ApiDataParseHandler.CHECK_PAY_STATUS_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams();
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CheckPayStatusRet>(this.config,handler, url, nvps);
	}

	/**
	 * Unsubscribe(17): 取消訂閱
	 */
	public ApiInvoker<UnsubscribeRet> unsubscribe(String packageName) {
		String url = apiUrl.getUnsubscribeUrl(packageName);
		ApiDataParseHandler<UnsubscribeRet> handler = ApiDataParseHandler.UNSUBSCRIBE_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams();
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<UnsubscribeRet>(this.config,handler, url, nvps);
	}

	/**
	 * Check New Client(18): 檢查Client有沒有新版本
	 */
	public ApiInvoker<CheckClientVersionRet> checkClientVersion(String msisdn) {
		String url = apiUrl.getCheckNewClientUrl();
		ApiDataParseHandler<CheckClientVersionRet> handler = ApiDataParseHandler.CHECK_CLIENT_VERSION_HANDLER;
		String[] paramNames = new String[] { "msisdn" };
		if (msisdn == null)
			msisdn = "";
		String[] paramValues = new String[] { msisdn };
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		ApiInvoker<CheckClientVersionRet> apiInvoker = new ApiInvoker<CheckClientVersionRet>(
				this.config,handler, url, nvps);
		return apiInvoker;
	}

	/**
	 * Download Client(19): 下載 Client
	 */
	public HttpRequestBase downloadClient(String dlToken)
			throws UnsupportedEncodingException {
		String url = apiUrl.getDownloadClientUrl();
		List<NameValuePair> nvps;
		if (dlToken != null) {
			String[] paramNames = new String[] { "dltoken" };
			String[] paramValues = new String[] { dlToken };
			nvps = createRequestParams(paramNames, paramValues, true);
		} else {
			nvps = createRequestParams();
		}
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		return post;
	}

	/**
	 * Check My Apps Version(20): 檢查我下載的App版本
	 */
	public ApiInvoker<AppVersionsRet> myAppVersions(String msisdn, int page) {
		String url = apiUrl.getMyAppsVersionUrl(page);
		ApiDataParseHandler<AppVersionsRet> handler = ApiDataParseHandler.MY_APP_VERSIONS_PARSE_HANDLER;
		String[] paramNames = new String[] { "msisdn" };
		if (msisdn == null)
			msisdn = "";
		String[] paramValues = new String[] { msisdn };
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<AppVersionsRet>(this.config,handler, url, nvps);
	}

	/**
	 * Download App(21): 下載 App
	 */
	public HttpRequestBase downloadApp(String packageName, String categoryId,
			Integer lappv, String vlogId) throws UnsupportedEncodingException {
		if (config.getServerType() == Configuration.RUNTIME_SERVER_TYPE.STAGING) {
			if (LangUtils.isBlank(categoryId) || "0".equals(categoryId))
				throw new RuntimeException("categoryId invalid: " + categoryId);
		}
		String url = apiUrl.getDownloadAppUrl(packageName);
		List<NameValuePair> nvps;
		String[] paramNames;
		String[] paramValues;
		if (lappv != null) {
			if (LangUtils.isBlank(vlogId)) {
				paramNames = new String[] { "categoryId", "lappv" };
				paramValues = new String[] { categoryId, lappv.toString() };
			} else {
				paramNames = new String[] { "categoryId", "lappv", "vlogId" };
				paramValues = new String[] { categoryId, lappv.toString(),
						vlogId };
			}
		} else {
			if (LangUtils.isBlank(vlogId)) {
				paramNames = new String[] { "categoryId" };
				paramValues = new String[] { categoryId };
			} else {
				paramNames = new String[] { "categoryId", "vlogId" };
				paramValues = new String[] { categoryId, vlogId };
			}
		}

		nvps = createRequestParams(paramNames, paramValues, true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		return post;
	}

	/**
	 * 呼叫舊有的 API 13: 取得授權
	 */
	public ApiInvoker<LicenseInfoRet> getLicense(String packageName, Integer lappv) {
		String[] paramNames = new String[] { "ver" };
		String[] paramValues = new String[] { "" + lappv };
		String url = apiUrl.getLicenseUrl(packageName);
		ApiDataParseHandler<LicenseInfoRet> handler = ApiDataParseHandler.LICENSE_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<LicenseInfoRet>(this.config, handler, url, nvps);
	}
	
	public ApiInvoker<OrderRet> getNewOrder(String packageName,String storeOid,String payeeInfo){
		return getNewOrder(packageName, storeOid, payeeInfo, null);
	}
	public ApiInvoker<OrderRet> getNewOrder(String packageName,String storeOid,String payeeInfo, UPayConfig uPayConfig){
		String[] paramNames;
		String[] paramValues;
		if(uPayConfig==null){
			paramNames = new String[] {  "storeOid", "payeeInfo" };
			paramValues = new String[] { storeOid, payeeInfo };
		}else{
			paramNames = new String[] {  "storeOid", "payeeInfo", "isTest", "checkSignUrl", "merchantId", "merchantName", 
					"merchantPublicCer", "privateKeyFileName", "privateKeyAlias", "privateKeyPassword", "transTimeout", "backEndUrl"};
			paramValues = new String[] { storeOid, payeeInfo, uPayConfig.getIsTest(), uPayConfig.getCheckSignUrl(), uPayConfig.getMerchantId(), 
					uPayConfig.getMerchantName(), uPayConfig.getMerchantPublicCer(), uPayConfig.getPrivateKeyFileName(),
					uPayConfig.getPrivateKeyAlias(), uPayConfig.getPrivateKeyPassword(), uPayConfig.getTransTimeout(), uPayConfig.getBackEndUrl()};
		}
		
		String url = apiUrl.getPaymentOrderNumtUrl(packageName);
		ApiDataParseHandler<OrderRet> handler = ApiDataParseHandler.G_PAYMENT_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<OrderRet>(this.config,handler, url, nvps);
		
	}
	/**
	 * 獲取app上架的store清單(26)
	 */
	public ApiInvoker<CheckAppStoreRet> checkAppStoreList(String pkg, int ver) {
		String[] paramNames = new String[] {  "ver" };
		String[] paramValues = new String[] { ""+ver };
		String url = apiUrl.getCheckAppStoreListUrl(pkg);
		ApiDataParseHandler<CheckAppStoreRet> handler = ApiDataParseHandler.APP_STORE_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				false);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CheckAppStoreRet>(this.config,handler, url, nvps);
	}
	/**
	 * 獲取app付款狀態for app sdk(27)
	 */
	public ApiInvoker<SdkAppInfoRet> getSdkAppInfo(String pkg, int ver, String dlog, String first) {
		String[] paramNames = new String[] {  "ver","dlog","first" };
		String[] paramValues = new String[] {""+ver,dlog,""+first };
		String url = apiUrl.getSdkAppInfoUrl(pkg);
		ApiDataParseHandler<SdkAppInfoRet> handler = ApiDataParseHandler.SDK_APP_INFO_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<SdkAppInfoRet>(this.config,handler, url, nvps);
	}
	/*
	 * 發送client端的log給server
	 */
	public ApiInvoker<CommonRet> sendLogs(List<? extends LogMessage> logs){
		StringBuilder sb = new StringBuilder("<log>");
		for (int i = 0; i < logs.size(); i++) {
			sb.append(logs.get(i).toXML());
		}
		sb.append("</log>");
		String[] paramNames = new String[] {  "log"};
		String[] paramValues = new String[] {sb.toString() };
		String url = apiUrl.getSendLogUrl();
		ApiDataParseHandler<CommonRet> handler = ApiDataParseHandler.COMMON_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,
				true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CommonRet>(this.config,handler, url, nvps);
	}
	/*
	 * 获取订单信息(44)
	 */
	public ApiInvoker<OrderRet> getOrderStatus(String pkg){
		String url = apiUrl.getOrderStatusUrl(pkg);
		ApiDataParseHandler<OrderRet> handler = ApiDataParseHandler.G_PAYMENT_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(null, null,true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<OrderRet>(this.config,handler, url, nvps);
	}
	/*
	 * 订单取消退费(45)
	 */
	public ApiInvoker<CommonRet> orderRefund(String orderNo,String reason){
		String[] paramNames = new String[] {  "orderNo","reason"};
		String[] paramValues = new String[] {orderNo,reason};
		String url = apiUrl.getOrderRefundUrl();
		ApiDataParseHandler<CommonRet> handler = ApiDataParseHandler.COMMON_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(paramNames, paramValues,true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<CommonRet>(this.config,handler, url, nvps);
	}
	
	/*
	 * 取得PePay付款URL參數資訊(46)
	 */
	public ApiInvoker<PePayOrderRet> getPePayOrderUrl(String pkg) {
		String url = apiUrl.getPePayOrderUrl(pkg);
		ApiDataParseHandler<PePayOrderRet> handler = ApiDataParseHandler.PEPAYORDER_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = createRequestParams(null, null,true);
		if (Logger.DEBUG) {
			L.d(url + "?" + URLEncodedUtils.format(nvps, "UTF-8"));
		}
		return new ApiInvoker<PePayOrderRet>(this.config, handler, url, nvps);
	}
	/**
	 * 是否已登入
	 * @return
	 */
	public static boolean isLogin() {
		return isLogin;
	}
}