package cn.com.vapk.vstore.client;

import java.lang.ref.WeakReference;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.util.LangUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.AsyncTask;

abstract class ApiInvokerTask<T> extends AsyncTask<Void, Void, Void> {

	private WeakReference<Activity> atyRef;
	private ApiInvoker<T> apiInvoker;
	private ProgressDialog dialog;
	private String progressMsg;

	ApiInvokerTask(Activity aty, ApiInvoker<T> apiInvoker) {
		atyRef = new WeakReference<Activity>(aty);
		this.apiInvoker = apiInvoker;
	}

	ApiInvokerTask<T> setProgressMsg(String progressMsg) {
		this.progressMsg = progressMsg;
		return this;
	}

	final protected void onPreExecute() {
		if (atyRef == null || atyRef.get() == null) {
			return;
		}
		Activity aty = atyRef.get();
		dialog = new ProgressDialog(aty);
		String msg = !LangUtils.isBlank(progressMsg) ? progressMsg : aty
				.getResources().getString(R.string.dialog_aquring_data);
		dialog.setMessage(msg);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				apiInvoker.stop();
			}
		});
		dialog.show();
	};

	@Override
	final protected Void doInBackground(Void... params) {
		apiInvoker.invoke();
		return null;
	}

	@Override
	final protected void onPostExecute(Void result) {
		if (atyRef == null)
			return;

		Activity aty = atyRef.get();
		if (aty == null)
			return;

		if (aty.isFinishing())
			return;

		dialog.dismiss();
		if (apiInvoker.isStop())
			return;

		if (apiInvoker.isFail()) {
			DialogUtils.createConnectionToServerFailAlertDialog(aty).show();
			return;
		}

		handleRet(aty, apiInvoker.getRet());
	}

	abstract protected void handleRet(Activity aty, T ret);

}
