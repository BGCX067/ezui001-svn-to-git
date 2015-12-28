package tw.com.sti.store.api.vo;

import org.json.JSONObject;

final public class CPAppsRet extends AppsRet {

	private String provider;

	public CPAppsRet(JSONObject json) {
		super(json);
		provider = json.optString("provider");
	}

	public String getProvider() {
		return provider;
	}
}
