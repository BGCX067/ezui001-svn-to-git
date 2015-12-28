package cn.com.vapk.vstore.client;

import tw.com.sti.store.api.android.AndroidApiService;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cn.com.vapk.vstore.client.R;
import cn.com.vapk.vstore.client.util.ConfigurationFactory;

public class EulaActivity extends Activity implements View.OnClickListener {
	private CloseClientReceiver closeClientReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eula);
		findViewById(R.id.submit).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);

		closeClientReceiver = new CloseClientReceiver(this);
		closeClientReceiver.register();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (closeClientReceiver != null)
			closeClientReceiver.unregister();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
			agreeEula();
			break;
		case R.id.cancel:
			refuseEula();
			break;
		}
	}

	private void agreeEula() {
		ActionController.agreeEula(this);
		finish();
	}

	private void refuseEula() {
		finish();
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	// Old Version Client
	public static boolean isAgreeEula(Context context) {
		String userId = AndroidApiService.getInstance(context,ConfigurationFactory.getInstance()).getCredential(context).getUserId();
		return ActionSession.isAgreeEula(context, userId);
	}

	// Old Version Client
	public static void agreeEula(Context context) {
		String userId = AndroidApiService.getInstance(context,ConfigurationFactory.getInstance()).getCredential(context).getUserId();
		ActionSession.agreeEula(context, userId);
	}
}