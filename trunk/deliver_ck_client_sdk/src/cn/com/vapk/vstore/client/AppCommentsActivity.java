package cn.com.vapk.vstore.client;

import java.util.ArrayList;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.CommentsRet;
import tw.com.sti.store.api.vo.CommentsRet.Comment;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.ActionController.AppTitleInfo;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.BitmapUtils;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

/*
 * Finish activity for:
 * without package name
 */
public class AppCommentsActivity extends ListActivity implements
		OnClickListener {
	private static final Logger L = Logger.getLogger(AppCommentsActivity.class);

	private ApiInvoker<CommentsRet> apiInvoker;
	private String packageName;
	private int pageNumber = 1;
	private CommentAdapter commentAdapter;
	private CloseClientReceiver closeClientReceiver;

	private boolean pageEnd;
	private boolean loading;
	private View footProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_comments);
//		findViewById(R.id.backBtn).setOnClickListener(this);

		footProgress = getLayoutInflater().inflate(R.layout.list_loading, null);
		getListView().addFooterView(footProgress);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();

		setAppTitleBar();
		requestAppComments(null);//傳入null表示使用預設的psize，可視需要傳入自定義值(此activity有兩處requestAppComments須設定)
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (apiInvoker != null)
			apiInvoker.stop();

		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	private void setAppTitleBar() {
		Intent intent = getIntent();
		final AppTitleInfo ati = (AppTitleInfo) intent
				.getSerializableExtra(ActionController.EXTRA_APP_TITLE_INFO);

		if (Logger.DEBUG) {
			L.d("Icon URL: " + ati.icon);
			L.d("Provider: " + ati.provider);
			L.d("Title: " + ati.title);
			L.d("Rating: " + ati.rating);
			L.d("pkg: " + ati.pkg);
		}

		if (LangUtils.isBlank(ati.pkg)) {
			finish();
		} else {
			UI.bindText(this, R.id.app_provider, ati.provider);
			UI.bindText(this, R.id.app_title, ati.title);
			UI.bindRating(this, R.id.app_rating, ati.rating);

			packageName = ati.pkg;
		}

		if (ati.rating == 0)
			UI.invisibleView(this, R.id.app_rating);

		if (!LangUtils.isBlank(ati.icon)) {
			new AsyncTask<Void, Void, Void>() {
				Bitmap icon;

				@Override
				protected Void doInBackground(Void... params) {
					icon = BitmapUtils.downloadBitmap(ati.icon);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					((ImageView) findViewById(R.id.app_icon))
							.setImageBitmap(icon);
					super.onPostExecute(result);
				}
			}.execute();
		}
	}

	private void requestAppComments(final Integer pSize) {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		final AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// packageName = "com.iKala.FEPlayer";
				apiInvoker = apiService.appComments(packageName, pageNumber, pSize);
				apiInvoker.invoke();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				responseAppComments();
				loading = false;
			}
		}.execute();
	}

	private void responseAppComments() {
		UI.stopProgressing(this);

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;

		CommentsRet commentsRet = apiInvoker.getRet();
		Comment[] comments = commentsRet.getComments();
		int commentSize = comments.length;

		if (Logger.DEBUG)
			L.d("get " + commentSize + " comments");

		if (commentsRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("appsRet.isSuccess()");

			if (commentSize == 0 || comments == null) {
				if (Logger.DEBUG)
					L.d("No comments!");
				return;
			}

			if (commentAdapter == null) {
				commentAdapter = new CommentAdapter();
				setListAdapter(commentAdapter);
			}

			pageEnd = commentsRet.isPageEnd();
			if (pageEnd) {
				getListView().removeFooterView(footProgress);
			}

			commentAdapter.addItem(comments);
		}
	}

	private class CommentAdapter extends BaseAdapter {

		private ArrayList<Comment> commentList;
		private LayoutInflater mInflater;

		public CommentAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (commentList == null)
				commentList = new ArrayList<Comment>();
		}

		public void addItem(Comment[] comments) {

			for (int i = 0; i < comments.length; i++) {
				if (comments[i] != null) {
					commentList.add(comments[i]);
				}
			}

			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return commentList.size();
		}

		@Override
		public Object getItem(int position) {
			return commentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int loadPoint = 3;
			ViewHolder holder = null;
			Resources res = getResources();

			/*
			 * 若已到loadPoint且並不是最後一頁資料 則再向server request
			 */
			if ((position + loadPoint == getCount()) && !pageEnd) {
				nextPage();
			}

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.comment_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if ((position % 2) == 0) { // 偶數行換色
				convertView.setBackgroundColor(res
						.getColor(R.color.bg_list_item_even));
			} else {
				convertView.setBackgroundColor(res
						.getColor(android.R.color.transparent));
			}

			Comment c = commentList.get(position);

			String headerText = String.format(
					res.getString(R.string.comment_header), c.getPoster(),
					c.getDate());
			UI.bindText(convertView, R.id.comment_header, headerText);
			UI.bindText(convertView, R.id.comment_text, c.getContent());
			UI.bindRating(convertView, R.id.comment_rating, c.getRating());

			return convertView;
		}
	}
	
	private synchronized void nextPage() {
		if (pageEnd || loading) {
			return;
		}
		loading = true;
		pageNumber++;
		if (Logger.DEBUG)
			L.d("page number: " + pageNumber);
		requestAppComments(null);//傳入null表示使用預設的psize，可視需要傳入自定義值(此activity有兩處requestAppComments須設定)
	}
	
	// Comment list item
	static class ViewHolder {
		RatingBar comment_rating;
		TextView comment_header;
		TextView comment_text;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		}
		return null;
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		finish();
		return false;
	}

	@Override
	public void onClick(View v) {
//		if (v.getId() == R.id.backBtn)
//			finish();
	}
}