package cn.com.vapk.vstore.client;

import tw.com.sti.clientsdk.LicenseManager;
import tw.com.sti.clientsdk.LoginCallback;
import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import tw.com.sti.store.api.vo.LoginRet;
import tw.com.sti.store.api.vo.SdkAppInfoRet.SdkApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.api.ApiParametersValidator;
import cn.com.vapk.vstore.client.ui.TextInputFilterFactory;
import cn.com.vapk.vstore.client.util.AsyncTask;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.utils.NetworkUtilities;

public class AppLicenseManager extends LicenseManager {
	private static final Logger L=Logger.getLogger(AppLicenseManager.class);
//    public static final int SHARED_PREFER_AUTOLOGIN_ID = 0;
//    private static final int DIALOG_AQURING_DATA = 1;
    private AlertDialog loginDialog;
    private AndroidApiService apiService;
    /** Called when the activity is first created. */
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.license_manager);
        apiService=AndroidApiService.getInstance(getApplicationContext(),ConfigurationFactory.getInstance());
    }

    private void showLoginErrMsg(String msg) {
        new AlertDialog.Builder(this).setTitle(R.string.dlg_login_fail)
                .setMessage(msg)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        loginByUI();
                    }
                })
                .setPositiveButton(R.string.close,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                loginByUI();
                            }

                        }).show();
    }

    private void doLogin(final String userId, final String password,
            final boolean autoLogin) {
        // check network status
        boolean netOpen = NetworkUtilities.isNetworkOpen(this);
        if (!netOpen) {
            showLoginErrMsg(getResources()
                    .getString(R.string.dialog_no_network));
            return;
        }

        if (!ApiParametersValidator.Login.account(userId)) {
            showLoginErrMsg(getString(R.string.dlg_invalid_account));
            return;
        }
        if (!ApiParametersValidator.Login.password(password)) {
            showLoginErrMsg(getString(R.string.dlg_invalid_pwd));
            return;
        }

        showDialog(DIALOG_AQURING_DATA);

        new AsyncTask<Void, Void, ApiInvoker<LoginRet>>() {
            @Override
            protected ApiInvoker<LoginRet> doInBackground(Void... params) {
                ApiInvoker<LoginRet> apiInvoker = apiService.login(userId,
                        password);
                apiInvoker.invoke();
                return apiInvoker;
            }

            protected void onPostExecute(ApiInvoker<LoginRet> apiInvoker) {
                if (apiInvoker.isStop()) {
                    if (Logger.DEBUG)
                        L.d("apiInvoker.isStop()");
                    safeDismissDialog(DIALOG_AQURING_DATA);
                    loginByUI();
                    return;
                }

                if (apiInvoker.isFail()) {
                    if (Logger.DEBUG)
                        L.d("apiInvoker.isFail()");
                    safeDismissDialog(DIALOG_AQURING_DATA);
                    showLoginErrMsg(getResources().getString(
                            R.string.dialog_unable_to_conntect_to_server));
                    return;
                }

                LoginRet loginRet = apiInvoker.getRet();
                if (loginRet.isFail()) {
                    if (Logger.DEBUG)
                        L.d("loginRet.isFail()");
                    safeDismissDialog(DIALOG_AQURING_DATA);
                    String alertMsg = loginRet.getRetMsg();
                    if (LangUtils.isBlank(alertMsg)) {
                        alertMsg = getResources().getString(
                                R.string.dialog_login_fail_msg);
                    }
                    showLoginErrMsg(alertMsg);
                    return;
                }

                if (loginRet.isHasNewClient()) {
                    if (Logger.DEBUG)
                        L.d("loginRet.isHasNewClient()");
                    safeDismissDialog(DIALOG_AQURING_DATA);
                    getCallback().cancel();
                    DialogUtils.createNewClientAlertDialog(AppLicenseManager.this,
                            apiInvoker.getRet().getNewClientInfo()).show();
                    return;
                }

                Context context = getApplicationContext();
                boolean appFilterSettingEnable = loginRet
                        .isAppFilterSettingEnable();
                apiService.saveCredential(context,
                        loginRet.getCredential(userId));
                ActionController.loginSuccess(autoLogin,
                        appFilterSettingEnable, context);
                getCallback().success(loginRet.getCredential(userId));
            };
        }.execute();

    }

    private void loginByUI() {
        Resources r = getResources();
        LayoutInflater inflater = LayoutInflater.from(this);
        View loginView = inflater.inflate(R.layout.login_dialog, null);
        TextView regLink = (TextView) loginView.findViewById(R.id.reg_link);
        regLink.setMovementMethod(LinkMovementMethod.getInstance());
        if(apiService==null){
        	L.d("api service is null");
        }else{
        	L.d("api service is not null");
        }
        String userId = apiService.getCredential(getApplicationContext()).getUserId();
        if (userId != null)
            ((EditText) loginView.findViewById(R.id.login_userid))
                    .setText(userId);
        if (LoginActivity.isAutoLogin(this))
            ((CheckBox) loginView.findViewById(R.id.login_auto))
                    .setChecked(true);
        InputFilter[] accountfilters = TextInputFilterFactory.loginAccount();
        InputFilter[] passwordfilters = TextInputFilterFactory.loginPassword();
        ((EditText) loginView.findViewById(R.id.login_userid))
                .setFilters(accountfilters);
        ((EditText) loginView.findViewById(R.id.login_password))
                .setFilters(passwordfilters);

        loginDialog = new AlertDialog.Builder(this).setView(loginView)
                .setTitle(R.string.please_login_smart_app)
                .setCancelable(false)
                .setPositiveButton(r.getText(R.string.login),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String userid = ((EditText) loginDialog
                                        .findViewById(R.id.login_userid))
                                        .getText().toString();
                                String password = ((EditText) loginDialog
                                        .findViewById(R.id.login_password))
                                        .getText().toString();
                                boolean autoLogin = ((CheckBox) loginDialog
                                        .findViewById(R.id.login_auto))
                                        .isChecked();
                                loginDialog.dismiss();
                                doLogin(userid.trim(), password.trim(),
                                        autoLogin);
                            }
                        }) //
                .setNegativeButton(r.getText(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                            	getCallback().cancel();
                            	finish();
                            }
                        }).show();
    }

    private void doAgreement() {
        if (Logger.DEBUG)
            L.d("doAgreement");
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_eula)
                .setMessage(R.string.eula)
                .setCancelable(false)
                .setPositiveButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                EulaActivity.agreeEula(getApplicationContext());
                                dialog.dismiss();
                                showDialog(DIALOG_AQURING_DATA);
                                getCallback().success(apiService.getCredential(getApplicationContext()));
                            }
                        })//
                .setNegativeButton(R.string.refuse,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	getCallback().cancel();
                            	finish();
                            }
                        })//
                .show();
    }

    private void checkAuth1() {
        if (Logger.DEBUG)
            L.d("checkAuth");
        boolean agree = EulaActivity.isAgreeEula(this);
        if (!agree) {
            safeDismissDialog(DIALOG_AQURING_DATA);
            doAgreement();
            return;
        }
    }

	@Override
	protected void checkLoginStatusOrShowLoginUI(final LoginCallback callback) {
        boolean netOpen = NetworkUtilities.isNetworkOpen(this);
        if (!netOpen) {
            NetworkUtilities.showNoNetworkAlertDialog(this, true);
            return;
        }

        if (AndroidApiService.isLogin()) {
            showDialog(DIALOG_AQURING_DATA);
            callback.success(apiService.getCredential(getApplicationContext()));
            return;
        }
        if (!LoginActivity.isAutoLogin(this)) {
            loginByUI();
            return;
        }
        showDialog(DIALOG_AQURING_DATA);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                if (obj == null) {
                	callback.success(apiService.getCredential(getApplicationContext()));
                } else {
                    safeDismissDialog(DIALOG_AQURING_DATA);
                    showLoginErrMsg(obj.toString());
                }
            }
        };
        new Thread() {
            public void run() {
                String ret = LoginActivity.autoLogin(getApplicationContext());
                Message msg = handler.obtainMessage();
                msg.obj = ret;
                handler.sendMessage(msg);
            };
        }.start();

	}

	@Override
	protected void downloadNewVersionApp(SdkApp apk) {
        String vlogId = getIntent().getStringExtra("vlogId");
        final String categoryId = cn.com.vapk.vstore.client.installapp.ActionController.CATEGORY_ID_SDK_UPGRADE_APP;
        cn.com.vapk.vstore.client.installapp.ActionController.installApp(this,
                apk.getPkg(), categoryId, apk.getTitle(),
                apk.getVersion(), vlogId);
        UI.toast(this, R.string.toast_download_Status);
	}

	@Override
	protected Configuration getConfiguration() {
		return ConfigurationFactory.getInstance();
	}

	@Override
	protected void showPaymentUI() {
		SdkApp app = super.getAppInfo();
		Bundle bundle = new Bundle();
		bundle.putString("pkg", app.getPkg());
		bundle.putString("title", app.getTitle());
		bundle.putFloat("price", app.getPrice());
		ActionController.payment(this, this.getCallerPackage(), bundle);
	}
}