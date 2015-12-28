package cn.com.vapk.vstore.client.receiver;

import tw.com.sti.store.api.android.util.Logger;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cn.com.vapk.vstore.client.ActionController;
import cn.com.vapk.vstore.client.receiver.listener.LoginEvent;
import cn.com.vapk.vstore.client.receiver.listener.LoginEventListener;

public class LoginEventReceiver extends BroadcastReceiver {
	private static final Logger L = Logger.getLogger(LoginEventReceiver.class);
	private Activity aty;

	public LoginEventReceiver(Activity aty) {
		this.aty = aty;
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (Logger.DEBUG)
			L.d("Activity: " + aty+" action="+action);
		if (ActionController.LOGIN_EVENT_SUCCESS.equals(action)){
			//啟動下載，啟動付款
			String event=intent.getStringExtra(LoginEvent.class.getName());
			if((Object)aty instanceof LoginEventListener){
				LoginEvent ev=new LoginEvent();
				ev.setEventType(event);
				L.d("receive login event "+event);
				((LoginEventListener)((Object)aty)).onLogin(ev);
			}
		}
	}

	public void register() {
		IntentFilter itf = new IntentFilter();
		itf.addAction(ActionController.LOGIN_EVENT_SUCCESS);
		aty.registerReceiver(this, itf,
				ActionController.PERMISSION_LOGIN_EVENT_SUCCESS, null);
		L.d("on register");
	}

	public void unregister() {
		aty.unregisterReceiver(this);
	}

	void previousWork() {

	}
}