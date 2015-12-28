package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.Credential;
import tw.com.sti.store.api.vo.LoginRet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.vapk.vstore.client.DialogUtils.ViewFocusClickListener;
import cn.com.vapk.vstore.client.api.ApiParametersValidator;
import cn.com.vapk.vstore.client.ui.TextInputFilterFactory;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;
import cn.com.vapk.vstore.client.R;

public class RegisterActivity extends Activity implements OnClickListener {

	private static final Logger L = Logger.getLogger(RegisterActivity.class);
	
	private static final int DLG_LOADING = 0;
	private final int DLG_INVALID_ACCOUNT = 1;
	private final int DLG_INVALID_PASSWORD = 2;
	private final int DLG_LOGIN_REQUEST_FAIL = 4;
	private final int DLG_INVALID_CONFIRM_PASSWORD = 5;
	private final int DLG_INVALID_NICKNAME = 6;
	private final int DLG_USER_ALREADY_EXIST = 7;
	private final int DLG_INVALID_SIGNATURE = 8;
	private final int DLG_INVALID_ACCOUNT_LENGTH = 9;
	private final int DLG_INVALID_CPASSWORD = 10;
//	private final int DLG_INVALID_AGREEMENT = 11;
	
//	private CheckBox agreement;
	private TextView mAccount;
	private TextView mPassword;
	private TextView cPassword;
	private TextView mNickname;
	private TextView signature;
	private String account;

	private ApiInvoker<LoginRet> apiInvoker;
	private CloseClientReceiver closeClientReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
//		agreement = (CheckBox) findViewById(R.id.register_agreement);
		mAccount = (TextView) findViewById(R.id.account);
		mPassword = (TextView) findViewById(R.id.password);
		cPassword = (TextView) findViewById(R.id.password_confirm);
		mNickname = (TextView) findViewById(R.id.nickname);
		signature = (TextView) findViewById(R.id.signature);
		findViewById(R.id.submit).setOnClickListener(this);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
		mAccount.setFilters(TextInputFilterFactory.loginAccount());
		mPassword.setFilters(TextInputFilterFactory.loginPassword());
		cPassword.setFilters(TextInputFilterFactory.loginPassword());
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
				onRegisterClick();
				return;
		}
		
	}	
	
	private void onRegisterClick() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}
//		if(!agreement.isChecked()){
//			showDialog(DLG_INVALID_AGREEMENT);
//			return;
//		}

		account = mAccount.getText().toString().trim().replace(" ", "");
		if (!ApiParametersValidator.Login.account(account)) {
			showDialog(DLG_INVALID_ACCOUNT);
			return;
		}else if(account.length()>100){
			showDialog(DLG_INVALID_ACCOUNT_LENGTH);
			return;
		}

		String password = mPassword.getText().toString().trim();
		if (!ApiParametersValidator.Login.password(password)||password.length()<6||password.length()>20) {
			showDialog(DLG_INVALID_PASSWORD);
			return;
		}

		String cpassword = cPassword.getText().toString().trim();
		if (!ApiParametersValidator.Login.password(cpassword)||cpassword.length()<6||cpassword.length()>20) {
			showDialog(DLG_INVALID_CPASSWORD);
			return;
		}
		
		if(!password.equals(cpassword)){
			showDialog(DLG_INVALID_CONFIRM_PASSWORD);
			return ;
		}
		
		String nickName = mNickname.getText().toString().trim();
		if (Logger.DEBUG) {
			L.d("nickName: " + nickName);
		}
		if (nickName.length() <=0||nickName.length()>20) {
			showDialog(DLG_INVALID_NICKNAME);
			return;
		}
		String signatureText = this.signature.getText().toString().trim();
		if (signatureText.length()>10) {
			showDialog(DLG_INVALID_SIGNATURE);
			return;
		}
		
		requestRegister(account, password ,nickName , signatureText);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			onRegisterClick();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	private void requestRegister(final String account, final String password,final String nickName , String signature) {
		showDialog(DLG_LOADING);

		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		apiInvoker = apiService.register(account,password,nickName,signature);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				apiInvoker.invoke();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				responseRegisterFinish();
			}
		}.execute();
	}

	private void responseRegisterFinish() {
		L.d("responseRegisterFinish................");
		removeDialog(DLG_LOADING);
		LoginRet registerRet = apiInvoker.getRet();
	
		if (!registerRet.isSuccess()){
			// 失敗
			L.d("Register fail");
			new AlertDialog.Builder(this).setTitle(R.string.dlg_register_fail)
			.setMessage(registerRet.getRetMsg())
			.setPositiveButton(R.string.retry, new ViewFocusClickListener(mAccount)).show();
		}else{
			// Success
			this.finish();
			Credential credential = registerRet.getCredential(account);
			AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
            apiService.saveCredential(this, credential);
        	boolean afse = registerRet.isAppFilterSettingEnable();
			ActionController.loginSuccess(this, false, afse);
            Log.d("register","end...............................");
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this, false);
		}
		case DLG_USER_ALREADY_EXIST: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.user_already_exist)
					.setPositiveButton(R.string.retry,
							new ViewFocusClickListener(mAccount)).create();
		}
		case DialogUtils.DLG_CONN_TO_SERVER_FAIL: {
			return DialogUtils.createConnectionToServerFailAlertDialog(this);
		}
		case DialogUtils.DLG_API_RET_NEW_CLIENT: {
			return DialogUtils.createNewClientAlertDialog(this, apiInvoker
					.getRet().getNewClientInfo());
		}
//		case DLG_INVALID_AGREEMENT: {
//			return new AlertDialog.Builder(this)
//					.setTitle(R.string.dlg_register_fail)
//					.setMessage(R.string.dlg_invalid_agreement)
//					.setPositiveButton(R.string.reRegister,
//							new ViewFocusClickListener(agreement)).create();
//		}
		case DLG_INVALID_ACCOUNT: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_account)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(mAccount)).create();
		}
		case DLG_INVALID_ACCOUNT_LENGTH: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_account_length)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(mAccount)).create();
		}
		case DLG_INVALID_PASSWORD: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_pwd)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(mPassword)).create();
		}
		case DLG_INVALID_CPASSWORD: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_cpwd)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(cPassword)).create();
		}
		case DLG_INVALID_CONFIRM_PASSWORD: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_confirm_pwd)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(mPassword)).create();
		}
		case DLG_INVALID_NICKNAME: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_nickName)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(mNickname)).create();
		}
		case DLG_LOADING: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getResources().getString(R.string.register_now_dot));
			dialog.setOnCancelListener(new DialogUtils.ApiInvokerStopCancelListener(
					apiInvoker).setCancelMessage(getApplicationContext(),
					R.string.cnl_msg_login));
			return dialog;
		}
		case DLG_LOGIN_REQUEST_FAIL: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_conntect_to_server_fail)
					.setPositiveButton(R.string.reRegister, null).create();
		}
		case DLG_INVALID_SIGNATURE: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_register_fail)
					.setMessage(R.string.dlg_invalid_signature)
					.setPositiveButton(R.string.reRegister,
							new ViewFocusClickListener(signature)).create();
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