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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.ActionController.AppTitleInfo;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.BitmapUtils;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class ReportAppActivity extends Activity implements OnClickListener {
	private static final Logger L = Logger.getLogger(ReportAppActivity.class);

	private int checkedItem = 0;
	private String packageName;

	private ApiInvoker<CommonRet> apiInvoker;
	private CloseClientReceiver closeClientReceiver;

	private RadioButton option_01, option_02, option_03, option_04;

	private static final int DLG_LOADING = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_app);

		UI.bindHTML(this, R.id.act_title_text, getString(R.string.report_title));
		Intent intent = getIntent();
		final AppTitleInfo ati = (AppTitleInfo) intent
				.getSerializableExtra(ActionController.EXTRA_APP_TITLE_INFO);

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

		option_01 = (RadioButton) findViewById(R.id.option_01);
		option_02 = (RadioButton) findViewById(R.id.option_02);
		option_03 = (RadioButton) findViewById(R.id.option_03);
		option_04 = (RadioButton) findViewById(R.id.option_04);

		RadioGroup radio_group = (RadioGroup) findViewById(R.id.radio_group);
		radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == option_01.getId()) {
					checkedItem = 1;
				} else if (checkedId == option_02.getId()) {
					checkedItem = 2;
				} else if (checkedId == option_03.getId()) {
					checkedItem = 3;
				} else if (checkedId == option_04.getId()) {
					checkedItem = 4;
				}

				if (Logger.DEBUG)
					L.d("Checked Item: " + checkedItem);
			}
		});

		Button submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
		Button cancelBtn = (Button) findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(this);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
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
		case R.id.submit:
			if (checkedItem == 0) {
				DialogUtils.createAlertDialog(this, R.string.select_empty_msg)
						.show();
				break;
			} else
				requestReportApp(checkedItem);

			showDialog(DLG_LOADING);

			break;
		case R.id.cancel:
			if (Logger.DEBUG)
				L.d("Finish activity for cancel");

			this.finish();
			break;
		}
	}

	private void requestReportApp(int checkedItem) {
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
		apiInvoker = apiService.reportApp(packageName, appVersion, checkedItem);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				apiInvoker.invoke();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				responseReportApp();
			}
		}.execute();
	}

	private void responseReportApp() {
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