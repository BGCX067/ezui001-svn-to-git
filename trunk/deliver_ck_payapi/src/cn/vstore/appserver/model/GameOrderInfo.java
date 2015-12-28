package cn.vstore.appserver.model;

import java.io.Serializable;

public class GameOrderInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4925583833367666405L;
	private GamePayInformation payInfo;
	private Application app;
	private Prosumer user;
	public GamePayInformation getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(GamePayInformation payInfo) {
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
