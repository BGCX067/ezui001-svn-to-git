package cn.com.vapk.vstore.client.installapp;

import tw.com.sti.store.api.android.util.Logger;
import cn.com.vapk.vstore.client.ActionController;
import cn.com.vapk.vstore.client.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

final class DownloadAppNotifier {

	private final int NOTI_ID_BASE = 100000000;
	private Context ctx;
	private NotificationManager notiMgr;

	DownloadAppNotifier(Service service) {
		this.ctx = service.getApplicationContext();
		notiMgr = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	void notify(DownloadAppInfo dai) {
		switch (dai.getStatus()) {
		case PENDING:
			cancelNotification(dai);
			if (Logger.DEBUG)
				throw new RuntimeException("DownloadAppInfo Status PENDING");
			return;
		case RUNNING:
			notifyDownloadPct(dai);
			return;
		case FINISHED:
			notifyResult(dai);
			return;
		default:
			cancelNotification(dai);
			if (Logger.DEBUG)
				throw new RuntimeException("Unknown DownloadAppInfo Status");
			return;
		}
	}

	private void notifyResult(DownloadAppInfo dai) {
		switch (dai.getResult()) {
		case FAIL_SD_UNMOUNT:
			notifyDownloadFail(dai,
					ctx.getString(R.string.dl_fail_sd_card_unavailable));
			return;
		case FAIL_DEVICE_UPSUPPORT:
			notifyDownloadFail(dai,
					ctx.getString(R.string.dl_fail_device_unsupport));
			return;
		case FAIL_CONNECT_TO_SERVER:
			notifyDownloadFail(dai,
					ctx.getString(R.string.dl_fail_network_connect));
			return;
		case FAIL_FILE_WRITE:
			notifyDownloadFail(dai, ctx.getString(R.string.dl_fail_file_write));
			return;
		case FAIL_FILE_WRITE_OR_SERVER_CONNECT:
			notifyDownloadFail(
					dai,
					ctx.getString(R.string.dl_fail_file_write_or_network_disconnected));
			return;
		case SUCCESS:
			AppInstallReceiver.sendDownloadFinish(ctx, dai.pkg);
			cancelNotification(dai);
			return;
		case CANCEL:
			AppInstallReceiver.sendDownloadFail(ctx, dai.pkg); // TODO
			cancelNotification(dai);
		default:
			cancelNotification(dai);
			if (Logger.DEBUG)
				throw new RuntimeException("Unknown DownloadAppInfo Status");
			return;
		}
	}

	private void notifyDownloadFail(DownloadAppInfo dai, String desc) {
		String appTitle = dai.appTitle;
		// Toast.makeText(ctx, appTitle + desc, Toast.LENGTH_LONG).show();
		int icon = android.R.drawable.stat_sys_warning; // icon from
		CharSequence tickerText = appTitle + desc;
		CharSequence contentTitle = appTitle + ctx.getString(R.string.download);
		CharSequence contentText = desc;
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());

		Intent intent = ActionController.Intents.addDetail(ctx, dai.categoryId,
				dai.pkg);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(ctx, contentTitle, contentText,
				contentIntent);
		notiMgr.notify(getNotificationId(dai), notification);
		AppInstallReceiver.sendDownloadFail(ctx, dai.pkg);
	}

	private void notifyDownloadPct(DownloadAppInfo dai) {
		int percent = dai.getDownloadedPercent();
		String notiTitle = dai.appTitle + ctx.getString(R.string.download);
		RemoteViews notiView = new RemoteViews(ctx.getPackageName(),
				R.layout.noti_progressbar);
		notiView.setImageViewResource(R.id.noti_progressbar_icon,
                android.R.drawable.stat_sys_download);
		notiView.setTextViewText(R.id.noti_progressbar_percent, percent + "%");
		notiView.setProgressBar(R.id.noti_progressbar_bar, 100, percent, false);
		notiView.setTextViewText(R.id.noti_progressbar_text, notiTitle);

		Intent intent = ActionController.Intents.addDetail(ctx, dai.categoryId,
				dai.pkg);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new Notification(
				android.R.drawable.stat_sys_download, notiTitle,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.contentView = notiView;
		notification.contentIntent = contentIntent;
		notiMgr.notify(getNotificationId(dai), notification);
		AppInstallReceiver.sendDownloadPct(ctx, dai.pkg, percent);
	}

	private void cancelNotification(DownloadAppInfo dai) {
		notiMgr.cancel(getNotificationId(dai));
	}

	private int getNotificationId(DownloadAppInfo dai) {
		return NOTI_ID_BASE + dai.id;
	}

}