package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

public class UserInstallLog implements Serializable {

	private Long logUid;
    private String userId;
    private String iccid;
    private String imei;
    private String mac;
    private Date installDate;
    private String downloadType;
    private Double installStatus;
    private String msisdn;
    private String appId;
    private Integer versionId;
    private Long downloadId;
    private String modelName;
    private Double failCode;
    private String userUid;
    private Date createTime;
    private Long storeId;
    private static final long serialVersionUID = 1L;
    private String snum;
    private String IP;

    public Long getLogUid() {
        return logUid;
    }

   
    public void setLogUid(Long logUid) {
        this.logUid = logUid;
    }

   
    public String getUserId() {
        return userId;
    }

   
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    
    public String getIccid() {
        return iccid;
    }

   
    public void setIccid(String iccid) {
        this.iccid = iccid == null ? null : iccid.trim();
    }

   
    public String getImei() {
        return imei;
    }

    
    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

   
    public Date getInstallDate() {
        return installDate;
    }

    
    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

   
    public String getDownloadType() {
        return downloadType;
    }

    
    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType == null ? null : downloadType.trim();
    }

   
    public Double getInstallStatus() {
        return installStatus;
    }

   
    public void setInstallStatus(Double installStatus) {
        this.installStatus = installStatus;
    }

   
    public String getMsisdn() {
        return msisdn;
    }

    
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn == null ? null : msisdn.trim();
    }

   
    public String getAppId() {
        return appId;
    }

    
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

   
    public Integer getVersionId() {
        return versionId;
    }

   
    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(Long downloadId) {
        this.downloadId = downloadId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? null : modelName.trim();
    }

    public Double getFailCode() {
        return failCode;
    }

    public void setFailCode(Double failCode) {
        this.failCode = failCode;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid == null ? null : userUid.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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