package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.ApiService.AppsType;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.AppsRet.App;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.ActionController.Intents;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;

public class MyDownloadActivity extends AbsAppsDisplayActivity<AppsRet>
		implements OnClickListener {

	private static final Logger L = Logger.getLogger(MyDownloadActivity.class);

	private static final String CAT_ID = "5";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!ActionController.loginFlag){
			this.getIntent().putExtra(ActionController.EXTRA_FORWARD_INTENT, Intents.myDownload(this));
			ActionController.index(this);			
			finish();
		}else{
			SharedPreferences pref = getPreferences(MODE_PRIVATE);
			String prefType = pref.getString(PREF_KEY_APPS_TYPE,
					AppsType.ALL.name());
			AppsType appsType = Enum.valueOf(AppsType.class, prefType);

			setContentView(R.layout.my_download);
			String title = getString(R.string.my_download_title);
			UI.bindText(this, R.id.pricetype_bar_title, title);
			UI.bindText(this, R.id.all_apps, getString(R.string.pricetype_all));
			UI.bindTabOnClickListener(this, this);

			initAppsDisplay(Mode.MY_DOWNLOAD);
			showDonwloadProgress();
			displayApps(appsType);
		}		
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (Logger.DEBUG)
			L.d("onNewIntent");
		reloadApps();
	}

	@Override
	public void onClick(View v) {
		UI.handleTabOnClickEvent(this, v);
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
	protected ApiInvoker<AppsRet> appsRetInvoker(AppsType appsType, Integer appFilter, int page, Integer pSize) {
		AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		ApiInvoker<AppsRet> apiInvoker = apiService.myDownloadApps(appsType, page);
		return apiInvoker;
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}
}