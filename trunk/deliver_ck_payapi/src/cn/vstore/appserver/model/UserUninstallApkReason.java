package cn.vstore.appserver.model;

import java.util.Date;

public class UserUninstallApkReason {
	
	private Long id;
	private String userId;
	private String imei;
	private String iccid;
	private String appId;
	private int versionId;
	private double reasonId;
	private Date reportTime;
	private Long downloadId;
	private String userUid;
	private Long storeId;
	
	public Long getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(Long downloadId) {
		this.downloadId = downloadId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	public double getReasonId() {
		return reasonId;
	}
	public void setReasonId(double reasonId) {
		this.reasonId = reasonId;
	}
	public Date getReportTime() {
		return reportTime;
	}
	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}
	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	
}
