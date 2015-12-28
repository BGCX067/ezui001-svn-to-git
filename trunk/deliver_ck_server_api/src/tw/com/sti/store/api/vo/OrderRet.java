package tw.com.sti.store.api.vo;

import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

final public class OrderRet extends BaseRet {

	private OrderInfo order;

	public OrderRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;

		JSONObject orderret = json.optJSONObject("order");
		if (orderret == null) {
			throw new ApiDataInvalidException(json, OrderRet.class,
					"CheckPayOrderRet's pay order == null");
		}
		
		order=OrderInfo.createInstance(orderret);
	}

	public OrderInfo getOrder() {
		return order;
	}
}

