package cn.vstore.appserver.form;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BaseForm {

    private String aver; // Android SDK 版本
    private String cver; // Client版本
    private String lang; // 語系
    private String time; // 手機端時間 millisecond
    private String imei;
    private String imsi;
    private String iccid;
    private String mac;
    private String dvc; // 手機型號
    private String wpx; // 手機螢幕寬度(pixel)
    private String hpx; // 手機螢幕高度(pixel)
    private String appfilter; // 0表示不Filter, 1表示要filter Feature Apps, Category Apps, CP’s Apps, Search Apps
    private String vsign;
    private String store;
    private BigDecimal storeId;
    private String psize; // 每頁筆數 default=10
    private String snum; // 渠道号码
    
    public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getVsign() {
		return vsign;
	}

	public void setVsign(String vsign) {
		this.vsign = vsign;
	}

    public String getAver() {
        return aver;
    }

    public void setAver(String aver) {
        this.aver = aver;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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

    public BigDecimal getStoreId() {
		return storeId;
	}

	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public String getPsize() {
		return psize;
	}

	public void setPsize(String psize) {
		this.psize = psize;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSnum() {
		return snum;
	}

	public void setSnum(String snum) {
		this.snum = snum;
	}
}
