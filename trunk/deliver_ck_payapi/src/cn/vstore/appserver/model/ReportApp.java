package cn.vstore.appserver.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 */
public class ReportApp implements Serializable {

    private static final long serialVersionUID = -3040947704295755137L;
    private int reportId;
    private String userId;
    private String appId;
    private String userUid;
    private int versionId;
    private String imei;
    private String mac;
    private String iccid;
    
    private BigDecimal storeId;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
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

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public BigDecimal getStoreId() {
		return storeId;
	}

	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}
}