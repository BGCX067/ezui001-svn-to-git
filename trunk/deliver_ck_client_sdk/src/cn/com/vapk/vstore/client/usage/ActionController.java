package cn.com.vapk.vstore.client.usage;

import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import cn.com.vapk.vstore.client.ActionSession;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import android.app.Activity;
import android.content.Intent;

public class ActionController {
	private static final Logger L = Logger.getLogger(ActionController.class);

	public static final String ACTION_CLIENT_USAGE = "cn.com.vapk.vstore.client.action.CLIENT_USAGE";

	public static final String EXTRA_USER_ID = "user_id";
	public static final String EXTRA_USER_UID = "user_uid";
	public static final String EXTRA_IS_FIRST_TIME = "is_first_time";

	public final static void broadcastClientUsage(Activity aty) {
		if (Logger.DEBUG)
			L.d("broadcastClientUsage");
		String userId ="root";
		String isFirstTime = ActionSession.isAgreeEula(aty, userId)? "0":"1";
		Intent intent = new Intent();
		intent.setAction(ACTION_CLIENT_USAGE);
		AndroidApiService apiService = AndroidApiService.getInstance(aty,ConfigurationFactory.getInstance());
		intent.putExtra(EXTRA_USER_ID, apiService.getCredential(aty).getUid());
		intent.putExtra(EXTRA_USER_UID, apiService.getCredential(aty).getUserId());
		intent.putExtra(EXTRA_IS_FIRST_TIME, isFirstTime);
		aty.sendBroadcast(intent);
	}
}
