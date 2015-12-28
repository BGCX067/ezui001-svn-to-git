package tw.com.sti.store.api.vo;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

final public class OverPaymentQuotaRet extends BaseRet {

	private boolean overQuota;
	private String overQuotaMsg;

	public OverPaymentQuotaRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;

		JSONObject paymentQuota = json.optJSONObject("paymentQuota");
		if (paymentQuota == null) {
			throw new ApiDataInvalidException(json, OverPaymentQuotaRet.class,
					"OverPaymentQuotaRet's paymentQuota == null");
		}

		try {
			overQuota = paymentQuota.getBoolean("overQuota");
			overQuotaMsg = paymentQuota.optString("overQuotaMsg");
		} catch (JSONException e) {
			throw new ApiDataInvalidException(json, OverPaymentQuotaRet.class,
					"OverPaymentQuotaRet's overQuota is empty", e);
		}
	}

	public boolean isOverQuota() {
		return overQuota;
	}

	public String getOverQuotaMsg() {
		return overQuotaMsg;
	}

}
