package cn.com.vapk.vstore.client;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.vapk.vstore.client.R;

public final class UI {

	final static void stopProgressing(Activity activity) {
		activity.findViewById(R.id.progressing).setVisibility(View.GONE);
	}

	final static void startProgressing(Activity activity) {
		activity.findViewById(R.id.progressing).setVisibility(View.VISIBLE);
	}

	final static boolean handleMenuOnSelectEvent(Activity activity,
			MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home:
			ActionController.featureApps(activity);
			return true;
		case R.id.menu_logout:
			ActionController.logout(activity);
			FeatureAppsActivity.setLoginFlag(false);
			return true;
		case R.id.menu_search:
			ActionController.search(activity);
			return true;
		case R.id.menu_setting:
			ActionController.setting(activity);
			return true;
		default:
			return false;
		}
	}

	final static boolean handleTabOnClickEvent(Activity activity, View v) {
		switch (v.getId()) {
		case R.id.tab_item_feature:
			ActionController.featureApps(activity);
			return true;
		case R.id.tab_item_class:
			ActionController.categories(activity);
			return true;
		case R.id.tab_item_search:
			ActionController.search(activity);
			return true;
		case R.id.tab_item_download:
			ActionController.myDownload(activity);
			return true;
		default:
			return false;
		}
	}

	final static void bindTabOnClickListener(Activity aty, OnClickListener ocl) {
		int[] ids = new int[] { R.id.tab_item_feature, R.id.tab_item_download,
				R.id.tab_item_search, R.id.tab_item_class };
		for (int id : ids) {
			View v = aty.findViewById(id);
			boolean clickable = v.isClickable();
			if (clickable)
				v.setOnClickListener(ocl);
		}
	}

	final static void bindText(View root, int viewId, String text) {
		if (root == null)
			return;
		if (text == null)
			text = "";
		TextView tv = (TextView) root.findViewById(viewId);
		if (tv != null)
			tv.setText(text);
	}

	public final static void bindText(Activity aty, int viewId, String text) {
		if (aty == null)
			return;
		if (text == null)
			text = "";
		TextView tv = (TextView) aty.findViewById(viewId);
		if (tv != null)
			tv.setText(text);
	}

	final static void bindHTML(View root, int viewId, String text) {
		if (root == null)
			return;
		if (text == null)
			text = "";
		TextView tv = (TextView) root.findViewById(viewId);
		if (tv != null)
			tv.setText(Html.fromHtml(text));
	}

	final static void bindHTML(Activity aty, int viewId, String text) {
		if (aty == null)
			return;
		if (text == null)
			text = "";
		TextView tv = (TextView) aty.findViewById(viewId);
		if (tv != null)
			tv.setText(Html.fromHtml(text));
	}

	final static void bindRating(View root, int viewId, float rating) {
		if (root == null)
			return;

		RatingBar rb = (RatingBar) root.findViewById(viewId);
		if (rb != null)
			rb.setRating(rating);
	}

	final static void bindRating(Activity aty, int viewId, float rating) {
		if (aty == null)
			return;

		RatingBar rb = (RatingBar) aty.findViewById(viewId);
		if (rb != null)
			rb.setRating(rating);
	}

	final static void invisibleView(Activity aty, int viewId) {
		if (aty == null)
			return;
		View v = aty.findViewById(viewId);
		if (v != null)
			v.setVisibility(View.INVISIBLE);
	}

	final static void visibleView(Activity aty, int viewId) {
		if (aty == null)
			return;
		View v = aty.findViewById(viewId);
		if (v != null)
			v.setVisibility(View.VISIBLE);
	}

	final static void toast(Context ctx, int resId) {
		Toast.makeText(ctx, resId, Toast.LENGTH_LONG).show();
	}
}
