package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.AppsRet.App;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.AsyncTask;

abstract class PageableAppsAdapter implements AppsListAdapter {

	private static final int LOAD_POINT = 4;
	private AppsListAdapter appsListAdapter;
	private boolean loading;
	private boolean pageEnd;
	private int page;
	private int loadPage;

	public PageableAppsAdapter(AppsRet appsRet, AppsListAdapter appsListAdapter) {
		this.appsListAdapter = appsListAdapter;
		pageEnd = appsRet.isPageEnd();
		page = 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (!pageEnd && !loading && (position + LOAD_POINT) == getCount()) {
			nextPage();
		}

		return appsListAdapter.getView(position, convertView, parent);
	}

	private synchronized void nextPage() {
		if (pageEnd || loading) {
			return;
		}
		loading = true;
		loadPage = page + 1;
		new AsyncTask<Void, Void, AppsRet>() {

			@Override
			protected void onPreExecute() {
				preLoadAppsRet(loadPage);
			};

			@Override
			protected AppsRet doInBackground(Void... params) {
				AppsRet appsRet = loadAppsRet(loadPage);
				loading = false;
				return appsRet;
			}

			@Override
			protected void onPostExecute(AppsRet appsRet) {
				if (appsRet != null) {
					pageEnd = appsRet.isPageEnd();
					page = loadPage;
					if (appsRet.isSuccess())
						appsListAdapter.addApps(appsRet.getApps());
					appsListAdapter.notifyDataSetChanged();
				}
				postLoadAppsRet(loadPage, appsRet);
			};
		}.execute();
	}

	abstract protected AppsRet loadAppsRet(int page);

	protected void preLoadAppsRet(int page) {
	}

	protected void postLoadAppsRet(int page, AppsRet appsRet) {
	}

	@Override
	public boolean areAllItemsEnabled() {
		return appsListAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return appsListAdapter.isEnabled(position);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		appsListAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		appsListAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public int getCount() {
		return appsListAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return appsListAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return appsListAdapter.getItemId(position);
	}

	@Override
	public boolean hasStableIds() {
		return appsListAdapter.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return appsListAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return appsListAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return appsListAdapter.isEmpty();
	}

	@Override
	public void addApps(App[] apps) {
		appsListAdapter.addApps(apps);
	}

	@Override
	public App[] getApps() {
		return appsListAdapter.getApps();
	}

	@Override
	public void setDownloadProgress(String pkg, int pct) {
		appsListAdapter.setDownloadProgress(pkg, pct);
	}

	@Override
	public void notifyDataSetChanged() {
		appsListAdapter.notifyDataSetChanged();
	}

	@Override
	public void notifyAppInstalled(String pkg) {
		appsListAdapter.notifyAppInstalled(pkg);
	}

	@Override
	public void notifyAppUninstalled(String pkg) {
		appsListAdapter.notifyAppUninstalled(pkg);
	}

	@Override
	public void notifyAppStatusChange(String pkg) {
		appsListAdapter.notifyAppStatusChange(pkg);
	}
}