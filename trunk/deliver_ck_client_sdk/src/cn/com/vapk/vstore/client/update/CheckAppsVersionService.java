package cn.com.vapk.vstore.client.update;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.AppUtils;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.AppVersionsRet;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;
import android.app.IntentService;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CheckAppsVersionService extends IntentService {
	private final Logger L = Logger.getLogger(CheckAppsVersionService.class);

	public CheckAppsVersionService() {
		super("CheckAppsVersionService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		requestApps(1, 0);
	}

	private void requestApps(int page, int newAppSize) {
		if (!NetworkUtils.isNetworkOpen(getApplicationContext())) {
			if (Logger.DEBUG)
				L.d("No Network.");
			return;
		}

		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String msisdn = tm.getLine1Number();
		AndroidApiService apiService = AndroidApiService.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
		ApiInvoker<AppVersionsRet> apiInvoker = apiService.myAppVersions(
				msisdn, page);
		apiInvoker.invoke();

		responseApps(apiInvoker, page, newAppSize);
	}

	private void responseApps(ApiInvoker<AppVersionsRet> apiInvoker, int page,
			int newAppSize) {
		if (apiInvoker.isStop()) {
			if (Logger.DEBUG)
				L.d("apiInvoker.isStop().");
			return;
		}

		if (apiInvoker.isFail()) {
			if (Logger.DEBUG)
				L.d("apiInvoker.isFail().");
			return;
		}

		AppVersionsRet appvsRet = apiInvoker.getRet();
		if (!appvsRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d(appvsRet.getRetMsg());

			return;
		}

		newAppSize += AppUtils.updateAppsSize(getApplicationContext(),appvsRet.getApps());
		if (!appvsRet.isPageEnd()) {
			requestApps(++page, newAppSize);
		} else if (newAppSize > 0) {
			ActionController.notifyNewApps(getApplicationContext(), newAppSize);
		}

		if (Logger.DEBUG)
			L.d("Has " + newAppSize + " new version apps.");
	}
}