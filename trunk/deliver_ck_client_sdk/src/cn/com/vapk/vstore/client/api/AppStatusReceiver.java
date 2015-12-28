package cn.com.vapk.vstore.client.api;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.AppInfo.PayStatus;
import tw.com.sti.store.api.vo.AppsRet.App;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

abstract public class AppStatusReceiver extends BroadcastReceiver {

	private static final Logger L = Logger.getLogger(AppStatusReceiver.class);
	private static final String ACTION_APP_RATING = "cn.com.vapk.vstore.client.action.APP_RATING";
	private static final String ACTION_APP_PAY_STATUS = "cn.com.vapk.vstore.client.action.APP_PAY_STATUS";
	private static final String EXTRA_PACKAGE_NAME = "package_name";
	private static final String EXTRA_APP_RATING_SCORE = "app_rating_score";
	private static final String EXTRA_APP_PAY_STATUS = "app_pay_status";
	private static final String EXTRA_APP_SUBSCRIBE_EXP_DATE = "app_subscribe_exp_date";

	final static String PERMISSION_APP_STATUS_RECEIVER = "cn.com.vapk.vstore.client.permission.RECEIVER_APP_STATUS";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (ACTION_APP_RATING.equals(action)) {
			updateAppsRating(intent.getStringExtra(EXTRA_PACKAGE_NAME),
					intent.getFloatExtra(EXTRA_APP_RATING_SCORE, 0));
		} else if (ACTION_APP_PAY_STATUS.equals(action)) {
			String pkg = intent.getStringExtra(EXTRA_PACKAGE_NAME);
			PayStatus payStatus = (PayStatus) intent
					.getSerializableExtra(EXTRA_APP_PAY_STATUS);
			String subscribeExpDate = intent
					.getStringExtra(EXTRA_APP_SUBSCRIBE_EXP_DATE);
			updateAppsPayStatus(pkg, payStatus, subscribeExpDate);
		}
	}

	private void updateAppsPayStatus(String pkg, PayStatus payStatus,
			String subscribeExpDate) {
		if (LangUtils.isBlank(pkg)) {
			if (Logger.DEBUG)
				throw new RuntimeException("pkg is blank.");
			return;
		}
		if (payStatus == null) {
			if (Logger.DEBUG)
				throw new RuntimeException("payStatus is null.");
			return;
		}
		App[] apps = getApps();
		if (apps == null || apps.length == 0) {
			if (Logger.DEBUG)
				L.d("updateAppsPayStatus apps == null || apps.length == 0");
			return;
		}

		boolean updated = false;
		for (App app : apps) {
			if (!app.getPkg().equals(pkg)
					|| app.getPayStatus().equals(payStatus))
				continue;

			updated = true;
			if (!PayStatus.UN_SUBSCRIBE.equals(payStatus)) {
				app.setPayStatus(payStatus);
			} else if (LangUtils.isBlank(subscribeExpDate)) {
				app.setPayStatus(PayStatus.NO_PAID);
			} else {
				app.setSubscribeExpDate(subscribeExpDate);
				app.setPayStatus(PayStatus.UN_SUBSCRIBE);
			}
		}

		if (updated) {
			if (Logger.DEBUG)
				L.d("update pkg: " + pkg + ", payStatus: " + payStatus
						+ ", subscribeExpDate: " + subscribeExpDate);
			updated(pkg);
		}
	}

	private void updateAppsRating(String pkg, float rating) {
		if (LangUtils.isBlank(pkg)) {
			if (Logger.DEBUG)
				throw new RuntimeException("pkg is blank.");
			return;
		}
		App[] apps = getApps();
		if (apps == null || apps.length == 0) {
			if (Logger.DEBUG)
				L.d("updateAppsRating apps == null || apps.length == 0");
			return;
		}

		boolean updated = false;
		for (App app : apps) {
			if (!app.getPkg().equals(pkg) || app.getRating() == rating)
				continue;

			updated = true;
			app.setRating(rating);
		}

		if (updated) {
			if (Logger.DEBUG)
				L.d("update pkg: " + pkg + ", rating: " + rating);
			updated(pkg);
		}
	}

	/**
	 * 取得要被更新的Apps
	 */
	abstract protected App[] getApps();

	/**
	 * 更新Apps後的Callback method
	 * 
	 * @param apps
	 *            被更新狀態的App
	 */
	abstract protected void updated(String pkg);

	public static final void broadcastPaid(Context ctx, String pkg) {
		Intent intent = new Intent(ACTION_APP_PAY_STATUS);
		intent.putExtra(EXTRA_PACKAGE_NAME, pkg);
		intent.putExtra(EXTRA_APP_PAY_STATUS, PayStatus.PAID);
		ctx.sendBroadcast(intent, PERMISSION_APP_STATUS_RECEIVER);
	}

	public static final void broadcastPayProcessing(Context ctx, String pkg) {
		Intent intent = new Intent(ACTION_APP_PAY_STATUS);
		intent.putExtra(EXTRA_PACKAGE_NAME, pkg);
		intent.putExtra(EXTRA_APP_PAY_STATUS, PayStatus.PAY_PROCESSING);
		ctx.sendBroadcast(intent, PERMISSION_APP_STATUS_RECEIVER);
	}

	public static final void broadcastUnsubscribe(Context ctx, String pkg,
			String subscribeExpDate) {
		Intent intent = new Intent(ACTION_APP_PAY_STATUS);
		intent.putExtra(EXTRA_PACKAGE_NAME, pkg);
		intent.putExtra(EXTRA_APP_PAY_STATUS, PayStatus.UN_SUBSCRIBE);
		intent.putExtra(EXTRA_APP_SUBSCRIBE_EXP_DATE, subscribeExpDate);
		ctx.sendBroadcast(intent, PERMISSION_APP_STATUS_RECEIVER);
	}

	public static final void boardcastRating(Context ctx, String pkg,
			float rating) {
		Intent intent = new Intent(ACTION_APP_RATING);
		intent.putExtra(EXTRA_PACKAGE_NAME, pkg);
		intent.putExtra(EXTRA_APP_RATING_SCORE, rating);
		ctx.sendBroadcast(intent);
	}

	public void register(Context ctx) {
		IntentFilter itf = new IntentFilter();
		itf.addAction(ACTION_APP_RATING);
		itf.addAction(ACTION_APP_PAY_STATUS);
		ctx.registerReceiver(this, itf, PERMISSION_APP_STATUS_RECEIVER, null);
	}

	public void unregister(Context ctx) {
		ctx.unregisterReceiver(this);
	}
}