package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.android.util.Logger;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cn.com.vapk.vstore.client.R;

public class CloseClientReceiver extends BroadcastReceiver {
	private static final Logger L = Logger.getLogger(CloseClientReceiver.class);
	private Activity aty;

	CloseClientReceiver(Activity aty) {
		this.aty = aty;
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (Logger.DEBUG)
			L.d("Activity: " + aty);
		if (ActionController.ACTION_CLOSE_SMART.equals(action))
			aty.finish();
	}

	public void register() {
		IntentFilter itf = new IntentFilter();
		itf.addAction(ActionController.ACTION_CLOSE_SMART);
		aty.registerReceiver(this, itf,
				ActionController.PERMISSION_CLOSE_CLIENT_RECEIVER, null);
	}

	public void unregister() {
		aty.unregisterReceiver(this);
	}

	void previousWork() {

	}
}