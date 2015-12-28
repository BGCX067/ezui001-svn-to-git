package cn.vstore.appserver.form.payment;

import cn.vstore.appserver.form.TokenForm;

public class CancelGameOrderForm extends TokenForm{
	private String orderNo;
	private String reason;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
