package cn.com.vapk.vstore.client;

import java.io.Serializable;

import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.ApplicationRet.Application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import cn.com.vapk.vstore.client.receiver.listener.LoginEvent;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.R;

public class ActionController {

    private static final Logger L = Logger.getLogger(ActionController.class);

    static final String ACTION_CLOSE_SMART = "cn.com.vapk.vstore.client.action.CLOSE_SMART";
    static final String ACTION_LOGOUT = "cn.com.vapk.vstore.client.action.LOGOUT";
    static final String ACTION_CHANGE_APP_FILTER = "cn.com.vapk.vstore.client.action.CHANGE_APP_FILTER";

    final static String PERMISSION_CLOSE_CLIENT_RECEIVER = "cn.com.vapk.vstore.client.permission.CLOSE_CLIENT";
    public final static String LOGIN_EVENT_SUCCESS = "cn.com.vapk.vstore.client.action.LOGIN_EVENT_SUCCESS";
    public final static String PERMISSION_LOGIN_EVENT_SUCCESS = "cn.com.vapk.vstore.client.permission.LOGIN_EVENT_SUCCESS";

    public static final String EXTRA_CATEGORY_ID = "category_id";
    static final String EXTRA_CATEGORY_TITLE = "category_title";
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    static final String EXTRA_CONTENT_PROVIDER_ID = "content_provider_id";
    static final String EXTRA_APP_ICON = "app_icon";
    static final String EXTRA_SCREEN_SHOTS = "screen_shots";
    static final String EXTRA_SCREEN_SHOT_POSITION = "screen_shot_position";
    static final String EXTRA_APP_TITLE_INFO = "app_title_info";
    static final String EXTRA_MY_RATING_INFO = "my_rating_info";
    static final String EXTRA_SEARCH_QUERY = "search_query";
    static final String EXTRA_SETTING_INFO = "setting_info";
    static final String EXTRA_FORWARD_INTENT = "forward_intent";

    static boolean loginFlag;
    
    
    public static boolean isLoginNeededForVtion(Activity activity){
    
    	L.d("In isLoginNeededForVtion:"+activity.getClass().getName() );
    	
    	if(activity.getClass().getName().contains("MyDownloadActivity")){    		
    		return true;
    	}else{    		
    		if(activity instanceof  AppDetailActivity ){
    			AppDetailActivity act = (AppDetailActivity)activity;
    			if(act.needLogin){    				
    				return true;
    			}    			
    		}
    		
    		return false;
    	}
    	
    }
    
    final static boolean isLogin() {
        return loginFlag;
    }

    final static void closeSMart(Activity aty) {
        if (Logger.DEBUG)
            L.d("closeSMart");
        aty.sendBroadcast(new Intent(ACTION_CLOSE_SMART),
                PERMISSION_CLOSE_CLIENT_RECEIVER);
    }

    final static void index(Activity aty) {
        if (Logger.DEBUG)
            L.d("index");
        if (eula(aty) ) {
            return;
        }
        
        // Initialize Check Client Version
        cn.com.vapk.vstore.client.update.ActionController
                .checkClientVersion(aty);
        if (!loginFlag && isLoginNeededForVtion(aty)) {
            login(aty);
            return;
        }
        if (isLoginNeededForVtion(aty) && eula(aty) ) {
            return;
        }
        
        
        cn.com.vapk.vstore.client.usage.ActionController
                .broadcastClientUsage(aty);      
        cn.com.vapk.vstore.client.update.ActionController.checkAppsVersion(aty);       
        featureApps(aty);
        L.d("Done Eula 3");        
    }

    final static void login(Activity aty) {
        if (Logger.DEBUG)
            L.d("login");
        if (loginFlag && Logger.DEBUG)
            throw new RuntimeException("Login Again.");

//        if (ActionSession.isIPLoginEnable(aty)) {
//            ipLogin(aty);
//            return;
//        }

        if (ActionSession.isAutoLoginEnable(aty)) {
            autoLogin(aty);
            return;
        }

        formLogin(aty);
    }

    final static void ipLogin(Activity aty) {
        if (Logger.DEBUG)
            L.d("ipLogin");
        Intent intent = new Intent(aty, IPLoginActivity.class);
        passForwardIntent(aty, intent);
        aty.startActivity(intent);
    }

    final static void ipLoginFail(Activity aty) {
        if (Logger.DEBUG)
            L.d("ipLoginFail");
        formLogin(aty);
    }

    final static void autoLogin(Activity aty) {
        if (Logger.DEBUG)
            L.d("autoLogin");
        Intent intent = new Intent(aty, AutoLoginActivity.class);
        passForwardIntent(aty, intent);
        passLoginEvent(aty,intent);
        aty.startActivity(intent);
    }

    final static void autoLoginFail(Activity aty) {
        if (Logger.DEBUG)
            L.d("autoLoginFail");
        ActionSession.disableAutoLogin(aty);
        formLogin(aty);
    }

    final static void formLogin(Activity aty) {
        if (Logger.DEBUG)
            L.d("formLogin");
        Intent intent = new Intent(aty, LoginActivity.class);
        passForwardIntent(aty, intent);
        passLoginEvent(aty,intent);
        aty.startActivity(intent);
    }

    final static void logout(Activity aty) {
        if (Logger.DEBUG)
            L.d("logout");
        loginFlag = false;
        ActionSession.disableAutoLogin(aty);
        AndroidApiService.getInstance(aty,ConfigurationFactory.getInstance()).cleanCredential(aty);
        Intent intent = Intents.featureApps(aty);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        aty.startActivity(intent);
        Toast.makeText(aty, aty.getResources().getString(R.string.logout_successed), Toast.LENGTH_SHORT).show();
    }

    public final static void loginSuccess(boolean autoLogin,
            boolean appFilterSettingEnable, Context ctx) {
        if (Logger.DEBUG)
            L.d("loginSuccess");
        ActionSession.disableIPLogin(ctx);
        if (autoLogin)
            ActionSession.enableAutoLogin(ctx);
        else
            ActionSession.disableAutoLogin(ctx);

        if (appFilterSettingEnable)
            ActionSession.enableAppFilterSetting(ctx);
        else
            ActionSession.disableAppFilterSetting(ctx);

        loginFlag = true;
    }
    
    final static void passLoginEvent(Activity aty,Intent intent){
    	String event=aty.getIntent().getStringExtra(LoginEvent.class.getName());
    	L.d("get login event :"+event);
    	if(event!=null){
    		aty.getIntent().removeExtra(LoginEvent.class.getName());
    		intent.putExtra(LoginEvent.class.getName(), event);
    	}    	
    }

    final static void loginSuccess(Activity aty, boolean autoLogin,
            boolean appFilterSettingEnable) {
        if (Logger.DEBUG)
            L.d("loginSuccess");
        cn.com.vapk.vstore.client.update.ActionController.checkAppsVersion(aty);
//        closeSMart(aty);
        loginSuccess(autoLogin, appFilterSettingEnable, aty);
        if (isLoginNeededForVtion(aty) && eula(aty)) {
            return;
        }else{
        	Intent levt=new Intent(LOGIN_EVENT_SUCCESS);
        	passLoginEvent(aty,levt);
        	aty.sendBroadcast(levt,
        			PERMISSION_LOGIN_EVENT_SUCCESS);
        	L.d("send login event to receiver");
        }
        cn.com.vapk.vstore.client.usage.ActionController
                .broadcastClientUsage(aty);
        if (forward(aty))
            return;
//        featureApps(aty);
    }

    final static boolean eula(Activity aty) {
        if (Logger.DEBUG)
            L.d("eula");

//        String userId = ApiService.getInstance(aty).getUserId();
        String userId ="root";     
        
        if (LangUtils.isBlank(userId)) {
            if (Logger.WARN)
                L.w("eula userId is blank.");
            if (Logger.DEBUG)
                throw new RuntimeException("agreeEula userId is blank.");
            return false;
        }
        if (ActionSession.isAgreeEula(aty, userId)) {
            return false;
        }

        Intent intent = new Intent(aty, EulaActivity.class);
        passForwardIntent(aty, intent);
        aty.startActivity(intent);
        return true;
    }

    final static void agreeEula(Activity aty) {
        if (Logger.DEBUG)
            L.d("agreeEula");
        cn.com.vapk.vstore.client.usage.ActionController
                .broadcastClientUsage(aty);
//        String userId = ApiService.getInstance(aty).getUserId();
        String userId = "root";
        if (LangUtils.isBlank(userId)) {
            if (Logger.WARN)
                L.w("agreeEula userId is blank.");
            if (Logger.DEBUG)
                throw new RuntimeException("agreeEula userId is blank.");
            return;
        }
        ActionSession.agreeEula(aty, userId);
        closeSMart(aty);
        if (forward(aty))
            return;
        featureApps(aty);
    }

    final static void featureApps(Activity aty) {
        if (Logger.DEBUG)
            L.d("featureApps, TaskId: " + aty.getTaskId());
        Intent intent = Intents.featureApps(aty);
        if(isLogin() == FeatureAppsActivity.isLoginFlag()) {
        	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        } else {
        	FeatureAppsActivity.setLoginFlag(isLogin());
        }
        aty.startActivity(intent);
    }

    final static void categories(Activity aty) {
        if (Logger.DEBUG)
            L.d("categories");
        Intent intent = new Intent(aty, CategoriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        aty.startActivity(intent);
    }

    final static void categoryApps(Activity aty, String categoryId,
            String categoryTitle) {
        if (Logger.DEBUG)
            L.d("categoryApps");
        Intent intent = new Intent(aty, CategoryAppsActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(EXTRA_CATEGORY_TITLE, categoryTitle);
        aty.startActivity(intent);
    }

    final static void myDownload(Activity aty) {
        if (Logger.DEBUG)
            L.d("myDownload");
        aty.startActivity(Intents.myDownload(aty));
    }

    final static void search(Activity aty) {
        if (Logger.DEBUG)
            L.d("search");
        Intent intent = new Intent(aty, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        aty.startActivity(intent);
    }

    final static void searchResult(Activity aty, String query) {
        if (Logger.DEBUG)
            L.d("searchResult");
        aty.startActivity(Intents.searchResult(aty, query));
    }

    final static void appDetail(Activity aty, String pkg, String categoryId) {
        if (Logger.DEBUG)
            L.d("appDetail");
        aty.startActivity(Intents.addDetail(aty, categoryId, pkg));
    }

    final static void appComments(Activity aty, AppTitleInfo ati) {
        if (Logger.DEBUG)
            L.d("appComments");
        Intent intent = new Intent(aty, AppCommentsActivity.class);
        intent.putExtra(EXTRA_APP_TITLE_INFO, ati);
        aty.startActivity(intent);
    }

    final static void reportApp(Activity aty, AppTitleInfo ati) {
        if (Logger.DEBUG)
            L.d("reportApp");
        Intent intent = new Intent(aty, ReportAppActivity.class);
        intent.putExtra(EXTRA_APP_TITLE_INFO, ati);
        aty.startActivity(intent);
    }

    final static void uninstallReasonApp(Activity aty, AppTitleInfo ati) {
        if (Logger.DEBUG)
            L.d("uninstallReasonApp");
        Intent intent = new Intent(aty, UninstallReasonActivity.class);
        intent.putExtra(EXTRA_APP_TITLE_INFO, ati);
        aty.startActivity(intent);
    }

    final static void rateApp(Activity aty, MyRatingInfo mri) {
        if (Logger.DEBUG)
            L.d("rateApp");
        Intent intent = new Intent(aty, RateAppActivity.class);
        intent.putExtra(EXTRA_MY_RATING_INFO, mri);
        aty.startActivity(intent);
    }

    final static void cpApps(Activity aty, String cpId, String icon) {
        if (Logger.DEBUG)
            L.d("cpApps");
        Intent intent = new Intent(aty, CPAppsActivity.class);
        intent.putExtra(EXTRA_CONTENT_PROVIDER_ID, cpId);
        if (!LangUtils.isBlank(icon)) {
            intent.putExtra(EXTRA_APP_ICON, icon);
        }
        aty.startActivity(intent);
    }

    final static void screenShot(Activity aty, String[] screenShots,
            int screenShotPosition) {
        if (Logger.DEBUG)
            L.d("screenShot");
        Intent intent = new Intent(aty, ScreenShotActivity.class);
        intent.putExtra(EXTRA_SCREEN_SHOTS, screenShots);
        intent.putExtra(EXTRA_SCREEN_SHOT_POSITION, screenShotPosition);
        aty.startActivity(intent);
    }

    final static void setting(Activity aty) {
        if (Logger.DEBUG)
            L.d("setting");
        Intent intent = new Intent(aty, SettingActivity.class);
        boolean afse = ActionSession.isAppFilterSettingEnable(aty);
        int appFilter = AndroidApiService.getInstance(aty,ConfigurationFactory.getInstance()).getAppFilter();
        SettingInfo settingInfo = new SettingInfo(afse, appFilter);
        intent.putExtra(EXTRA_SETTING_INFO, settingInfo);
        aty.startActivity(intent);
    }

    final static void changeAppFilter(Activity aty, int filter) {
        if (Logger.DEBUG)
            L.d("filterChange");
        AndroidApiService.getInstance(aty,ConfigurationFactory.getInstance()).saveAppFilter(aty, filter);
        closeSMart(aty);
        Intent forwardIntent = Intents.featureApps(aty);
        forward(aty, forwardIntent);
    }

    static final boolean sendMail(Activity aty, String mail) {
        if (LangUtils.isBlank(mail))
            return false;
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mail });
            intent.setType("plain/text");
            aty.startActivity(intent);
            return true;
        } catch (Exception e) {
            if (Logger.ERROR)
                L.e("sendMail fail.", e);
            if (Logger.DEBUG)
                throw new RuntimeException("sendMail fail.", e);
            return false;
        }
    }

    static final boolean openWeb(Activity aty, String webAddress) {
        if (LangUtils.isBlank(webAddress))
            return false;
        try {
            Uri uri = Uri.parse(webAddress);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // intent.addCategory(Intent.CATEGORY_BROWSABLE);
            aty.startActivity(intent);
            return true;
        } catch (Exception e) {
            if (Logger.ERROR)
                L.e("openWeb fail.", e);
            if (Logger.DEBUG)
                throw new RuntimeException("openWeb fail.", e);
            return false;
        }
    }

    static final boolean launchApp(Activity aty, String pkg) {
        if (LangUtils.isBlank(pkg))
            return false;
        try {
            PackageManager pm = aty.getPackageManager();
            Intent mLaunchIntent = pm.getLaunchIntentForPackage(pkg);
            if (mLaunchIntent == null) {
                return false;
            }
            mLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            aty.startActivity(mLaunchIntent);
            return true;
        } catch (Exception e) {
            if (Logger.ERROR)
                L.e("launchApp fail at pkg: " + pkg, e);
            return false;
        }
    }

    public static final void payment(Activity aty, String pkg) {
    
        cn.com.vapk.vstore.client.payment.ActionController.payment(aty, pkg);
    }
    
    public static final void payment(Activity aty, String pkg, Bundle bundle) {
    	if("true".equals(bundle.get("pepay"))) {
    		cn.com.vapk.vstore.client.payment.ActionController.pePayment(aty, pkg, bundle);
    	} else {
    		cn.com.vapk.vstore.client.payment.ActionController.payment(aty, pkg, bundle);
    	}
    }

    static final boolean smartUri(Activity aty, Uri uri) {
        if (uri == null || !"smart".equals(uri.getScheme())) {
            return false;
        }

        String host = uri.getHost();
        if (Logger.DEBUG)
            L.d("Smart Uri Host: " + host);

        if ("details".equals(host)) {
            String id = uri.getQueryParameter("id");
            if (Logger.DEBUG)
                L.d("Smart Uri details id: " + id);
            if (LangUtils.isBlank(id)) {
                return false;
            }
            final String categoryId = "-3";
            forward(aty, Intents.addDetail(aty, categoryId, id.trim()));
            return true;
        }

        if ("search".equals(host)) {
            String q = uri.getQueryParameter("q");
            if (Logger.DEBUG)
                L.d("Smart Uri search q: " + q);
            if (LangUtils.isBlank(q)) {
                return false;
            }
            forward(aty, Intents.searchResult(aty, q.trim()));
            return true;
        }

        if ("category".equals(host)) {
            String categoryId = uri.getQueryParameter("categoryId");
            if (Logger.DEBUG)
                L.d("Smart Uri category id: " + categoryId);
            if (LangUtils.isBlank(categoryId)) {
                return false;
            }
            forward(aty, Intents.categoryApps(aty, categoryId.trim()));
            return true;
        }

        return false;
    }

    static final boolean httpUri(Activity aty, Uri uri) {
        if (uri == null || !"http".equals(uri.getScheme())) {
            return false;
        }

        String host = uri.getHost();
        if (Logger.DEBUG)
            L.d("Http Uri Host: " + host);
        if (!ConfigurationFactory.getInstance().getApiHostname().equals(host)) {
            return false;
        }

        String path = uri.getPath();
        if (Logger.DEBUG)
            L.d("Http Uri Path: " + path);
        if ("/details".equals(path)) {
            String id = uri.getQueryParameter("id");
            if (Logger.DEBUG)
                L.d("Http Uri details id: " + id);
            if (LangUtils.isBlank(id)) {
                return false;
            }
            final String categoryId = "-3";
            forward(aty, Intents.addDetail(aty, categoryId, id.trim()));
            return true;
        }

        if ("/search".equals(path)) {
            String q = uri.getQueryParameter("q");
            if (Logger.DEBUG)
                L.d("Http Uri search q: " + q);
            if (LangUtils.isBlank(q)) {
                return false;
            }
            forward(aty, Intents.searchResult(aty, q));
            return true;
        }

        if ("/category".equals(path)) {
            String categoryId = uri.getQueryParameter("categoryId");
            if (Logger.DEBUG)
                L.d("Http Uri category id: " + categoryId);
            if (LangUtils.isBlank(categoryId)) {
                return false;
            }
            forward(aty, Intents.categoryApps(aty, categoryId.trim()));
            return true;
        }

        return false;
    }

    private static final boolean forward(Activity aty) {
        Intent atyIntent = aty.getIntent();
        
        Intent forwardIntent = atyIntent
                .getParcelableExtra(EXTRA_FORWARD_INTENT);
        if (forwardIntent == null)
            return false;

        if (Logger.DEBUG)
            L.d("forward to " + forwardIntent);
        atyIntent.removeExtra(EXTRA_FORWARD_INTENT);
        aty.startActivity(forwardIntent);
        return true;
    }

    static final void forward(Activity aty, Intent forwardIntent) {
        if (Logger.DEBUG)
            L.d("forward");
        cn.com.vapk.vstore.client.update.ActionController
                .checkClientVersion(aty);
        aty.getIntent().putExtra(EXTRA_FORWARD_INTENT, forwardIntent);

        if (!loginFlag && isLoginNeededForVtion(aty)) {
            login(aty);
            return;
        }

        if (isLoginNeededForVtion(aty) && eula(aty))
            return;

        forward(aty);
    }

    private static final void passForwardIntent(Activity aty,
            Intent targetIntent) {
        Intent atyIntent = aty.getIntent();
        Intent forwardIntent = atyIntent
                .getParcelableExtra(EXTRA_FORWARD_INTENT);
        if (forwardIntent != null) {
            if (Logger.DEBUG)
                L.d("pass Forward Intent to " + targetIntent);
            atyIntent.removeExtra(EXTRA_FORWARD_INTENT);
            targetIntent.putExtra(EXTRA_FORWARD_INTENT, forwardIntent);
        }
    }

    /* intents */
    public final static class Intents {
        final static Intent featureApps(Context ctx) {
            Intent intent = new Intent(ctx, FeatureAppsActivity.class);
            return intent;
        }

        public final static Intent addDetail(Context ctx, String categoryId,
                String pkg) {
            Intent intent = new Intent(ctx, AppDetailActivity.class);
            // if (Build.VERSION.SDK.compareTo("3") > 0)
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
            intent.putExtra(EXTRA_PACKAGE_NAME, pkg);
            return intent;
        }

        final static Intent categoryApps(Context ctx, String categoryId) {
            Intent intent = new Intent(ctx, CategoryAppsActivity.class);
            intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
            return intent;
        }

        final static Intent searchResult(Context ctx, String query) {
            Intent intent = new Intent(ctx, SearchResultActivity.class);
            intent.putExtra(EXTRA_SEARCH_QUERY, query);
            return intent;
        }

        final static Intent myDownload(Context ctx) {
            Intent intent = new Intent(ctx, MyDownloadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            return intent;
        }
    }

    /* info */

    static class AppTitleInfo implements Serializable {
        private static final long serialVersionUID = -9196931376891449098L;
        String icon;
        String pkg;
        String title;
        String provider;
        float rating;

        public AppTitleInfo(String icon, String pkg, String title,
                String provider, float rating) {
            super();
            this.icon = icon;
            this.pkg = pkg;
            this.title = title;
            this.provider = provider;
            this.rating = rating;
            if (Logger.DEBUG
                    && LangUtils.isBlankAny(icon, pkg, title, provider) > -1) {
                throw new RuntimeException(
                        "Any is blank: icon, pkg, title, provider at: "
                                + (LangUtils.isBlankAny(icon, pkg, title,
                                        provider)));
            }
        }

        AppTitleInfo(Application app) {
            this(app.getIcon(), app.getPkg(), app.getTitle(),
                    app.getProvider(), app.getRating());
        }
    }

    final static class MyRatingInfo extends AppTitleInfo {
        private static final long serialVersionUID = 458113752074349768L;
        String comment;
        int rating;
        boolean ratingable;
        boolean commentable;

        public MyRatingInfo(String icon, String pkg, String title,
                String provider, float rating, String comment, int myRating,
                boolean ratingable, boolean commentable) {
            super(icon, pkg, title, provider, rating);
            this.comment = comment;
            this.rating = myRating;
            this.ratingable = ratingable;
            this.commentable = commentable;
        }

        MyRatingInfo(Application app) {
            super(app);
            if (app.getMyRating() != null)
                rating = app.getMyRating();
            comment = app.getMyComment();
            ratingable = app.isRatingable();
            commentable = app.isCommentable();
        }
    }

    final static class SettingInfo implements Serializable {
        private static final long serialVersionUID = -2069236960210773217L;
        boolean appFilterSettingEnable;
        int appFilter;

        public SettingInfo(boolean afse, int appFilter) {
            this.appFilterSettingEnable = afse;
            this.appFilter = appFilter;
        }
    }
}
