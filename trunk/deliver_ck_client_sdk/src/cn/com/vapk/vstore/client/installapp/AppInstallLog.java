package cn.com.vapk.vstore.client.installapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.sti.store.api.android.util.Logger;

import tw.com.sti.security.util.Base64Coder;

import tw.com.sti.store.api.vo.LogMessage;

public class AppInstallLog implements LogMessage {
	private static final Logger L=Logger.getLogger(AppInstallLog.class);
	private String downType;
	private String iccid;
	private int installRet;
	private Date installTime;
	private String phoneNo;
	private String pkgId;
	private String userId;
	private String token;
	private int version;
	private String downloadId;
	private int failCode;
	private int reasonId;
	private String versionCheck;
	private String versionCheckDB;

	public AppInstallLog(String downType, String iccid, int eventType,
			Date installTime, String phoneNo, String pkgId, String userId,
			String token, int version, String downloadId, int failCode,
			int reasonId, String versionCheck) {
		super();
		this.downType = downType;
		this.iccid = iccid;
		this.installRet = eventType;
		this.installTime = installTime;
		this.phoneNo = phoneNo;
		this.pkgId = pkgId;
		this.userId = userId;
		this.token = token;
		this.version = version;
		this.versionCheckDB = Base64Coder.encodeString("" + version);
		this.downloadId = downloadId;
		this.failCode = failCode;
		this.reasonId = reasonId;
		this.versionCheck = versionCheck;
	}

	public int getReasonId() {
		return reasonId;
	}

	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	/**
	 * 取得
	 * 
	 * @return the downType
	 */
	public String getDownType() {
		return downType;
	}

	/**
	 * 取得
	 * 
	 * @return the iccid
	 */
	public String getIccid() {
		return iccid;
	}

	/**
	 * 取得
	 * 
	 * @return the installRet
	 */
	public int getInstallRet() {
		return installRet;
	}

	/**
	 * 取得
	 * 
	 * @return the installTime
	 */
	public Date getInstallTime() {
		return installTime;
	}

	/**
	 * 取得
	 * 
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * 取得
	 * 
	 * @return the pkgId
	 */
	public String getPkgId() {
		return pkgId;
	}

	/**
	 * 取得
	 * 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 設定
	 * 
	 * @param downType
	 */
	public void setDownType(String downType) {
		this.downType = downType;
	}

	/**
	 * 設定
	 * 
	 * @param iccid
	 */
	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	/**
	 * 設定
	 * 
	 * @param installRet
	 */
	public void setInstallRet(int installRet) {
		this.installRet = installRet;
	}

	/**
	 * 設定
	 * 
	 * @param installTime
	 */
	public void setInstallTime(Date installTime) {
		this.installTime = installTime;
	}

	/**
	 * 設定
	 * 
	 * @param phoneNo
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	/**
	 * 設定
	 * 
	 * @param pkgId
	 */
	public void setPkgId(String pkgId) {
		this.pkgId = pkgId;
	}

	/**
	 * 設定
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getFailCode() {
		return failCode;
	}

	public void setFailCode(int failCode) {
		this.failCode = failCode;
	}

	public String toXML() {
		StringBuilder ret = new StringBuilder("<installApp>");
		if (token != null)
			ret.append("<Token>").append(token).append("</Token>");
		if (iccid != null)
			ret.append("<iccid>").append(iccid).append("</iccid>");
		if (pkgId != null)
			ret.append("<pkgId>").append(pkgId).append("</pkgId>");
		ret.append("<version>").append(version).append("</version>");
		if (userId != null)
			ret.append("<userId>").append(userId).append("</userId>");
		if (installTime != null)
			ret.append("<actionTime>")
					.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(installTime)).append("</actionTime>");
		if (downType != null)
			ret.append("<downType>").append(downType).append("</downType>");
		ret.append("<installRet>").append(installRet).append("</installRet>");
		if (phoneNo != null)
			ret.append("<phoneNo>").append(phoneNo).append("</phoneNo>");
		if (downloadId != null)
			ret.append("<downloadId>").append(downloadId)
					.append("</downloadId>");
		ret.append("<failCode>").append(failCode).append("</failCode>");
		ret.append("<reasonId>").append(reasonId).append("</reasonId>");
		if (versionCheck != null)
			ret.append("<versionCheck>").append(versionCheck)
					.append("</versionCheck>");
		if (versionCheckDB != null)
			ret.append("<versionCheckDB>").append(versionCheckDB)
					.append("</versionCheckDB>");

		ret.append("</installApp>");
		if (Logger.DEBUG) {
			L.d(ret.toString().substring(0, 256));
			L.d(ret.toString().substring(256));
		}
		return ret.toString();
	}
}
