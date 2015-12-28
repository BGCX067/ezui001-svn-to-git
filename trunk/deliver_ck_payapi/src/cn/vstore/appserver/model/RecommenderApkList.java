package cn.vstore.appserver.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class RecommenderApkList extends ApplictionDetail{
	private String priceText;
	private String recommendMemo;
	private String currency;
	private int lastVersion;
	
	public String getPriceText() {
		return priceText;
	}
	public void setPriceText(String priceText) {
		this.priceText = priceText;
	}
	public String getRecommendMemo() {
		return recommendMemo;
	}
	public void setRecommendMemo(String recommendMemo) {
		this.recommendMemo = recommendMemo;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getLastVersion() {
		return lastVersion;
	}
	public void setLastVersion(int lastVersion) {
		this.lastVersion = lastVersion;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}
}
