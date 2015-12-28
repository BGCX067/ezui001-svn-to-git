package cn.vstore.appserver.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

import cn.vstore.appserver.form.BaseForm;

/**
 * @version $Id: UserInstallApp.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class StoreClientInstallLog {

	private BigDecimal storeId;
	private int appVersion;
	private String imei;
	private String iccid;
	private String mac;
	private String snum;
    private String IP;


    public StoreClientInstallLog() {
    }

    public StoreClientInstallLog(BaseForm baseForm, int appVersion, BigDecimal storeId, String IP) {
    	this.storeId = storeId;
    	this.appVersion = appVersion;
    	this.imei = baseForm.getImei();
    	this.iccid = baseForm.getIccid();
    	this.mac = baseForm.getMac();
    	this.snum = baseForm.getSnum();
    	this.IP = IP;
    }

	public BigDecimal getStoreId() {
		return storeId;
	}


	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}


	public int getAppVersion() {
		return appVersion;
	}


	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}


	public String getImei() {
		return imei;
	}


	public void setImei(String imei) {
		this.imei = imei;
	}


	public String getIccid() {
		return iccid;
	}


	public void setIccid(String iccid) {
		this.iccid = iccid;
	}


	public String getMac() {
		return mac;
	}


	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getSnum() {
		return snum;
	}

	public void setSnum(String snum) {
		this.snum = snum;
	}

}
