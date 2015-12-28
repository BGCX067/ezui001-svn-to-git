package cn.com.vapk.vstore.utils;

import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import cn.com.vapk.vstore.client.util.Constants;
import cn.com.vapk.vstore.client.util.NetworkUtils;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import tw.com.sti.store.api.util.EasySSLSocketFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NetworkUtilities {

	/**
	 * get a HttpClient with connection and socket timeout set to 15 second
	 */
	public static HttpClient getHttpClient() {
		return getHttpClient(Constants.API_TIMEOUT);
	}

	public static HttpClient getHttpClient(int timeout) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();// http scheme
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), ConfigurationFactory.getInstance().getApiHttpPort()));// https scheme
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), ConfigurationFactory.getInstance().getApiHttpsPort()));

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		// params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new
		// ConnPerRouteBean(30));
		// params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

		HttpClient httpClient = new DefaultHttpClient(cm, params);
		return httpClient;
	}

	public static boolean isNetworkOpen(Context ctx) {
		return NetworkUtils.isNetworkOpen(ctx);
	}

	public static final void checkNetworkOpen(final Activity activity) {
		if (!NetworkUtilities.isNetworkOpen(activity)) {
			showNoNetworkAlertDialog(activity);
		}
	}

	public static final void showNoNetworkAlertDialog(final Activity activity) {
		showNoNetworkAlertDialog(activity, false);
	}

	public static final void showNoNetworkAlertDialog(final Activity activity,
		final boolean closeIfCancle) {
		new AlertDialog.Builder(activity) //
		.setMessage(R.string.dialog_no_network) //
		.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (closeIfCancle)
					activity.finish();
			}
		}).show();
	}
}
