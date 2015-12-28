package cn.vstore.appserver.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>Title: License的封裝類別</p>
 * <p>Description: <br>
 * <p>Copyright: 敦陽科技版權所有 Copyright (c) 2009 Stark Technology Inc. All Rights Reserved.</p>
 * <p>Company: 敦陽科技股份有限公司(Stark Technology Inc.)</p>
 * @author Howard
 * @version 1.0
 */
public class License {

	/** 授權方式, 免費 */
	private static final int TYPE_FREE = 0;
	/** 授權方式, 付費後就無限制 */
	private static final int TYPE_UNLIMITED = 1;
	/** 授權方式, 依日期區間 */
	private static final int TYPE_BY_DURATION = 2;
	/** 授權方式, 依使用次數 */
	private static final int TYPE_BY_TIMES = 3;

	private int priceType = 0; //  0 : free 1. one-time charge 2.monthly
	private int version = 0; // if no version in xml, version = 0 ==> auth again
	private int available =-1;// available = 0, usable, available 1: buying 
	private String orderId;


	// private boolean isValid;
	private String licenseId;
	private String appPackageId;
	private Date licenseCreateDate;
	private String userId;
	private boolean licensedByUser;
	private Date durationEnd;
	private Date durationStart;
	private String IMEI;
	private String IMSI;
	private int licenseType;
	private int licensedTimes;
	private String macAddress;
	private String storeClientID;
	private String store;

	private boolean licensedByIMEI;
	private boolean licensedByIMSI;
	private boolean licensedByMacAddress;
	
	private boolean isInnAppPay;
	
	public License() {
	}

	private boolean verfiyFreeType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userId,
			String IMEI, String IMSI) {
		return checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI);
	}

	private boolean verfiyUnlimitedType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userId,
			String IMEI, String IMSI) {
		return checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI);
	}

	private boolean verfiyDurationType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userId,
			String IMEI, String IMSI) {
		if (!checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI)) {
			return false;
		}

		Calendar today = Calendar.getInstance();

		Calendar durationStartDay = Calendar.getInstance();
		durationStartDay.setTime(this.getDurationStart());
		durationStartDay.set(Calendar.HOUR, 0);
		durationStartDay.set(Calendar.MINUTE, 0);
		durationStartDay.set(Calendar.MILLISECOND, 0);
		durationStartDay.set(Calendar.SECOND, 0);

		Calendar durationEndDay = Calendar.getInstance();
		durationEndDay.setTime(this.getDurationEnd());
		durationEndDay.set(Calendar.HOUR, 23);
		durationEndDay.set(Calendar.MINUTE, 59);
		durationEndDay.set(Calendar.MILLISECOND, 999);
		durationEndDay.set(Calendar.SECOND, 59);

		if (!(durationStartDay.before(today) && durationEndDay.after(today))) {
			return false;
		}
		return true;
	}

	private boolean VerfityTimesType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, int usedTimes,
			String userId, String IMEI, String IMSI) {
		if (!checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI)) {
			return false;
		}
		if (this.getLicensedTimes() <= usedTimes) {
			return false;
		}
		return true;
	}

	private boolean checkLicenseInfo(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userid,
			String imei, String imsi) {
		if (licensedByUser) {
			if (this.getUserId()==null||userid==null||!this.getUserId().trim().equals(userid.trim())) {
				return false;
			}
		}
		if (licensedByIMEI) {
			if (this.getIMEI()==null||imei==null||!this.getIMEI().trim().equals(imei.trim())) {
				return false;
			}
		}
		if (licensedByIMSI) {
			if (this.getIMSI()==null||imsi==null||!this.getIMSI().trim().equals(imsi.trim())) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 * @param packageId
	 * @param userId
	 * @param IMEI
	 * @param IMSI
	 * @param usedTimes
	 * @param versionCode
	 * @return
	 * -1 : not usable
	 * 0 : usable
	 * 1 : paying
	 */
	public int isValid(String packageId, String userId, String IMEI,
			String IMSI, int usedTimes, int versionCode) {

		boolean licesByUser = this.isLicensedByUser();
		boolean licesByIMEI = this.isLicensedByIMEI();
		boolean licesByIMSI = this.isLicensedByIMSI();
		
		
		if(this.getAppPackageId() == null){
			return -1;
		}else if(!this.getAppPackageId().equals(packageId)){
			return -1;
		}else if(this.getVersion() !=  versionCode){
			
			return -1;
		}else{
			
			switch(this.getPriceType()){
				case -1:
					return -1;
				case 0:
					return verifyFreeType(licesByUser, licesByIMEI, licesByIMSI,
							userId, IMEI, IMSI);
				case 1:
					return verifyOneTimeType(licesByUser, licesByIMEI, licesByIMSI,
							userId, IMEI, IMSI);
				case 2:
					return verifyMonthlyType(licesByUser, licesByIMEI, licesByIMSI,
							userId, IMEI, IMSI);
				default : return -1;
			
			}
		}
		
	}	
	
	/**
	 * verify the start part of the license
	 * @return
	 */
	private boolean verifyStartDate(){
		
		if(this.getDurationStart() == null){
				
			return true;
			
		}else{
			
			Calendar today = Calendar.getInstance();
			Calendar durationStartDay = Calendar.getInstance();
			durationStartDay.setTime(this.getDurationStart());
			durationStartDay.set(Calendar.HOUR, 0);
			durationStartDay.set(Calendar.MINUTE, 0);
			durationStartDay.set(Calendar.MILLISECOND, 0);
			durationStartDay.set(Calendar.SECOND, 0);
			if(durationStartDay.before(today)){
				;	
				return true;
			}else{
				
				return false;
			}
		}		
	}
	
	/**
	 * verify the start part of the license
	 * @return
	 */
	private boolean verifyEndDate(){
	
		if(this.getDurationEnd() == null){
			
			return true;
			
		}else{
				
			Calendar today = Calendar.getInstance();
			Calendar durationEndDay = Calendar.getInstance();
			
			
			durationEndDay.setTime(this.getDurationEnd());
			durationEndDay.set(Calendar.HOUR, 23);
			durationEndDay.set(Calendar.MINUTE, 59);
			durationEndDay.set(Calendar.MILLISECOND, 999);
			durationEndDay.set(Calendar.SECOND, 59);
			if(durationEndDay.after(today)){
				
				return true;
			}else{

				return false;
			}
		}		
	}	
	
	//0 : usable 1: paying -1 : not usable
	
	private int verifyFreeType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userId,
			String IMEI, String IMSI) {
		
		
		boolean verfiyDatePart =  verifyStartDate() && verifyEndDate();
		if(!verfiyDatePart){
			return -1;
		}else if( !checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI)){
			return -1;
			
		}else{
			return available;
		}
		
		
	}

	
	public int verifyOneTimeType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userId,
			String IMEI, String IMSI){
		boolean verfiyDatePart =  verifyStartDate() && verifyEndDate();
		if(!verfiyDatePart){
			return -1;
		}else if( !checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI)){
			return -1;
			
		}else{
			return available;
		}
	}
	
	public int verifyMonthlyType(boolean licensedByUser,
			boolean licensedByIMEI, boolean licensedByIMSI, String userId,
			String IMEI, String IMSI){
		
		boolean verfiyDatePart =  verifyStartDate() && verifyEndDate();
		if(!verfiyDatePart){
			return -1;
		}else if( !checkLicenseInfo(licensedByUser, licensedByIMEI, licensedByIMSI,
				userId, IMEI, IMSI)){
			return -1;
			
		}else{
			return available;
		}
	}	
//	
//	public boolean isValid(String packageId, String userId, String IMEI,
//			String IMSI, int usedTimes, int versionCode) {
//		// 1.user_id , imei
//		// 2. get user_id, imei
//		boolean licesByUser = this.isLicensedByUser();
//		boolean licesByIMEI = this.isLicensedByIMEI();
//		boolean licesByIMSI = this.isLicensedByIMSI();
//		
//		Log.w("test","version code:"+versionCode +":"+this.getVersion());
//		if(this.getVersion() !=  versionCode){
//			
//			return false;
//		}
//		
//		
//
//		if (this.getAppPackageId() != null
//				&& this.getAppPackageId().equals(packageId)) {
//			switch (this.getLicenseType()) {
//			case (TYPE_FREE):
//				return verfiyFreeType(licesByUser, licesByIMEI, licesByIMSI,
//						userId, IMEI, IMSI);
//			case (TYPE_UNLIMITED):
//				return verfiyUnlimitedType(licesByUser, licesByIMEI,
//						licesByIMSI, userId, IMEI, IMSI);
//			case (TYPE_BY_DURATION):
//				return verfiyDurationType(licesByUser, licesByIMEI,
//						licesByIMSI, userId, IMEI, IMSI);
//			case (TYPE_BY_TIMES):
//				return VerfityTimesType(licesByUser, licesByIMEI, licesByIMSI,
//						usedTimes, userId, IMEI, IMSI);
//			default:
//				return false;
//
//			}
//		} else {
//			return false;
//		}
//
//	}	
	

//	public boolean isValid(String packageId, String userId, String IMEI,
//			String IMSI, int usedTimes) {
//		// 1.user_id , imei
//		// 2. get user_id, imei
//		boolean licesByUser = this.isLicensedByUser();
//		boolean licesByIMEI = this.isLicensedByIMEI();
//		boolean licesByIMSI = this.isLicensedByIMSI();
//
//		if (this.getAppPackageId() != null
//				&& this.getAppPackageId().equals(packageId)) {
//			switch (this.getLicenseType()) {
//			case (TYPE_FREE):
//				return verfiyFreeType(licesByUser, licesByIMEI, licesByIMSI,
//						userId, IMEI, IMSI);
//			case (TYPE_UNLIMITED):
//				return verfiyUnlimitedType(licesByUser, licesByIMEI,
//						licesByIMSI, userId, IMEI, IMSI);
//			case (TYPE_BY_DURATION):
//				return verfiyDurationType(licesByUser, licesByIMEI,
//						licesByIMSI, userId, IMEI, IMSI);
//			case (TYPE_BY_TIMES):
//				return VerfityTimesType(licesByUser, licesByIMEI, licesByIMSI,
//						usedTimes, userId, IMEI, IMSI);
//			default:
//				return false;
//
//			}
//		} else {
//			return false;
//		}
//		/*
//		 * isValid=false; if(this.getAppPackageId()!=null &&
//		 * this.getAppPackageId().equals(packageId)){ if (this.getLicenseType()
//		 * == TYPE_FREE || this.getLicenseType() == TYPE_UNLIMITED){ isValid =
//		 * true; } Date today = new Date(); if
//		 * (this.getDurationStart().getTime() <= today.getTime() &&
//		 * this.getDurationEnd().getTime() >= today.getTime()){ isValid = true;
//		 * } if (this.getLicensedTimes() >= this.getAvailableTimes()){ isValid =
//		 * true; } if (this.isLicensedByIMEI()){ isValid = true; } if
//		 * (this.isLicensedByIMSI()){ isValid = true; } if
//		 * (this.isLicensedByMacAddress()){ isValid = true; } if
//		 * (this.isLicensedByUser()){ isValid = true; }
//		 * 
//		 * } return isValid;
//		 */
//	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}

	public String getAppPackageId() {
		return appPackageId;
	}

	public void setAppPackageId(String appPackageId) {
		this.appPackageId = appPackageId;
	}

	public Date getLicenseCreateDate() {
		return licenseCreateDate;
	}

	public void setLicenseCreateDate(Date licenseCreateDate) {
		this.licenseCreateDate = licenseCreateDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isLicensedByUser() {
		return licensedByUser;
	}

	public void setLicensedByUser(boolean licensedByUser) {
		this.licensedByUser = licensedByUser;
	}

	public Date getDurationEnd() {
		return durationEnd;
	}

	public void setDurationEnd(Date durationEnd) {
		this.durationEnd = durationEnd;
	}

	public Date getDurationStart() {
		return durationStart;
	}

	public void setDurationStart(Date durationStart) {
		this.durationStart = durationStart;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getIMSI() {
		return IMSI;
	}

	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}

	public int getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(int licenseType) {
		this.licenseType = licenseType;
	}

	public int getLicensedTimes() {
		return licensedTimes;
	}

	public void setLicensedTimes(int licensedTimes) {
		this.licensedTimes = licensedTimes;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getStoreClientID() {
		return storeClientID;
	}

	public void setStoreClientID(String storeClientID) {
		this.storeClientID = storeClientID;
	}

	public boolean isLicensedByIMEI() {
		return licensedByIMEI;
	}

	public void setLicensedByIMEI(boolean licensedByIMEI) {
		this.licensedByIMEI = licensedByIMEI;
	}

	public boolean isLicensedByIMSI() {
		return licensedByIMSI;
	}

	public void setLicensedByIMSI(boolean licensedByIMSI) {
		this.licensedByIMSI = licensedByIMSI;
	}

	public boolean isLicensedByMacAddress() {
		return licensedByMacAddress;
	}

	public void setLicensedByMacAddress(boolean licensedByMacAddress) {
		this.licensedByMacAddress = licensedByMacAddress;
	}
	
	public String toXml(){
		StringBuffer sb=new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><license xmlns=\"http://www.51vapp.com/android/license\">");
		sb.append("<appPackageId>"+this.getAppPackageId()+"</appPackageId>");
		sb.append("<licenseType>"+this.getLicenseType()+"</licenseType>");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		if(this.getDurationStart()!=null){
			try{
				sb.append("<durationStart>"+sdf.format(this.getDurationStart())+"</durationStart>");
				if(this.getDurationEnd()!=null){
					sb.append("<durationEnd>"+sdf.format(this.getDurationEnd())+"</durationEnd>");
				}
			}catch(Exception e){
				return null;
			}
		}else{
			return null;
		}		
		switch(this.getLicenseType()){
		case 0:
		case 1:
			break;
		case 2:
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//			if(this.getDurationEnd()!=null&&this.getDurationStart()!=null){
//				try{
//					sb.append("<durationStart>"+sdf.format(this.getDurationStart())+"</durationStart>");
//					sb.append("<durationEnd>"+sdf.format(this.getDurationEnd())+"</durationEnd>");
//				}catch(Exception e){
//					return null;
//				}
//			}else{
//				return null;
//			}
			break;
		case 3:
			if(this.getLicensedTimes()<1) return null;
			sb.append("<licensedTimes>"+this.getLicensedTimes()+"</licensedTimes>");
			break;
		default:
			return null;
		}
		if(this.getUserId()!=null){
//			sb.append("<licensedByUser>true</licensedByUser>");
			sb.append("<uid>"+this.getUserId()+"</uid>");
		}
//		if(this.isLicensedByIMEI()&&this.getIMEI()!=null){
//			sb.append("<licensedByIMEI>true</licensedByIMEI>");
//			sb.append("<IMEI>"+this.getIMEI()+"</IMEI>");
//		}
		if(this.isLicensedByIMSI()&&this.getIMSI()!=null){
			sb.append("<licensedByIMSI>true</licensedByIMSI>");
			sb.append("<IMSI>"+this.getIMSI()+"</IMSI>");
		}
		if(this.getOrderId()!=null){
			sb.append("<orderId>"+this.getOrderId()+"</orderId>");
		}
		//InnAppCharage已購買
		if(isInnAppPay){
			sb.append("<appSelfPurchase>");
			sb.append("<identify>1</identify>");
			sb.append("<verifyByIMEI>1</verifyByIMEI>");
			sb.append("<verifyByICCID>0</verifyByICCID>");
			sb.append("<verifyByUserID>0</verifyByUserID>");
			sb.append("<verifyByVersion>1</verifyByVersion>");
			sb.append("</appSelfPurchase>");
		}
		sb.append("<version>"+this.getVersion()+"</version>");
		sb.append("<available>"+this.getAvailable()+"</available>");
		sb.append("<priceType>"+this.getPriceType()+"</priceType>");
		sb.append("<store>"+this.getStore()+"</store>");
		sb.append("<imei>"+this.getIMEI()+"</imei>");
		sb.append("</license>");
		return sb.toString();
	}

	@Override
	public String toString() {
		return "License [IMEI=" + IMEI + ", IMSI=" + IMSI + ", appPackageId="
				+ appPackageId + ", durationEnd=" + durationEnd
				+ ", durationStart=" + durationStart + ", licenseCreateDate="
				+ licenseCreateDate + ", licenseId=" + licenseId
				+ ", licenseType=" + licenseType + ", licensedByIMEI="
				+ licensedByIMEI + ", licensedByIMSI=" + licensedByIMSI
				+ ", licensedByMacAddress=" + licensedByMacAddress
				+ ", licensedByUser=" + licensedByUser + ", licensedTimes="
				+ licensedTimes + ", macAddress=" + macAddress
				+ ", storeClientID=" + storeClientID + ", userId=" + userId
				+ ", store=" + store
				+ "]";
	}

	public void setPriceType(int priceType) {
		this.priceType = priceType;
	}

	public int getPriceType() {
		return priceType;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}
	
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public boolean isInnAppPay() {
		return isInnAppPay;
	}

	public void setInnAppPay(boolean isInnAppPay) {
		this.isInnAppPay = isInnAppPay;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}
}