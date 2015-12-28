package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.Credential;
import tw.com.sti.store.api.vo.LoginRet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.DialogUtils.ViewFocusClickListener;
import cn.com.vapk.vstore.client.api.ApiParametersValidator;
import cn.com.vapk.vstore.client.ui.TextInputFilterFactory;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class LoginActivity extends Activity implements OnClickListener {

	private static final Logger L = Logger.getLogger(LoginActivity.class);
	private static final String PREF_KEY_AUTO_LOGIN_CHECKED = "auto_login_checked";

	private final int DLG_INVALID_ACCOUNT = 1;
	private final int DLG_INVALID_PASSWORD = 2;
	private final int DLG_LOGINNING = 3;
	private final int DLG_LOGIN_REQUEST_FAIL = 4;

	private CheckBox mAutoLogin;
	private TextView mAccount;
	private TextView mPassword;
	private String account;

	private ApiInvoker<LoginRet> apiInvoker;
	private String retMsg;
	private CloseClientReceiver closeClientReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		mAutoLogin = (CheckBox) findViewById(R.id.auto_login);
		mAccount = (TextView) findViewById(R.id.account);
		mPassword = (TextView) findViewById(R.id.password);
		findViewById(R.id.submit).setOnClickListener(this);
		findViewById(R.id.submit_register).setOnClickListener(this);
		mAccount.setText(AndroidApiService.getInstance(this,ConfigurationFactory.getInstance()).getCredential(this.getApplicationContext()).getUserId());
		TextView regLink = (TextView) findViewById(R.id.reg_link);
		regLink.setMovementMethod(LinkMovementMethod.getInstance());
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		if (pref.getBoolean(PREF_KEY_AUTO_LOGIN_CHECKED, true) == false)
			mAutoLogin.setChecked(false);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
		mAccount.setFilters(TextInputFilterFactory.loginAccount());
		mPassword.setFilters(TextInputFilterFactory.loginPassword());
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
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.submit:
				onLoginClick();
				return;
			case R.id.submit_register:
				onRegisterClick();
				return;
		}
		
	}
	private void onRegisterClick() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}
		this.finish();
		Intent atyIntent = this.getIntent();        
        
		Intent intent = new Intent(this, RegisterActivity.class);
		intent.putExtra(ActionController.EXTRA_FORWARD_INTENT, atyIntent
                .getParcelableExtra(ActionController.EXTRA_FORWARD_INTENT));
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
	}
	
	
	private void onLoginClick() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		account = mAccount.getText().toString().trim().replace(" ", "");
		if (Logger.DEBUG) {
			L.d("account: " + account);
		}
		if (LangUtils.isBlank(account)) {
			showDialog(DLG_INVALID_ACCOUNT);
			return;
		}

		String password = mPassword.getText().toString().trim()
				.replace(" ", "");
		if (Logger.DEBUG) {
			L.d("password: " + password);
		}
		if (!ApiParametersValidator.Login.password(password)) {
			showDialog(DLG_INVALID_PASSWORD);
			return;
		}

		requestLogin(account, password);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			onLoginClick();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	private void requestLogin(final String account, final String password) {
		showDialog(DLG_LOGINNING);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.login(account, password);
				apiInvoker.invoke();
				return null;
			}

			protected void onPostExecute(Void result) {
				responseLogin();
			};

		}.execute();
	}

	private void responseLogin() {
		
		L.d("In response Login");
		removeDialog(DLG_LOGINNING);

		if (apiInvoker.isStop()) {
			return;
		}

		if (apiInvoker.isFail()) {
			showDialog(DLG_LOGIN_REQUEST_FAIL);
			return;
		}

		LoginRet loginRet = apiInvoker.getRet();
		if (loginRet.isSuccess()) {
			if (Logger.DEBUG)
				L.d("LoginRet.isSuccess()");
			boolean autoLogin = mAutoLogin.isChecked();
			Credential credential = loginRet.getCredential(account);
			if (credential == null) {
				if (Logger.DEBUG)
					L.e("Login's Credential is null.");
				showDialog(DialogUtils.DLG_CONN_TO_SERVER_FAIL);
				return;
			}
			AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
			apiService.saveCredential(this, credential);
			boolean afse = loginRet.isAppFilterSettingEnable();
			ActionController.loginSuccess(this, autoLogin, afse);
			if (autoLogin == false) {
				SharedPreferences pref = getPreferences(MODE_PRIVATE);
				Editor edit = pref.edit();
				edit.putBoolean(PREF_KEY_AUTO_LOGIN_CHECKED, false);
				edit.commit();
			}
			finish();
			return;
		}

		if (loginRet.isFail()) {
			if (Logger.DEBUG)
				L.d("LoginRet.isFail()");
			retMsg = loginRet.getRetMsg();
			if (!LangUtils.isBlank(retMsg))
				new AlertDialog.Builder(this).setTitle(R.string.dlg_login_fail)
						.setMessage(retMsg)
						.setPositiveButton(R.string.relogin, null).show();
			else
				finish();
			return;
		}

		if (loginRet.isHasNewClient()) {
			if (Logger.DEBUG)
				L.d("LoginRet.isHasNewClient()");
			showDialog(DialogUtils.DLG_API_RET_NEW_CLIENT);
			return;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this, false);
		}
		case DialogUtils.DLG_CONN_TO_SERVER_FAIL: {
			return DialogUtils.createConnectionToServerFailAlertDialog(this);
		}
		case DialogUtils.DLG_API_RET_NEW_CLIENT: {
			return DialogUtils.createNewClientAlertDialog(this, apiInvoker
					.getRet().getNewClientInfo());
		}
		case DLG_INVALID_ACCOUNT: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_login_fail)
					.setMessage(R.string.dlg_invalid_account_null)
					.setPositiveButton(R.string.relogin,
							new ViewFocusClickListener(mAccount)).create();
		}
		case DLG_INVALID_PASSWORD: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_login_fail)
					.setMessage(R.string.dlg_invalid_pwd)
					.setPositiveButton(R.string.relogin,
							new ViewFocusClickListener(mPassword)).create();
		}
		case DLG_LOGINNING: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getResources().getString(R.string.login_now_dot));
			dialog.setOnCancelListener(new DialogUtils.ApiInvokerStopCancelListener(
					apiInvoker).setCancelMessage(getApplicationContext(),
					R.string.cnl_msg_login));
			return dialog;
		}
		case DLG_LOGIN_REQUEST_FAIL: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_login_fail)
					.setMessage(R.string.dlg_conntect_to_server_fail)
					.setPositiveButton(R.string.relogin, null).create();
		}
		}
		return null;
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	// Old Version Client
	public static final boolean isLogin() {
		return ActionController.loginFlag;
	}

	public static final boolean isAutoLogin(Context context) {
		return ActionSession.isAutoLoginEnable(context);
	}

	public static final String autoLogin(Context context) {
		if (!NetworkUtils.isNetworkOpen(context)) {
			return context.getResources().getString(R.string.dialog_no_network);
		}
		if (!isAutoLogin(context)) {
			return context.getResources().getString(
					R.string.dialog_autologin_fail_msg);
		}
		AndroidApiService apiService = AndroidApiService.getInstance(context,ConfigurationFactory.getInstance());
		ApiInvoker<LoginRet> apiInvoker = apiService.autoLogin();
		apiInvoker.getConfig().setApiTimeout(4000);
		apiInvoker.invoke();
		if (!apiInvoker.isSuccess())
			return context.getResources().getString(
					R.string.dialog_unable_to_conntect_to_server);

		LoginRet loginRet = apiInvoker.getRet();
		if (!loginRet.isSuccess())
			return context.getResources().getString(
					R.string.dialog_autologin_fail_msg);

		String userId = apiService.getCredential(context).getUserId();
		boolean autoLogin = true;
		boolean appFilterSettingEnable = loginRet.isAppFilterSettingEnable();
		apiService.saveCredential(context, loginRet.getCredential(userId));
		ActionController.loginSuccess(autoLogin, appFilterSettingEnable,
				context);

		return null;
	}
}