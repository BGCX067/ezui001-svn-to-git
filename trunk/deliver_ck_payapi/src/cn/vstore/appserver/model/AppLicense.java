package cn.vstore.appserver.model;

import java.util.Date;

//import java.sql.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AppLicense {
	private int licenseType;
	private int licensedTimes;
	private Date expDate;
	private Date effDate;
	private Date createDate;
	private String licensedByImei;
	private String licensedByImsi;
	private String licensedByUser;
//	private double licenseType;
//	private double licensedTimes;
	
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	public Date getEffDate() {
		return effDate;
	}
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getLicensedByImei() {
		return licensedByImei;
	}
	public void setLicensedByImei(String licensedByImei) {
		this.licensedByImei = licensedByImei;
	}
	public String getLicensedByImsi() {
		return licensedByImsi;
	}
	public void setLicensedByImsi(String licensedByImsi) {
		this.licensedByImsi = licensedByImsi;
	}
	public String getLicensedByUser() {
		return licensedByUser;
	}
	public void setLicensedByUser(String licensedByUser) {
		this.licensedByUser = licensedByUser;
	}
	public int getLicensedTimes() {
		return licensedTimes;
	}
	public void setLicensedTimes(int licensedTimes) {
		this.licensedTimes = licensedTimes;
	}
	public int getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(int licenseType) {
		this.licenseType = licenseType;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}
//	public double getLicenseType() {
//		return licenseType;
//	}
//	public void setLicenseType(double licenseType) {
//		this.licenseType = licenseType;
//	}
//	public double getLicensedTimes() {
//		return licensedTimes;
//	}
//	public void setLicensedTimes(double licensedTimes) {
//		this.licensedTimes = licensedTimes;
//	}
	
}
