package cn.vstore.appserver.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class News {
	private BigDecimal newsUid;
	private Date realEffDate;
	private Date realExpDate;
	private String createBy;
	private Date lastUpdateDate;
	private String lastUpdateBy;
	private String createName;
	private String lastUpdateName;
	private BigDecimal storeId;
	private String title;
	private String url;
	private String content;
	private Date createDate;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}
	public BigDecimal getNewsUid() {
		return newsUid;
	}
	public void setNewsUid(BigDecimal newsUid) {
		this.newsUid = newsUid;
	}
	public Date getRealEffDate() {
		return realEffDate;
	}
	public void setRealEffDate(Date realEffDate) {
		this.realEffDate = realEffDate;
	}
	public Date getRealExpDate() {
		return realExpDate;
	}
	public void setRealExpDate(Date realExpDate) {
		this.realExpDate = realExpDate;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getLastUpdateBy() {
		return lastUpdateBy;
	}
	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getLastUpdateName() {
		return lastUpdateName;
	}
	public void setLastUpdateName(String lastUpdateName) {
		this.lastUpdateName = lastUpdateName;
	}
	public BigDecimal getStoreId() {
		return storeId;
	}
	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}
	
	
}
