package cn.vstore.appserver.model;

import java.util.Date;

public class Appv {
	private String 	priceType;
	private Date	testingBeginDate;
	private Date	testingEndDate;
	private double	price;
	private int onUse;
	private int versionId;
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public Date getTestingBeginDate() {
		return testingBeginDate;
	}
	public void setTestingBeginDate(Date testingBeginDate) {
		this.testingBeginDate = testingBeginDate;
	}
	public Date getTestingEndDate() {
		return testingEndDate;
	}
	public void setTestingEndDate(Date testingEndDate) {
		this.testingEndDate = testingEndDate;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getOnUse() {
		return onUse;
	}
	public void setOnUse(int onUse) {
		this.onUse = onUse;
	}
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
}
