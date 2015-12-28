package cn.com.vapk.vstore.client;

import java.util.ArrayList;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.CategoriesRet;
import tw.com.sti.store.api.vo.CategoriesRet.Category;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.ImageDownloader;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class CategoriesActivity extends ListActivity implements OnClickListener {

	private static final Logger L = Logger.getLogger(CategoriesActivity.class);

	private ApiInvoker<CategoriesRet> apiInvoker;
	private CloseClientReceiver closeClientReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories);
		requestCategories();

		UI.bindTabOnClickListener(this, this);
		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
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
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Category category = (Category) getListAdapter().getItem(position);
		ActionController.categoryApps(this, category.getId(),
				category.getTitle());
	}

	private void requestCategories() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.categories(null);
				apiInvoker.invoke();
				return null;
			}

			protected void onPostExecute(Void result) {
				responseCategories();
			};
		}.execute();
	}

	private void responseCategories() {
		UI.stopProgressing(this);

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;

		CategoriesRet categoriesRet = apiInvoker.getRet();
		if (categoriesRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("appsRet.isSuccess()");
			displayCategories(categoriesRet.getCategories());
			return;
		}

	}

	private void displayCategories(Category[] apps) {
		setListAdapter(new CategoriesAdapter(apps));
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
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		}
		return null;
	}

	class CategoriesAdapter extends BaseAdapter {

		private ArrayList<CategoriesRet.Category> categories;
		private LayoutInflater layoutInflater;
		private int evenColor;
		private int oddColor;
		private final ImageDownloader imageDownloader;

		public CategoriesAdapter(Category[] apps) {
			imageDownloader = new ImageDownloader();
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.def_app_icon);
			imageDownloader.defalutImage = bitmap;
			this.categories = new ArrayList<CategoriesRet.Category>(60);
			this.layoutInflater = getLayoutInflater();
			Resources r = getResources();
			evenColor = r.getColor(R.color.bg_list_item_even);
			oddColor = r.getColor(android.R.color.transparent);
			addCategories(apps);
		}

		private void addCategories(Category[] categories) {
			int count = categories.length;
			for (int i = 0; i < count; i++) {
				Category category = categories[i];
				if (category != null) {
					this.categories.add(category);
				}
			}
		}

		@Override
		public int getCount() {
			return categories.size();
		}

		@Override
		public Object getItem(int position) {
			return categories.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.category_item,
						null);
				convertView.setTag(new ViewHolder(convertView));
			}

			if (position % 2 == 1) { // 偶數行換色
				convertView.setBackgroundColor(evenColor);
			} else {
				convertView.setBackgroundColor(oddColor);
			}

			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			Category category = categories.get(position);
			viewHolder.title.setText(category.getTitle());
			if (LangUtils.isBlank(category.getIcon())) {
				Drawable drawable = getResources().getDrawable(
						R.drawable.def_app_icon);
				viewHolder.icon.setImageDrawable(drawable);
			} else {
				imageDownloader.download(category.getIcon(), viewHolder.icon);
			}
			if (category.getNewAppCount() > 0) {
				viewHolder.newAppCount.setText("(" + category.getNewAppCount()
						+ ")");
				viewHolder.newAppCount.setVisibility(View.VISIBLE);
			} else {
				viewHolder.newAppCount.setVisibility(View.GONE);
			}
			if (category.isHasNewApp()) {
				viewHolder.hasNewApp.setVisibility(View.VISIBLE);
			} else {
				viewHolder.hasNewApp.setVisibility(View.GONE);
			}

			return convertView;
		}

		private class ViewHolder {
			ImageView icon;
			TextView title;
			TextView newAppCount;
			ImageView hasNewApp;

			ViewHolder(View view) {
				icon = (ImageView) view.findViewById(R.id.icon);
				title = (TextView) view.findViewById(R.id.title);
				newAppCount = (TextView) view.findViewById(R.id.new_app_count);
				hasNewApp = (ImageView) view.findViewById(R.id.has_new_app);
			}
		}
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}
}