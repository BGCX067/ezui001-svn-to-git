package cn.com.vapk.vstore.client.update;

import java.io.Serializable;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.CheckClientVersionRet.NewClient;

import cn.com.vapk.vstore.client.ASC;
import cn.com.vapk.vstore.client.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

final public class ActionController {

	private static final Logger L = Logger.getLogger(ActionController.class);

	static final String ACTION_CHECK_CLIENT_VERSION = "cn.com.vapk.vstore.client.action.CHECK_CLIENT_VERSION";
	static final String ACTION_CHECK_APPS_VERSION = "cn.com.vapk.vstore.client.action.CHECK_APPS_VERSION";
	public static final String ACTION_CHECK_APPS_VERSION_OPEN_MYDOWNLOAD = "cn.com.vapk.vstore.client.action.CHECK_APPS_VERSION_OPEN_MYDOWNLOAD";

	static final String EXTRA_NEW_CLIENT_ALERT_INFO = "new_client_alert_info";

	static final int NOTI_ID_CHECK_CLIENT_VERSION = 10000001;
	static final int NOTI_CHECK_NEW_CLIENT_ID = 10000002;

	public static final void checkClientVersion(Activity aty) {
		if (Logger.DEBUG)
			L.d("checkClientVersion");
		Intent intent = new Intent(aty, CheckClientVersionReceiver.class);
		intent.setAction(ACTION_CHECK_CLIENT_VERSION);
		aty.sendBroadcast(intent);
	}

	public static final void checkAppsVersion(Activity aty) {
		if (Logger.DEBUG)
			L.d("checkAppService");
		Intent intent = new Intent(aty, CheckAppsVersionReceiver.class);
		intent.setAction(ACTION_CHECK_APPS_VERSION);
		aty.sendBroadcast(intent);
	}

	static void alarmCheckClientVersion(Context ctx, long triggerAtTime) {
		if (Logger.DEBUG) {
			String time = LangUtils.formatDate(triggerAtTime,
					"yyyy/MM/dd HH:mm:ss");
			L.d("alarmCheckClientVersion " + time);
		}

		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, CheckClientVersionService.class);
		PendingIntent operation = PendingIntent.getService(ctx, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,
				AlarmManager.INTERVAL_DAY, operation);
	}

	static void alarmCheckAppVersion(Context ctx, long triggerAtTime) {
		if (Logger.DEBUG) {
			String time = LangUtils.formatDate(triggerAtTime,
					"yyyy/MM/dd HH:mm:ss");
			L.d("alarmCheckAppVersion " + time);
		}
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, CheckAppsVersionService.class);
		PendingIntent pIntent = PendingIntent.getService(ctx, 0, intent, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,
				AlarmManager.INTERVAL_DAY, pIntent);
	}

	// 開始下載後 再次通知新版本更新以備下載安裝過程中失敗時 能供使用者再次下載 直至安裝完畢才取消
	static void notifyNewClient(Context ctx, NewClient client) {
		if (Logger.DEBUG)
			L.d("notifyNewClient with NewClient");
		notifyNewClient(ctx, new NewClientAlertInfo(client));
	}

	static void notifyNewClient(Context ctx, NewClientAlertInfo ncai) {
		if (Logger.DEBUG)
			L.d("notifyNewClient with NewClientAlertInfo");
		NotificationManager nm = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification sMartNoti = new Notification(R.drawable.icon, ncai.ticker,
				System.currentTimeMillis());

		Intent intent = new Intent(ctx, NewClientAlertDialogActivity.class);
		intent.putExtra(EXTRA_NEW_CLIENT_ALERT_INFO, ncai);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		sMartNoti.setLatestEventInfo(ctx, ncai.title, ncai.notification,
				contentIntent);

		nm.notify(NOTI_ID_CHECK_CLIENT_VERSION, sMartNoti);
	}

	static void notifyNewApps(Context ctx, int newAppSize) {
		if (Logger.DEBUG)
			L.d("notifyNewApps");
		NotificationManager nm = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon,
				ctx.getString(R.string.noti_check_app_ticker),
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		CharSequence contentTitle = ctx
				.getString(R.string.noti_check_app_content_title);
		CharSequence contentText = String.format(
				ctx.getString(R.string.noti_check_app_content_context),
				newAppSize);

		Intent notificationIntent = new Intent(ctx, ASC.class);
		notificationIntent.setAction(ACTION_CHECK_APPS_VERSION_OPEN_MYDOWNLOAD);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(ctx, contentTitle, contentText,
				contentIntent);

		nm.notify(NOTI_CHECK_NEW_CLIENT_ID, notification);
	}

	// 若下載安裝NEW CLIENT過程失敗 則會跳此notification告知user 並且連結至下載dialog
	static void notifyUpdateClientError(Context ctx, String errorNoti,
			NewClientAlertInfo ncai) {
		if (Logger.DEBUG)
			L.d("notifyUpdateClientError");
		NotificationManager nm = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon,
				ctx.getString(R.string.noti_download_client_error_ticker),
				System.currentTimeMillis());

		CharSequence contentTitle = ctx
				.getString(R.string.noti_download_client_error_ticker);
		CharSequence contentText = errorNoti;

		Intent notificationIntent = new Intent(ctx,
				NewClientAlertDialogActivity.class);
		notificationIntent.putExtra(EXTRA_NEW_CLIENT_ALERT_INFO, ncai);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(ctx, contentTitle, contentText,
				contentIntent);

		nm.notify(ActionController.NOTI_ID_CHECK_CLIENT_VERSION, notification);
	}

	static void downloadClient(Context ctx, NewClientAlertInfo ncai) {
		Intent intent = new Intent(ctx, DownloadClientService.class);
		intent.putExtra(EXTRA_NEW_CLIENT_ALERT_INFO, ncai);
		ctx.startService(intent);
	}

	public static void downloadClient(Context ctx) {
		if (Logger.DEBUG)
			L.d("start to download new version client.");
		Intent intent = new Intent(ctx, DownloadClientService.class);
		ctx.startService(intent);
	}

	final static class NewClientAlertInfo implements Serializable {

		private static final long serialVersionUID = 8125082506884816894L;
		final int apkBytes;
		final String downloadToken;
		final String dialogMsg;
		final String title;
		final String ticker;
		final String notification;

		private NewClientAlertInfo(NewClient client) {
			apkBytes = client.getApkBytes();
			downloadToken = client.getDownloadToken();
			dialogMsg = client.getMsg();
			title = client.getTitle();
			ticker = client.getTicker();
			notification = client.getNotification();
		}
	}
}