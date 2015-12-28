package tw.com.sti.clientsdk;

import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.vo.Credential;
import android.app.Activity;

public abstract class LoginActivity extends Activity {
    LoginCallback callback=new LoginCallback() {
		
		@Override
		public void success(Credential c) {
		    AndroidApiService.getInstance(getApplicationContext(),getConfiguration()).saveCredential(getApplicationContext(),c);
		}

		@Override
		public void cancel() {
			// TODO Auto-generated method stub
			
		}
	};
	public final LoginCallback getCallback() {
		return callback;
	}
	protected abstract Configuration getConfiguration();
}
