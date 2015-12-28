package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.AppsRet.App;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.com.vapk.vstore.client.api.AppStatusReceiver;
import cn.com.vapk.vstore.client.installapp.PkgInstallReceiver;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class FeatureAppsActivity extends ListActivity implements
		OnClickListener {

	private static boolean loginFlag = false;
	private static final Logger L = Logger.getLogger(FeatureAppsActivity.class);
	private static final String CAT_ID = "6";

	private ApiInvoker<AppsRet> apiInvoker;
	private PkgInstallReceiver pkgInstallReceiver;
	private AppStatusReceiver appStatusReceiver;
	private CloseClientReceiver closeClientReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature_apps);
		UI.bindTabOnClickListener(this, this);
		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
		requestFeatureApps(null);//預設傳入null(不啟動appFilter)，可視需要傳入new integer(1)代表啟動appFilter
	}

	@Override
	public void onClick(View v) {
		if (Logger.DEBUG)
			L.d("onClick");

		if (UI.handleTabOnClickEvent(this, v))
			return;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (apiInvoker != null)
			apiInvoker.stop();

		if (pkgInstallReceiver != null)
			pkgInstallReceiver.unregister(this);

		if (appStatusReceiver != null)
			appStatusReceiver.unregister(this);

		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		App app = (App) getListAdapter().getItem(position);
		ActionController.appDetail(this, app.getPkg(), CAT_ID);
	}

	private void requestFeatureApps(final Integer appFilter) {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.featureApps(appFilter);
				apiInvoker.invoke();
				return null;
			}

			protected void onPostExecute(Void result) {
				responseFeatureApp();
			};
		}.execute();
	}

	private void responseFeatureApp() {
		UI.stopProgressing(this);
		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;
		AppsRet appsRet = apiInvoker.getRet();
		if (appsRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("isSuccess()");
			displayApps(appsRet.getApps());
			return;
		}else{
			if (Logger.DEBUG)
				L.e("Get FeatureApps Fail......");
			showDialog(DialogUtils.DLG_CONN_TO_SERVER_FAIL);
			return;
		}
	}

	private void displayApps(App[] apps) {
		final AppsAdapter adapter = new AppsAdapter(apps, this);
		pkgInstallReceiver = new PkgInstallReceiver() {
			@Override
			protected void remove(Context ctx, String packageName) {
				adapter.notifyDataSetChanged();
			}

			@Override
			protected void install(Context ctx, String packageName) {
				adapter.notifyDataSetChanged();
			}

			@Override
			protected void replace(Context ctx, String packageName) {
				adapter.notifyDataSetChanged();
			}
		};
		pkgInstallReceiver.register(this);

		appStatusReceiver = new AppStatusReceiver() {
			@Override
			protected App[] getApps() {
				return adapter.getApps();
			}

			@Override
			protected void updated(String pkg) {
				adapter.notifyDataSetChanged();
			}
		};
		appStatusReceiver.register(this);
		setListAdapter(adapter);
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		case DialogUtils.DLG_CONN_TO_SERVER_FAIL: {
			return DialogUtils.createConnectionToServerFailAlertDialog(this);
		}
		}
		return null;
	}

	public static boolean isLoginFlag() {
		return loginFlag;
	}

	public static void setLoginFlag(boolean loginFlag) {
		FeatureAppsActivity.loginFlag = loginFlag;
	}
}