package net.smart.appstore.server.api.support;

import java.math.BigDecimal;
import java.util.Date;

public class AppInstallLog {
	private String token;
	private String iccid;
	private String pkgId;
	private int version;
	private String userId;
	private Date actionTime;
	private String downType;
	private int installRet;
	private String phoneno;
	private int failCode;
	private BigDecimal downloadId;
	private int reasonId;
	public BigDecimal getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(BigDecimal downloadId) {
		this.downloadId = downloadId;
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
	public String getPkgId() {
		return pkgId;
	}
	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getActionTime() {
		return actionTime;
	}
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
	public String getDownType() {
		return downType;
	}
	public void setDownType(String downType) {
		this.downType = downType;
	}
	public int getInstallRet() {
		return installRet;
	}
	public void setInstallRet(int installRet) {
		this.installRet = installRet;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public int getFailCode() {
		return failCode;
	}
	public void setFailCode(int failCode) {
		this.failCode = failCode;
	}
	public int getReasonId() {
		return reasonId;
	}
	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}
}
