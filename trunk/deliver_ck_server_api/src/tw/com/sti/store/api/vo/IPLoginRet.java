package tw.com.sti.store.api.vo;


import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.util.LangUtils;

final public class IPLoginRet extends BaseRet {

	private boolean ipLogin;
	private String account;
	private String uid;
	private String token;
	private boolean appFilterSettingEnable;

	public IPLoginRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;

		JSONObject credential = json.optJSONObject("credential");
		if (credential == null) {
			throw new ApiDataInvalidException(json, IPLoginRet.class,
					"IPLoginRet credential == null");
		}

		appFilterSettingEnable = credential.optInt("afse", 1) == 1;
		try {
			ipLogin = credential.getBoolean("ipLogin");
		} catch (JSONException e) {
			throw new ApiDataInvalidException(json, IPLoginRet.class,
					"IPLoginRet JSON invalid.", e);
		}

		if (ipLogin) {
			account = credential.optString("account");
			token = credential.optString("token");
			uid = credential.optString("uid");

			if (LangUtils.isBlankAny(token, uid, account) > -1) {
				throw new ApiDataInvalidException(json, IPLoginRet.class,
						"token, uid, account is blank any");
			}
		}
	}

	public boolean isIpLogin() {
		return ipLogin;
	}

	public boolean isAppFilterSettingEnable() {
		return appFilterSettingEnable;
	}

	public Credential getCredential() {
		if (LangUtils.isBlankAny(token, uid, account) > -1)
			return null;
		return new Credential(token, uid, account);
	}

}
