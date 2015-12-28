package tw.com.sti.store.api.vo;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

final public class UnsubscribeRet extends BaseRet {

	private int status;
	private String statusMsg;
	private String subscribeExpDate;

	public UnsubscribeRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;

		JSONObject unsubscribe = json.optJSONObject("unsubscribe");
		if (unsubscribe == null) {
			throw new ApiDataInvalidException(json, UnsubscribeRet.class,
					"UnsubscribeRet's unsubscribe == null. json: " + json);
		}

		try {
			status = unsubscribe.getInt("status");
			statusMsg = unsubscribe.optString("statusMsg");
			subscribeExpDate = unsubscribe.optString("subscribeExpDate");
		} catch (JSONException e) {
			throw new ApiDataInvalidException(json, UnsubscribeRet.class,
					"UnsubscribeRet's data invalid.", e);
		}
	}

	public boolean isUnsubscribeSuccess() {
		return status == 1;
	}

	public boolean isNonsubscribe() {
		return status == 4;
	}

	public String getUnsubscribeMsg() {
		return statusMsg;
	}

	public String getSubscribeExpDate() {
		return subscribeExpDate;
	}

}
