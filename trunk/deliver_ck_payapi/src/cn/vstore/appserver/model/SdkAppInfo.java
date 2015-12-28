package cn.vstore.appserver.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SdkAppInfo extends Application{
	
	private String id;
	private String priceText;
	private int ratingTimes;
	private String currency;
	private int file_size;
	private String myPriceType;
	private Double myPrice;
	private String devmodel;
	private String userUid;
	private String userId;
	private String iccid;
	private String imei;
	private int isFirstTime;
	private int newVersion;
	private Date log_time;
	private BigDecimal storeId;
	private Prosumer prosumer;
	private int onUse;
	
	public String getPriceText() {
		return priceText;
	}
	public void setPriceText(String priceText) {
		this.priceText = priceText;
	}
	public int getRatingTimes() {
		return ratingTimes;
	}
	public void setRatingTimes(int ratingTimes) {
		this.ratingTimes = ratingTimes;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getFile_size() {
		return file_size;
	}
	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}
	public String getMyPriceType() {
		return myPriceType;
	}
	public void setMyPriceType(String myPriceType) {
		this.myPriceType = myPriceType;
	}
	public Double getMyPrice() {
		return myPrice;
	}
	public void setMyPrice(Double myPrice) {
		this.myPrice = myPrice;
	}
	public String getDevmodel() {
		return devmodel;
	}
	public void setDevmodel(String devmodel) {
		this.devmodel = devmodel;
	}
	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public int getIsFirstTime() {
		return isFirstTime;
	}
	public void setIsFirstTime(int isFirstTime) {
		this.isFirstTime = isFirstTime;
	}
	public int getNewVersion() {
		return newVersion;
	}
	public void setNewVersion(int newVersion) {
		this.newVersion = newVersion;
	}
	public Date getLog_time() {
		return log_time;
	}
	public void setLog_time(Date log_time) {
		this.log_time = log_time;
	}
	public BigDecimal getStoreId() {
		return storeId;
	}
	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Prosumer getProsumer() {
		return prosumer;
	}
	public void setProsumer(Prosumer prosumer) {
		this.prosumer = prosumer;
	}
	public int getOnUse() {
		return onUse;
	}
	public void setOnUse(int onUse) {
		this.onUse = onUse;
	}
	
}
