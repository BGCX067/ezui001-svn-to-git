package cn.com.vapk.vstore.client.installapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

abstract public class PkgInstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING,
				false);
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			if (!replacing)
				install(ctx, parsePkg(intent.getData()));
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			if (!replacing)
				remove(ctx, parsePkg(intent.getData()));
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			replace(ctx, parsePkg(intent.getData()));
		} else {
			return;
		}
	}

	private String parsePkg(Uri uri) {
		return uri.toString().replace("package:", "");
	}

	abstract protected void replace(Context ctx, String packageName);

	abstract protected void install(Context ctx, String packageName);

	abstract protected void remove(Context ctx, String packageName);

	public void register(Context ctx) {
		IntentFilter itf = new IntentFilter();
		itf.addAction(Intent.ACTION_PACKAGE_ADDED);
		itf.addAction(Intent.ACTION_PACKAGE_REMOVED);
		itf.addAction(Intent.ACTION_PACKAGE_REPLACED);
		itf.addDataScheme("package");
		ctx.registerReceiver(this, itf);
	}

	public void unregister(Context ctx) {
		ctx.unregisterReceiver(this);
	}
}
