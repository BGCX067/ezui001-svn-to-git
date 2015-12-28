package cn.vstore.appserver.model;

import java.io.Serializable;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-7 上午12:46:08 
 * 类说明 首页焦点图对象
 */

public class HomeAdvertisement  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1802313305125982357L;
	private int id;
	private String picturePath;
	private int type;
	private String pkg;
	private String spaceID;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public String getSpaceID() {
		return spaceID;
	}
	public void setSpaceID(String spaceID) {
		this.spaceID = spaceID;
	}
	
	
}
