package cn.com.vapk.vstore.client;

import java.util.ArrayList;
import java.util.HashMap;

import tw.com.sti.store.api.android.util.AppUtils;
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
import cn.com.vapk.vstore.client.util.ImageDownloader;

public class AppsAdapter extends BaseAdapter implements AppsListAdapter {

	private Context context;
	private ArrayList<App> apps;
	private LayoutInflater layoutInflater;
	private int evenColor;
	private int oddColor;
	private final ImageDownloader imageDownloader;
	private HashMap<String, Integer> downloadProgressDatas;

	public AppsAdapter(App[] apps, Activity activity,
			ImageDownloader imageDownloader) {
		this.context = activity.getApplicationContext();
		this.layoutInflater = activity.getLayoutInflater();
		this.apps = new ArrayList<AppsRet.App>(60);
		this.imageDownloader = imageDownloader;
		this.downloadProgressDatas = new HashMap<String, Integer>();
		Resources r = context.getResources();
		evenColor = r.getColor(R.color.bg_list_item_even);
		oddColor = r.getColor(android.R.color.transparent);
		Bitmap bitmap = BitmapFactory
				.decodeResource(r, R.drawable.def_app_icon);
		imageDownloader.defalutImage = bitmap;
		addApps(apps);
	}

	public AppsAdapter(App[] apps, Activity activity) {
		this(apps, activity, new ImageDownloader());
	}

	public void setDownloadProgress(String pkg, int pct) {
		if (pct >= 0)
			downloadProgressDatas.put(pkg, pct);
		else
			downloadProgressDatas.remove(pkg);

		notifyDataSetChanged();
	}

	@Override
	public void notifyAppInstalled(String pkg) {
		notifyDataSetChanged();
	}

	@Override
	public void notifyAppUninstalled(String pkg) {
		notifyDataSetChanged();
	}

	@Override
	public void notifyAppStatusChange(String pkg) {
		notifyDataSetChanged();
	}

	public void addApps(App[] apps) {
		if (apps == null)
			return;
		int count = apps.length;
		for (int i = 0; i < count; i++) {
			App app = apps[i];
			if (app != null) {
				this.apps.add(app);
			}
		}
	}

	@Override
	public App[] getApps() {
		return apps.toArray(new App[apps.size()]);
	}

	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public Object getItem(int position) {
		return apps.get(position);
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
		App app = null;
		try {
			app = apps.get(position);
		} catch (Exception e) {
			return convertView;
		}
		if (app == null)
			return convertView;
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
			Integer pct = downloadProgressDatas.get(app.getPkg());
			if (pct != null) {
				viewHolder.pgsBar.setProgress(pct);
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