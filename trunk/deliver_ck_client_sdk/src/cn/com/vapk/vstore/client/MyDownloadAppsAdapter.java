package cn.com.vapk.vstore.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import tw.com.sti.store.api.android.util.AppUtils;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.AppsRet.App;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.installapp.DownloadAppService;
import cn.com.vapk.vstore.client.util.ImageDownloader;

public class MyDownloadAppsAdapter extends BaseAdapter implements
		AppsListAdapter {

	private Context context;
	private HashMap<String, App> apps;
	private ArrayList<App> myApps;
	private ArrayList<App> dlApps;
	private ArrayList<App> playApps;
	private LayoutInflater layoutInflater;
	private int evenColor;
	private int oddColor;
	private final ImageDownloader imageDownloader;
	private HashMap<String, Integer> downloadProgressDatas;

	public MyDownloadAppsAdapter(App[] apps, Activity activity,
			ImageDownloader imageDownloader) {
		this.context = activity.getApplicationContext();
		this.layoutInflater = activity.getLayoutInflater();
		this.apps = new HashMap<String, App>(60);
		this.myApps = new ArrayList<AppsRet.App>();
		this.dlApps = new ArrayList<AppsRet.App>();
		this.playApps = new ArrayList<AppsRet.App>();
		this.imageDownloader = imageDownloader;
		this.downloadProgressDatas = new HashMap<String, Integer>();
		Resources r = context.getResources();
		evenColor = r.getColor(R.color.bg_list_item_even);
		oddColor = r.getColor(android.R.color.transparent);
		Bitmap bitmap = BitmapFactory
				.decodeResource(r, R.drawable.def_app_icon);
		imageDownloader.defalutImage = bitmap;
		addApps(apps);
		playApps.addAll(dlApps);
		playApps.addAll(myApps);
	}

	public void addApps(App[] apps) {
		if (apps == null)
			return;
		int count = apps.length;
		for (int i = 0; i < count; i++) {
			App app = apps[i];
			if (app != null) {
				this.apps.put(app.getPkg(), app);
				if (DownloadAppService.isInDownload(app.getPkg()))
					dlApps.add(app);
				else if (AppUtils.isMyDownloadApp(app,context))
					myApps.add(app);
			}
		}
	}

	@Override
	public App[] getApps() {
		Collection<App> apps = this.apps.values();
		return apps.toArray(new App[apps.size()]);
	}

	public void setDownloadProgress(String pkg, int pct) {
		if (pct >= 0) {
			downloadProgressDatas.put(pkg, pct);
			App dlApp = apps.get(pkg);
			if (dlApp != null) {
				if (!dlApps.contains(dlApp))
					dlApps.add(dlApp);
				if (myApps.contains(dlApp))
					myApps.remove(dlApp);
			}
		} else {
			downloadProgressDatas.remove(pkg);
			App dlApp = apps.get(pkg);
			if (dlApp != null) {
				if (dlApps.contains(dlApp))
					dlApps.remove(dlApp);
				if (!myApps.contains(dlApp) && AppUtils.isMyDownloadApp(dlApp,context))
					myApps.add(dlApp);
			}
		}

		notifyDataSetChanged();
	}

	@Override
	public void notifyAppInstalled(String pkg) {
		App installApp = apps.get(pkg);
		if (pkg != null && !myApps.contains(installApp))
			myApps.add(installApp);

		notifyDataSetChanged();
	}

	@Override
	public void notifyAppUninstalled(String pkg) {
		App installApp = apps.get(pkg);
		if (pkg != null && myApps.contains(installApp)
				&& installApp!=null && !AppUtils.isMyDownloadApp(installApp,context))
			myApps.remove(installApp);

		notifyDataSetChanged();
	}

	@Override
	public void notifyAppStatusChange(String pkg) {
		App app = apps.get(pkg);
		if (app == null)
			return;

		if (dlApps.contains(app) || myApps.contains(app)) {
			notifyDataSetChanged();
			return;
		}

		if (AppUtils.isMyDownloadApp(app,context)) {
			myApps.add(app);
			notifyDataSetChanged();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		playApps.clear();
		playApps.addAll(dlApps);
		playApps.addAll(myApps);
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return playApps.size();
	}

	@Override
	public Object getItem(int position) {
		try {
			return playApps.get(position);
		} catch (RuntimeException e) {
			if (Logger.DEBUG)
				throw e;
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_app_item, null);
			convertView.setTag(new ViewHolder(convertView));
		}

		if (position % 2 == 1) { // 偶數行換色
			convertView.setBackgroundColor(evenColor);
		} else {
			convertView.setBackgroundColor(oddColor);
		}

		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		App app = (App) getItem(position);
		if (app == null) {
			notifyDataSetChanged();
			return convertView;
		}
		imageDownloader.download(app.getIcon(), viewHolder.appIcon);
		viewHolder.appTitle.setText(app.getTitle());
//		viewHolder.appProvider.setText(app.getProvider());
		viewHolder.appStatus.setText(AppUtils.getStatusText(app,context));
		float rating = app.getRating();
		if (rating == 0)
			viewHolder.appRating.setVisibility(View.INVISIBLE);
		else {
			viewHolder.appRating.setVisibility(View.VISIBLE);
			viewHolder.appRating.setRating(rating);
		}

		if (downloadProgressDatas != null) {
			if (dlApps.contains(app)) {
				Integer pct = downloadProgressDatas.get(app.getPkg());
				viewHolder.pgsBar.setProgress(pct == null ? 0 : pct);
				viewHolder.pgsBar.setVisibility(View.VISIBLE);
			} else
				viewHolder.pgsBar.setVisibility(View.GONE);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView appTitle;
//		TextView appProvider;
		TextView appStatus;
		RatingBar appRating;
		ImageView appIcon;
		ProgressBar pgsBar;

		ViewHolder(View view) {
			appTitle = (TextView) view.findViewById(R.id.app_title);
//			appProvider = (TextView) view.findViewById(R.id.app_provider);
			appStatus = (TextView) view.findViewById(R.id.app_status);
			appRating = (RatingBar) view.findViewById(R.id.app_rating);
			appIcon = (ImageView) view.findViewById(R.id.app_icon);
			pgsBar = (ProgressBar) view.findViewById(R.id.download_progress);
		}
	}
}