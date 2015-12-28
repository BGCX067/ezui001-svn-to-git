package cn.vstore.appserver.model;

import java.io.Serializable;

public class OrderInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4925583833367666405L;
	private PaymentInformation payInfo;
	private Application app;
	private Prosumer user;
	public PaymentInformation getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(PaymentInformation payInfo) {
		this.payInfo = payInfo;
	}
	public Application getApp() {
		return app;
	}
	public void setApp(Application app) {
		this.app = app;
	}
	public Prosumer getUser() {
		return user;
	}
	public void setUser(Prosumer user) {
		this.user = user;
	}
	
}
