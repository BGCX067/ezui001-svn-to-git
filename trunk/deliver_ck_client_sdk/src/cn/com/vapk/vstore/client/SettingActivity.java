package cn.com.vapk.vstore.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.ActionController.SettingInfo;

public class SettingActivity extends ListActivity implements
		DialogInterface.OnClickListener {
	private static final Logger L = Logger.getLogger(SettingActivity.class);

	private boolean appFilterSettingEnable;
	private int appFilter, userFilter;
	private CloseClientReceiver closeClientReceiver;

	private AlertDialog alertDialog;
	private Builder aboutBuilder, filterBuilder;
	private LayoutInflater mInflater;
	private View dialogView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		Intent intent = getIntent();
		SettingInfo settingInfo = (SettingInfo) intent
				.getSerializableExtra(ActionController.EXTRA_SETTING_INFO);

		appFilterSettingEnable = settingInfo.appFilterSettingEnable;
		appFilter = settingInfo.appFilter;
		if (Logger.DEBUG) {
			L.d("appFilterSettingEnable:  " + appFilterSettingEnable);
			L.d("appFilter:  " + appFilter);
		}

		aboutBuilder = new AlertDialog.Builder(this);
		filterBuilder = new AlertDialog.Builder(this);
		mInflater = getLayoutInflater();

		settingList();

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	private void settingList() {
		String filterTitle = getString(R.string.filter_title);
		String aboutSMart = getString(R.string.about_s_mart);

		List<String> list = new ArrayList<String>();

		if (appFilterSettingEnable) {
//			list.add(filterTitle);
			list.add(aboutSMart);
		} else
			list.add(aboutSMart);

//		if (Server.RUNTIME == Server.SERVER_DEVELOPER
//				|| Server.RUNTIME == Server.SERVER_STAGING) {
//			int x = 8 * 60 * 60 * 1000;
//			int checkTime = CheckClientVersionReceiver.getCheckTime(this);
//			if (checkTime != -1)
//				list.add("Check Client Version Time: "
//						+ LangUtils.formatDate(checkTime - x, "HH:mm:ss"));
//			checkTime = CheckAppsVersionReceiver.getCheckAppsTime(this);
//			if (checkTime != -1)
//				list.add("Check App Version Time: "
//						+ LangUtils.formatDate(checkTime - x, "HH:mm:ss"));
//		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.setting_list_item, R.id.listText, list);
		getListView().setAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (!appFilterSettingEnable) {
			position++;
		}
		switch (position) {
		case 0:
//			showFilterDialog();
			showAboutDialog();
			break;
		case 1:
			showAboutDialog();
			break;
		}
	}

	// About S mart
	private void showAboutDialog() {
		dialogView = mInflater.inflate(R.layout.setting_about_s_mart_dialog,
				null);

		try {
			PackageInfo pi = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_ACTIVITIES);
			UI.bindText(dialogView, R.id.version_text, pi.versionName);
		} catch (NameNotFoundException e) {
			if (Logger.DEBUG)
				L.d("NameNotFoundException: ", e);
		}

		String thisYear = LangUtils.formatDate(new Date(), "yyyy");
		UI.bindText(dialogView, R.id.copyright_text,
				String.format(getString(R.string.copyright), thisYear));
		aboutBuilder.setTitle(getString(R.string.about_s_mart))
				.setPositiveButton(getString(R.string.submit), null);

		alertDialog = aboutBuilder.create();
		alertDialog.setView(dialogView, 0, 0, 0, 0);
		alertDialog.show();
	}

	// Filter
	private void showFilterDialog() {
		int width;
		int height;
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();
		try {
			Class<Display> cls = Display.class;
			Method method = cls.getMethod("getRotation");
			Object retobj = method.invoke(display);
			int rotation = Integer.parseInt(retobj.toString());
			if (Surface.ROTATION_0 == rotation
					|| Surface.ROTATION_180 == rotation) {
				width = display.getWidth();
				height = display.getHeight();
			} else {
				width = display.getHeight();
				height = display.getWidth();
			}
		} catch (Exception e) {
			if (display.getOrientation() == 1) {
				width = display.getHeight();
				height = display.getWidth();
			} else {
				width = display.getWidth();
				height = display.getHeight();
			}
		}

		String os = String.format(getString(R.string.filter_os),
				Build.VERSION.RELEASE);
		String matrix = String.format(getString(R.string.filter_matrix), width,
				height);

		dialogView = mInflater.inflate(R.layout.setting_filter_dialog, null);

		UI.bindHTML(dialogView, R.id.os_text, os);
		UI.bindHTML(dialogView, R.id.matrix_text, matrix);

		final CharSequence[] items = { getString(R.string.filter_off),
				getString(R.string.filter_on) };

		filterBuilder.setTitle(getString(R.string.filter_title))
				.setSingleChoiceItems(items, appFilter, this)
				.setPositiveButton(getString(R.string.submit), this);
		alertDialog = filterBuilder.create();
		alertDialog.setView(dialogView, 0, 0, 0, 0);
		alertDialog.show();
	}

	public void onClick(DialogInterface dialog, int item) {
		if (Logger.DEBUG)
			L.d("filter item  " + item + " is selected");

		switch (item) {
		case -1:
			if (userFilter != appFilter) {
				ActionController.changeAppFilter(this, userFilter);
				if (Logger.DEBUG)
					L.d("Send the filter setting.");
			}
			break;
		case 0:
			userFilter = item;
			break;
		case 1:
			userFilter = item;
			break;
		}
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}
}