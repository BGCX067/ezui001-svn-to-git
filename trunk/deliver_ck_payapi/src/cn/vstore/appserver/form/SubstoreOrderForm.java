package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SubstoreOrderForm extends TokenForm{
	private String userId;
	private String pkg;
	private String imei;
	private String storeOid;
	private String reason;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getStoreOid() {
		return storeOid;
	}
	public void setStoreOid(String storeOid) {
		this.storeOid = storeOid;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
