package cn.com.vapk.vstore.client.usage;

import java.util.Date;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.CommonRet;

import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/*
 *  若是invoker fail或是ret fail 則五分鐘後再送
 *  若是ret success 則刪掉這一筆並送下一筆
 *  parameters send to server: (l_uid, l_userId, l_iccid, l_imei, l_ver, l_time, l_dvc, l_isFirstTime) 
 */
public class ClientUsageService extends Service {

	private static final Logger L = Logger.getLogger(ClientUsageService.class);

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		ClientUsage clientUsage = ClientUsage.query(this);
		if (clientUsage != null) {
			requestServer();
		} else {
			stopSelf();
			if (Logger.DEBUG)
				L.d("stopService, checkExistingItem = 0");
		}
	}
	private boolean running=false;
	private void requestServer() {
		try{
			if (!NetworkUtils.isNetworkOpen(this)) {
				setAlarm();
				if (Logger.DEBUG)
					L.d("Network not open.");
			}
			if(running) return;
			running=true;
			doInBackground();
		}catch(Throwable e){}
		finally{
			running=false;
		}
	}
	protected Void doInBackground(Void... params) {
		Context context = getApplicationContext();
		ClientUsage clientUsage;
		while ((clientUsage = ClientUsage.query(context)) != null) {
			AndroidApiService apiService = AndroidApiService.getInstance(context,ConfigurationFactory.getInstance());
			ApiInvoker<CommonRet> apiInvoker = apiService.clientUsage(
					clientUsage.uid, clientUsage.userId,
					clientUsage.iccid, clientUsage.imei,
					clientUsage.version, clientUsage.time,
					clientUsage.dvc, clientUsage.imsi,
					clientUsage.msisdn, clientUsage.isFirstTime);
			apiInvoker.invoke();

			boolean success = isSuccess(apiInvoker);

			if (!success) {
				setAlarm();
				stopSelf();
				return null;
			} else {
				ClientUsage.delete(context, clientUsage.id);
			}
		}

		return null;
	}
	private boolean isSuccess(ApiInvoker<CommonRet> apiInvoker) {

		if (apiInvoker.isStop()) {
			if (Logger.DEBUG)
				L.d("apiInvoker.isStop().");
			return false;
		}

		if (apiInvoker.isFail()) {
			if (Logger.DEBUG)
				L.d("apiInvoker.isFail().");
			return false;
		}

		CommonRet clientUsageRet = apiInvoker.getRet();
		if (clientUsageRet.isFail()) {
			if (Logger.ERROR) {
				L.e(clientUsageRet.getRetMsg());
			}
			return false;
		}

		// Success
		if (Logger.DEBUG)
			L.d("responseResult success");
		return true;
	}

	private void setAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ClientUsageService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
				0);
		int repeatTime = 300000; // 1000 * 60 * 5
		long triggerAtTime = System.currentTimeMillis() + repeatTime;
		am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
		if (Logger.DEBUG)
			L.d("Set alarm at " + new Date(triggerAtTime).toLocaleString()
					+ " and stop service.");
	}
}