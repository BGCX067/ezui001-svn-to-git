package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.vo.BaseRet;
import tw.com.sti.store.api.vo.NewClientInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;
import cn.com.vapk.vstore.client.R;

public final class DialogUtils {

	public final static int DLG_NO_NETWORK = 100;
	public final static int DLG_CONN_TO_SERVER_FAIL = 101;
	public final static int DLG_API_RET_FAIL = 102;
	public final static int DLG_API_RET_NEW_CLIENT = 103;
//	final static int DLG_API_RET_RELOGIN = 104;

	public static final boolean handleApiInvokeDialog(final Activity aty,
			ApiInvoker<? extends BaseRet> apiInvoker) {
		if (aty.isFinishing())
			return true;

		if (apiInvoker.isStop()) {
			return true;
		}

		if (apiInvoker.isFail()) {
			createConnectionToServerFailAlertDialog(aty).show();
			return true;
		}

		BaseRet appRet = apiInvoker.getRet();
		boolean isRelogin = appRet.isReLogin();
		
		if (isRelogin && ActionController.isLoginNeededForVtion(aty)) {
			createReLoginAlertDialog(aty, appRet.getRetMsg()).show();
			return true;
		}

		if (appRet.isFail()) {
			createRetFailMsgAlertDialog(aty, appRet.getRetMsg()).show();
			return true;
		}

		if (appRet.isHasNewClient()) {
			createNewClientAlertDialog(aty, appRet.getNewClientInfo()).show();
			return true;
		}

		return false;
	}

	/**
	 * 產生Dialog，則點選"登入"後會跳到登入畫面
	 */
	static final AlertDialog createReLoginAlertDialog(final Activity aty,
			String msg) {
		Builder builder = new AlertDialog.Builder(aty);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.relogin,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActionController.logout(aty);
						aty.finish();
					}
				});
		builder.setNegativeButton(R.string.close,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActionController.closeSMart(aty);
						aty.finish();
					}
				});
		return builder.create();
	}

	/**
	 * 產生Dialog，則點選"更新"後會進行Client更新
	 */
	public static final AlertDialog createNewClientAlertDialog(final Activity aty,
			final NewClientInfo newClinetRet) {
		Builder builder = new AlertDialog.Builder(aty);
		builder.setMessage(newClinetRet.getMsg());
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.update,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cn.com.vapk.vstore.client.update.ActionController
								.downloadClient(aty);
						aty.finish();
					}
				});
		builder.setNegativeButton(R.string.close,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActionController.closeSMart(aty);
						aty.finish();
					}
				});
		return builder.create();
	}

	/**
	 * 產生Dialog，則點選close後會關閉該Activity
	 */
	static final AlertDialog createRetFailMsgAlertDialog(Activity aty,
			String msg) {
		return createFinishActivityAlertDialog(aty, msg, true);
	}

	/**
	 * 產生一無網路 Dialog，則點選close後會關閉該Activity
	 */
	static final AlertDialog createNoNetworkAlertDialog(Activity aty) {
		return createNoNetworkAlertDialog(aty, true);
	}

	/**
	 * 產生一無網路 Dialog
	 */
	static final AlertDialog createNoNetworkAlertDialog(Activity aty,
			boolean finishAty) {
		return createFinishActivityAlertDialog(aty, R.string.dlg_no_network,
				finishAty);
	}

	/**
	 * 產生一伺服器連線失敗 Dialog，則點選close後會關閉該Activity
	 */
	static final AlertDialog createConnectionToServerFailAlertDialog(
			Activity aty) {
		return createFinishActivityAlertDialog(aty,
				R.string.dlg_conntect_to_server_fail, true);
	}

	/**
	 * 產生 Dialog
	 */
	static final AlertDialog createAlertDialog(Activity aty, int msgResId) {
		String msg = aty.getResources().getString(msgResId);
		return createFinishActivityAlertDialog(aty, msg, false);
	}

	/**
	 * 產生 Dialog
	 */
	static final AlertDialog createAlertDialog(Activity aty, String msg) {
		return createFinishActivityAlertDialog(aty, msg, false);
	}

	/**
	 * 產生 Dialog，如果finishAty，則會關閉Activity
	 */
	private static final AlertDialog createFinishActivityAlertDialog(
			Activity aty, int msgResId, boolean finishAty) {
		String msg = aty.getResources().getString(msgResId);
		return createFinishActivityAlertDialog(aty, msg, finishAty);
	}

	/**
	 * 產生 Dialog，如果finishAty == true，則點選close後會關閉該Activity
	 */
	static final AlertDialog createFinishActivityAlertDialog(Activity aty,
			String msg, boolean finishAty) {
		Builder builder = new AlertDialog.Builder(aty);
		builder.setMessage(msg);
		if (finishAty) {
			builder.setPositiveButton(R.string.close,
					new ActivityFinishClickListener(aty));
			builder.setOnCancelListener(new FinishActivityCancelListener(aty));
		} else {
			builder.setPositiveButton(R.string.close, null);
		}
		return builder.create();
	}

	/**
	 * focus 到 View
	 */
	static final class ViewFocusClickListener implements
			DialogInterface.OnClickListener {

		private View v;

		public ViewFocusClickListener(View v) {
			this.v = v;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			v.requestFocus();
		}
	}

	/**
	 * 關閉Activity
	 */
	static final class ActivityFinishClickListener implements
			DialogInterface.OnClickListener {

		private Activity a;

		public ActivityFinishClickListener(Activity a) {
			this.a = a;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			a.finish();
		}
	}

	/**
	 * Cancel 時，若有finishActivity，則finishActivity.finish();
	 */
	static final class FinishActivityCancelListener implements
			DialogInterface.OnCancelListener {

		private Activity finishActivity;

		FinishActivityCancelListener(Activity finishAty) {
			this.finishActivity = finishAty;
		}

		@Override
		final public void onCancel(DialogInterface dialog) {
			if (finishActivity != null) {
				finishActivity.finish();
			}
		}
	}

	/**
	 * Cancel 時，把Invoker呼叫停止，若有finishActivity，則finishActivity.finish();
	 */
	static final class ApiInvokerStopCancelListener implements
			DialogInterface.OnCancelListener {

		private ApiInvoker<?> apiInvoker;
		private Activity finishActivity;
		private int cancelMsgResourceId;
		private Context ctx;

		ApiInvokerStopCancelListener(ApiInvoker<?> apiInvoker) {
			this.apiInvoker = apiInvoker;
		}

		ApiInvokerStopCancelListener(ApiInvoker<?> apiInvoker,
				Activity finishAty) {
			this.apiInvoker = apiInvoker;
			this.finishActivity = finishAty;
		}

		public ApiInvokerStopCancelListener setCancelMessage(Context ctx,
				int resId) {
			cancelMsgResourceId = resId;
			this.ctx = ctx;
			return this;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			boolean stop = false;
			if (apiInvoker != null) {
				stop = apiInvoker.stop();
			}
			if (!stop) {
				return;
			}
			if (cancelMsgResourceId != 0) {
				Toast.makeText(ctx, cancelMsgResourceId, Toast.LENGTH_LONG)
						.show();
			}
			if (finishActivity != null) {
				finishActivity.finish();
			}
		}
	}

}
