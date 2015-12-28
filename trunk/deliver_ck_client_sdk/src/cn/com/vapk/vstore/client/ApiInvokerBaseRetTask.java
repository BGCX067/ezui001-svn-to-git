package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.vo.BaseRet;
import android.app.Activity;

abstract class ApiInvokerBaseRetTask<T extends BaseRet> extends
		ApiInvokerTask<T> {

	ApiInvokerBaseRetTask(Activity aty, ApiInvoker<T> apiInvoker) {
		super(aty, apiInvoker);
	}

	final protected void handleRet(Activity aty, T ret) {
		if (ret.isSuccess()) {
			success(ret);
		}

		if (ret.isReLogin()) {
			DialogUtils.createReLoginAlertDialog(aty, ret.getRetMsg()).show();
			return;
		}

		if (ret.isFail()) {
			DialogUtils.createRetFailMsgAlertDialog(aty, ret.getRetMsg()).show();
			return;
		}

		if (ret.isHasNewClient()) {
			DialogUtils.createNewClientAlertDialog(aty, ret.getNewClientInfo())
					.show();
			return;
		}
	}

	abstract protected void success(T ret);

}
