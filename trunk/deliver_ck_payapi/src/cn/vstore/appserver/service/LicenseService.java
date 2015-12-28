package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.smart.appstore.server.api.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

//import cn.vstore.appserver.model.AppLicense;
import cn.vstore.appserver.model.Appv;
import cn.vstore.appserver.model.LicenseInfo;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.Prosumer;
//import cn.vstore.appserver.model.UserSubscribe;
import cn.vstore.appserver.service.LicenseFactory.SignedLicense;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.GetLicenseCode;
import cn.vstore.appserver.util.NextMonthEndDay;
//import cn.vstore.appserver.util.StringUtils;

/**
 * 32.	获取license(34) 
 * 
 * @version $Id:$
 */
@Service("LicenseService")
public class LicenseService {
	private final static Logger logger = LoggerFactory.getLogger(LicenseService.class);
	private final static int ON_USE=1;
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Autowired
	private AuthenticationService authenticationService;

	/**
	 * 1.獲取購買的License
	 * @param iccid
	 * @param imei
	 * @param storeId
	 * @return
	 */
	public ServiceResult<LicenseInfo> getLicense(String iccid, String imei, BigDecimal storeId, String token, int version,
			String pkg)throws Throwable{
		return getLicense(iccid, imei, storeId, token, version, pkg, false);
	}
	
	public ServiceResult<LicenseInfo> getLicense(String iccid, String imei, BigDecimal storeId, String token, int version,
			String pkg, boolean isInnAppPayMethod)throws Throwable{
		logger.info("Service parameter iccid=" + iccid + ", imei=" + imei + ", storeId=" + storeId + ", token=" + token + ", version=" + version + ", pkg=" + pkg + ", isInnAppPayMethod=" + isInnAppPayMethod);
		if(iccid.trim().length()==0) iccid="0";
    	if(imei.trim().length()==0) imei="0";
    	
//    	License returnValueLicense = new License();
    	
//    	取得 Store
    	Map<String, Object> storeMap = new HashMap<String, Object>();
    	storeMap.put("storeId", storeId);
    	String store = (String)sqlMapClientTemplate.queryForObject("StoreInfo.getStorePkgName", storeMap);
    	
//    	權限驗證
    	String userId = null;
    	String userUid = null;
    	int blankType = 0;
		Prosumer users = authenticationService.getProsumerByAccount(token);
		logger.info("userUid = " + users.getUserUid());
		userId = users.getUserId();
		userId = (userId != null) ? userId.trim().toUpperCase() : null;
		blankType = users.getBlankType();
		userUid = users.getUserUid();
    	logger.info("userId: "+userId + " userUid:" + userUid + " blankType:" + blankType);

    	List<SignedLicense> sls = new ArrayList<SignedLicense>();
    	
//    	給cp測試用
//    	version == integer最小值-65536，直接回傳固定值的License
    	if(version == Integer.MIN_VALUE){
			return getTestLicense( pkg, version, userUid, userId, iccid, imei, isInnAppPayMethod, storeId, store);
    	}
    	
//    	update PROSUMER_APP_LICENSE的憑證為過期, LICENSE_EXPIRE: 憑證是否逾期，Y：過期/N：未過期, SOURCE_FROM: 1=from template 11=from charge
    	Map<String, Object> updateMap = new HashMap<String, Object>();
    	updateMap.put("userUid", (userUid != null) ? userUid.trim() : null);
    	updateMap.put("pkg", (pkg != null) ? pkg.trim() : null);
    	updateMap.put("storeId", storeId);
    	updateMap.put("versionId", version);
    	sqlMapClientTemplate.update("License.updateProsumerAppLicense", updateMap);
    	
//    	是否InnApp已付費
    	boolean isInnAppPay = false;
    	//是否購買 0=不明，1=購買，2=未購買
    	int orderStatus=2;
    	int priceTypeV=0;
    	
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pkg", (pkg != null) ? pkg.trim() : null);
		map.put("versionId", version);
		map.put("storeId", storeId);
		logger.info("pkg : " + map.get("pkg") + " versionId : " + map.get("versionId") + " storeId : " + map.get("storeId"));

		//找最新的上架過此store的app,用最新的app priceType判斷是否該收費
		Appv appv = (Appv)sqlMapClientTemplate.queryForObject("License.getAppInfo", map);
		if(appv!=null){
			String priceType = appv.getPriceType();
			Date testStartDate = appv.getTestingBeginDate();
			Date testEndDate = appv.getTestingEndDate();
			BigDecimal orderId = null;
			Date startDate = null;
			boolean isTestLicense = false;
			if(false && blankType>0 && testStartDate!=null && testStartDate.before(new Date())){
//        		白名單，測試app
				priceTypeV = 0;
				orderStatus = 1;
				isTestLicense = true;
			}else{
				if(priceType!=null && priceType.trim().equals(String.valueOf(Constants.PRICE_TYPE_MONTH))){
					priceTypeV = 2;
//        			檢查是否月租中
		            Map<String, Object> order_map = new HashMap<String, Object>();
		            order_map.put("appId", pkg);
		            order_map.put("userUid", users.getUserUid());
		            order_map.put("storeId", storeId);
		            //取得是否購買
		            PaymentInformation order = (PaymentInformation) sqlMapClientTemplate.queryForObject("Application.getUserPaymentIsOrdered", order_map);
		            //the order_id is exists  
		        	if(order!=null){
		        		startDate=order.getRightStartDate();
		        		orderStatus =1;
		        		orderId=new BigDecimal(order.getId());
		        		logger.debug("找到此user購買的訂單"+orderId+" startDate="+startDate+" priceTypeV="+priceTypeV+" priceType="+priceType);
		        	}else{
		        		logger.debug("找到此user購買的訂單 priceType="+priceType);
		        	}
				}else if(priceType!=null&&priceType.trim().equals(String.valueOf(Constants.PRICE_TYPE_ONCE))){
					priceTypeV=1;
					boolean isTestBlankType = false;
//        			檢查是否購買單次
		            Map<String, Object> order_map = new HashMap<String, Object>();
		            order_map.put("appId", pkg);
		            order_map.put("userUid", users.getUserUid());
		            order_map.put("storeId", storeId);
		            //取得是否購買
		            PaymentInformation order = (PaymentInformation) sqlMapClientTemplate.queryForObject("Application.getUserPaymentIsOrdered", order_map);
		            //the order_id is exists  
		        	if(order!=null){
		        		startDate=order.getRightStartDate();
		        		orderStatus =1;
		        		orderId=new BigDecimal(order.getId());
		        		logger.debug("找到此user購買的訂單"+orderId+" startDate="+startDate+" priceTypeV="+priceTypeV+" priceType="+priceType);
		        	}else{
		        		logger.debug("找到此user購買的訂單 priceType="+priceType);
		        	}
				}else if(priceType!=null && priceType.trim().equals(String.valueOf(Constants.PRICE_INNAPP_PURCHASE))){
//        			檢查是否購買
//        			取得是否為測試用的假付款流程 (BlankType >= 30, true:測試使用, false:實際使用)
					isInnAppPay = true;
					priceTypeV = 1;
		            Map<String, Object> order_map = new HashMap<String, Object>();
		            order_map.put("appId", pkg);
		            order_map.put("userUid", users.getUserUid());
		            order_map.put("storeId", storeId);
		            //取得是否購買
		            PaymentInformation order = (PaymentInformation) sqlMapClientTemplate.queryForObject("Application.getUserPaymentIsOrdered", order_map);
		            //the order_id is exists  
		        	if(order!=null){
		        		startDate=order.getRightStartDate();
		        		orderStatus =1;
		        		orderId=new BigDecimal(order.getId());
		        		logger.debug("找到此user購買的訂單"+orderId+" startDate="+startDate+" priceTypeV="+priceTypeV+" priceType="+priceType);
		        	}else{
		        		logger.debug("找到此user購買的訂單 priceType="+priceType);
		        	}
				}else{
					//免費型 & 購物車型
					priceTypeV=0;
					orderStatus=1;
	        		logger.debug("免費型app startDate="+startDate+" priceTypeV="+priceTypeV+" priceType="+priceType);
				}
			}
			//可獲取license
			if(orderStatus==1){
//				logger.info("查找是否有合法的license模板");
	    		logger.info("(available==0 || available==1) pkg : " + map.get("pkg") + " versionId : " + map.get("versionId") + " storeId : " + map.get("storeId"));
//				List<AppLicense> appLicenseList = sqlMapClientTemplate.queryForList("License.getAppLicenseInfo", map);
//    				---test---
//				logger.info("appLicenseList size : " + appLicenseList.size());
//				if(!appLicenseList.isEmpty()){
//					for(AppLicense a : appLicenseList){
//						logger.info("appLicenseList content : " + a.getLicensedByUser());
//						logger.info("appLicenseList times : " + a.getLicensedTimes());
//					}
//				}
//				
////    				appLicenseList找不到模板
//				if(appLicenseList.isEmpty()){
//					return new ServiceResult<LicenseInfo>(GetLicenseCode.No_LICENSE);
//				}
//				
//				for(AppLicense a : appLicenseList){
	    		{
					License l=new License();
					int licenseType = 2;
//					
//    					double licenseType = a.getLicenseType();
//					
//					logger.info("appLicenseList licenseType : " + a.getLicenseType());
					
					l.setAppPackageId(pkg.trim());
					logger.info("license appPkgId : " + l.getAppPackageId());
					
					l.setVersion(version);
					logger.info("license version : " + l.getVersion());
					
					l.setAvailable(orderStatus);
					logger.info("license available : " + l.getAvailable());
					
					l.setPriceType(priceTypeV);
					logger.info("license priceTypeV : " + l.getPriceType());
					
					if(orderId!=null){
						l.setOrderId(orderId+"");
						logger.info("license orderId : " + l.getOrderId());
					}
					boolean forCharge=false;
					if(isTestLicense){
						licenseType = 2;
						l.setDurationStart(testStartDate);
						logger.info("license testStartDate : " + l.getDurationStart());
						
						l.setDurationEnd(testEndDate);
						logger.info("license testEndDate : " + l.getDurationEnd());
					}else if(priceTypeV==2){
						forCharge = true;
						licenseType = 2;    						
						if(startDate!=null){
							l.setDurationStart(startDate);
							logger.info("license StartDate1 : " + l.getDurationStart());
						}else{
							l.setDurationStart(new Date());
							logger.info("license StartDate2 : " + l.getDurationStart());
						}
						Calendar cal=Calendar.getInstance();
						cal.setTime(l.getDurationStart());
						//如果今日购买，跳过今日+30天
						NextMonthEndDay n=NextMonthEndDay.nextMonthlyEndDay(cal.getTime(),true);
						l.setDurationEnd(n.getEndDay());
						logger.info("license EndDate : " + l.getDurationEnd());
					}else{
//        				免費型 & InApp 型 & 計次, 用APP_LICENSE的日期
						SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
						l.setDurationEnd(sdf.parse("99991231"));
						logger.info("license StartDate free : " + l.getDurationEnd());
						
						l.setDurationStart(sdf.parse("19000101"));
						logger.info("license StartDate free : " + l.getDurationStart());
					}
					
					if(priceTypeV==0 && pkg!=null){
	    				Date expDate = (Date) sqlMapClientTemplate.queryForObject("License.getExpDate", map);
	    				if(expDate!=null){
	    					logger.info("expDate!=null " + expDate);
	    					l.setDurationEnd(expDate);
	    				}
					}  
					
					l.setIMEI((imei != null) ? imei.trim() : null );
					logger.info("license imei : " + l.getIMEI());
					
					l.setIMSI((iccid != null ) ? iccid.trim() : null);
					logger.info("license imsi : " + l.getIMSI());
					
					l.setLicenseCreateDate(new Date());
//					String LICENSED_BY_IMEI = a.getLicensedByImei();
//					logger.info("LICENSED_BY_IMEI : " + LICENSED_BY_IMEI);
//					l.setLicensedByIMEI((LICENSED_BY_IMEI != null && LICENSED_BY_IMEI.trim().equalsIgnoreCase("Y")) ? true : false);
//					
//					String LICENSED_BY_IMSI = a.getLicensedByImsi();
//					logger.info("LICENSED_BY_IMSI : " + LICENSED_BY_IMSI);
//					l.setLicensedByIMSI((LICENSED_BY_IMSI != null && LICENSED_BY_IMSI.trim().equalsIgnoreCase("Y")) ? true : false);
//					
//					String LICENSED_BY_USER = a.getLicensedByUser();
//					logger.info("LICENSED_BY_USER : " + LICENSED_BY_USER);
//					l.setLicensedByUser((LICENSED_BY_USER != null && LICENSED_BY_USER.trim().equalsIgnoreCase("Y")) ? true : false);
//					
//					l.setLicensedTimes(a.getLicensedTimes());
//					logger.info("license licensedTimes : " + l.getLicensedTimes());
					
					l.setLicenseType(licenseType);
					logger.info("license licensedType : " + l.getLicenseType());
//    					l.setLicensedTimes((int)a.getLicensedTimes());
//    					l.setLicenseType((int)licenseType);
					
					l.setUserId((userUid != null) ? userUid.trim() : null);
					logger.info("license userId : " + l.getUserId());
					
					l.setInnAppPay(isInnAppPay);
					logger.info("license isInnAppPay : " + l.isInnAppPay());
					
					l.setStore(store);
					logger.info("license store : " + l.getStore());
					
					BigDecimal licenseId = (BigDecimal)sqlMapClientTemplate.queryForObject("License.getMaxIdFromProsumerAppLicense");
					logger.info("license queryResult licenseId : " + licenseId);
					logger.info("first license durationEnd : " + l.getDurationEnd() + " " + (l.getDurationEnd()==null));
					SignedLicense sl = LicenseFactory.genLicense(l);
					sl.setId(licenseId);
					logger.info("license licensedId : sl.getId = : " + sl.getId());
					
					sl.setLicense(l);
					sls.add(sl);
					logger.info("first sls size : " + sls.size());
					Map<String, Object> insertProsumerAppLicenseMap = new HashMap<String, Object>();
					insertProsumerAppLicenseMap.put("licenseId", licenseId);
					insertProsumerAppLicenseMap.put("userId", userId);
					insertProsumerAppLicenseMap.put("appPackageId", l.getAppPackageId());
					insertProsumerAppLicenseMap.put("licenseType", l.getLicenseType());
					insertProsumerAppLicenseMap.put("licensedByIMEI", l.isLicensedByIMEI()?"Y":"N");
					insertProsumerAppLicenseMap.put("licensedByIMSI", l.isLicensedByIMSI()?"Y":"N");
					insertProsumerAppLicenseMap.put("licensedByUser", l.isLicensedByUser()?"Y":"N");
					insertProsumerAppLicenseMap.put("userImei", l.getIMEI());
					insertProsumerAppLicenseMap.put("userImsi", l.getIMSI());
					insertProsumerAppLicenseMap.put("licensedTimes", l.getLicensedTimes());
					insertProsumerAppLicenseMap.put("effDate", l.getDurationStart()!=null?new java.sql.Date(l.getDurationStart().getTime()):null);
					insertProsumerAppLicenseMap.put("expDate", l.getDurationEnd()!=null?new java.sql.Date(l.getDurationEnd().getTime()):null);
					insertProsumerAppLicenseMap.put("data", sl.getData());
					insertProsumerAppLicenseMap.put("sign", sl.getSign());
					insertProsumerAppLicenseMap.put("licenseExpire", "N");
					if(forCharge){
						insertProsumerAppLicenseMap.put("sourceFrom", Constants.LICENSE_CREATE_FROM_CHARGE);            			
					}else{
						insertProsumerAppLicenseMap.put("sourceFrom", Constants.LICENSE_CREATE_FROM_TEMPLATE);
					}
					insertProsumerAppLicenseMap.put("versionId", version);
					insertProsumerAppLicenseMap.put("paymentAvailable", orderStatus);
					if(isTestLicense){
						insertProsumerAppLicenseMap.put("forTesting", 1);            			
					}else{
						insertProsumerAppLicenseMap.put("forTesting", 0);
					}
					insertProsumerAppLicenseMap.put("userUid", (userUid != null) ? userUid.trim() : null);
					insertProsumerAppLicenseMap.put("storeId", storeId);
					sqlMapClientTemplate.insert("License.insertProsumerAppLicense", insertProsumerAppLicenseMap);
				}
//        		如果找不到模版，並且是付費類型
				if(sls.size() ==0 && (priceTypeV==2||priceTypeV==1)){
					License l=new License();
					int licenseType=2;
					l.setAppPackageId(pkg.trim());
					l.setVersion(version);
					l.setAvailable(orderStatus);
					l.setPriceType(priceTypeV);
					if(orderId!=null) l.setOrderId(orderId+"");
					boolean forCharge=false;
					if(isTestLicense){
						l.setDurationStart(testStartDate);
						l.setDurationEnd(testEndDate);
					}else{
						forCharge=true;
						Calendar cal=Calendar.getInstance();
						//月租型要算到月的週期
						if(priceTypeV==2){
    						if(startDate!=null) cal.setTime(startDate);
    						//如果今日购买，跳过今日+30天
    						NextMonthEndDay n=NextMonthEndDay.nextMonthlyEndDay(cal.getTime(),true);
    						l.setDurationEnd(n.getEndDay());
						}else{
    						cal.set(9999,11, 31);
    						l.setDurationEnd(cal.getTime());
						}
						// 購買或訂閱日期
						if(startDate!=null){
							l.setDurationStart(startDate);
						}else{
							l.setDurationStart(new Date());
						}
					}
					l.setIMEI((imei != null) ? imei.trim() : null );
					l.setIMSI((iccid != null) ? iccid.trim() : null );
					l.setLicenseCreateDate(new Date());
					l.setLicensedByIMEI(false);
					l.setLicensedByIMSI(false);
					l.setLicensedByUser(false);
					l.setLicensedTimes(0);
					l.setLicenseType(licenseType);
					l.setUserId((userUid != null) ? userUid.trim() : null);
					l.setInnAppPay(isInnAppPay);
					l.setStore(store);
					BigDecimal licenseId = (BigDecimal)sqlMapClientTemplate.queryForObject("License.getMaxIdFromProsumerAppLicense");
					SignedLicense sl=LicenseFactory.genLicense(l);
					sl.setId(licenseId);
					sl.setLicense(l);
					sls.add(sl);
					Map<String, Object> insertProsumerAppLicenseMap = new HashMap<String, Object>();
					insertProsumerAppLicenseMap.put("licenseId", licenseId);
					insertProsumerAppLicenseMap.put("userId", userId);
					insertProsumerAppLicenseMap.put("appPackageId", l.getAppPackageId());
					insertProsumerAppLicenseMap.put("licenseType", l.getLicenseType());
					insertProsumerAppLicenseMap.put("licensedByIMEI", l.isLicensedByIMEI()?"Y":"N");
					insertProsumerAppLicenseMap.put("licensedByIMSI", l.isLicensedByIMSI()?"Y":"N");
					insertProsumerAppLicenseMap.put("licensedByUser", l.isLicensedByUser()?"Y":"N");
					insertProsumerAppLicenseMap.put("userImei", l.getIMEI());
					insertProsumerAppLicenseMap.put("userImsi", l.getIMSI());
					insertProsumerAppLicenseMap.put("licensedTimes", l.getLicensedTimes());
					insertProsumerAppLicenseMap.put("effDate", l.getDurationStart()!=null?new java.sql.Date(l.getDurationStart().getTime()):null);
					insertProsumerAppLicenseMap.put("expDate", l.getDurationEnd()!=null?new java.sql.Date(l.getDurationEnd().getTime()):null);
					insertProsumerAppLicenseMap.put("data", sl.getData());
					insertProsumerAppLicenseMap.put("sign", sl.getSign());
					insertProsumerAppLicenseMap.put("licenseExpire", "N");
					if(forCharge){
						insertProsumerAppLicenseMap.put("sourceFrom", Constants.LICENSE_CREATE_FROM_CHARGE);            			
					}else{
						insertProsumerAppLicenseMap.put("sourceFrom", Constants.LICENSE_CREATE_FROM_TEMPLATE);
					}
					insertProsumerAppLicenseMap.put("versionId", version);
					insertProsumerAppLicenseMap.put("paymentAvailable", orderStatus);
					if(isTestLicense){
						insertProsumerAppLicenseMap.put("forTesting", 1);            			
					}else{
						insertProsumerAppLicenseMap.put("forTesting", 0);
					}
					insertProsumerAppLicenseMap.put("userUid", (userUid != null) ? userUid.trim() : null);
					insertProsumerAppLicenseMap.put("storeId", storeId);
					sqlMapClientTemplate.insert("License.insertProsumerAppLicense", insertProsumerAppLicenseMap);
				}
			}else if(appv.getOnUse()!=ON_USE){
				//沒有付費，且此app下架
        		logger.debug("找不到此user購買的訂單，且此app已經下架 startDate="+startDate+" priceTypeV="+priceTypeV+" priceType="+priceType);
				return new ServiceResult<LicenseInfo>(GetLicenseCode.No_LICENSE);
			}
		}else{
			logger.debug("找不到此app");
		}
    	logger.info("**********************************");
    	LicenseInfo returnValue = returnXml( sls, isInnAppPayMethod, priceTypeV, storeId, pkg, version);
    	logger.info("license returnValue is null : " + (returnValue==null));
    	if(returnValue!=null){
    		logger.info("license returnValue Id : " + returnValue.getId());
    		logger.info("license returnValue pkg : " + returnValue.getPkg());
    		logger.info("license returnValue data : " + returnValue.getData());
    		logger.info("license returnValue sign : " + returnValue.getSign());
    	}
    	
    	return new ServiceResult<LicenseInfo>(CommonCode.SUCCESS, returnValue);
	}
	public LicenseInfo returnXml(List<SignedLicense> sls, boolean isInnAppPayMethod, int priceTypeV, BigDecimal storeId, String pkg, int version){
		LicenseInfo licenseInfo = new LicenseInfo();
		if(isInnAppPayMethod){
			if(sls.size()>0){
				SignedLicense sl = sls.get(0);
				licenseInfo.setId(sl.getId());
				licenseInfo.setPkg(pkg);
				licenseInfo.setData(sl.getData());
				licenseInfo.setSign(sl.getSign());
			}
    	}else{
			for(int i = 0; i<sls.size(); i++){
				SignedLicense sl = sls.get(i);
				licenseInfo.setId(sl.getId());
				licenseInfo.setPkg(pkg);
				licenseInfo.setData(sl.getData());
				licenseInfo.setSign(sl.getSign());
			}
    	}
		return licenseInfo;
	}
	
	public ServiceResult<LicenseInfo> getTestLicense (String pkgId, int version,String userUid, String userId, 
			String iccid, String imei, boolean inApp, BigDecimal storeId, String store) 
	throws Exception{
		logger.info("getTestLicense ... ");
		int available = 0; //0:付款有效, 1:購買中

		License l = new License();
		l.setInnAppPay(inApp);
		int licenseType = 2;
		l.setAppPackageId(pkgId.trim());
		l.setVersion(version);
		l.setAvailable(available);
		l.setPriceType(0);
//		if(orderId!=null) l.setOrderId(orderId+"");免費的不用orderId
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		l.setDurationStart(cal.getTime());
		l.setDurationEnd(new Date());
//		cal.add(Calendar.DATE, 1);
//		l.setDurationStart(new Date());
//		l.setDurationEnd(cal.getTime());
		l.setIMEI((imei != null) ? imei.trim() : null );
		l.setIMSI((iccid != null) ? iccid.trim() : null );
		l.setLicenseCreateDate(new Date());
		l.setLicensedByIMEI(true);
		l.setLicensedByIMSI(false);
		l.setLicensedByUser(false);
		l.setLicensedTimes(0);
		l.setLicenseType(licenseType);
		l.setUserId((userId != null) ? userId.trim() : null );
		BigDecimal licenseId = (BigDecimal)sqlMapClientTemplate.queryForObject("License.getMaxIdFromProsumerAppLicense");
		l.setStore(store);
		SignedLicense sl=LicenseFactory.genLicense(l);
		sl.setId(licenseId);
		sl.setLicense(l);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("licenseId", licenseId);
		map.put("userId", l.getUserId());
		map.put("appPackageId", l.getAppPackageId());
		map.put("licenseType", l.getLicenseType());
		map.put("licensedByIMEI", l.isLicensedByIMEI()?"Y":"N");
		map.put("licensedByIMSI", l.isLicensedByIMSI()?"Y":"N");
		map.put("licensedByUser", l.isLicensedByUser()?"Y":"N");
		map.put("userImei", l.getIMEI());
		map.put("userImsi", l.getIMSI());
		map.put("licensedTimes", l.getLicensedTimes());
		map.put("effDate", l.getDurationStart()!=null?new java.sql.Date(l.getDurationStart().getTime()):null);
		map.put("expDate", l.getDurationEnd()!=null?new java.sql.Date(l.getDurationEnd().getTime()):null);
		map.put("data", sl.getData());
		map.put("sign", sl.getSign());
		map.put("licenseExpire", "N");
		map.put("sourceFrom", Constants.LICENSE_CREATE_FROM_TEST_VERSION_CODE);
		map.put("versionId", version);
		map.put("paymentAvailable", available);
		map.put("forTesting", 2);
		map.put("userUid", (userUid != null) ? userUid.trim() : null);
		map.put("storeId", storeId);
		sqlMapClientTemplate.insert("License.insertProsumerAppLicense", map);
		
		LicenseInfo licenseInfo = new LicenseInfo();
		licenseInfo.setId(sl.getId());
		licenseInfo.setPkg(sl.getPkgId());
		licenseInfo.setData(sl.getData());
		licenseInfo.setSign(sl.getSign());			
		
		return new ServiceResult<LicenseInfo>(CommonCode.SUCCESS, licenseInfo);
	}
}
