package tw.com.sti.store.api.android.util;

import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.AppInfo.PayStatus;
import tw.com.sti.store.api.vo.AppInfo.PriceType;
import tw.com.sti.store.api.vo.AppInfo.Status;
import tw.com.sti.store.api.vo.AppVersionsRet.AppVersion;
import tw.com.sti.store.api.vo.ApplicationRet.Application;
import tw.com.sti.store.api.vo.ApplicationRet.Application.Action;
import tw.com.sti.store.api.vo.AppsRet.App;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

final public class AppUtils {

	public final static boolean hasNewVersion(Context context,String pkg,int version) {
		boolean installed = false;
		boolean newVersion = false;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pkginfo = pm.getPackageInfo(pkg,
					PackageManager.GET_ACTIVITIES);
			installed = true;
			newVersion = pkginfo.versionCode < version;
		} catch (NameNotFoundException e) {
		}
		return installed&&newVersion;
	}
	public final static boolean isInstall(String pkg, Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		try {
			pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	public final static String getStatusText(App app, Context context) {
		boolean installed = false;
		boolean newVersion = false;
		String pkg=app.getPkg();
		int version=app.getVersion();
		PayStatus payStatus=app.getPayStatus();
		PriceType priceType=app.getPriceType();
		String priceText=app.getPriceText();
		String subscribeExpDate=app.getSubscribeExpDate();
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pkginfo = pm.getPackageInfo(pkg,
					PackageManager.GET_ACTIVITIES);
			installed = true;
			newVersion = pkginfo.versionCode < version;
		} catch (NameNotFoundException e) {
		}
		boolean paid = PayStatus.PAID.equals(payStatus);
		return getStatusText(installed, newVersion, paid,priceType,priceText,subscribeExpDate);
	}
	private final static String getStatusText(boolean installed, boolean newVersion,
			boolean paid,PriceType priceType,String priceText,String subscribeExpDate) {
		boolean unSubscribeNotExp = !LangUtils.isBlank(subscribeExpDate);
		int status = Status.getStatus(priceType, installed, newVersion,
				paid, unSubscribeNotExp);
		switch (status) {
		/* FREE */
		case Status.TYPE_FREE:
			return priceText;
		case Status.TYPE_FREE | Status.INSTALLED:
			return "已安装";
		case Status.TYPE_FREE | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
			/* ONE_TIME */
		case Status.TYPE_ONE_TIME:
			return priceText;
		case Status.TYPE_ONE_TIME | Status.INSTALLED:
			return "已安装未购买";
		case Status.TYPE_ONE_TIME | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
		case Status.TYPE_ONE_TIME | Status.PAID:
			return "已购买未安装";
		case Status.TYPE_ONE_TIME | Status.PAID | Status.INSTALLED:
			return "已购买";
		case Status.TYPE_ONE_TIME | Status.PAID | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
			/* MONTHLY */
		case Status.TYPE_MONTHLY:
			return priceText;
		case Status.TYPE_MONTHLY | Status.INSTALLED:
			return "已安装未订阅";
		case Status.TYPE_MONTHLY | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
		case Status.TYPE_MONTHLY | Status.SUBSCRIBE:
			return "已订阅未安装";
		case Status.TYPE_MONTHLY | Status.SUBSCRIBE | Status.INSTALLED:
			return "已订阅";
		case Status.TYPE_MONTHLY | Status.SUBSCRIBE | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
		case Status.TYPE_MONTHLY | Status.UN_SUBSCRIBE_NOT_EXP:
		case Status.TYPE_MONTHLY | Status.UN_SUBSCRIBE_NOT_EXP | Status.INSTALLED:
		case Status.TYPE_MONTHLY | Status.UN_SUBSCRIBE_NOT_EXP | Status.INSTALLED | Status.NEW_VERSION:
			return subscribeExpDate;
			/* IN_APP_PURCHASE */
		case Status.TYPE_IN_APP_PURCHASE:
			return priceText;
		case Status.TYPE_IN_APP_PURCHASE | Status.INSTALLED:
			return "已安装";
		case Status.TYPE_IN_APP_PURCHASE | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
		case Status.TYPE_IN_APP_PURCHASE | Status.PAID:
			return "已购买未安装";
		case Status.TYPE_IN_APP_PURCHASE | Status.PAID | Status.INSTALLED:
			return "已购买";
		case Status.TYPE_IN_APP_PURCHASE | Status.PAID | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
			/* SERVER_PRODUCT_PURCHASE */
		case Status.TYPE_SERVER_PRODUCT_PURCHASE:
			return priceText;
		case Status.TYPE_SERVER_PRODUCT_PURCHASE | Status.INSTALLED:
			return "已安装";
		case Status.TYPE_SERVER_PRODUCT_PURCHASE | Status.INSTALLED | Status.NEW_VERSION:
			return "可更新";
		}

//		if (Logger.DEBUG)
//			throw new RuntimeException("Unknown Status Code");
		return priceText;
	}
	public final static boolean isMyDownloadApp(App app,Context context) {
		if (AppUtils.isInstall(app.getPkg(), context))
			return true;

		if (PayStatus.PAID.equals(app.getPayStatus())
				|| !LangUtils.isBlank(app.getSubscribeExpDate()))
			return true;

		if (PriceType.FREE.equals(app.getPriceType())
				|| PriceType.SERVER_PRODUCT_PURCHASE.equals(app.getPriceType()))
			return false;

		return false;
	}
	public final static int updateAppsSize(Context ctx,AppVersion[] appVersions) {
		if (appVersions == null || appVersions.length == 0)
			return 0;
	
		PackageManager pm = ctx.getPackageManager();
		int size = 0;
		for (AppVersion appv : appVersions) {
			final String pkg = appv.getPkg();
			try {
				PackageInfo pkginfo = pm.getPackageInfo(pkg,
						PackageManager.GET_ACTIVITIES);
				if (pkginfo.versionCode < appv.getVersion())
					size++;
			} catch (NameNotFoundException e) {
			}
		}
	
		return size;
	}
	public final static Integer getLocalVersionCode(Context context,String pkg) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pkginfo = pm.getPackageInfo(pkg,
					PackageManager.GET_ACTIVITIES);
			return pkginfo.versionCode;
		} catch (Exception e) {
			return null;
		}
	}
	
	public final static Action[] getActions(Context context,Application app) {
		boolean installed = false;
		boolean newVersion = false;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pkginfo = pm.getPackageInfo(app.getPkg(),
					PackageManager.GET_ACTIVITIES);
			installed = true;
			newVersion = pkginfo.versionCode < app.getVersion();
		} catch (NameNotFoundException e) {
		}
		boolean paid = PayStatus.PAID.equals(app.getPayStatus());
		return getActions(installed, newVersion, paid,app);
	}
	
	private final static Action[] getActions(boolean installed, boolean newVersion,
			boolean paid,Application app) {
		// 安裝、新版本由下面的程式控制邏輯
		boolean unSubscribeNotExp = !LangUtils.isBlank(app.getSubscribeExpDate());
		int status = Status.getStatus(app.getPriceType(), false, false, paid,
				unSubscribeNotExp);
	
		if (!installed) { // 未安裝
			switch (status) {
			case Status.TYPE_MONTHLY | Status.SUBSCRIBE: // 只有月租已訂閱未安裝需多回傳UN_SUBSCRIBE
				return new Action[] { Action.INSTALL };
			default:
				return new Action[] { Action.INSTALL };
			}
		} else { // 已安装
			if (newVersion) // 已安装有新版本統一回傳 UPDATE, UNINSTALL
				return new Action[] { Action.UPDATE, Action.UNINSTALL };
	
			// 已安装無新版本，除了計次未購買，月租未訂閱，其他action1都是OPEN
			Action action1;
			switch (status) {
			case Status.TYPE_ONE_TIME:
				if (app.getPrice() == 0)
					action1 = Action.OPEN;
				else
					action1 = Action.BUY;
				break;
			case Status.TYPE_MONTHLY:
				if (app.getPrice() == 0)
					action1 = Action.OPEN;
				else
					action1 = Action.SUBSCRIBE;
				break;
			default:
				action1 = Action.OPEN;
				break;
			}
			return new Action[] { action1, Action.UNINSTALL };
		}
	}
}
