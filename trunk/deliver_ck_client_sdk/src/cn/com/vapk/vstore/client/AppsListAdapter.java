package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.vo.AppsRet.App;
import android.widget.ListAdapter;
import cn.com.vapk.vstore.client.R;

interface AppsListAdapter extends ListAdapter {

	public void addApps(App[] apps);

	public App[] getApps();

	public void setDownloadProgress(String pkg, int pct);

	public void notifyDataSetChanged();

	public void notifyAppInstalled(String pkg);

	public void notifyAppUninstalled(String pkg);

	public void notifyAppStatusChange(String pkg);
}