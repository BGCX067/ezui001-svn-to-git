package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.ApiService.AppsType;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.CPAppsRet;
import tw.com.sti.store.api.vo.AppsRet.App;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.ImageDownloader;

public class CPAppsActivity extends AbsAppsDisplayActivity<CPAppsRet> {

	private static final String CAT_ID = "7";

	private String cpId;
	private TextView titleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		cpId = intent
				.getStringExtra(ActionController.EXTRA_CONTENT_PROVIDER_ID);
		if (LangUtils.isBlank(cpId)) {
			if (Logger.DEBUG) {
				throw new RuntimeException("CP_ID is blank.");
			}
			finish();
			return;
		}

		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		String prefType = pref.getString(PREF_KEY_APPS_TYPE,
				AppsType.ALL.name());
		AppsType appsType = Enum.valueOf(AppsType.class, prefType);

		setContentView(R.layout.cp_apps);
		UI.bindText(this, R.id.all_apps, getString(R.string.pricetype_all));
		titleView = (TextView) findViewById(R.id.app_title);
		String icon = intent.getStringExtra(ActionController.EXTRA_APP_ICON);
		ImageView iconView = (ImageView) findViewById(R.id.app_icon);
		if (LangUtils.isBlank(icon)) {
			iconView.setVisibility(View.GONE);
		} else {
			new ImageDownloader().download(icon, iconView);
		}

		initAppsDisplay();
		displayApps(appsType);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common, menu);
		if(!ActionController.loginFlag){
			menu.removeItem(R.id.menu_logout);
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (!UI.handleMenuOnSelectEvent(this, item)) {
			if (Logger.DEBUG)
				throw new RuntimeException("handleMenuOnSelectEvent false.");
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onAppItemClick(App app) {
		ActionController.appDetail(this, app.getPkg(), CAT_ID);
	}

	@Override
	protected ApiInvoker<CPAppsRet> appsRetInvoker(AppsType appsType, Integer appFilter, int page, Integer pSize) {
		AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		ApiInvoker<CPAppsRet> apiInvoker = apiService.cpApps(cpId, appsType, appFilter, page, pSize);
		return apiInvoker;
	}

	@Override
	protected boolean postAppsRetInvoker(ApiInvoker<CPAppsRet> apiInvoker) {
		if (titleView != null && apiInvoker.isSuccess()) {
			CPAppsRet ret = apiInvoker.getRet();
			String provider = ret.getProvider();
			if (ret.isSuccess() && !LangUtils.isBlank(provider))
				titleView.setText(ret.getProvider());
			titleView = null;
		}
		return super.postAppsRetInvoker(apiInvoker);
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}
}