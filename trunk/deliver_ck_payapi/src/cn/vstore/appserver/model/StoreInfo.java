package cn.vstore.appserver.model;

import java.math.BigDecimal;

public class StoreInfo {
	private BigDecimal id;
	private String storePkgName;
	private String storeName;
	private String pubKeyBase64;
	private String pwdResetUrl;
	private String clientDownloadUrl;
	private int isApproved;
	private int isLogin;
	private int isDownload;
	
	public String getPwdResetUrl() {
		return pwdResetUrl;
	}
	public void setPwdResetUrl(String pwdResetUrl) {
		this.pwdResetUrl = pwdResetUrl;
	}
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getStorePkgName() {
		return storePkgName;
	}
	public void setStorePkgName(String storePkgName) {
		this.storePkgName = storePkgName;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getPubKeyBase64() {
		return pubKeyBase64;
	}
	public void setPubKeyBase64(String pubKeyBase64) {
		this.pubKeyBase64 = pubKeyBase64;
	}
	public String getClientDownloadUrl() {
		return clientDownloadUrl;
	}
	public void setClientDownloadUrl(String clientDownloadUrl) {
		this.clientDownloadUrl = clientDownloadUrl;
	}
	public int getIsApproved() {
		return isApproved;
	}
	public void setIsApproved(int isApproved) {
		this.isApproved = isApproved;
	}
	public int getIsLogin() {
		return isLogin;
	}
	public void setIsLogin(int isLogin) {
		this.isLogin = isLogin;
	}
	public int getIsDownload() {
		return isDownload;
	}
	public void setIsDownload(int isDownload) {
		this.isDownload = isDownload;
	}
	
}
