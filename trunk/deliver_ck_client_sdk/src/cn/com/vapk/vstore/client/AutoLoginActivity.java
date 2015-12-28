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
import android.content.DialogInterface;
import android.os.Bundle;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.NetworkUtils;

public class AutoLoginActivity extends Activity {

	private static final Logger L = Logger.getLogger(AutoLoginActivity.class);

	private final int DLG_LOGIN_FAIL = 1;
	private ApiInvoker<LoginRet> apiInvoker;
	private String retMsg;
	private String userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
		userId = apiService.getCredential(getApplicationContext()).getUserId();
		if (LangUtils.isBlank(userId)) {
			if (Logger.DEBUG)
				throw new RuntimeException("AutoLogin's userId is blank.");

			autoLoginFail();
			return;
		}

		setContentView(R.layout.ip_login);
		requestLogin();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (apiInvoker != null)
			apiInvoker.stop();
	}

	private void autoLoginFail() {
		ActionController.autoLoginFail(this);
		finish();
	}

	private void requestLogin() {
		if (!NetworkUtils.isNetworkOpen(this)) {
			showDialog(DialogUtils.DLG_NO_NETWORK);
			return;
		}

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				AndroidApiService apiService = AndroidApiService
						.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
				apiInvoker = apiService.autoLogin();
				apiInvoker.invoke();
				return null;
			}

			protected void onPostExecute(Void result) {
				responseLogin();
			};

		}.execute();
	}

	private void responseLogin() {
		UI.stopProgressing(this);

		if (apiInvoker.isStop()) {
			if (Logger.DEBUG)
				L.d("apiInvoker.isStop()");
			return;
		}

		if (apiInvoker.isFail()) {
			if (Logger.DEBUG)
				L.d("apiInvoker.isFail()");
			showDialog(DialogUtils.DLG_CONN_TO_SERVER_FAIL);
			return;
		}

		LoginRet loginRet = apiInvoker.getRet();
		if (loginRet.isSuccess()) {
			Credential credential = loginRet.getCredential(userId);
			if (credential == null) {
				if (Logger.DEBUG)
					throw new RuntimeException("IPLogin's Credential is null.");
				autoLoginFail();
				return;
			}
			boolean autoLogin = true;
			AndroidApiService apiService = AndroidApiService.getInstance(this,ConfigurationFactory.getInstance());
			apiService.saveCredential(this, credential);
			boolean afse = loginRet.isAppFilterSettingEnable();
			ActionController.loginSuccess(this, autoLogin, afse);
			finish();
			return;
		}

		if (loginRet.isFail()) {
			retMsg = loginRet.getRetMsg();
			if (!LangUtils.isBlank(retMsg))
				showDialog(DLG_LOGIN_FAIL);
			else {
				autoLoginFail();
			}
			return;
		}

		if (loginRet.isHasNewClient()) {
			if (Logger.DEBUG)
				L.d("appsRet.isHasNewClient()");
			showDialog(DialogUtils.DLG_API_RET_NEW_CLIENT);
			return;
		}

		if (Logger.DEBUG)
			throw new RuntimeException("AutoLogin no handle for response.");
		autoLoginFail();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DialogUtils.DLG_NO_NETWORK: {
			return DialogUtils.createNoNetworkAlertDialog(this);
		}
		case DialogUtils.DLG_CONN_TO_SERVER_FAIL: {
			return DialogUtils.createConnectionToServerFailAlertDialog(this);
		}
		case DialogUtils.DLG_API_RET_NEW_CLIENT: {
			return DialogUtils.createNewClientAlertDialog(this, apiInvoker
					.getRet().getNewClientInfo());
		}
		case DLG_LOGIN_FAIL: {
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_login_fail)
					.setMessage(retMsg)
					.setPositiveButton(R.string.relogin,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									autoLoginFail();
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									autoLoginFail();
								}
							}).create();
		}
		}
		return null;
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}
}