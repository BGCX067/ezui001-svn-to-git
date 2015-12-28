package cn.com.vapk.vstore.client;

import java.util.List;
import java.util.Map;

import tw.com.sti.clientsdk.LicenseTool;
import tw.com.sti.clientsdk.ShoppingCartActivity;
import tw.com.sti.security.util.Base64Coder;
import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.AppUtils;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.ApplicationRet;
import tw.com.sti.store.api.vo.ApplicationRet.Application;
import tw.com.sti.store.api.vo.ApplicationRet.Application.Action;
import tw.com.sti.store.api.vo.CheckPayStatusRet;
import tw.com.sti.store.api.vo.CommentsRet.Comment;
import tw.com.sti.store.api.vo.CommonRet;
import tw.com.sti.store.api.vo.LicenseInfoRet;
import tw.com.sti.store.api.vo.LicenseInfoRet.LicenseInfo;
import tw.com.sti.store.api.vo.OrderInfo;
import tw.com.sti.store.api.vo.OrderRet;
import tw.com.sti.store.api.vo.PePayOrderRet;
import tw.com.sti.store.api.vo.UnsubscribeRet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.com.vapk.vstore.client.ActionController.AppTitleInfo;
import cn.com.vapk.vstore.client.ActionController.MyRatingInfo;
import cn.com.vapk.vstore.client.api.AppStatusReceiver;
import cn.com.vapk.vstore.client.installapp.AppInstallReceiver;
import cn.com.vapk.vstore.client.installapp.DownloadAppService;
import cn.com.vapk.vstore.client.receiver.LoginEventReceiver;
import cn.com.vapk.vstore.client.receiver.listener.LoginEvent;
import cn.com.vapk.vstore.client.receiver.listener.LoginEventListener;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.BitmapUtils.BitmapDownloaderTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.ImageDownloader;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class AppDetailActivity extends Activity implements OnClickListener,LoginEventListener {

	private static final Logger L = Logger.getLogger(AppDetailActivity.class);

	private static final int DIALOG_NOT_ALLOW_INSTALL_NON_MARKET_APPS = 1;
	private static final int DIALOG_UNABLE_LAUNCH_APP = 2;
	private static final int DIALOG_INSTALL_MSG = 3;
	private static final int DIALOG_UNSUBSCRIBE_CONFIRM = 4;
	private static final int DIALOG_UNINSTALL_CONFIRM = 5;

	private String categoryId;
	private String pkg;
	private Application app;
	private ImageButton screenShot;
	private ViewGroup screenShotDots;
	private int screenShotPosition;
	private String[] screenShotImgUrls;
	private ImageDownloader imageDownloader;
	private AppInstallReceiver appInstallReceiver;
	private Button[] buttons;
	private View screenShotNext;
	private View screenShotPrevious;
	private AsyncTask<Void, Void, ApiInvoker<ApplicationRet>> task;
	private CloseClientReceiver closeClientReceiver;
	private LoginEventReceiver loginEventReceiver;

	private View downloadBox;
	private View buttonBox;
	private ProgressBar donwloadPregorss;
	private TextView donwloadPregorssPct;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		categoryId = intent.getStringExtra(ActionController.EXTRA_CATEGORY_ID);
		pkg = intent.getStringExtra(ActionController.EXTRA_PACKAGE_NAME);
		if (LangUtils.isBlankAny(categoryId, pkg) > -1) {
			if (Logger.DEBUG)
				throw new RuntimeException(
						"LangUtils.isBlankAny(categoryId, pkg) at: "
								+ LangUtils.isBlankAny(categoryId, pkg));
			finish();
			return;
		}

		setContentView(R.layout.app_detail);
		buttonBox = findViewById(R.id.button_box);

		UI.bindTabOnClickListener(this, this);
		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
		loginEventReceiver = new LoginEventReceiver(this);
		loginEventReceiver.register();
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestApplication();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (Logger.DEBUG)
			L.d("onDestroy");
		if (task != null)
			task.cancel(true);
		if (appInstallReceiver != null)
			appInstallReceiver.unregister(this);
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
		if (loginEventReceiver != null)
			loginEventReceiver.unregister();
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

	private void requestApplication() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		if (task != null) {
			task.cancel(true);
			task = null;
		}
		task = new AsyncTask<Void, Void, ApiInvoker<ApplicationRet>>() {
			private ApiInvoker<ApplicationRet> apiInvoker;

			protected void onCancelled() {
				if (Logger.DEBUG)
					L.d("AsyncTask onCancelled");
				if (apiInvoker != null)
					apiInvoker.stop();
				apiInvoker = null;
			};

			@Override
			protected ApiInvoker<ApplicationRet> doInBackground(Void... params) {
				if (Logger.DEBUG)
					L.d("AsyncTask doInBackground");
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.application(pkg);
				apiInvoker.invoke();
				return apiInvoker;
			}

			protected void onPostExecute(ApiInvoker<ApplicationRet> apiInvoker) {
				if (Logger.DEBUG)
					L.d("AsyncTask onPostExecute");
				if (apiInvoker != null)
					responseApplication(apiInvoker);
			};
		}.execute();
	}

	// 因為 onResume 有重新讀取，會有多緒的問題，所以要把apiInvoker傳過來
	private void responseApplication(ApiInvoker<ApplicationRet> apiInvoker) {
		if (Logger.DEBUG)
			L.d("responseApplication");

		task = null;
		if (isFinishing())
			return;

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;

		ApplicationRet appRet = apiInvoker.getRet();
		if (appRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("appsRet.isSuccess()");
			displayApplication(appRet.getApplication());
			return;
		}

	}

	private void displayApplication(final Application app) {
		this.app = app;

		UI.stopProgressing(this);
		UI.bindText(this, R.id.app_title, app.getTitle());
		UI.bindText(this, R.id.app_provider, app.getProvider());
		UI.bindText(this, R.id.download_times, app.getDownloadTimes());
		UI.bindText(this, R.id.rating_times, app.getRatingTimes());
		UI.bindText(this, R.id.app_status, app.getPriceText());
		if (app.getRating() == 0)
			UI.invisibleView(this, R.id.app_rating);
		else {
			UI.bindRating(this, R.id.app_rating, app.getRating());
			UI.visibleView(this, R.id.app_rating);
			AppStatusReceiver.boardcastRating(this, app.getPkg(),
					app.getRating());
		}

		UI.bindText(this, R.id.introduction, app.getIntroduction());
		UI.bindText(this, R.id.about, app.getAbout());

		findViewById(R.id.link_provider_apps).setOnClickListener(this);
		findViewById(R.id.link_provider_web_site).setOnClickListener(this);
		findViewById(R.id.link_provider_contact).setOnClickListener(this);
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(this);
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(this);
//		Button button3 = (Button) findViewById(R.id.button3);
//		button3.setOnClickListener(this);
		buttons = new Button[] { button1, button2 };

		displayIcon(app.getIcon());
		displayScreenShot(app.getScreenShots());
		displayComments(app.getComments());
		if(app.isCommentable()){
			displayMyRating(app.getMyRating());
		}
		displayReportApp(app.isReportable());
		if (DownloadAppService.isInDownload(app.getPkg()))
			diaplayDownloadPregress(0);
		displayActionButton();
		registerAppInstallReceiver();
	}

	private void registerAppInstallReceiver() {
		if (appInstallReceiver != null)
			return;
		appInstallReceiver = new AppInstallReceiver() {
			private String pkg = app.getPkg();

			@Override
			protected void downloadFail(String pkg) {
				downloadFinish(pkg);
			}

			@Override
			protected void downloadFinish(String pkg) {
				if (!this.pkg.equals(pkg))
					return;
				if (downloadBox != null)
					downloadBox.setVisibility(View.GONE);
				buttonBox.setVisibility(View.VISIBLE);
			}

			@Override
			protected void downloadPercent(String pkg, int percent) {
				if (!this.pkg.equals(pkg))
					return;
				diaplayDownloadPregress(percent);
			}

			@Override
			protected void install(Context ctx, String packageName) {
				if (!this.pkg.equals(pkg))
					return;
				displayActionButton();
			}

			@Override
			protected void replace(Context ctx, String packageName) {
				install(ctx, packageName);
			}

			@Override
			protected void remove(Context ctx, String packageName) {
				install(ctx, packageName);
			}
		};
		appInstallReceiver.register(this);
	}

	private void displayIcon(String url) {
		ImageView icon = (ImageView) findViewById(R.id.app_icon);
		if (icon.getTag() != null)
			return;
		icon.setTag("inited");
		new BitmapDownloaderTask(icon).execute(url);
	}

	private void displayScreenShot(String[] imgUrls) {
		screenShot = (ImageButton) findViewById(R.id.screen_shot);
		if (screenShot.getTag() != null)
			return;
		screenShot.setTag("inited");
		screenShot.setOnClickListener(this);
		screenShotNext = findViewById(R.id.next);
		screenShotPrevious = findViewById(R.id.previous);
		screenShotDots = (ViewGroup) findViewById(R.id.screen_shot_dots);
		int imgs = imgUrls == null ? 0 : imgUrls.length;
		if (imgs <= 1) {
			screenShotNext.setVisibility(View.GONE);
			screenShotPrevious.setVisibility(View.GONE);
			screenShotDots.setVisibility(View.GONE);
			if (imgs == 0)
				return;
		}

		if (imgs > 1) {
			for (int i = 0; i < imgs; i++) {
				ImageView dot = new ImageView(this);
				dot.setImageResource(i == 0 ? R.drawable.screen_shot_dot_pressed
						: R.drawable.screen_shot_dot_normal);
				screenShotDots.addView(dot);
			}
			screenShotNext.setOnClickListener(this);
			screenShotPrevious.setOnClickListener(this);
		}

		imageDownloader = new ImageDownloader();
		imageDownloader.download(imgUrls[0], screenShot);
		if (imgs == 1) {
			imageDownloader = null;
			return;
		}

		screenShotPosition = 0;
		screenShotImgUrls = imgUrls;
		screenShotPrevious.setVisibility(View.GONE);
		screenShotNext.setVisibility(View.VISIBLE);
	}

	private void changeScreenShot(int offset) {
		ImageView dot = (ImageView) screenShotDots
				.getChildAt(screenShotPosition);
		dot.setImageResource(R.drawable.screen_shot_dot_normal);
		screenShotPosition += offset;
		// Loop show
		// if (screenShotPosition < 0)
		// screenShotPosition = screenShotImgUrls.length - 1;
		// else if (screenShotPosition >= screenShotImgUrls.length)
		// screenShotPosition = 0;
		if (screenShotPosition == 0) {
			screenShotPrevious.setVisibility(View.GONE);
			screenShotNext.setVisibility(View.VISIBLE);
		} else if (screenShotPosition == screenShotImgUrls.length - 1) {
			screenShotPrevious.setVisibility(View.VISIBLE);
			screenShotNext.setVisibility(View.GONE);
		} else {
			screenShotPrevious.setVisibility(View.VISIBLE);
			screenShotNext.setVisibility(View.VISIBLE);
		}
		dot = (ImageView) screenShotDots.getChildAt(screenShotPosition);
		dot.setImageResource(R.drawable.screen_shot_dot_pressed);
		imageDownloader.download(screenShotImgUrls[screenShotPosition],
				screenShot);
	}

	private void displayComments(Comment[] comments) {
		
		
		L.d("comments:"+comments);
		if (comments == null || comments.length == 0) {
			View commentsView = findViewById(R.id.app_detail_comments);
			if (commentsView != null)
				commentsView.setVisibility(View.GONE);
			return;
		}
		ViewStub stub = (ViewStub) findViewById(R.id.stub_app_detail_comments);
		if (stub != null)
			stub.inflate();
		findViewById(R.id.view_all_comments).setOnClickListener(this);
		ViewGroup cmtsBox = (ViewGroup) findViewById(R.id.comments_box);
		cmtsBox.removeAllViews();
		int size = comments.length - 1;
		LayoutInflater inflater = getLayoutInflater();
		for (int i = 0; i <= size; i++) {
			Comment comment = comments[i];
			inflater.inflate(R.layout.comment_item, cmtsBox);
			View cmtItem = cmtsBox.getChildAt(cmtsBox.getChildCount() - 1);
			UI.bindText(cmtItem, R.id.comment_text, comment.getContent());
			String hd = getString(R.string.comment_header, comment.getPoster(),
					comment.getDate());
			UI.bindText(cmtItem, R.id.comment_header, hd);
			((RatingBar) cmtItem.findViewById(R.id.comment_rating))
					.setRating(comment.getRating());
			if (i != size) {
				inflater.inflate(R.layout.list_divider, cmtsBox);
			}
		}
	}

	private void displayMyRating(Integer myRating) {
		ViewStub stub = (ViewStub) findViewById(R.id.stub_app_detail_my_rating);
		if (stub != null)
			stub.inflate();
		findViewById(R.id.rate_app).setOnClickListener(this);
		if (myRating != null)
			UI.bindRating(this, R.id.my_rating, myRating);
	}

	private void displayReportApp(boolean reportable) {
		View view = findViewById(R.id.link_report_app);
		if (reportable) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
		view.setOnClickListener(this);
	}

	private void diaplayDownloadPregress(int precent) {
		if (downloadBox == null) {
			ViewStub stub = (ViewStub) findViewById(R.id.stub_app_detail_download_progress);
			if (stub != null) {
				downloadBox = stub.inflate();
				donwloadPregorss = (ProgressBar) downloadBox
						.findViewById(R.id.download_progress);
				donwloadPregorssPct = (TextView) downloadBox
						.findViewById(R.id.download_progress_pct);
			}
		}
		buttonBox.setVisibility(View.GONE);
		if (donwloadPregorss == null || donwloadPregorssPct == null)
			return;
		donwloadPregorss.setProgress(precent);
		String pctText = String.format(
				getString(R.string.donwload_progress_pct), precent);
		donwloadPregorssPct.setText(pctText+"%");
	}

	private void displayActionButton() {
		Application.Action[] actions = AppUtils.getActions(this,app);
		if (actions == null || actions.length == 0) {
			if (Logger.DEBUG)
				throw new RuntimeException("no action.");
			return;
		}
		for (int i = 0; i < actions.length; i++) {
			Application.Action action = actions[i];
			Button button = buttons[i];
			button.setTag(action);
			switch (action) {
			case INSTALL:
				button.setText(R.string.action_download);
				break;
			case UNINSTALL:
				button.setText(R.string.action_uninstall);
				break;
			case UPDATE:
				button.setText(R.string.action_update);
				break;
			case OPEN:
				button.setText(R.string.action_open);
				break;
			case BUY:
				button.setText(R.string.action_buy);
				break;
			case SUBSCRIBE:
				button.setText(R.string.action_subscribe);
				break;
			case UN_SUBSCRIBE:
				button.setText(R.string.action_unsubscribe);
				break;
			case CANCEL:
				button.setText(R.string.action_unsubscribe);
				break;
			}
		}
		if (actions.length == 1)
			buttons[1].setVisibility(View.GONE);
		else if (buttons[1].getVisibility() == View.GONE)
			buttons[1].setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (Logger.DEBUG)
			L.d("onClick");

		if (UI.handleTabOnClickEvent(this, v))
			return;

		switch (v.getId()) {
		case R.id.screen_shot:
			ActionController.screenShot(this, app.getScreenShots(),
					screenShotPosition);
			return;
		case R.id.link_provider_apps:
			ActionController.cpApps(this, app.getProviderID(), app.getIcon());
			return;
		case R.id.link_provider_web_site:
			if (!ActionController.openWeb(this, app.getProviderSite()))
				UI.toast(this, R.string.unable_to_browse_provider_website);
			return;
		case R.id.link_provider_contact:
			if (!ActionController.sendMail(this, app.getProviderEmail()))
				UI.toast(this, R.string.unable_to_browse_provider_website);
			return;
		case R.id.view_all_comments:
			ActionController.appComments(this, new AppTitleInfo(app));
			return;
		case R.id.rate_app:
			ActionController.rateApp(this, new MyRatingInfo(app));
			return;
		case R.id.link_report_app:
			ActionController.reportApp(this, new AppTitleInfo(app));
			return;
		case R.id.previous:
			changeScreenShot(-1);
			return;
		case R.id.next:
			changeScreenShot(1);
			return;
//		case R.id.button3:
//			getOrderStatusRequest();
//			return;
		case R.id.button1:
		case R.id.button2:
			Application.Action action = (Action) v.getTag();
			onAction(action);
		}
	}
	private void getOrderStatusRequest(){
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		if (task != null) {
			task.cancel(true);
			task = null;
		}
		AsyncTask<Void, Void, ApiInvoker<OrderRet>> task = new AsyncTask<Void, Void, ApiInvoker<OrderRet>>() {
			private ApiInvoker<OrderRet> apiInvoker;

			protected void onCancelled() {
				if (Logger.DEBUG)
					L.d("AsyncTask onCancelled");
				if (apiInvoker != null)
					apiInvoker.stop();
				apiInvoker = null;
			};

			@Override
			protected ApiInvoker<OrderRet> doInBackground(Void... params) {
				if (Logger.DEBUG)
					L.d("AsyncTask doInBackground");
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.getOrderStatus(pkg);
				apiInvoker.invoke();
				return apiInvoker;
			}

			protected void onPostExecute(ApiInvoker<OrderRet> apiInvoker) {
				if (Logger.DEBUG)
					L.d("AsyncTask onPostExecute");
				if (apiInvoker != null)
					getOrderStatusResponse(apiInvoker);
			};
		}.execute();		
	}
	private void getOrderStatusResponse(ApiInvoker<OrderRet> apiInvoker){
		if (Logger.DEBUG)
			L.d("responseApplication");

		task = null;
		if (isFinishing())
			return;

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;

		OrderRet appRet = apiInvoker.getRet();
		if (appRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("orderRet.isSuccess()");
			final OrderInfo order=appRet.getOrder();
			String msg="app = "+order.getPkg()+" orderNo="+order.getOrderNo()+"\n"
					+" order time="+order.getOrderTime()+" begin time="+order.getRightStartDate()+" end time="+order.getRightEndDate();
			new AlertDialog.Builder(this)
	        .setCancelable(false)
	        .setMessage(msg)
	        .setNegativeButton(R.string.close,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	cancelOrderRequest(order);
                    }
                }).show();
			return;
		}
	}
	private void cancelOrderRequest(final OrderInfo order){
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		if (task != null) {
			task.cancel(true);
			task = null;
		}
		AsyncTask<Void, Void, ApiInvoker<CommonRet>> task = new AsyncTask<Void, Void, ApiInvoker<CommonRet>>() {
			private ApiInvoker<CommonRet> apiInvoker;

			protected void onCancelled() {
				if (Logger.DEBUG)
					L.d("AsyncTask onCancelled");
				if (apiInvoker != null)
					apiInvoker.stop();
				apiInvoker = null;
			};

			@Override
			protected ApiInvoker<CommonRet> doInBackground(Void... params) {
				if (Logger.DEBUG)
					L.d("AsyncTask doInBackground");
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.orderRefund(order.getOrderNo(), "my test");
				apiInvoker.invoke();
				return apiInvoker;
			}

			protected void onPostExecute(ApiInvoker<CommonRet> apiInvoker) {
				if (Logger.DEBUG)
					L.d("AsyncTask onPostExecute");
				if (apiInvoker != null)
					cancelOrderResponse(apiInvoker);
			};
		}.execute();		
	}
	private void cancelOrderResponse(ApiInvoker<CommonRet> apiInvoker){
		if (Logger.DEBUG)
			L.d("responseApplication");

		task = null;
		if (isFinishing())
			return;

		if (DialogUtils.handleApiInvokeDialog(this, apiInvoker))
			return;

		CommonRet appRet = apiInvoker.getRet();
		if (appRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("orderRet.isSuccess()");
			return;
		}
	}

	private void onAction(Application.Action action) {
		switch (action) {
		case INSTALL:
			if (!LangUtils.isBlank(app.getAlertInstallMsg()))
				showDialog(DIALOG_INSTALL_MSG);
			else
				install();
			break;
		case UNINSTALL: {
			if (LangUtils.isBlank(app.getAlertUninstallMsg()))
				uninstall();
			else
				showDialog(DIALOG_UNINSTALL_CONFIRM);
			break;
		}
		case UPDATE:
			install();
			break;
		case OPEN:
			if (!ActionController.launchApp(this, pkg))
				showDialog(DIALOG_UNABLE_LAUNCH_APP);
			break;
		case BUY:
		case SUBSCRIBE:
			needLogin = true;
			if(!ActionController.loginFlag){
				this.getIntent().putExtra(LoginEvent.class.getName(), LoginEvent.PAY);
				ActionController.index(this);
				needLogin = false;	
			}else{
				if (app.isPayProcessing()) {
					checkPayStatusRet();
				} else {
					checkPaymentQuota();
				}
			}
			break;
		case UN_SUBSCRIBE:
			String msg = app.getAlertUnsubscribeMsg();
			if (msg != null && msg.trim().length() == 0)
				unsubscribe();
			else
				showDialog(DIALOG_UNSUBSCRIBE_CONFIRM);
			break;
		case CANCEL:
			getOrderStatusRequest();
			break;
		}
	}

	public static boolean needLogin =  false;
	
	private void install() {
		
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}
		
		needLogin =  true;

//		if(!ActionController.loginFlag){
////			this.getIntent().putExtra(ActionController.EXTRA_FORWARD_INTENT, Intents.addDetail(this, categoryId, pkg));
//			this.getIntent().putExtra(LoginEvent.class.getName(), LoginEvent.DOWNLOAD);
//			ActionController.index(this);
//			needLogin =  false;			
////			ActionController.appDetail(this, app.getPkg(), categoryId);
////			finish();
//		}else{
			String state = Environment.getExternalStorageState();
			if (!Environment.MEDIA_MOUNTED.equals(state)) {
				UI.toast(this, R.string.plz_insert_memory_card);
				return;
			}
	
			try {
				int allow = Settings.Secure.getInt(getContentResolver(),
						Settings.Secure.INSTALL_NON_MARKET_APPS);
				if (allow != 1) { // not allow
					showDialog(DIALOG_NOT_ALLOW_INSTALL_NON_MARKET_APPS);
					return;
				}
			} catch (SettingNotFoundException e) { // 無此Setting，繼續下載
				if (Logger.INFO)
					L.i("Reading Settings.Secure.INSTALL_NON_MARKET_APPS fail.", e);
			}
			
			cn.com.vapk.vstore.client.installapp.ActionController.installApp(this,
					app.getPkg(), categoryId, app.getTitle(), app.getVersion());
			UI.toast(this, R.string.toast_download_Status);
			finish();
//		}
		
	}

	private void uninstall() {
		ActionController.uninstallReasonApp(this, new AppTitleInfo(app));
		finish();
	}

	private void checkPayStatusRet() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		final AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		final ApiInvoker<CheckPayStatusRet> apiInvoker = apiService
				.checkPayStatus(app.getPkg(), AppUtils.getLocalVersionCode(this,app.getPkg()));
		new ApiInvokerBaseRetTask<CheckPayStatusRet>(this, apiInvoker) {
			@Override
			protected void success(CheckPayStatusRet ret) {
				if (Logger.DEBUG)
					L.d("CheckPayStatusRet.isSuccess()");
				if (ret.isNoPaid()) {
					if (Logger.DEBUG)
						L.d("CheckPayStatusRet.isNoPaid()");
					checkPaymentQuota();
					return;
				} else if (ret.isPaid()) {
					if (Logger.DEBUG)
						L.d("CheckPayStatusRet.isPaid()");
					getLicense(ret);
					return;
				} else {
					DialogUtils.createFinishActivityAlertDialog(
							AppDetailActivity.this, ret.getStatusMsg(), true)
							.show();
				}
			}
		}.execute();
	}

	private void getLicense(final CheckPayStatusRet checkPayStatusRet) {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}
		final AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		final ApiInvoker<LicenseInfoRet> apiInvoker = apiService.getLicense(app.getPkg(), AppUtils.getLocalVersionCode(this, app.getPkg()));
		new ApiInvokerTask<LicenseInfoRet>(this, apiInvoker) {
			@Override
			protected void handleRet(Activity aty, LicenseInfoRet licenseInfoRet) {
				AppStatusReceiver.broadcastPaid(getApplicationContext(), pkg);
				DialogUtils.createFinishActivityAlertDialog(
						AppDetailActivity.this,
						checkPayStatusRet.getStatusMsg(), true).show();
				if (licenseInfoRet != null) {
					LicenseTool.deleteAllLicense(AppDetailActivity.this, app.getPkg());
					List<LicenseInfo> l = licenseInfoRet.getApplication();
					for(LicenseInfo li : l) {
						LicenseTool.insertLicense(AppDetailActivity.this, li.getPkg(), li.getId().longValue(), li.getData().getBytes(), Base64Coder.decode(li.getSign()));
					}
				}
			}
		};
	}

	private void checkPaymentQuota() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		final AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		final ApiInvoker<PePayOrderRet> apiInvoker = apiService.getPePayOrderUrl(app.getPkg());
		
		new ApiInvokerBaseRetTask<PePayOrderRet>(this, apiInvoker) {
			@Override
			protected void success(PePayOrderRet ret) {
				if (Logger.DEBUG)
					L.d("pePayOrderRet.isSuccess()");
				try {
					Bundle bundle = new Bundle();
					bundle.putString("pepay", "true");
					bundle.putString("pePayOrderUrl", ret.getPePayOrderUrl());
					bundle.putString("pePayBaseUrl", ret.getPePayBaseUrl());
					bundle.putString("payType", String.valueOf(ShoppingCartActivity.PAY_BY_PEPAY_TY_BILL));
					Map<String, String> params = ret.getParams();
					if(params.size() > 0) {
						for(String k : params.keySet()) {
							bundle.putString(k, params.get(k));
						}
					}
					ActionController.payment(AppDetailActivity.this, app.getPkg(), bundle);
					finish();
				} catch(Exception e) {
					L.e("Parse PePayOrderRetUrl Fail : " + e.toString());
				}
			}
			
		}.execute();
	}

	private void unsubscribe() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		final AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		final ApiInvoker<UnsubscribeRet> apiInvoker = apiService
				.unsubscribe(app.getPkg());

		new ApiInvokerBaseRetTask<UnsubscribeRet>(this, apiInvoker) {
			@Override
			protected void success(UnsubscribeRet ret) {
				if (ret.isUnsubscribeSuccess() || ret.isNonsubscribe()) {
					DialogUtils.createFinishActivityAlertDialog(
							AppDetailActivity.this, ret.getUnsubscribeMsg(),
							true).show();
					AppStatusReceiver.broadcastUnsubscribe(
							getApplicationContext(), pkg,
							ret.getSubscribeExpDate());
				} else
					DialogUtils.createAlertDialog(AppDetailActivity.this,
							ret.getUnsubscribeMsg()).show();
			}
		}.setProgressMsg(getString(R.string.unsubscribe_now_dot)).execute();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		case DIALOG_INSTALL_MSG: {
			DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					install();
				}
			};
			return new AlertDialog.Builder(this)
					.setMessage(app.getAlertInstallMsg())
					.setPositiveButton(R.string.submit, ocl)
					.setNegativeButton(R.string.cancel, null).create();

		}
		case DIALOG_NOT_ALLOW_INSTALL_NON_MARKET_APPS: {
			DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						startActivity(new Intent(
								Settings.ACTION_APPLICATION_SETTINGS));
					} catch (Exception e) {
						L.e("Start ACTION_APPLICATION_SETTINGS fail", e);
						UI.toast(
								getApplicationContext(),
								R.string.plz_forward_to_allow_install_non_market_apps_youself);
					}
				}
			};
			return new AlertDialog.Builder(this)
					.setMessage(
							R.string.plz_enable_allow_install_non_market_apps)
					.setPositiveButton(R.string.setting, ocl)
					.setNegativeButton(R.string.cancel, null).create();
		}
		case DIALOG_UNABLE_LAUNCH_APP: {
			return DialogUtils.createAlertDialog(this,
					R.string.unable_to_launch_app);
		}
		case DIALOG_UNSUBSCRIBE_CONFIRM: {
			String msg = app.getAlertUnsubscribeMsg() == null ? getString(R.string.unsubscribe_confirm)
					: app.getAlertUnsubscribeMsg();
			return new AlertDialog.Builder(this)//
					.setMessage(msg)//
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									unsubscribe();
								}
							})//
					.setNegativeButton(R.string.no, null).create();
		}
		case DIALOG_UNINSTALL_CONFIRM: {
			return new AlertDialog.Builder(this)//
					.setMessage(app.getAlertUninstallMsg())//
					.setPositiveButton(R.string.submit,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									uninstall();
								}
							})//
					.setNegativeButton(R.string.cancel, null).create();
		}
		}
		return null;
	}

	@Override
	public boolean onSearchRequested() {
		ActionController.search(this);
		return false;
	}

	@Override
	public void onLogin(LoginEvent event) {
		if(LoginEvent.PAY.equals(event.getEventType())){
			onAction(Application.Action.BUY);
		}
		if(LoginEvent.DOWNLOAD.equals(event.getEventType())){
			onAction(Application.Action.INSTALL);
		}
	}

}