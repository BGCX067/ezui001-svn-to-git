package cn.com.vapk.vstore.client.update;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.CheckClientVersionRet;
import tw.com.sti.store.api.vo.CheckClientVersionRet.NewClient;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class CheckClientVersionService extends IntentService {

	private final Logger L = Logger.getLogger(CheckClientVersionService.class);

	public CheckClientVersionService() {
		super("CheckClientVersionService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (Logger.DEBUG)
			L.d("onHandleIntent " + intent);
		requestClientUpdate();
	}

	private void requestClientUpdate() {
		if (!NetworkUtils.isNetworkOpen(getApplicationContext())) {
			if (Logger.DEBUG)
				L.d("No Network.");
			return;
		}
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String msisdn = tm.getLine1Number();
		AndroidApiService apiService = AndroidApiService.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
		ApiInvoker<CheckClientVersionRet> apiInvoker = apiService
				.checkClientVersion(msisdn);
		apiInvoker.invoke();
		responseClientUpdate(apiInvoker);
	}

	private void responseClientUpdate(
			ApiInvoker<CheckClientVersionRet> apiInvoker) {
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

		CheckClientVersionRet checkClientVersionRet = apiInvoker.getRet();
		if (checkClientVersionRet.isFail()) {
			if (Logger.DEBUG) {
				L.d("clientUpdateRet.isFail().");
				L.d("Check Client Version response msg: "
						+ checkClientVersionRet.getRetMsg());
			}
			return;
		}

		NewClient newClient = checkClientVersionRet.getNewClient();
		if (newClient == null) {
			if (Logger.DEBUG)
				L.d("checkClientVersionRet.getNewClient() == null");
			return;
		}

		ActionController.notifyNewClient(this, newClient);
	}
}