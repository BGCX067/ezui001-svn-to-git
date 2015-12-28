package cn.vstore.appserver.api.payment.support;

import javax.servlet.http.HttpServletRequest;

public class ConstantPayUrl {
	//支付宝手机端请求URL
	public final static String ALIPAY_REQUEST = "/api/alipay/order/{propsId:.+}";
	//支付宝服务器端响应URL
	public final static String ALIPAY_RECEIVER = "/api/integrate/alipay/receiver";
	//银联手机端请求URL
	public final static String UNION_REQUEST = "/api/unionpay/order/{propsId:.+}";
	//银联服务器端响应URL
	public final static String UNION_RECEIVER = "/api/integrate/unionpay/receiver";
	
	//神州付订单
	public final static String SHENZHOUFU_ORDER_REQUEST = "/api/szfpay/order/{propsId:.+}";
	public final static String SHENZHOUFU_PAY_RESPONSE = "/api/integrate/szfpay/receiver";
	
	//查询订单支付状态
	public final static String QUERY_ORDER_PAY_STATUS = "/api/integrate/orderpay/receiver";
	
	public static String getServerPath(HttpServletRequest request){
		return request.getScheme()+":"+request.getServerName()+request.getContextPath();
	}
}
