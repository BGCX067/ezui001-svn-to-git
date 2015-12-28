package cn.com.vapk.vstore.client;

import java.util.ArrayList;
import java.util.List;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.ApiService.AppsType;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.AppsRet.App;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.api.AppStatusReceiver;
import cn.com.vapk.vstore.client.installapp.AppInstallReceiver;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ImageDownloader;
import cn.com.vapk.vstore.client.util.NetworkUtils;

abstract class AbsAppsDisplayActivity<T extends AppsRet> extends Activity
		implements OnItemClickListener {

	private static final Logger L = Logger
			.getLogger(AbsAppsDisplayActivity.class);

	public static enum Mode {
		NORMAL, MY_DOWNLOAD
	}

	protected static final String PREF_KEY_APPS_TYPE = "apps_type";
	private Mode mode = Mode.NORMAL;
	private View freeAppsBtn, paidAppsBtn, allAppsBtn;
	private Displyer freeAppsDisplyer, paidAppsDisplyer, allAppsDisplyer;
	private AppsType display;
	private ImageDownloader imageDownloader;
	private View appsEmpty;
	private AppInstallReceiver appInstallReceiver;
	private AppStatusReceiver appStatusReceiver;
	private CloseClientReceiver closeClientReceiver;
	private boolean showDonwloadProgress;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (appInstallReceiver != null)
			appInstallReceiver.unregister(this);

		if (appStatusReceiver != null)
			appStatusReceiver.unregister(this);

		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	protected void reloadApps() {
		freeAppsDisplyer = null;
		paidAppsDisplyer = null;
		allAppsDisplyer = null;
		displayApps(display);
	}

	protected void initAppsDisplay(Mode mode) {
		this.mode = mode;
		imageDownloader = new ImageDownloader();
		freeAppsBtn = findViewById(R.id.free_apps);
		paidAppsBtn = findViewById(R.id.paid_apps);
		allAppsBtn = findViewById(R.id.all_apps);
		appsEmpty = findViewById(R.id.apps_empty);
		OnClickListener appsBtnOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppsType appsType;
				switch (v.getId()) {
				case R.id.free_apps:
					appsType = AppsType.FREE;
					break;
				case R.id.paid_apps:
					appsType = AppsType.PAID;
					break;
				case R.id.all_apps:
					appsType = AppsType.ALL;
					break;
				default:
					return;
				}
				SharedPreferences pref = getPreferences(MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString(PREF_KEY_APPS_TYPE, appsType.name());
				editor.commit();
				displayApps(appsType);
			}
		};
		freeAppsBtn.setOnClickListener(appsBtnOnClickListener);
		paidAppsBtn.setOnClickListener(appsBtnOnClickListener);
		allAppsBtn.setOnClickListener(appsBtnOnClickListener);
		appInstallReceiver = new AppInstallReceiver() {
			@Override
			protected void remove(Context ctx, String packageName) {
				notifyAppUninstalled(packageName);
			}

			@Override
			protected void install(Context ctx, String packageName) {
				notifyAppInstalled(packageName);
			}

			@Override
			protected void replace(Context ctx, String packageName) {
				notifyAppInstalled(packageName);
			}

			@Override
			protected void downloadFail(String pkg) {
				if (showDonwloadProgress)
					setDownloadProgress(pkg, -1);
			}

			@Override
			protected void downloadFinish(String pkg) {
				if (showDonwloadProgress)
					setDownloadProgress(pkg, -1);
			}

			@Override
			protected void downloadPercent(String pkg, int pct) {
				if (showDonwloadProgress)
					setDownloadProgress(pkg, pct);
			}

			private final void notifyAppInstalled(String pkg) {
				if (freeAppsDisplyer != null) {
					freeAppsDisplyer.notifyAppInstalled(pkg);
				}
				if (paidAppsDisplyer != null) {
					paidAppsDisplyer.notifyAppInstalled(pkg);
				}
				if (allAppsDisplyer != null) {
					allAppsDisplyer.notifyAppInstalled(pkg);
				}
			}

			private final void notifyAppUninstalled(String pkg) {
				if (freeAppsDisplyer != null) {
					freeAppsDisplyer.notifyAppUninstalled(pkg);
				}
				if (paidAppsDisplyer != null) {
					paidAppsDisplyer.notifyAppUninstalled(pkg);
				}
				if (allAppsDisplyer != null) {
					allAppsDisplyer.notifyAppUninstalled(pkg);
				}
			}

			private final void setDownloadProgress(String pkg, int pct) {
				if (freeAppsDisplyer != null) {
					freeAppsDisplyer.setDownloadProgress(pkg, pct);
				}
				if (paidAppsDisplyer != null) {
					paidAppsDisplyer.setDownloadProgress(pkg, pct);
				}
				if (allAppsDisplyer != null) {
					allAppsDisplyer.setDownloadProgress(pkg, pct);
				}
			}
		};
		appInstallReceiver.register(this);
		appStatusReceiver = new AppStatusReceiver() {
			@Override
			protected App[] getApps() {
				ArrayList<App> apps = new ArrayList<AppsRet.App>();
				if (freeAppsDisplyer != null) {
					addAppToList(apps, freeAppsDisplyer.getApps());
				}
				if (paidAppsDisplyer != null) {
					addAppToList(apps, paidAppsDisplyer.getApps());
				}
				if (allAppsDisplyer != null) {
					addAppToList(apps, allAppsDisplyer.getApps());
				}
				return apps.toArray(new App[apps.size()]);
			}

			private void addAppToList(List<App> list, App[] apps) {
				if (apps == null || apps.length == 0)
					return;
				for (App app : apps) {
					list.add(app);
				}
			}

			@Override
			protected void updated(String pkg) {
				if (freeAppsDisplyer != null) {
					freeAppsDisplyer.notifyAppStatusChange(pkg);
				}
				if (paidAppsDisplyer != null) {
					paidAppsDisplyer.notifyAppStatusChange(pkg);
				}
				if (allAppsDisplyer != null) {
					allAppsDisplyer.notifyAppStatusChange(pkg);
				}
			}
		};
		appStatusReceiver.register(this);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
	}

	protected void initAppsDisplay() {
		initAppsDisplay(Mode.NORMAL);
	}

	void displayApps(AppsType appsType) {
		if (Logger.DEBUG)
			L.d("displayApps: " + appsType);
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		switch (appsType) {
		case FREE:
			if (freeAppsDisplyer == null) {
				ListView lv = (ListView) findViewById(R.id.free_apps_list);
				freeAppsDisplyer = new Displyer(AppsType.FREE, lv, freeAppsBtn);
			}
			freeAppsDisplyer.display();
			break;
		case PAID:
			if (paidAppsDisplyer == null) {
				ListView lv = (ListView) findViewById(R.id.paid_apps_list);
				paidAppsDisplyer = new Displyer(AppsType.PAID, lv, paidAppsBtn);
			}
			paidAppsDisplyer.display();
			break;
		case ALL:
			if (allAppsDisplyer == null) {
				ListView lv = (ListView) findViewById(R.id.all_apps_list);
				allAppsDisplyer = new Displyer(AppsType.ALL, lv, allAppsBtn);
			}
			allAppsDisplyer.display();
			break;
		}

		if (freeAppsDisplyer != null && appsType != AppsType.FREE) {
			freeAppsDisplyer.invisible();
		}
		if (paidAppsDisplyer != null && appsType != AppsType.PAID) {
			paidAppsDisplyer.invisible();
		}
		if (allAppsDisplyer != null && appsType != AppsType.ALL) {
			allAppsDisplyer.invisible();
		}
	}

	void showDonwloadProgress() {
		showDonwloadProgress = true;
	}

	private final class Displyer {

		private ListView appsListView;
		private AppsType appsType;
		private PageableAppsAdapter adapter;
		private View appsBtn;
		private View footer;
		private AsyncTask<Void, Void, ApiInvoker<T>> task;

		public Displyer(AppsType appsType, ListView appsListView, View appsBtn) {
			this.appsListView = appsListView;
			this.appsType = appsType;
			this.appsBtn = appsBtn;
		}

		private final void notifyAppInstalled(String pkg) {
			if (adapter != null)
				adapter.notifyAppInstalled(pkg);
		}

		private final void notifyAppUninstalled(String pkg) {
			if (adapter != null)
				adapter.notifyAppUninstalled(pkg);
		}

		private final void setDownloadProgress(String pkg, int pct) {
			if (adapter != null)
				adapter.setDownloadProgress(pkg, pct);
		}

		private final App[] getApps() {
			if (adapter != null)
				return adapter.getApps();
			return null;
		}

		private final void notifyAppStatusChange(String pkg) {
			if (adapter != null)
				adapter.notifyAppStatusChange(pkg);
		}

		private void invisible() {
			if (Logger.DEBUG)
				L.d("invisible");
			appsListView.setVisibility(View.GONE);
		}

		private void loading() {
			if (Logger.DEBUG)
				L.d("loading");
			UI.startProgressing(AbsAppsDisplayActivity.this);
			appsListView.setVisibility(View.GONE);
			appsEmpty.setVisibility(View.GONE);
		}

		private void loaded() {
			if (Logger.DEBUG)
				L.d("loaded");
			if (display != appsType)
				return;

			UI.stopProgressing(AbsAppsDisplayActivity.this);
			if (adapter.getCount() == 0)
				appsEmpty.setVisibility(View.VISIBLE);
			else
				appsEmpty.setVisibility(View.GONE);
			appsListView.setVisibility(View.VISIBLE);
		}

		private AsyncTask<Void, Void, ApiInvoker<T>> doTask(final Integer appFilter, final Integer pSize) {
			return new AsyncTask<Void, Void, ApiInvoker<T>>() {
				@Override
				protected ApiInvoker<T> doInBackground(Void... params) {
					ApiInvoker<T> apiInvoker = appsRetInvoker(appsType, appFilter, 1, pSize);
					apiInvoker.invoke();
					return apiInvoker;
				}

				protected void onPostExecute(ApiInvoker<T> apiInvoker) {
					if (postAppsRetInvoker(apiInvoker))
						return;

					AppsRet appsRet = apiInvoker.getRet();
					if (appsRet == null) {
						task = null;
						return;
					}

					if (!appsRet.isPageEnd()) {
						footer = getLayoutInflater().inflate(
								R.layout.list_loading, null);
						appsListView.addFooterView(footer);
					}

					AppsListAdapter appsListAdapter = null;
					if (Logger.DEBUG)
						L.d("mode: " + mode);
					if (Mode.NORMAL.equals(mode)) {
						appsListAdapter = new AppsAdapter(appsRet.getApps(),
								AbsAppsDisplayActivity.this, imageDownloader);
					} else {
						appsListAdapter = new MyDownloadAppsAdapter(
								appsRet.getApps(), AbsAppsDisplayActivity.this,
								imageDownloader);
					}
					adapter = new PageableAppsAdapter(appsRet, appsListAdapter) {

						private ApiInvoker<T> apiInvoker;

						@Override
						protected AppsRet loadAppsRet(int page) {
							apiInvoker = appsRetInvoker(appsType, appFilter, page, pSize);
							apiInvoker.invoke();
							return apiInvoker.isSuccess() ? apiInvoker.getRet()
									: null;
						}

						protected void postLoadAppsRet(int page, AppsRet appsRet) {
							if (appsRet != null && appsRet.isPageEnd()&&footer!=null) {
								try{
								appsListView.removeFooterView(footer);
								}catch(Throwable e){
									//這裡經常發生NullPointerException, remveFooterView()
								}
								footer = null;
							}
							postAppsRetInvoker(apiInvoker);
						}
					};
					appsListView.setAdapter(adapter);
					appsListView
							.setOnItemClickListener(AbsAppsDisplayActivity.this);
					loaded();
					task = null;
				};
			};
		}

		void display() {
			display = appsType;
			setDisplayAppsBtnStatus(appsBtn);

			// 讀取完後會new adapter並會把task清除
			// 如果讀取失敗 adapter == null, task == null 而可以再次讀取
			if (adapter != null) {
				loaded();
				return;
			}
			if (task == null)
				task = doTask(null, null );//預設appFilter與pSize為null，可視需要傳入自定義值
			switch (task.getStatus()) {
			case PENDING:
				task.execute();
				loading();
				break;
			case RUNNING:
				loading();
				break;
			case FINISHED:
				loaded();
				break;
			}
		}
	}

	abstract protected ApiInvoker<T> appsRetInvoker(AppsType appsType, Integer appFilter, int page, Integer pSize);

	// abstract protected <T extends AppsRet> ApiInvoker<T> appsRetInvoker(
	// ApplicationListType appsType, int page);

	/**
	 * @return true 表示AppsDisplay不要對ApiInvoker繼續執行
	 */
	protected boolean postAppsRetInvoker(ApiInvoker<T> apiInvoker) {
		if (isFinishing())
			return true;

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return true;

		AppsRet appsRet = apiInvoker.getRet();
		if (appsRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("appsRet.isSuccess()");
			return false;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		}
		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Adapter adapter = parent.getAdapter();
		App app = (App) adapter.getItem(position);
		// TODO bug: 會發生 app 是 null
		if (app != null)
			onAppItemClick(app);
	}

	abstract protected void onAppItemClick(App app);

	private void setDisplayAppsBtnStatus(View v) {
		boolean able = v != freeAppsBtn;
		freeAppsBtn.setClickable(able);
		freeAppsBtn.setEnabled(able);
		freeAppsBtn.setFocusable(able);

		able = v != paidAppsBtn;
		paidAppsBtn.setClickable(able);
		paidAppsBtn.setEnabled(able);
		paidAppsBtn.setFocusable(able);

		able = v != allAppsBtn;
		allAppsBtn.setClickable(able);
		allAppsBtn.setEnabled(able);
		allAppsBtn.setFocusable(able);
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}
}