package cn.vstore.appserver.model;

import java.io.Serializable;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-30 下午12:22:22 
 * 类说明 可更新应用名称
 */

public class UpAppInfo  implements Serializable{
	
	private String pkg;
	private int versionId;
	private String versionName;
	private String iconPath;
	private String title;
	
	
	
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	
	
}
