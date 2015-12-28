package cn.com.vapk.vstore.client.payment;

import tw.com.sti.clientsdk.ShoppingCartActivity;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.util.LangUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ActionController {

	private static final Logger L = Logger.getLogger(ActionController.class);

	private static final String ACTION_PAYMENT = "cn.com.vapk.vstore.client.action.PAYMENT";
	
	public static final String GET_ACTION_PAYMENT(){
		return ACTION_PAYMENT;
	}

	public static final void payment(Activity aty, String pkg) {
		payment(aty, pkg, null);
	}

	public static final void payment(Activity aty, String pkg, Bundle extras) {
		if (Logger.DEBUG)
			L.d("payment");
		if (LangUtils.isBlank(pkg)) {
			if (Logger.DEBUG)
				throw new RuntimeException("payment pkg name is null.");
			return;
		}

		Intent intent = new Intent(aty, GPayActivity.class);
		intent.setAction(ACTION_PAYMENT);
		
		intent.putExtra(GPayActivity.EXTRA_PKG_NAME, extras == null ? pkg : extras.getString("pkg"));
		intent.putExtra(GPayActivity.EXTRA_APP_TITLE, extras == null ? "" : extras.getString("title"));
		intent.putExtra(GPayActivity.EXTRA_APP_PRICE, extras == null ? "" : extras.getFloat("price"));
//		intent.putExtra(GPayActivity.EXTRA_APP_PRICE_TYPE, extras == null ? "" : extras.getInt("priceType"));

		if (extras != null)
			intent.putExtras(extras);
		
		aty.startActivity(intent);
	}
	
	public static final void pePayment(Activity aty, String pkg, Bundle extras) {
		if (Logger.DEBUG)
			L.d("pePayment");
		if (LangUtils.isBlank(pkg)) {
			if (Logger.DEBUG)
				throw new RuntimeException("pePayment pkg name is null.");
			return;
		}

		Intent intent = new Intent(aty, GPayActivity.class);
		intent.setAction(ACTION_PAYMENT);
		intent.putExtras(extras);
		
		if (extras != null) {
			intent.putExtras(extras);
		}
		
		aty.startActivity(intent);
	}
}
