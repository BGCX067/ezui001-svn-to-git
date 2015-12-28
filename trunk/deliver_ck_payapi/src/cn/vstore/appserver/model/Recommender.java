package cn.vstore.appserver.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Recommender {
	
	private BigDecimal id;
	private String recommenderTitle; //username
	private String recommenderIcon; //iconUrl
	private String recommenderDESC; //desc
	private Date lastUpdateDate; //lastUpdateDate
	private String webPic1;
	private String webPic2;
	private Date createTime;
	private Date upDate;
	private String webDESC;
	
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getRecommenderTitle() {
		return recommenderTitle;
	}
	public void setRecommenderTitle(String recommenderTitle) {
		this.recommenderTitle = recommenderTitle;
	}
	public String getRecommenderIcon() {
		return recommenderIcon;
	}
	public void setRecommenderIcon(String recommenderIcon) {
		this.recommenderIcon = recommenderIcon;
	}
	public String getRecommenderDESC() {
		return recommenderDESC;
	}
	public void setRecommenderDESC(String recommenderDESC) {
		this.recommenderDESC = recommenderDESC;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}
	public String getWebPic1() {
		return webPic1;
	}
	public void setWebPic1(String webPic1) {
		this.webPic1 = webPic1;
	}
	public String getWebPic2() {
		return webPic2;
	}
	public void setWebPic2(String webPic2) {
		this.webPic2 = webPic2;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpDate() {
		return upDate;
	}
	public void setUpDate(Date upDate) {
		this.upDate = upDate;
	}
	public String getWebDESC() {
		return webDESC;
	}
	public void setWebDESC(String webDESC) {
		this.webDESC = webDESC;
	}
}
