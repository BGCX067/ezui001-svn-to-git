package cn.com.vapk.vstore.client.installapp;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

import tw.com.sti.store.api.android.util.Logger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

final class DownloadAppInfo implements Serializable {

	private static final long serialVersionUID = 4963135926961197326L;

	enum Status {
		PENDING, //
		RUNNING, //
		FINISHED;
	}

	enum Result {
		SUCCESS, //
		CANCEL, //
		FAIL_DEVICE_UPSUPPORT, //
		FAIL_CONNECT_TO_SERVER, //
		FAIL_SD_UNMOUNT, //
		FAIL_FILE_WRITE, //
		FAIL_FILE_WRITE_OR_SERVER_CONNECT;
	}

	private int addDownloadedBytesTimes;
	DownloadAppObserver observer;
	final int id;
	final String pkg;
	final String categoryId;
	final String appTitle;
	final String vlogId;
	private Status status = Status.PENDING;
	private Result result;
	private int apkBytes;
	private int downloadedBytes;
	private int fail;
	String downloadId;
	File apk;
	String contentType;
	int version;

	DownloadAppInfo(String pkg, String categoryId, String appTitle,
			int version, String vlogId) {
		super();
		this.id = new Random(System.currentTimeMillis()).nextInt(100000000);
		this.pkg = pkg;
		this.categoryId = categoryId;
		this.appTitle = appTitle;
		this.vlogId = vlogId;
	}

	Status getStatus() {
		return status;
	}

	Result getResult() {
		return result;
	}

	int getFail() {
		return fail;
	}

	void ready() {
		if (!modifiable())
			return;
		this.status = Status.RUNNING;
	}

	void cancel() {
		if (!modifiable())
			return;
		this.status = Status.FINISHED;
		this.result = Result.CANCEL;
		notifyObserver();
	}

	void success() {
		if (!modifiable())
			return;
		this.result = Result.SUCCESS;
		this.status = Status.FINISHED;
		notifyObserver();
	}

	void fail(Result fail) {
		if (!modifiable())
			return;
		this.result = fail;
		this.status = Status.FINISHED;
		notifyObserver();
	}

	void setApkBytes(int apkBytes) {
		if (!modifiable())
			return;
		this.apkBytes = apkBytes;
		this.status = Status.RUNNING;
		notifyObserver();
	}

	void addDownloadedBytes(int addBytes) {
		if (!modifiable())
			return;
		this.downloadedBytes += addBytes;
		this.status = Status.RUNNING;
		if (addDownloadedBytesTimes++ % 25 == 0)
			notifyObserver();
	}

	int getDownloadedPercent() {
		return downloadedBytes * 100 / apkBytes;
	}

	Integer getLocalAppVersion(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(pkg,
					PackageManager.GET_ACTIVITIES);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	private void notifyObserver() {
		if (observer != null)
			observer.notifyUpdate(this);
	}

	private boolean modifiable() {
		if (Status.FINISHED == status) {
			if (Logger.DEBUG)
				throw new RuntimeException("Modify data when Status.FINISHED");
			return false;
		}
		return true;
	}

}
