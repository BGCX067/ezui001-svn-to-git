package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.smart.appstore.server.api.Constants;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.model.SdkAppInfo;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.GetSdkAppInfoCode;

@Service("SdkAppInfoService")
public class SdkAppInfoService {
	
	private static final Logger logger = LoggerFactory.getLogger(SdkAppInfoService.class);
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Autowired
    private AuthenticationService authenticationService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Transactional(propagation = Propagation.NEVER)
	public ServiceResult<SdkAppInfo> sdkAppInfo(SdkAppInfoService self, String token,
			String imei, String pkg, BigDecimal storeId, int version, int dolog, String iccid, int isFirstTime, String devmodel){
		
		try {
			int newVersion = 0;
			boolean isNotFoundApp = false;
			double price = -1;
			String priceType = "";
			
//		取出ApplicationDetail資訊
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pkg", pkg);
			map.put("storeId", storeId);
			SdkAppInfo sdkAppInfo = (SdkAppInfo) sqlMapClientTemplate.queryForObject("Application.getNewSdkAppInfo", map);
			if(sdkAppInfo!=null){
				price = sdkAppInfo.getPrice();
				priceType = sdkAppInfo.getPriceType();		
				newVersion = sdkAppInfo.getVersion();
			}else{
				isNotFoundApp=true;
			}

			if(!isNotFoundApp){
			    String userUid = null;
			    String userId = null;
			    // 取得使用者資訊
			    Prosumer users = null;
			    if (!StringUtils.isBlank(token)) {
			    	users = authenticationService.getProsumerByAccount(token);
			        
			        if(users != null){  
			        	userUid = users.getUserUid();
			            userId = users.getUserId();
			            // 取得付費資訊
			            List<String> appPkgList = new ArrayList<String>();
			            appPkgList.add(sdkAppInfo.getPkg());
			            List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgList, storeId);
			            
			            // 存入PaymentInformationList使取得PayStatus
			            if(paymentInformationList != null && paymentInformationList.size() >0){
			            	// 月租且取消訂閱後，尚未到期者，秀SubscribeExpDate日期
			            	sdkAppInfo.setPaymentInformationList(paymentInformationList);
			            	
			            	for(PaymentInformation payment : paymentInformationList){
			            		sdkAppInfo.setPayStatus(payment.getStatus());
			            		sdkAppInfo.setMyPrice(payment.getAmount());
			            	}
			            }else if(price == 0 && priceType.equals("" + Constants.PRICE_TYPE_ONCE)){
			            	sdkAppInfo.setMyPriceType("" + Constants.PRICE_TYPE_ONCE);
			            	sdkAppInfo.setPayStatus(Constants.STATUS_PAYMENT_SUCCESS);
			            	sdkAppInfo.setMyPrice(price);
			            }
			        }
			    }
			    
//				取得prosumer，JSP判斷prosumer是否存在，顯示要出現的資料
			    sdkAppInfo.setProsumer(users);
			    
			    if(dolog == 1){
			    	logger.debug("imei = " +imei+ " iccid = " +iccid+ " pkg = " +pkg+ " newVersion = " +newVersion+ " version = " +version+
			    					" isFirstTime = " +isFirstTime+ " devmodel = " +devmodel+ " userUid = " +userUid+ " userId = " +userId);
			    	SdkAppInfo insertSdkAppInfo = new SdkAppInfo();
			    	insertSdkAppInfo.setImei(imei);
			    	insertSdkAppInfo.setIccid(iccid);
			    	insertSdkAppInfo.setPkg(pkg);
			    	insertSdkAppInfo.setNewVersion(newVersion);
			    	insertSdkAppInfo.setVersion(version);
			    	insertSdkAppInfo.setIsFirstTime(isFirstTime);
			    	insertSdkAppInfo.setDevmodel(devmodel);
			    	insertSdkAppInfo.setUserUid((userUid != null) ? userUid.trim() : null);
			    	insertSdkAppInfo.setUserId((userId != null) ? userId.trim() : null);
			    	sqlMapClientTemplate.insert("Application.insertUserGetAppversionLog", insertSdkAppInfo);
			    	logger.debug("insert id : " + insertSdkAppInfo.getId());
			    	SdkAppInfo updateSdkAppInfo = new SdkAppInfo();
			    	updateSdkAppInfo.setId(insertSdkAppInfo.getId());
			    	updateSdkAppInfo.setImei(imei);
			    	updateSdkAppInfo.setPkg(pkg);
			    	updateSdkAppInfo.setVersion(version);
			    	updateSdkAppInfo.setStoreId(storeId);
			    	sqlMapClientTemplate.update("Application.updateUserInstallApkOfGetVersionId", updateSdkAppInfo);
			    	
			    	sdkAppInfo.setId(insertSdkAppInfo.getId());
			    }
			    
//        如果priceType=3且付費priceType改為1,沒付費改為0，priceType=4的priceType改為0
			    if(priceType != null && priceType.length() > 0 && priceType.trim().equals(""+Constants.PRICE_INNAPP_PURCHASE)){
//			    	if(isPayment == true){
			    		sdkAppInfo.setPriceType("" + Constants.PRICE_TYPE_ONCE);
//			    	}else{
//			    		sdkAppInfo.setPriceType("" + Constants.PRICE_TYPE_FREE);
//			    	}
			    }else if(priceType != null && priceType.length() > 0 && priceType.trim().equals(""+Constants.PRICE_INNAPP_CAR)){
			    	sdkAppInfo.setPriceType("" + Constants.PRICE_TYPE_FREE);
			    }else{
			    	sdkAppInfo.setPriceType(priceType);
			    }

				logger.info("prosumer : " + sdkAppInfo.getProsumer());
				return new ServiceResult<SdkAppInfo>(CommonCode.SUCCESS, sdkAppInfo);
			}else{
				return new ServiceResult<SdkAppInfo>(GetSdkAppInfoCode.No_APP, null);
			}
		} catch (Throwable e) {
			return new ServiceResult<SdkAppInfo>(CommonCode.SERVICE_FAIL, null, e);
		}
	}
	
	public ServiceResult<SdkAppInfo> getTest(String pkg, int version)throws Exception{
		
		SdkAppInfo sdkAppInfo = new SdkAppInfo();
		sdkAppInfo.setPkg(pkg);
		sdkAppInfo.setVersion(version);
		sdkAppInfo.setTitle("");
		sdkAppInfo.setIcon("");
		sdkAppInfo.setProvider("");
		sdkAppInfo.setRatingTimes(0);
		sdkAppInfo.setPrice(0.0);
		sdkAppInfo.setPriceType("0");
		sdkAppInfo.setPriceText("");
		sdkAppInfo.setOnUse(1);
		
		return new ServiceResult<SdkAppInfo>(CommonCode.SUCCESS, sdkAppInfo);
	}
}
