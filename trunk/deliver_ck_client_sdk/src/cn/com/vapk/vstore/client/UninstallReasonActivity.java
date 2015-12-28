package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import cn.com.vapk.vstore.client.installapp.AppInstallLogReceiver;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.BitmapUtils;

public class UninstallReasonActivity extends Activity implements
		OnClickListener {
	private static final Logger L = Logger
			.getLogger(UninstallReasonActivity.class);

	private int checkedItem;
	private String packageName;
	private CloseClientReceiver closeClientReceiver;

	private RadioButton option_01, option_02, option_03, option_04;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uninstall_reason);

		UI.bindHTML(this, R.id.act_title_text,
				getString(R.string.uninstall_reason_title));
		Intent intent = getIntent();
		final AppTitleInfo ati = (AppTitleInfo) intent
				.getSerializableExtra(ActionController.EXTRA_APP_TITLE_INFO);
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
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			if (checkedItem == 0) {
				DialogUtils.createAlertDialog(this, R.string.select_empty_msg)
						.show();
			} else
				uninstallApp();
			break;
		case R.id.cancel:
			this.finish();
			break;
		}
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	// Old Version Client

	private void uninstallApp() {
		AppInstallLogReceiver.UNINSTALL_REASON_ID.put(packageName, checkedItem);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DELETE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.parse("package:" + packageName);
		intent.setData(uri);
		startActivity(intent);
		finish();
	}
}