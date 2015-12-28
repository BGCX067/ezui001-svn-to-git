package cn.vstore.appserver.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id: UserInstallApp.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class UserInstallApp {

    private long downloadId;
    private String appId;
    private int appVersion;
    private String token;
    private String iccid;
    private String imei;
    private String mac;
    private String categoryId;
    private String dvc;
    private String lappv;
    private String userId;
    private String userUid;
    private String snum;
    private String IP;
    
    private BigDecimal storeId;

    public UserInstallApp() {
    }
    
    public UserInstallApp(long downloadId, String appId, int appVersion, String token,
            String iccid, String imei, String mac, String categoryId, String dvc, String lappv, String userId,
            String userUid, BigDecimal storeId, String snum, String IP) {
        this.downloadId = downloadId;
        this.appId = appId;
        this.appVersion = appVersion;
        this.token = token;
        this.iccid = iccid;
        this.imei = imei;
        this.mac = mac;
        this.categoryId = categoryId;
        this.dvc = dvc;
        this.lappv = lappv;
        this.userId = userId;
        this.userUid = userUid;
        this.storeId = storeId;
        this.snum = snum;
        this.IP = IP;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDvc() {
        return dvc;
    }

    public void setDvc(String dvc) {
        this.dvc = dvc;
    }

    public String getLappv() {
        return lappv;
    }

    public void setLappv(String lappv) {
        this.lappv = lappv;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
    
    public BigDecimal getStoreId() {
    	return storeId;
    }
    
    public void setStoreId(BigDecimal storeId) {
    	this.storeId = storeId;
    }

    public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
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
