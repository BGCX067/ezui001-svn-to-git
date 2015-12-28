package cn.com.vapk.vstore.client.installapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;

import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;

import cn.com.vapk.vstore.client.util.ConfigurationFactory;

import android.app.Service;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.os.Process;

final class DownloadAppThread extends Thread {
	private final Logger L = Logger.getLogger(DownloadAppThread.class);
	private Context ctx;
	final DownloadAppInfo dai;
	private HttpRequestBase request;

	public DownloadAppThread(Service service, DownloadAppInfo dai) {
		this.ctx = service.getApplicationContext();
		this.dai = dai;
		dai.ready();

		AndroidApiService apiService = AndroidApiService.getInstance(ctx,ConfigurationFactory.getInstance());
		try {
			request = apiService.downloadApp(dai.pkg, dai.categoryId,
					dai.getLocalAppVersion(ctx), dai.vlogId);
		} catch (UnsupportedEncodingException e) {
		}
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		if (Logger.DEBUG)
			L.d("run");

		if (request == null) {
			dai.fail(DownloadAppInfo.Result.FAIL_DEVICE_UPSUPPORT);
			return;
		}

		// check sd card status
		final String dirState = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(dirState)) {
			if (Logger.DEBUG)
				L.d("SD card not MOUNTED");
			dai.fail(DownloadAppInfo.Result.FAIL_SD_UNMOUNT);
			return;
		}

		try {
			download();
		} catch (Exception e) {
			if (Logger.ERROR)
				L.e("download Exception", e);
			dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
			return;
		}
	}

	private void download() {
		AndroidHttpClient http = null;
		try {
			http = AndroidHttpClient.newInstance("Android");
			HttpResponse response;
			try {
				response = http.execute(request);
			} catch (IOException e) {
				if (Logger.ERROR)
					L.e("http.execute(request)", e);
				// if (request.isAborted()) {
				// return;
				// }
				dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
				return;
			}

			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode != HttpStatus.SC_OK) {
				if (Logger.DEBUG) {
					L.d("responseCode: " + responseCode);
					throw new RuntimeException("Server Response Invalid.");
				}
				dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
				return;
			}

			HttpEntity entity = response.getEntity();
			if (entity == null) {
				if (Logger.DEBUG)
					L.d("HttpEntity == null");
				dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
				return;
			}

			try {
				int apkBytes = (int) entity.getContentLength();
				String contentType = entity.getContentType().getValue();
				Header[] headers = response.getHeaders("apk-download-id");
				if (headers.length == 0
						|| LangUtils.isBlank(headers[0].getValue())) {
					if (Logger.DEBUG)
						L.d("Header no downloadIdd");
					dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
					return;
				}
				dai.downloadId = headers[0].getValue();

				headers = response.getHeaders("Content-Disposition");
				String fileName = null;
				if (headers.length != 0
						&& !LangUtils.isBlank(headers[0].getValue())) {
					fileName = headers[0].getValue().replace(
							"attachment; filename=", "");
				}

				headers = response.getHeaders("apk-version");
				int version;
				if (headers.length != 0
						&& !LangUtils.isBlank(headers[0].getValue())) {
					try {
						version = new Integer(headers[0].getValue());
					} catch (Exception e) {
						if (Logger.ERROR)
							L.e("Header versionCode value invalid.", e);
						dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
						return;
					}
				} else {
					if (Logger.DEBUG)
						L.d("Header no versionCode");
					dai.fail(DownloadAppInfo.Result.FAIL_CONNECT_TO_SERVER);
					return;
				}

				if (Logger.DEBUG)
					L.d("downloadId: " + dai.downloadId + ", apkBytes: "
							+ apkBytes + ", contentType: " + contentType
							+ ", fileName: " + fileName);

				if (LangUtils.isBlank(fileName)) {
					if (Logger.DEBUG)
						throw new RuntimeException("fileName is blank");
					fileName = dai.pkg + ".apk";
				}
				if (LangUtils.isBlank(contentType)) {
					if (Logger.DEBUG)
						throw new RuntimeException("contentType is blank");
					contentType = "application/vnd.android.package-archive";
				}
				if (apkBytes <= 0) {
					if (Logger.DEBUG)
						throw new RuntimeException("apkBytes <= 0");
					apkBytes = 512 * 1024; // Default 512KB
				}

				File apkFile = createFile(fileName);
				if (apkFile == null) {
					if (Logger.DEBUG)
						L.d("createFile(fileName) == null");
					dai.fail(DownloadAppInfo.Result.FAIL_FILE_WRITE);
					return;
				}

				dai.contentType = contentType;
				dai.apk = apkFile;
				dai.version = version;
				dai.setApkBytes(apkBytes);

				InputStream is = null;
				OutputStream os = null;
				try {
					is = entity.getContent();
					os = new FileOutputStream(apkFile);
					int read = 0;
					byte[] bytes = new byte[4096];
					while ((read = is.read(bytes)) != -1) {
						os.write(bytes, 0, read);
						dai.addDownloadedBytes(read);
					}
					os.flush();
					os.close();
					dai.success();
					if (Logger.DEBUG)
						L.d("download apk finish");
				} catch (IOException e) {
					if (Logger.ERROR)
						L.e("IOException", e);
					dai.fail(DownloadAppInfo.Result.FAIL_FILE_WRITE_OR_SERVER_CONNECT);
					return;
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							if (Logger.ERROR)
								L.e("is.close()", e);
						}
					}
				}

			} finally {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					if (Logger.ERROR)
						L.e("entity.consumeContent", e);
				}
			}
		} finally {
			if (http != null)
				http.close();
		}
	}

	private File createFile(String fileName) {
		File apk = new File(getStorageDirPath() + fileName);
		try {
			if (apk.exists()) {
				apk.delete();
			} else {
				File dir = apk.getParentFile();
				if (!dir.exists())
					dir.mkdirs();
			}
			apk.createNewFile();
			if (Logger.DEBUG)
				L.d("Create file at: " + apk.getAbsolutePath());
			return apk;
		} catch (Exception e) {
			if (Logger.ERROR)
				L.e("createNewFile: " + apk.getAbsolutePath(), e);
			return null;
		}
	}

	final String getStorageDirPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "Android" + File.separator + "data"
				+ File.separator + ctx.getPackageName() + File.separator
				+ "files" + File.separator + "apk" + File.separator;
	}

	public void cancel() {
		interrupt();
		dai.cancel();
		// if (!request.isAborted()) {
		// request.abort();
		// }
	}

}