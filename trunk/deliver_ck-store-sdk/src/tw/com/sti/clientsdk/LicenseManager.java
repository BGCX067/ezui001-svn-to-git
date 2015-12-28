package tw.com.sti.clientsdk;

import java.util.List;
import java.util.ResourceBundle;

import tw.com.sti.clientsdk.provider.AppSdkProvider;
import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.AppInfo.AppPaidType;
import tw.com.sti.store.api.vo.AppInfo.PriceType;
import tw.com.sti.store.api.vo.Credential;
import tw.com.sti.store.api.vo.LicenseInfoRet;
import tw.com.sti.store.api.vo.LicenseInfoRet.LicenseInfo;
import tw.com.sti.store.api.vo.SdkAppInfoRet;
import tw.com.sti.store.api.vo.SdkAppInfoRet.SdkApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

public abstract class LicenseManager extends Activity {
	private static final Logger L=Logger.getLogger(LicenseManager.class);
    private static final String EXTRA_CALLER_PKG = "package";
    private static final String EXTRA_WIDGET_CALLBACK = "WidgetActionCallback";
    protected static final int DIALOG_AQURING_DATA = 1;
    protected static final int DIALOG_SERVICE_TERMINATION = 11;
    protected static final int DIALOG_NETWORK_FAILED = 21;
    protected static final int DIALOG_SUGGESTION_PAYSTATUS_UNKNOW = 31;
    protected static final int DIALOG_APP_NOT_PUBLISH_YET = 40;
    
    protected static final int ON_USE=1;
    private String callerPackage;
    private int callerVersion;
    private String appName;
    private String message;
    private SdkApp appInfo;
    private static final ResourceBundle resources = ResourceFactory.getResourceBundle();

    /** Called when the activity is first created. */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        Intent intent = getIntent();
        String widgetActionCallback = intent
                .getStringExtra(EXTRA_WIDGET_CALLBACK);
        callerPackage = intent.getStringExtra(EXTRA_CALLER_PKG);
        if(callerPackage==null){
        	paramterError();
        	return;
        }
        L.d("pkgId: " + callerPackage);
        L.d("widgetActionCallback: " + widgetActionCallback);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(callerPackage,
                    PackageManager.GET_ACTIVITIES);
            callerVersion = pi.versionCode;
        } catch (Exception e) {
        	paramterError();
            return;
        }

        if (widgetActionCallback != null) {
        	LicenseTool.addWidgetBroadcast(callerPackage, widgetActionCallback);
        }
        //如果希望強制升級，就加這行，一旦進入到licenseManager，就必須升級或者獲取到license
        LicenseTool.deleteAllLicense(this,callerPackage);
        checkLoginStatusOrShowLoginUI(callback);
    }
    private void paramterError(){
        showDialog(DIALOG_SERVICE_TERMINATION);
    }
    LoginCallback callback=new LoginCallback() {
		
		@Override
		public void success(Credential c) {
	    	AndroidApiService.getInstance(getApplicationContext(),getConfiguration()).saveCredential(getApplicationContext(),c);
    		checkAuth();
		}
//		@Override
//		public void sendResult(LoginResult result,Credential c) {
//			if(result==LoginResult.CANCELED_AND_FINISH){
//				failFinish();
//			}else if(result==LoginResult.CANCELED){
//				canceled();
//			}
//		}

		@Override
		public void cancel() {
			canceled();
		}
	};
    private ApiInvoker<SdkAppInfoRet> apiInvoker;
    private void checkAuth() {
        showDialog(DIALOG_AQURING_DATA);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				apiInvoker=AndroidApiService.getInstance(getApplicationContext(),getConfiguration()).getSdkAppInfo(callerPackage, callerVersion, "0", "0");
				apiInvoker.invoke();
				return null;
			}
			protected void onPostExecute(Void result) {
				responseSdkAppInfo();
			};
		}.execute();
    }
    private void responseSdkAppInfo(){
        safeDismissDialog(DIALOG_AQURING_DATA);
		if (apiInvoker.isStop()) {
			showDialog(DIALOG_NETWORK_FAILED);
			return;
		}
		if (apiInvoker.isFail()) {
			showDialog(DIALOG_NETWORK_FAILED);
			return;
		}
		SdkAppInfoRet ret=apiInvoker.getRet();
		if (ret.isFail()) {
            if (Logger.DEBUG)
                L.d("check app info fail");
            showDialog(DIALOG_APP_NOT_PUBLISH_YET);
            return;
        } 
		if (ret.isSuccess()) {
			appInfo=ret.getApplication();
	        appName = appInfo.getTitle();
	        if(appInfo.getOnUse()==ON_USE){
		        if (callerVersion < appInfo.getVersion()) {
		            upgrade(ret.getApplication());
		            return;
		        }
		        AppPaidType paymentRequired = AppPaidType.getPaymentRequired(appInfo.getPriceType(), appInfo.getPayStatus(), appInfo.getPriceType());
		        if (Logger.DEBUG) {
		            L.d("Paystatus: " + appInfo.getPaymentStatus());
		            L.d("MyPriceType: " + appInfo.getPriceType());
		            L.d("paymentRequired: " + paymentRequired);
		        }
		        if (paymentRequired == AppPaidType.NEED_PAY) {
		            if (appInfo.getVersion()==0) {
		                showDialog(DIALOG_SERVICE_TERMINATION);
		            } else {
		            	showPaymentUI();
		                finish();
		            }
		        } else {
		            if (paymentRequired == AppPaidType.PAY_PROCESSING) {
		                if (Logger.DEBUG)
		                    L.d("PAYMENT_REQUIRED_ORDER_PROCESSING");
		                requestLicenseForPayingStatus(callerPackage, callerVersion);
		            } else {
		                if (appInfo.getVersion()==0
		                        && appInfo.getPriceType() != PriceType.MONTHLY) {
		                    showDialog(DIALOG_SERVICE_TERMINATION);
		                } else {
		                    requestLicense(callerPackage, callerVersion);
		                }
		            }
		        }
	        }else{
	        	//如果下架，嘗試獲取license看看
	        	requestLicense(callerPackage, callerVersion);
	        }
		}
//		failFinish();
    }
    private void requestLicenseForPayingStatus(final String pkgName,final int version) {
    	showDialog(DIALOG_SUGGESTION_PAYSTATUS_UNKNOW);
    }

    private ApiInvoker<LicenseInfoRet> licenseInvoker;
    private void requestLicense(final String pkgName, final int version) {
        if (Logger.DEBUG)
            L.d("requestLicense");
        showDialog(DIALOG_AQURING_DATA);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				licenseInvoker=AndroidApiService.getInstance(getApplicationContext(),getConfiguration()).getLicense(pkgName, version);
				licenseInvoker.invoke();
				return null;
			}
			protected void onPostExecute(Void result) {
				responseLicense();
			};
		}.execute();
    }
    private void responseLicense(){
        if (licenseInvoker.isSuccess()&&licenseInvoker.getRet()!=null&&licenseInvoker.getRet().isSuccess()) {
            insertDigitalSignature(licenseInvoker.getRet());
            safeDismissDialog(DIALOG_AQURING_DATA);

            if (callerVersion == Integer.MAX_VALUE) {
                new AlertDialog.Builder(LicenseManager.this)
                        .setMessage(resources.getString("dialog_auth_success"))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString("close"),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }).show();
            } else {
                setResult(RESULT_OK);
                finish();
            }

        }else{
            safeDismissDialog(DIALOG_AQURING_DATA);
            if (Logger.DEBUG)
                L.d("LicenseInfoRet is null or size=0");
            if(appInfo!=null&&appInfo.getOnUse()!=ON_USE){
            	L.d("提示此app已經下架");
            	showDialog(DIALOG_SERVICE_TERMINATION);
            }else{
	            LicenseInfoRet ret = licenseInvoker.getRet();
	            String message = "\"" + appName + "\"" + resources.getString(ret == null ? "dialog_auth_fail_msg" : "dialog_auth_fail_msg_for_version");
	            new AlertDialog.Builder(LicenseManager.this)
	                    .setTitle(resources.getString("dialog_auth_fail"))
	                    .setMessage(message)
	                    .setPositiveButton(resources.getString("retry"),
	                            new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(
	                                        DialogInterface dialog,
	                                        int which) {
	                                    checkAuth();
	                                }
	                            })
	                    .setNegativeButton(resources.getString("close"),
	                            new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(
	                                        DialogInterface dialog,
	                                        int which) {
	                                    failFinish();
	                                }
	                            }).setCancelable(false).show();
	        }
            return;
        }
    }
    private void upgrade(final SdkApp apk) {
        if (Logger.DEBUG)
            L.d("upgrade");
        String message=null;
        AppPaidType paymentRequired = AppPaidType.getPaymentRequired(apk.getPriceType(), apk.getPayStatus(), apk.getPriceType());
        if (paymentRequired == AppPaidType.NEED_PAY) {
            if (Logger.DEBUG)
                L.d("upgrade PAYMENT_REQUIRED_NEED_PAY");
            message = "\""
                    + appName
                    + "\""
                    + resources.getString("dialog_pay_new_verision");
        }else{
            message = "\"" + appName + "\"" + resources.getString("dialog_new_verision");
        }
        new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage(message)
                    .setPositiveButton(resources.getString("submit"),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                	downloadNewVersionApp(apk);
                                	failFinish();
                                }
                            })
                    .setNegativeButton(resources.getString("close"),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    failFinish();
                                }
                            }).show();
    }

    private void insertDigitalSignature(LicenseInfoRet ret) {
        LicenseTool.deleteAllLicense(this, getIntent().getStringExtra(EXTRA_CALLER_PKG));
        List<LicenseInfo> l = ret.getApplication();
        for(LicenseInfo li : l) {
        	LicenseTool.insertLicense(this, li.getPkg(), li.getId().longValue(), li.getData().getBytes(), Base64Coder.decode(li.getSign()));
        }
    }
    private void canceled(){
    	AppSdkProvider.addSdkCancel(getIntent().getStringExtra(
                EXTRA_CALLER_PKG));
        setResult(RESULT_CANCELED);
    }
    private void failFinish() {
    	canceled();
        finish();
    }

    @Override
    protected final Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_AQURING_DATA://獲取license中
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(resources.getString("dialog_aquring_data"));
            dialog.setCancelable(false);
            return dialog;
        case DIALOG_SERVICE_TERMINATION://server端返回不合法的訊息
            String msg = "\"" + appName + "\"" + resources.getString("app_service_termination");
            return new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setNegativeButton(resources.getString("close"),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                    failFinish();
                                }
                            }).create();
        case DIALOG_NETWORK_FAILED://網路不通
            return new AlertDialog.Builder(this)
                    .setMessage(resources.getString("dialog_network_failed"))//
                    .setNegativeButton(resources.getString("close"),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                    failFinish();
                                }
                            }).create();
        case DIALOG_SUGGESTION_PAYSTATUS_UNKNOW://付款狀態不明
            return new AlertDialog.Builder(this)
                    .setMessage("\"" + appName + "\"" + message)//
                    .setNegativeButton(resources.getString("close"),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                    failFinish();
                                }
                            }).create();
        case DIALOG_APP_NOT_PUBLISH_YET:
            return new AlertDialog.Builder(this)
                    .setTitle(resources.getString("dialog_auth_fail")) //
                    .setMessage(resources.getString("dialog_app_not_publish_yet")) //
                    .setNegativeButton(resources.getString("close"),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    failFinish();
                                }
                            }).setCancelable(false).create();
        }
        return super.onCreateDialog(id);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (apiInvoker != null)
			apiInvoker.stop();
		if (licenseInvoker != null)
			licenseInvoker.stop();
	}
    final protected void safeDismissDialog(int id) {
        try {
            dismissDialog(id);
        } catch (Exception e) {
            if (Logger.ERROR)
                L.e("dismissDialog", e);
        }
    }
	public final String getCallerPackage() {
		return callerPackage;
	}
	public final LoginCallback getCallback() {
		return callback;
	}
	
	protected abstract void initView(Bundle savedInstanceState);
	protected abstract Configuration getConfiguration();
	protected abstract void downloadNewVersionApp(SdkApp apk);
	protected abstract void checkLoginStatusOrShowLoginUI(LoginCallback result);
	protected abstract void showPaymentUI();
	public SdkApp getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(SdkApp appInfo) {
		this.appInfo = appInfo;
	}

}