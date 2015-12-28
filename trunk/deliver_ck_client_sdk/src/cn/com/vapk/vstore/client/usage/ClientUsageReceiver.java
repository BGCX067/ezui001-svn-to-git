package cn.com.vapk.vstore.client.usage;

import tw.com.sti.store.api.android.util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClientUsageReceiver extends BroadcastReceiver {
	private static final Logger L = Logger.getLogger(ClientUsageReceiver.class);

	@Override
	public void onReceive(Context ctx, Intent intent) {
		final String action = intent.getAction();
		if (ActionController.ACTION_CLIENT_USAGE.equals(action)) {
			String uid = intent.getStringExtra(ActionController.EXTRA_USER_ID);
			String userId = intent
					.getStringExtra(ActionController.EXTRA_USER_UID);
			String isFirstTime = intent.getStringExtra(ActionController.EXTRA_IS_FIRST_TIME);
			if (ClientUsage.insert(ctx, uid, userId, isFirstTime))
				startSendLogService(ctx);
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			startSendLogService(ctx);
		}
	}

	private void startSendLogService(Context ctx) {
		Intent sendLogIntent = new Intent(ctx, ClientUsageService.class);
		ctx.startService(sendLogIntent);
		if (Logger.DEBUG)
			L.d("startService");
	}
}