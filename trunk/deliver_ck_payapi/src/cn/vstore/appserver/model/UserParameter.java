package cn.vstore.appserver.model;

import java.util.Date;

/**
 * 
 * @author grady
 * @version 2012-06-26 10:28:51
 *
 */
public class UserParameter {
	private Long id;
	private String store;
	private String aver;
	private String arel;
	private String model;
	private String cpu;
	private String brand;
	private String cver;
	private String lang;
	private String tzid;
	private String tzrawoffset;
	private String imei;
	private String mac;
	private String imsi;
	private String iccid;
	private String dvc;
	private String wpx;
	private String hpx;
	private String appfilter;
	private Long keyId;
	private String type;
	private Date askTime;
	private String snum;
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
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
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
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
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
	public String getTzid() {
		return tzid;
	}
	public void setTzid(String tzid) {
		this.tzid = tzid;
	}
	public String getTzrawoffset() {
		return tzrawoffset;
	}
	public void setTzrawoffset(String tzrawoffset) {
		this.tzrawoffset = tzrawoffset;
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
	public String getWpx() {
		return wpx;
	}
	public void setWpx(String wpx) {
		this.wpx = wpx;
	}
	public String getHpx() {
		return hpx;
	}
	public void setHpx(String hpx) {
		this.hpx = hpx;
	}
	public String getAppfilter() {
		return appfilter;
	}
	public void setAppfilter(String appfilter) {
		this.appfilter = appfilter;
	}
	public Long getKeyId() {
		return keyId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getAskTime() {
		return askTime;
	}
	public void setAskTime(Date askTime) {
		this.askTime = askTime;
	}
}
