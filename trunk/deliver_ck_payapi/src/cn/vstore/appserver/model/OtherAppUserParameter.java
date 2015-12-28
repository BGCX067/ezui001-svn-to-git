package cn.vstore.appserver.model;

import java.util.Date;

/**
 * 
 * @author raymond
 * @version 2012-07-18 10:28:51
 *
 */
public class OtherAppUserParameter {

	private Long id;
	private String packageName;
	private String aver;
	private String arel;
	private String cpu;
	private String brand;
	private String cver;
	private String lang;
	private String imei;
	private String mac;
	private String imsi;
	private String iccid;
	private String dvc;
	private String type;
	private Date create_time;
	private String snum;
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
	public String getSnum() {
		return snum;
	}
	public void setSnum(String snum) {
		this.snum = snum;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAver() {
		return aver;
	}
	public void setAver(String aver) {
		this.aver = aver;
	}
	public String getArel() {
		return arel;
	}
	public void setArel(String arel) {
		this.arel = arel;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCver() {
		return cver;
	}
	public void setCver(String cver) {
		this.cver = cver;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getIccid() {
		return iccid;
	}
	public void setIccid(String iccid) {
		this.iccid = iccid;
	}
	public String getDvc() {
		return dvc;
	}
	public void setDvc(String dvc) {
		this.dvc = dvc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
