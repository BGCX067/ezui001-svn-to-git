package tw.com.sti.store.api;

public class CommonParameter {
	private String storeId;
	private String sdkVer;
	private String sdkRel;
	private String clientVer;
	private String deviceId;
	private String subscriberId;
	private String simSerialNumber;
	private String token;
	private String wpx;
	private String hpx;
	private String userId;
	private String uid;
	private int appFilter=0;
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getSdkVer() {
		return sdkVer;
	}
	public void setSdkVer(String sdkVer) {
		this.sdkVer = sdkVer;
	}
	public String getSdkRel() {
		return sdkRel;
	}
	public void setSdkRel(String sdkRel) {
		this.sdkRel = sdkRel;
	}
	public String getClientVer() {
		return clientVer;
	}
	public void setClientVer(String clientVer) {
		this.clientVer = clientVer;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
	public String getSimSerialNumber() {
		return simSerialNumber;
	}
	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber = simSerialNumber;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getWpx() {
		return wpx;
	}
	public void setWpx(String wpx) {
		this.wpx = wpx;
	}
	public String getHpx() {
		return hpx;
	}
	public void setHpx(String hpx) {
		this.hpx = hpx;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getAppFilter() {
		return appFilter;
	}
	public void setAppFilter(int appFilter) {
		this.appFilter = appFilter;
	}
}
