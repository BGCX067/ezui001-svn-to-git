package tw.com.sti.store.api.vo;


import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.util.LangUtils;

abstract public class BaseRet {

	private String result;
	private String retMsg;
	private String sysMsg;
	private NewClientInfo newClientRet;

	public BaseRet(JSONObject json) {
		result = json.optString("ret");
		if (LangUtils.isBlank(result)) {
			throw new ApiDataInvalidException(json, BaseRet.class,
					"ret is blank.");
		}
		retMsg = json.optString("retMsg");
		sysMsg = json.optString("sysMsg");
		if (!json.isNull("newClient")) {
			newClientRet = NewClientInfo.createInstance(json
					.optJSONObject("newClient"));
		}
	}

	final public boolean isSuccess() {
		return "0001".equals(result);
	}

	final public boolean isReLogin() {
		return "0010".equals(result);
	}

	final public boolean isFail() {
		return (!isSuccess() && !isReLogin() && !isHasNewClient());
		// return "0002".equals(result) || "0011".equals(result)||
		// "0012".equals(result) || "0004".equals(result);
	}
	
	final public String getResult() {
		return result;
	}

	final public String getRetMsg() {
		return retMsg;
	}

	final public String getSysMsg() {
		return sysMsg;
	}

	final public boolean isHasNewClient() {
		return "0003".equals(result) && newClientRet != null;
	}

	final public NewClientInfo getNewClientInfo() {
		return newClientRet;
	}

}
