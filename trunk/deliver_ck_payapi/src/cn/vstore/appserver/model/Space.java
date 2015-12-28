package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-18 上午9:55:08 
 * 类说明 专区对象
 */

public class Space  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1802313305125982357L;
	private int id;
	private String title;
	private int status;
	private int rank;
	private Date createTime;
	private String url;
	private String intro;
	private int isusable;
	private String micon;
	private String wicon;
	private int appCount;
	private String wbicon;
	private String longTitle;
	private String remarks;
	private int type;
	private String top1;
	private String top2;
	private String top3;
	
	
	
	public String getTop1() {
		return top1;
	}
	public void setTop1(String top1) {
		this.top1 = top1;
	}
	public String getTop2() {
		return top2;
	}
	public void setTop2(String top2) {
		this.top2 = top2;
	}
	public String getTop3() {
		return top3;
	}
	public void setTop3(String top3) {
		this.top3 = top3;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public int getIsusable() {
		return isusable;
	}
	public void setIsusable(int isusable) {
		this.isusable = isusable;
	}
	public String getMicon() {
		return micon;
	}
	public void setMicon(String micon) {
		this.micon = micon;
	}
	public String getWicon() {
		return wicon;
	}
	public void setWicon(String wicon) {
		this.wicon = wicon;
	}
	public int getAppCount() {
		return appCount;
	}
	public void setAppCount(int appCount) {
		this.appCount = appCount;
	}
	public String getWbicon() {
		return wbicon;
	}
	public void setWbicon(String wbicon) {
		this.wbicon = wbicon;
	}
	public String getLongTitle() {
		return longTitle;
	}
	public void setLongTitle(String longTitle) {
		this.longTitle = longTitle;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	
}
