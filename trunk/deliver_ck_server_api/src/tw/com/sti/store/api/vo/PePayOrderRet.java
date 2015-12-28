package tw.com.sti.store.api.vo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

final public class PePayOrderRet extends BaseRet {
	
	// 訂單號碼
	private String orderNo;
	
	// PePay 付款 URL
	private String pePayOrderUrl;
	
	// PePay 付款 URL 給 WebView 使用
	private String pePayBaseUrl;
	
	private Map<String, String> params;

	public PePayOrderRet(JSONObject json) {
		super(json);
		if (isSuccess()) {
			orderNo = json.optString("orderNo");
			pePayOrderUrl = json.optString("pePayOrderUrl");
			pePayBaseUrl = json.optString("pePayBaseUrl");
			params = new HashMap<String, String>();
			params.put("SHOP_ID", json.optString("SHOP_ID"));
			params.put("ORDER_ID", json.optString("ORDER_ID"));
			params.put("ORDER_ITEM", json.optString("ORDER_ITEM"));
			params.put("AMOUNT", json.optString("AMOUNT"));
			params.put("CURRENCY", json.optString("CURRENCY"));
			params.put("PAY_TYPE", json.optString("PAY_TYPE"));
			params.put("CHECK_CODE", json.optString("CHECK_CODE"));
		}
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getPePayOrderUrl() {
		return pePayOrderUrl;
	}

	public String getPePayBaseUrl() {
		return pePayBaseUrl;
	}

	public Map<String, String> getParams() {
		return params;
	}
}
