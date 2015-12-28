package tw.com.sti.store.api.vo;

import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.util.LangUtils;

final public class LoginRet extends BaseRet {

	private String uid;
	private String token;
	private boolean appFilterSettingEnable;

	public LoginRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;

		JSONObject credential = json.optJSONObject("credential");
		if (credential == null) {
			throw new ApiDataInvalidException(json, LoginRet.class,
					"IPLogin credential == null");
		}

		appFilterSettingEnable = credential.optInt("afse", 1) == 1;
		token = credential.optString("token");
		uid = credential.optString("uid");

		if (LangUtils.isBlankAny(token, uid) > -1) {
			throw new ApiDataInvalidException(json, LoginRet.class,
					"LoginRet token, uid is blank Any.");
		}
	}

	public boolean isAppFilterSettingEnable() {
		return appFilterSettingEnable;
	}

	public Credential getCredential(String userId) {
		if (LangUtils.isBlankAny(token, uid, userId) > -1)
			return null;
		return new Credential(token, uid, userId);
	}

}
