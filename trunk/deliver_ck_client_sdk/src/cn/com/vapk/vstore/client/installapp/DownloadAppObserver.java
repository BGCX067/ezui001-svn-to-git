package cn.com.vapk.vstore.client.installapp;

abstract class DownloadAppObserver {

	DownloadAppObserver() {
	}

	abstract void updateInBackground(DownloadAppInfo dai);

	final void notifyUpdate(DownloadAppInfo dai) {
		updateInBackground(dai);
	}

}
