package cn.com.vapk.vstore.client.installapp;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import android.content.Context;
import android.content.Intent;

public final class ActionController {
	private static final Logger L = Logger.getLogger(ActionController.class);

	static final String EXTRA_PACKAGE_NAME = "package_name";
	static final String EXTRA_CATEGORY_ID = "category_id";
	static final String EXTRA_APP_TITLE = "app_title";
	static final String EXTRA_APP_VERSION = "app_version";
	static final String EXTRA_V_LOG_ID = "v_log_id";
	public static final String CATEGORY_ID_SDK_UPGRADE_APP = "-1";

	public static final void installApp(Context ctx, String pkg,
			String categoryId, String appTitle, int version) {
		installApp(ctx, pkg, categoryId, appTitle, version, null);
	}

	public static final void installApp(Context ctx, String pkg,
			String categoryId, String appTitle, int version, String vlogId) {
		if (Logger.DEBUG)
			L.d("installApp, pkg: " + pkg + ", categoryId: " + categoryId
					+ ", appTitle: " + appTitle + ", version: " + version
					+ ", vlogId: " + vlogId);

		Intent intent = new Intent(ctx, DownloadAppService.class);
		intent.putExtra(EXTRA_PACKAGE_NAME, pkg);
		intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
		intent.putExtra(EXTRA_APP_TITLE, appTitle);
		intent.putExtra(EXTRA_APP_VERSION, version);
		if (!LangUtils.isBlank(vlogId))
			intent.putExtra(EXTRA_V_LOG_ID, vlogId);
		ctx.startService(intent);
	}

	static final void sendInstallLog(Context ctx) {
		if (Logger.DEBUG)
			L.d("sendInstallLog");
		ctx.startService(new Intent(ctx, InstallLogSenderService.class));
	}

}
