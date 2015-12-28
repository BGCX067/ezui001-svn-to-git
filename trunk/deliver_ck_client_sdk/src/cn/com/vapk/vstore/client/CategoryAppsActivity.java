package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.ApiService.AppsType;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.CategoryAppsRet;
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
import cn.com.vapk.vstore.client.util.ConfigurationFactory;

public class CategoryAppsActivity extends AbsAppsDisplayActivity<CategoryAppsRet>
		implements OnClickListener {

	private String categoryId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		categoryId = intent.getStringExtra(ActionController.EXTRA_CATEGORY_ID);
		if (LangUtils.isBlank(categoryId)) {
			finish();
			return;
		}

		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		String prefType = pref.getString(PREF_KEY_APPS_TYPE, AppsType.ALL.name());
		AppsType appsType = Enum.valueOf(AppsType.class, prefType);

		setContentView(R.layout.category_apps);
        String categoryTitle = intent.getStringExtra(ActionController.EXTRA_CATEGORY_TITLE);
		UI.bindText(this, R.id.pricetype_bar_title, categoryTitle);
		UI.bindTabOnClickListener(this, this);

		initAppsDisplay();
		displayApps(appsType);
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
		ActionController.appDetail(this, app.getPkg(), categoryId);
	}

	@Override
	protected ApiInvoker<CategoryAppsRet> appsRetInvoker(AppsType appsType, Integer appFilter, int page, Integer pSize) {
		AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		ApiInvoker<CategoryAppsRet> apiInvoker = apiService.categoryApps(categoryId, appsType, appFilter, page, pSize);
		return apiInvoker;
	}

    @Override
    protected boolean postAppsRetInvoker(ApiInvoker<CategoryAppsRet> apiInvoker) {
        if (apiInvoker.isSuccess()) {
            CategoryAppsRet ret = apiInvoker.getRet();
            if (ret.isSuccess() && !LangUtils.isBlank(ret.getCategoryTitle()))
                UI.bindText(this, R.id.pricetype_bar_title, ret.getCategoryTitle());
        }
        return super.postAppsRetInvoker(apiInvoker);
    }

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}
}