package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.CommonRet;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.ActionController.AppTitleInfo;
import cn.com.vapk.vstore.client.ActionController.MyRatingInfo;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.BitmapUtils;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class RateAppActivity extends Activity implements OnClickListener {
	private static final Logger L = Logger.getLogger(RateAppActivity.class);

	private String packageName;
	private String commentText;
	private float rating;

	private RatingBar ratingBar;
	private EditText commentEditText;

	private ApiInvoker<CommonRet> apiInvoker;
	private CloseClientReceiver closeClientReceiver;

	private static final int DLG_LOADING = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_app);

		ratingBar = (RatingBar) findViewById(R.id.rating);
		commentEditText = (EditText) findViewById(R.id.comment_text);
		Button submitBtn = (Button) findViewById(R.id.submit);
		Button cancelBtn = (Button) findViewById(R.id.cancel);

		Intent intent = getIntent();
		MyRatingInfo mri = (MyRatingInfo) intent
				.getSerializableExtra(ActionController.EXTRA_MY_RATING_INFO);
		init(mri);

		commentEditText.setText(mri.comment);
		ratingBar.setRating(mri.rating);

		if (Logger.DEBUG) {
			L.d("Comment: " + mri.comment);
			L.d("Rating: " + mri.rating);
		}

		ratingBar.setIsIndicator(!mri.ratingable);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();

		submitBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (apiInvoker != null)
			apiInvoker.stop();
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit: {
			commentText = commentEditText.getText().toString();
			rating = ratingBar.getRating();

			if (Logger.DEBUG) {
				L.d("commentText: " + commentText);
				L.d("rating: " + rating);
			}

			if (rating == 0) {
				DialogUtils.createAlertDialog(this, R.string.rate_empty_msg)
						.show();
				break;
			} else if (commentText.length() > 500) { // 可以無評論，但最多500個字
				if (Logger.DEBUG)
					L.d("commentText.length() > 500, length: "
							+ commentText.length());
				DialogUtils.createAlertDialog(this, R.string.rate_alert_msg)
						.show();
				break;
			} else
				requestRateApp();

			showDialog(DLG_LOADING);
			break;
		}
		case R.id.cancel: {
			if (Logger.DEBUG)
				L.d("Finish activity for cancel");

			this.finish();
			break;
		}
		}
	}

	private void init(final AppTitleInfo ati) {
		if (Logger.DEBUG) {
			L.d("Icon URL: " + ati.icon);
			L.d("Provider: " + ati.provider);
			L.d("Title: " + ati.title);
			L.d("Rating: " + ati.rating);
			L.d("packageName: " + ati.pkg);
		}

		packageName = ati.pkg;

		UI.bindText(this, R.id.app_provider, ati.provider);
		UI.bindText(this, R.id.app_title, ati.title);
		UI.bindRating(this, R.id.app_rating, ati.rating);

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

	private void requestRateApp() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}
		Integer appVersion = null;
		try {
			PackageInfo pi = getApplicationContext().getPackageManager()
					.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			appVersion = pi.versionCode;
		} catch (NameNotFoundException e) {
			if (Logger.ERROR)
				L.e("appVersion null ", e);
		}

		AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		apiInvoker = apiService.rateApp(packageName, appVersion, (int) rating,
				commentText);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				apiInvoker.invoke();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				responseRateApp();
			}
		}.execute();
	}

	private void responseRateApp() {
		removeDialog(DLG_LOADING);

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;

		// Success
		this.finish();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		case DLG_LOADING: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.sendind_dot));
			dialog.setCancelable(false);
			return dialog;
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
}