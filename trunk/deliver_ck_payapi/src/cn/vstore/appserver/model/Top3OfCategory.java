package cn.vstore.appserver.model;

import java.io.Serializable;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-12 下午7:48:03 
 * 类说明 
 */

public class Top3OfCategory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -686077308129569666L;
	
	private String  appTitle;
	private int totalDownloadTimes;
	private String icon;
	
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getAppTitle() {
		return appTitle;
	}
	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}
	public int getTotalDownloadTimes() {
		return totalDownloadTimes;
	}
	public void setTotalDownloadTimes(int totalDownloadTimes) {
		this.totalDownloadTimes = totalDownloadTimes;
	}
	
	

}
