package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-28 下午9:25:42 
 * 类说明 反馈 
 */

public class Feedback implements Serializable{
	
	private String feedbackText;
	private String imie;
	private Date createDate;
	private int StoreId;
	public String getFeedbackText() {
		return feedbackText;
	}
	public void setFeedbackText(String feedbackText) {
		this.feedbackText = feedbackText;
	}
	public String getImie() {
		return imie;
	}
	public void setImie(String imie) {
		this.imie = imie;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getStoreId() {
		return StoreId;
	}
	public void setStoreId(int storeId) {
		StoreId = storeId;
	}
	
	

}
