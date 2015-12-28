package cn.com.vapk.vstore.client.installapp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

abstract public class AppInstallReceiver extends PkgInstallReceiver {

	private static final Set<String> DL_PKG_NAMES = Collections
			.synchronizedSet(new HashSet<String>());;
	private static final String ACTION_DL_FINISH = "cn.com.vapk.vstore.client.action.AppDownloadFinish";
	private static final String ACTION_DL_PCT = "cn.com.vapk.vstore.client.action.AppDownloadPct";
	private static final String ACTION_DL_FAIL = "cn.com.vapk.vstore.client.action.AppDownloadFail";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		super.onReceive(ctx, intent);
		String action = intent.getAction();
		String pkg = intent.getStringExtra("pkg");
		if (ACTION_DL_PCT.equals(action)) {
			int pct = intent.getIntExtra("pct", -1);
			if (pct != -1)
				downloadPercent(pkg, pct);
		} else if (ACTION_DL_FINISH.equals(action)) {
			downloadFinish(pkg);
		} else if (ACTION_DL_FAIL.equals(action)) {
			downloadFail(pkg);
		}
	}

	abstract protected void downloadFail(String pkg);

	abstract protected void downloadFinish(String pkg);

	abstract protected void downloadPercent(String pkg, int percent);

	static final void sendDownloadPct(Context context, String pkg, int pct) {
		Intent intent = new Intent();
		intent.setAction(ACTION_DL_PCT);
		intent.putExtra("pkg", pkg);
		intent.putExtra("pct", pct);
		context.sendBroadcast(intent);
	}

	static final void sendDownloadFail(Context context, String pkg) {
		DL_PKG_NAMES.remove(pkg);
		Intent intent = new Intent();
		intent.setAction(ACTION_DL_FAIL);
		intent.putExtra("pkg", pkg);
		context.sendBroadcast(intent);
	}

	static final void sendDownloadFinish(Context context, String pkg) {
		DL_PKG_NAMES.remove(pkg);
		Intent intent = new Intent();
		intent.setAction(ACTION_DL_FINISH);
		intent.putExtra("pkg", pkg);
		context.sendBroadcast(intent);
	}

	public final void register(Context ctx) {
		super.register(ctx);
		IntentFilter itf = new IntentFilter();
		itf.addAction(ACTION_DL_FINISH);
		itf.addAction(ACTION_DL_PCT);
		itf.addAction(ACTION_DL_FAIL);
		ctx.registerReceiver(this, itf);
	}

}
