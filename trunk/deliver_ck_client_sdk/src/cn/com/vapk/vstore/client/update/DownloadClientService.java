package cn.com.vapk.vstore.client.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.update.ActionController.NewClientAlertInfo;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;

import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;

/*
 * Need:
 * 1. Package Name and Package Title -> download path
 * 2. Package Size -> download progress
 */
final public class DownloadClientService extends Service {

	private final Logger L = Logger.getLogger(DownloadClientService.class);
	private NotificationManager notiMgr;
	private DownloadTask downloadTask;
	private Intent progressIntent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		progressIntent = new Intent();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		final NewClientAlertInfo ncai = (NewClientAlertInfo) intent
				.getSerializableExtra(ActionController.EXTRA_NEW_CLIENT_ALERT_INFO);
		if (ncai == null && Logger.DEBUG)
			L.d("NewClientAlertInfo == null");

		if (downloadTask == null
				|| AsyncTask.Status.FINISHED.equals(downloadTask.getStatus())) {
			downloadTask = new DownloadTask(ncai);
			downloadTask.execute();
		}

	}

	private void install(File file, String contentType) {
		try {
			if (Logger.DEBUG)
				L.d("install apk file: " + file);

			if (LangUtils.isBlank(contentType))
				contentType = "application/vnd.android.package-archive";

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), contentType);
			startActivity(intent);
		} catch (Exception e) {
			if (Logger.ERROR)
				L.e("install apk file exception: ", e);
		}
	}

	private final String getStoreAppDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator + getPackageName() + File.separator + "files"
				+ File.separator + "client" + File.separator;
	}

	private void updateDownloadProgress(int percent) {

		Notification notification = new Notification(
				android.R.drawable.stat_sys_download,
				getString(R.string.noti_download_client_ticker),
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.noti_progressbar);
		contentView.setImageViewResource(R.id.noti_progressbar_icon,
				android.R.drawable.stat_sys_download);
		contentView.setTextViewText(R.id.noti_progressbar_percent, percent
				+ "%");
		contentView.setProgressBar(R.id.noti_progressbar_bar, 100, percent,
				false);
		contentView.setTextViewText(R.id.noti_progressbar_text,
				getString(R.string.noti_downloading));
		notification.contentView = contentView;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				progressIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.contentIntent = contentIntent;
		notiMgr.notify(ActionController.NOTI_ID_CHECK_CLIENT_VERSION,
				notification);
	}

	class DownloadTask extends AsyncTask<Void, Void, Void> {

		private final NewClientAlertInfo ncai;
		private int downloadedBytes;
		private int apkBytes;
		private File apk;
		private String contentType;
		private String errNoti;
		private int downloadedBytesTimes;

		DownloadTask(NewClientAlertInfo ncai) {
			this.ncai = ncai;
		}

		@Override
		protected Void doInBackground(Void... params) {
			AndroidApiService apiService = AndroidApiService
					.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
			HttpRequestBase request = null;

			try {
				String dltoken = ncai == null ? null : ncai.downloadToken;
				request = apiService.downloadClient(dltoken);
			} catch (UnsupportedEncodingException e) {
				if (Logger.ERROR)
					L.e("UnsupportedEncodingException", e);
				errNoti = getString(R.string.noti_download_client_error_ticker);
				return null;
			}

			AndroidHttpClient http = null;
			try {
				http = AndroidHttpClient.newInstance("Android");
				HttpResponse response = http.execute(request);
				int responseCode = response.getStatusLine().getStatusCode();

				if (responseCode != HttpStatus.SC_OK) {
					if (Logger.DEBUG) {
						L.d("responseCode: " + responseCode);
						throw new RuntimeException("Server Response Invalid.");
					}
					errNoti = getString(R.string.noti_download_client_error_network);
					return null;
				}
				HttpEntity entity = response.getEntity();

				if (entity == null) {
					errNoti = getString(R.string.noti_download_client_error_network);
					return null;
				}

				apkBytes = (int) entity.getContentLength();
				contentType = entity.getContentType().getValue();
				String contentDisposition = response
						.getHeaders("Content-Disposition")[0].getValue();
				String fileName = contentDisposition.replace(
						"attachment; filename=", "");

				if (Logger.DEBUG) {
					L.d("apkBytes: " + apkBytes + ", contentType: "
							+ contentType + ", fileName: " + fileName);
				}

				if (LangUtils.isBlank(fileName)) {
					if (Logger.DEBUG)
						throw new RuntimeException("fileName is blank");
					fileName = "s_mart.apk";
				}
				if (LangUtils.isBlank(contentType)) {
					if (Logger.DEBUG)
						throw new RuntimeException("contentType is blank");
					contentType = "application/vnd.android.package-archive";
				}
				if (Logger.DEBUG && apkBytes <= 0) {
					throw new RuntimeException("apkBytes <= 0");
				}

				apk = new File(getStoreAppDir() + fileName);
				if (!apk.exists()) {
					File dir = apk.getParentFile();
					if (!dir.exists())
						dir.mkdirs();
					if (apk.exists())
						apk.delete();
					apk.createNewFile();
					if (Logger.DEBUG)
						L.d("Create file at: " + apk.getAbsolutePath());
				}

				InputStream is = null;
				OutputStream os = null;
				try {
					is = entity.getContent();
					os = new FileOutputStream(apk);
					int read = 0;
					byte[] bytes = new byte[4096];
					while ((read = is.read(bytes)) != -1) {
						os.write(bytes, 0, read);
						if (Logger.DEBUG)
							L.d("reads: " + read);
						downloadedBytes += read;
						if (downloadedBytesTimes++ % 30 == 0) {
							int percent = (downloadedBytes * 100) / apkBytes;
							updateDownloadProgress(percent);
						}
					}
					os.flush();
				} finally {
					if (os != null) {
						os.close();
					}
					if (is != null) {
						is.close();
					}
				}

				if (Logger.DEBUG)
					L.d("download apk finish");
				entity.consumeContent();
			} catch (Exception e) {
				request.abort();
				errNoti = getString(R.string.noti_download_client_error_file);
				notiMgr.cancel(ActionController.NOTI_ID_CHECK_CLIENT_VERSION);
				if (Logger.ERROR)
					L.e("download client exception", e);
			} finally {
				if (http != null)
					http.close();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!LangUtils.isBlank(errNoti)) {
				ActionController.notifyUpdateClientError(
						getApplicationContext(), errNoti, ncai);
			} else {
				notiMgr.cancel(ActionController.NOTI_ID_CHECK_CLIENT_VERSION);
				install(apk, contentType);
				if (ncai != null)
					ActionController.notifyNewClient(getApplicationContext(),
							ncai);
				stopSelf();
			}
		}
	};
}