package cn.com.vapk.vstore.client.update;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.UI;
import cn.com.vapk.vstore.client.update.ActionController.NewClientAlertInfo;
import cn.com.vapk.vstore.client.util.DebugUtils;
import cn.com.vapk.vstore.client.util.NetworkUtils;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/*
 * Alert message before download:
 * 1. no network -> alert words
 * 2. no SD card -> alert words
 * 3. available SD card space -> alert words
 * ===============================================
 * Need:
 * 1. package size to make sure SD card available space
 */
public class NewClientAlertDialogActivity extends Activity implements
		OnClickListener {
	private final Logger L = Logger
			.getLogger(NewClientAlertDialogActivity.class);

	private TextView errorMsg;
	private NewClientAlertInfo ncai;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (Logger.DEBUG)
			L.d("TaskId: " + getTaskId() + ", Categories: "
					+ getIntent().getCategories() + ", Flags: "
					+ DebugUtils.flags(getIntent().getFlags()));
		super.onCreate(savedInstanceState);

		ncai = (NewClientAlertInfo) getIntent().getSerializableExtra(
				ActionController.EXTRA_NEW_CLIENT_ALERT_INFO);

		if (ncai == null) {
			if (Logger.DEBUG)
				throw new RuntimeException("NewClientAlertInfo == null");
			finish();
			return;
		}

		if (Logger.DEBUG && ncai.apkBytes < 1)
			throw new RuntimeException("apkBytes: " + ncai.apkBytes);

		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.new_client_alert_dialog);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				R.drawable.icon);

		String message = ncai.dialogMsg;
		String title = ncai.title;
		if (LangUtils.isBlankAny(message, title) > -1) {
			if (Logger.DEBUG) {
				throw new RuntimeException(
						"LangUtils.isBlankAny(dialogMsg, title) at "
								+ LangUtils.isBlankAny(message, title));
			}

			ActionController.downloadClient(this, ncai);
			finish();
			return;
		}

		UI.bindText(this, R.id.message, message);
		setTitle(title);

		findViewById(R.id.submit).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);

		errorMsg = (TextView) findViewById(R.id.error);
		checkError();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (Logger.DEBUG)
			L.d("onDestroy TaskId: " + getTaskId());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			if (checkError()) {
				errorMsg.setVisibility(View.INVISIBLE);
				ActionController.downloadClient(this, ncai);
				finish();
			}
			return;
		case R.id.cancel:
			finish();
			return;
		}
	}

	private boolean checkError() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			errorMsg.setText(getString(R.string.dlg_no_network));
			return false;
		}
		if (!checkStoreSpace()) {
			return false;
		}
		return true;
	}

	private boolean checkStoreSpace() {
		final String dirState = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(dirState)) {
			errorMsg.setText(getString(R.string.dlg_sd_unmounted));
			return false;
		}

		final StatFs space = new StatFs(Environment
				.getExternalStorageDirectory().getAbsolutePath());
		final long freeSize = (long) space.getAvailableBlocks()
				* (long) space.getBlockSize();

		if (freeSize > ncai.apkBytes) {
			return true;
		}

		errorMsg.setText(getString(R.string.dlg_sd_space_full));
		if (Logger.DEBUG) {
			L.d("freeSize: " + freeSize);
			L.d("pkgSize: " + ncai.apkBytes);
		}
		return false;
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}
}