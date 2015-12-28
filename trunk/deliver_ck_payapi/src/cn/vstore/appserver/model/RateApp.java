package cn.vstore.appserver.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 */
public class RateApp implements Serializable {

    private static final long serialVersionUID = -3486947071670048843L;
    private int userRank;
    private String appComment;
    private int versionId;
    private String userId;
    private String userUid;
    private String appId;
    private String imei;
    private String mac;
    private String iccid;
    private String lastAppComment;
    private String snum;
    private String IP;
    private int status;

    private BigDecimal storeId;
    
    public int getUserRank() {
        return userRank;
    }

    public void setUserRank(int userRank) {
        this.userRank = userRank;
    }

    public String getAppComment() {
        return appComment;
    }

    public void setAppComment(String appComment) {
        this.appComment = appComment;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getLastAppComment() {
        return lastAppComment;
    }

    public void setLastAppComment(String lastAppComment) {
        this.lastAppComment = lastAppComment;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
