package cn.com.vapk.vstore.client.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import tw.com.sti.clientsdk.ShoppingCartActivity;
import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.util.Logger;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.api.AppStatusReceiver;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;

public class GPayActivity extends ShoppingCartActivity {
	private static final Logger L = Logger.getLogger(GPayActivity.class);
	private String pkg;
	private String appTitle;
	private float appPrice;
	// private int appPriceType;
	private int payType = 0;
	private View mSubmit;
	private String pePayOrderUrl;
	private String pePayBaseUrl;
	private Map<String, String> params;
	static final String EXTRA_PKG_NAME = "pkg_name";
	static final String EXTRA_APP_TITLE = "appTitle";
	static final String EXTRA_APP_PRICE = "appPrice";
	static final String EXTRA_APP_PRICE_TYPE = "appPriceType";
	private int paymentType = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String type = intent.getStringExtra("payType");
		if(type != null && !"".equals(type)) {
			try {
				payType = Integer.parseInt(type);
			} catch(NumberFormatException e) {}
		}
		if(payType == ShoppingCartActivity.PAY_BY_PEPAY_TY_BILL) {
			Bundle b = intent.getExtras();
			params = new HashMap<String, String>();
			for(String k : b.keySet()) {
				if(k.equals("pePayOrderUrl")) {
					pePayOrderUrl = b.getString(k);
				} else if(k.equals("pePayBaseUrl")) {
					pePayBaseUrl = b.getString(k);
				} else {
					params.put(k, b.getString(k));
				}
			}
		} else {
			pkg = intent.getStringExtra(EXTRA_PKG_NAME);
			appTitle = intent.getStringExtra(EXTRA_APP_TITLE);
			// appPriceType = intent.getIntExtra(EXTRA_APP_PRICE_TYPE, 0);
			appPrice = intent.getFloatExtra(EXTRA_APP_PRICE, 0);
		}
		if (Logger.DEBUG) {
			L.d("PayActivity onCreate");
		}
		initUI();
	}

	private void initUI() {
		if(payType == ShoppingCartActivity.PAY_BY_PEPAY_TY_BILL) {
			setContentView(R.layout.app_pay_webview);
			try {
				WebView paymentWebView = (WebView) findViewById(R.id.web_view);
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(pePayOrderUrl);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				if(params.size() > 0) {
					for(String k : params.keySet()) {
						nameValuePairs.add(new BasicNameValuePair(k, params.get(k)));
					}
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				} else {
					nameValuePairs = null;
				}
				HttpResponse response = httpclient.execute(httppost);
				
				String data = new BasicResponseHandler().handleResponse(response);
				paymentWebView.getSettings().setDefaultTextEncodingName("utf-8");
				paymentWebView.getSettings().setJavaScriptEnabled(true);
				paymentWebView.getSettings().setPluginsEnabled(true); 
				paymentWebView.loadDataWithBaseURL(pePayBaseUrl, data, "text/html", "utf-8", null);
				paymentWebView.setVisibility(View.VISIBLE);
			} catch(Exception e) {
				
			}
		} else {
			setContentView(R.layout.pay_main);
	
			TextView mAppTitle = (TextView) findViewById(R.id.app_title);
//			TextView mAppPriceType = (TextView) findViewById(R.id.app_price_type);
			TextView mAppPrice = (TextView) findViewById(R.id.app_price);
	
			mAppTitle.setText(appTitle);
			mAppPrice.setText(String.valueOf(appPrice));
	
			mSubmit = findViewById(R.id.submit);
			mSubmit.setEnabled(false);
			mSubmit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (paymentType) {
					case ShoppingCartActivity.PAY_BY_GPAY:
						// Toast.makeText(GPayActivity.this, "使用支付寶付費!!!",
						// Toast.LENGTH_SHORT).show();
						payByGPAY(
								pkg,
								"2088501902645082",
								"alipay@vtion.com.cn",
								"RSA",
								"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALSfM3DjRwFuqeUdK9wk8WtcE34BlkG2jIBbH1CkPJYvX4BqgHJkzQ3Xp9uh+n+4UAUHDP9o9XQzyN7Tod7bTq7YqyViOPeH5mznE3NFLOs/eM0yQIlWUWEF8+0gY7oHVbTt6DP8QQWb+/AaAmtEhaZd1vUM5nmjkHyfU2uNyzOZAgMBAAECgYEAshEpg6dtPyo8cFX8AYKiR3d0+ArUeWBgz8IF81iXKnD1tAJLM3vQ9E6he9QKw5OPwAUdeUeehLrw00zNDooi3G9mzuqAWYSR42J+73+MsOeeptREiLtxKjTpi9rQ5YGxbPWH++iHDv5sgellqKV+o8A2XQtSznOBWSmmETZhXkkCQQDswLjZ+EE9EyC96NgDfqk7ZuxE4+nn4ALMa+oZVOox3DernXot+EISjkm+33eWc4B2M6jWaSgZn5sxvG5sBJZTAkEAw05Jb/+3KxMKyEvjpxDL/tt3qa5yZ5i03t5Qbq7kjdVrIcQhguUZgu6mpEPm0mhMR5hHGEo69/0lH3Uj1VF44wJARGu+AGBamkf44ml0ZC4qwlwQVCh55OU8ac+WLBP6SsaV4Cb2tGpWzf4JGfyDFxvyODbdyHlDeYRC2bDXSKhauQJACPYLmo7yGZDbGsuYm27C3WO2ftuZNjWkfuFWcW2m4Pcc3kTAgOAAJqjKjpjq+1Z7wYE0KeImCIDd9i6KwVvmnwJBAMLG7fFBP4bcgplo8R8yKbhKUPlTR7smTdGwsLoByoAex0DCeWizrO8Y9AcmklaAOVo9mC4acdKVKRv0l3Q/S+o=",
								null);
						break;
					case ShoppingCartActivity.PAY_BY_UNIONPAY:
						// Toast.makeText(GPayActivity.this, "使用银联付費!!!",
						// Toast.LENGTH_SHORT).show();
						payByUnionPay(
								pkg,
								"true",// isTest,测试环境下为"true"
								"http://211.154.166.219/qzjy/MerOrderAction/deal.action",// checkSignUrl,银联验签url
								"898000000000002",// merchantId,网讯安卓的银联商户代号，测试代号为898000000000002
								"网讯安卓",// merchantName,网讯安卓的银联商户名称
								// merchantPublicCer,商户公钥证书：这是商户公钥证书898000000000002.cer所读出公钥串。
								"MIIDuDCCAyGgAwIBAgIQaOXUUCzukC6m5EAlw0LdZjANBgkqhkiG9w0BAQUFADAkMQswCQYDVQQGEwJDTjEVMBMGA1UEChMMQ0ZDQSBURVNUIENBMB4XDTExMDgxNzAyNDU1M1oXDTEyMDgxNzAyNDU1M1owfjELMAkGA1UEBhMCQ04xFTATBgNVBAoTDENGQ0EgVEVTVCBDQTERMA8GA1UECxMITG9jYWwgUkExFDASBgNVBAsTC0VudGVycHJpc2VzMS8wLQYDVQQDFCYwNDFAWjIwMTEwODE3QDg5ODAwMDAwMDAwMDAwMkAwMDAwMDAwMzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAzD7Xy03ptoXR7jx3BxGD5GN2Fsivu/QprnYZF+Axby8LjVNGs97tHn8CHfXzvFMqAvsd4dkKzKrTG+dOmrlunYLGFrntIHl8Mx3liFkGLYFuJUy1+HF/hIRAMPIkDux6AAhbbCZlawdx5faHkM5OQg2KGeBcD+8NUJA6IYOunIUCAwEAAaOCAY8wggGLMB8GA1UdIwQYMBaAFEZy3CVynwJOVYO1gPkL2+mTs/RFMB0GA1UdDgQWBBSrHyJHSuW1lYnmxBBh6ulilF4xSTALBgNVHQ8EBAMCBPAwDAYDVR0TBAUwAwEBADA7BgNVHSUENDAyBggrBgEFBQcDAQYIKwYBBQUHAwIGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwgwgfAGA1UdHwSB6DCB5TBPoE2gS6RJMEcxCzAJBgNVBAYTAkNOMRUwEwYDVQQKEwxDRkNBIFRFU1QgQ0ExDDAKBgNVBAsTA0NSTDETMBEGA1UEAxMKY3JsMTI3XzE1MzCBkaCBjqCBi4aBiGxkYXA6Ly90ZXN0bGRhcC5jZmNhLmNvbS5jbjozODkvQ049Y3JsMTI3XzE1MyxPVT1DUkwsTz1DRkNBIFRFU1QgQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDQYJKoZIhvcNAQEFBQADgYEARfg4YNGNETrJx+gy74UmPJ326T7H2hIE/lRcTyonq4NFpXmssau+TDV7btLUuhDxBGF1JysknFjeNAKMl9ZFGjKbOGGpQ7nfnEC8HIM7cp2n+gSlADRZbc8PHrqxLbjxsKoSUUFfh3PhfNXtWLfTxi5TT+hm6coV1K/EUX4t0AY=",
								"898000000000002.p12",// privateKeyFileName,私钥档案名，测试用为898000000000002.p12
								// privateKeyAlias,签名加密私钥串
								"889ce7a52067a87f905c91f502c69644_d1cba47d-cbb1-4e29-9d77-8d1fe1b0dccd",
								"898000000000002",// privateKeyPassword,签名加密私钥串的密码
								"",// transTimeout,超时时间，目前为空字串
								null);
						break;
					default:
						break;
					}
				}
			});
	
			View mCancel = findViewById(R.id.cancel);
			mCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
	
			initPayMethodUI();
		}
	}

	private void initPayMethodUI() {
		// TextView mPayMethod = (TextView) findViewById(R.id.pay_method);
		// TextView mPayMethodDetail = (TextView)
		// findViewById(R.id.pay_method_detail);
		// mPayMethodDetail.setVisibility(View.GONE);
		// mPayMethod.setText(R.string.payment_title);
		RadioGroup mRadioGroupPaymentMethods = (RadioGroup) findViewById(R.id.payment_methods);
		final RadioButton mRadioGPay = (RadioButton) findViewById(R.id.g_pay);
		final RadioButton mRadioUnionPay = (RadioButton) findViewById(R.id.union_pay);
		mRadioGroupPaymentMethods
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == mRadioGPay.getId()) {
							paymentType = 1;
							mSubmit.setEnabled(true);
						} else if (checkedId == mRadioUnionPay.getId()) {
							paymentType = 2;
							mSubmit.setEnabled(true);
						}
					}
				});
		// mSubmit.setEnabled(true);
		return;
	}

	@Override
	protected Configuration getConfiguration() {
		return ConfigurationFactory.getInstance();
	}

	@Override
	protected void payResult(final Context ctx, final String retCode,
			final String memo) {
		if (memo != null && !"".equals(memo)) {
			new AlertDialog.Builder(this)
					.setCancelable(false)
					.setMessage(memo)
					.setNegativeButton(R.string.close,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (RET_SUCCESS.equals(retCode)) {
										AppStatusReceiver.broadcastPaid(ctx,
												pkg);
									}

									finish();
								}
							}).show();
		}
	}

	@Override
	protected void showLogin() {
		if (Logger.DEBUG)
			L.d("showLogin");
	}
}
