package tw.com.sti.clientsdk;

import tw.com.sti.store.api.vo.Credential;

public interface LoginCallback {
	public abstract void success(Credential c);
	public abstract void cancel();
}
