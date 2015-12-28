package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.android.util.Logger;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import cn.com.vapk.vstore.client.ActionController.AppTitleInfo;
import cn.com.vapk.vstore.client.ActionController.MyRatingInfo;
import cn.com.vapk.vstore.client.usage.ClientUsageService;
import cn.com.vapk.vstore.client.R;

public class ASC extends ListActivity {

	private static final Logger L = Logger.getLogger(ASC.class);
	private String[] items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Logger.DEBUG)
			L.d("onCreate: " + getIntent().toString() + "\nTaskId: "
					+ getTaskId());

		Intent intent = getIntent();
		String action = intent.getAction();
		if (ActionController.smartUri(this, intent.getData())) {
			if (Logger.DEBUG)
				L.d("Smart Uri TaskId: " + getTaskId());
			finish();
			return;
		}
		if (ActionController.httpUri(this, intent.getData())) {
			if (Logger.DEBUG)
				L.d("Http Uri TaskId: " + getTaskId());
			finish();
			return;
		}
		if (cn.com.vapk.vstore.client.update.ActionController.ACTION_CHECK_APPS_VERSION_OPEN_MYDOWNLOAD
				.equals(action)) {
			ActionController.forward(this,
					ActionController.Intents.myDownload(this));
			finish();
			return;
		}
		
		ActionController.index(this);
		
		finish();
		return;

		// setContentView(R.layout.main);
		// items = getResources().getStringArray(R.array.activity_list);
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, android.R.id.text1, items);
		// setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		AppTitleInfo ati = new AppTitleInfo(
				"http://www.ithome.com.tw/img/newlogo_ithome.gif",
				"sti.test.iap01", "sti Title", "stiTest", 2);
		MyRatingInfo mri = new MyRatingInfo(
				"http://www.ithome.com.tw/img/newlogo_ithome.gif",
				"sti.test.iap01", "sti Title", "stiTest", 2, "", 3, true, true);

		String selected = items[position];
		if ("S Mart".equals(selected)) {
			ActionController.index(this);
		} else if ("Login".equals(selected)) {
			ActionController.formLogin(this);
		} else if ("Auto Login".equals(selected)) {
			ActionController.autoLogin(this);
		} else if ("IP Login".equals(selected)) {
			ActionController.ipLogin(this);
		} else if ("Logout".equals(selected)) {
			ActionController.logout(this);
		} else if ("EULA".equals(selected)) {
			ActionController.eula(this);
		} else if ("Feature Apps".equals(selected)) {
			ActionController.featureApps(this);
		} else if ("Categories".equals(selected)) {
			ActionController.categories(this);
		} else if ("Category Apps".equals(selected)) {
			ActionController.categoryApps(this, "", "");
		} else if ("App Detail".equals(selected)) {
			ActionController.appDetail(this, "sti.test.iap01", "104");
		} else if ("Search".equals(selected)) {
			ActionController.search(this);
		} else if ("Search Result".equals(selected)) {
			ActionController.searchResult(this, "");
		} else if ("My Downlaod".equals(selected)) {
			ActionController.myDownload(this);
		} else if ("App Comments".equals(selected)) {
			ActionController.appComments(this, ati);
		} else if ("Report App".equals(selected)) {
			ActionController.reportApp(this, ati);
		} else if ("Rate App".equals(selected)) {
			ActionController.rateApp(this, mri);
		} else if ("CP Apps".equals(selected)) {
			ActionController.cpApps(this, "", "");
		} else if ("Screen Shot".equals(selected)) {
			String[] str = {
					"http://blog.trendmicro.com/wp-content/uploads/2010/10/PE_LICAT.A1.gif",
					"http://www.ithome.com.tw/img/newlogo_ithome.gif",
					"http://www.ithome.com.tw/img/127/63742_1_1_l.jpg",
					"http://tw.ent3.yimg.com/mpost4/35/83/3583.jpg",
					"http://image.soft.idv.tw/blog_images3/2010/10/EmailFollowUpThen.com_13AC2/image_thumb.png",
					"http://image.soft.idv.tw/blog_images3/2010/10/EmailFollowUpThen.com_13AC2/image_3.png",
					"http://image.soft.idv.tw/blog_images3/2010/10/EmailFollowUpThen.com_13AC2/image.png",
					"http://www.blogcdn.com/www.engadget.com/media/2010/10/ballmer-today-2010-10-11-600.jpg",
					"http://www.blogcdn.com/www.engadget.com/media/2010/10/announcing-windows-phone-7--windows-mobile.jpg",
					"http://www.blogcdn.com/www.engadget.com/media/2010/10/2010-10-11186wp7launch.jpg" };
			ActionController.screenShot(this, str, 0);
		} else if ("Menu - setting".equals(selected)) {
			ActionController.setting(this);
		} else if ("Close App".equals(selected)) {
			ActionController.closeSMart(this);
		} else if ("Uninstall Reason App".equals(selected)) {
			ActionController.uninstallReasonApp(this, ati);
		} else if ("Start Client Usage Service".equals(selected)) {
			Intent sendLogIntent = new Intent(this, ClientUsageService.class);
			sendLogIntent.setAction("insert");
			// sendLogIntent.setAction(Intent.ACTION_BOOT_COMPLETED);
			startService(sendLogIntent);
			if (Logger.DEBUG) {
				L.d("startService");
			}
		} else if ("Update S Mart Notification".equals(selected)) {
			cn.com.vapk.vstore.client.update.ActionController
					.checkClientVersion(this);
		} else if ("Update App Notification".equals(selected)) {
			cn.com.vapk.vstore.client.update.ActionController
					.checkAppsVersion(this);
		} else if ("Install App".equals(selected)) {
			cn.com.vapk.vstore.client.installapp.ActionController.installApp(
					this, "hsiang.inapp01", "-3", "YH 購買01", 1);
		}
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}
}