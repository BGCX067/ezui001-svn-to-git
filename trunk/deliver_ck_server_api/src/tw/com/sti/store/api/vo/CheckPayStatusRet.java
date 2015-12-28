package tw.com.sti.store.api.vo;


import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.vo.AppInfo.PayStatus;

final public class CheckPayStatusRet extends BaseRet {

	private AppInfo.PayStatus payStatus;
	private String statusMsg;

	public CheckPayStatusRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;

		JSONObject payStatus = json.optJSONObject("payStatus");
		if (payStatus == null) {
			throw new ApiDataInvalidException(json, CheckPayStatusRet.class,
					"CheckPayStatusRet's payStatus == null");
		}

		try {
			int status = payStatus.getInt("status");
			this.payStatus = PayStatus.parse(status);
			statusMsg = payStatus.optString("statusMsg");
		} catch (JSONException e) {
			throw new ApiDataInvalidException(json, CheckPayStatusRet.class,
					"OverPaymentQuotaRet's data invalid", e);
		}
	}

	public boolean isPaid() {
		return PayStatus.PAID.equals(payStatus);
	}

	public boolean isNoPaid() {
		return PayStatus.NO_PAID.equals(payStatus);
	}

	public String getStatusMsg() {
		return statusMsg;
	}
}
