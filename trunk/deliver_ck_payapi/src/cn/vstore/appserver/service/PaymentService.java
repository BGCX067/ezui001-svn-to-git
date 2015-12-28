package cn.vstore.appserver.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.api.payment.unionpay.SignBy;
import cn.vstore.appserver.api.payment.unionpay.XmlDefinition;
import cn.vstore.appserver.api.payment.unionpay.XmlHttpConnection;
import cn.vstore.appserver.api.support.PayeeInfoFactory;
import cn.vstore.appserver.api.support.paytype.Gpay;
import cn.vstore.appserver.api.support.paytype.PePay;
import cn.vstore.appserver.api.support.paytype.UnionPay;
import cn.vstore.appserver.form.payment.SelectPaymentForm;
import cn.vstore.appserver.model.Amount;
import cn.vstore.appserver.model.Application;
import cn.vstore.appserver.model.OrderInfo;
import cn.vstore.appserver.model.OrderRefundHistory;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.model.UserPayment;
import cn.vstore.appserver.model.UserSubscribe;
import cn.vstore.appserver.model.UserSubscribeLog;
import cn.vstore.appserver.service.ResultCode.CheckPayStatusCode;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.CspCode;
import cn.vstore.appserver.service.ResultCode.GetOrderStatus;
import cn.vstore.appserver.service.ResultCode.IpayCode;
import cn.vstore.appserver.service.ResultCode.OverPaymentQuotaCode;
import cn.vstore.appserver.util.Constants;
import cn.vstore.core.model.vo.OrderPayInfoLog;
import cn.vstore.core.model.vo.StorePayeeInfo;
import cn.vstore.core.model.vo.StorePayeeInfoExample;
import cn.vstore.core.model.vo.StorePayeeInfoExample.Criteria;

/**
 * @version $Id$
 */
@Service("PaymentService")
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private static final String CSP_API_NAME = "GetUserAPSubStatus";

    private static final String IPAY_API_NAME = "GetOrderDeductionStatus";

    private static final String CONTROLLER_API_NAME = "CheckPayStatusApi";

    /**
     * 一個月內購買金額上限
     */
    private static final int IPAY_TOTAL_FEE_LIMIT = 5000;

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private BlankListService blankListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CspService cspService;

    @Autowired
    private IpayService ipayService;

    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private ConstantService constantService;

    /**
     * 檢查是否開啟假付費機制，並取出付款資訊
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<PaymentInformation> getPaymentInformations(PaymentService self, String userId,
        String imei, String userUid, List<String> appPkgNames) {

        // 若無任何package，則直接回傳
        if ((appPkgNames == null) || (appPkgNames != null && appPkgNames.size() == 0))
            return null;

        boolean isTestBlankType = blankListService.isMockPayment(blankListService, userId);
        if (isTestBlankType) {
            // 取出付款資訊(假--測試用)
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imei", imei);
            map.put("userUid", userUid);
            map.put("appList", appPkgNames);
            return (List<PaymentInformation>) sqlMapClientTemplate.queryForList("Payment.getPaymentInformationByTestList", map);
        } else {
            // 取出付款資訊
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imei", imei);
            map.put("userUid", userUid);
            map.put("appList", appPkgNames);
            return (List<PaymentInformation>) sqlMapClientTemplate.queryForList("Payment.getPaymentInformationList", map);
        }
    }
//    增加STORE_ID
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<PaymentInformation> getPaymentInformations(PaymentService self, String userId,
        String imei, String userUid, List<String> appPkgNames, BigDecimal storeId) {

        // 若無任何package，則直接回傳
        if ((appPkgNames == null) || (appPkgNames != null && appPkgNames.size() == 0))
            return null;

        boolean isTestBlankType = blankListService.isMockPayment(blankListService, userId);
        if (isTestBlankType) {
            // 取出付款資訊(假--測試用)
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imei", imei);
            map.put("userUid", userUid);
            map.put("appList", appPkgNames);
            map.put("storeID", storeId);
            return (List<PaymentInformation>) sqlMapClientTemplate.queryForList("Payment.getPaymentInformationByTestList", map);
        } else {
            // 取出付款資訊
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imei", imei);
            map.put("userUid", userUid);
            map.put("appList", appPkgNames);
            map.put("storeID", storeId);
            return (List<PaymentInformation>) sqlMapClientTemplate.queryForList("Payment.getPaymentInformationList", map);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public ServiceResult<OrderInfo> getNewOrder(String pkg,SelectPaymentForm selectPaymentForm, BigDecimal storeId,Object payeeInfo){
    	Prosumer users = authenticationService.getProsumerByAccount(selectPaymentForm.getToken());
    	if(users!=null){
    		int paytype=0;
    		if(payeeInfo!=null) 
    			paytype = PayeeInfoFactory.getPaymentType(payeeInfo);
            // 取出 Appliction，檢查是否有Apk, 條件為APP_ID
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", pkg);
            map.put("storeId", storeId);
            Application apkInfo = (Application) sqlMapClientTemplate.queryForObject("Application.getApplicationByAppId", map);
            if (apkInfo == null){
            	logger.debug("not find app "+pkg+" in store "+storeId);
                return new ServiceResult<OrderInfo>(CommonCode.SERVICE_FAIL);
            }
            // url前加上hostPath, set icon and screen shots url
            String hostPath = constantService.getHostPath();
            if (StringUtils.isNotBlank(apkInfo.getIcon()) && apkInfo.getIcon().charAt(0) == '/')
            	apkInfo.setIcon(hostPath + apkInfo.getIcon());

            Map<String, Object> order_map = new HashMap<String, Object>();
            order_map.put("appId", pkg);
            order_map.put("eimi", selectPaymentForm.getImei());
            order_map.put("userUid", users.getUserUid());
            order_map.put("storeId", storeId);
            
            logger.info("appId : " + order_map.get("appId") +" "+ (order_map.get("appId")==pkg));
            logger.info("eimi : " + order_map.get("eimi") +" "+ (order_map.get("eimi")==selectPaymentForm.getImei()));
            logger.info("userUid : " + order_map.get("userUid") +" "+ (order_map.get("userUid")==users.getUserUid()));
            logger.info("storeId : " + order_map.get("storeId") +" "+ (order_map.get("storeId")==storeId));
//            取得是否購買
            PaymentInformation order = (PaymentInformation) sqlMapClientTemplate.queryForObject("Application.getUserPaymentIsOrdered", order_map);
            
            logger.info("order 等於 " + (order==null));
//            logger.info("IPAY_TOKEN : " + order.getId());
            //the order_id is exists  
            if(order==null || 0==order.getStatus()){
            	BigDecimal payeeStoreId=storeId;
            	if(payeeInfo!=null){
	            	StorePayeeInfoExample spie=new StorePayeeInfoExample();
	            	Criteria criteria=spie.createCriteria().andPayTypeEqualTo(paytype);
	            	boolean find=false;
	            	if(payeeInfo instanceof Gpay){
	            		Gpay g=(Gpay)payeeInfo;
	            		criteria.andKey1EqualTo(g.getPartner()).andKey2EqualTo(g.getSeller());
	            		find=true;
	            	}else if(payeeInfo instanceof UnionPay){
	            		UnionPay u=(UnionPay)payeeInfo;
	            		criteria.andKey1EqualTo(u.getMerchantId());
	            		find=true;
	            	}else if(payeeInfo instanceof PePay) {
	            		find = true;
	            	}
	            	if(find){
	                	List<StorePayeeInfo> spilist=sqlMapClientTemplate.queryForList("store_payee_info.ibatorgenerated_selectByExample",spie);
	            		if(spilist!=null&&spilist.size()>0){
	            			payeeStoreId=new BigDecimal(spilist.get(0).getStoreId());
	            		}
	            	}
            	}
            	Date payTime = new Date();
				PaymentInformation pinfo=new PaymentInformation();
				pinfo.setPayTime(payTime);
				pinfo.setPkg(pkg);
				pinfo.setImei(selectPaymentForm.getImei());
				pinfo.setUserUid(users.getUserUid());
				pinfo.setPaymentType(paytype);
				pinfo.setAmount(apkInfo.getPrice());
				pinfo.setToken(selectPaymentForm.getToken());
				pinfo.setMyPriceType(apkInfo.getPriceType());
				pinfo.setStatus(Constants.STATUS_PAYMENT_LOG);//STATUS_PAYMENT_LOG=0  沒有付款
				pinfo.setVersionId(apkInfo.getVersion());
				pinfo.setStoreId(storeId);
				pinfo.setUserId(users.getUserId());
				pinfo.setRightStartDate(payTime);
				if((Constants.PRICE_TYPE_MONTHLY+"").equals(apkInfo.getPriceType())){
					Calendar cal=Calendar.getInstance();
					int amount=1;
					if(apkInfo.getMonthlyCycle()>=1){
						amount=apkInfo.getMonthlyCycle();
					}
					//每月以30日計算
					cal.add(Calendar.DAY_OF_MONTH, amount*30);
					pinfo.setRightEndDate(cal.getTime());
				}
				pinfo.setMonthlyCycle(apkInfo.getMonthlyCycle());
				pinfo.setPayeeStoreId(payeeStoreId);
				if(!StringUtils.isBlank(selectPaymentForm.getStoreOid())){
					pinfo.setStoreOid(selectPaymentForm.getStoreOid());
				}
				sqlMapClientTemplate.insert("Payment.insertOrder", pinfo);
				logger.debug("insert order id="+pinfo.getId()+" amount="+pinfo.getAmount());
				String orderId="00000000"+pinfo.getId();
				String orderNo=storeId+new SimpleDateFormat("yyyyMMdd").format(payTime)+orderId.substring(orderId.length()-8);
				pinfo.setOrderNo(orderNo);
				sqlMapClientTemplate.update("Payment.updateIpayUserPaymentLogOrderNoById",pinfo);
				logger.debug("update order id="+pinfo.getId()+" orderNo="+pinfo.getOrderNo());
				//如果有payeeInfo，保存log
				if (!StringUtils.isBlank(selectPaymentForm.getPayeeInfo())){
					OrderPayInfoLog pl=new OrderPayInfoLog();
					pl.setOrderId(pinfo.getId());
					pl.setPayeeInfo(selectPaymentForm.getPayeeInfo());
					sqlMapClientTemplate.insert("order_pay_info_log.ibatorgenerated_insert", pl);
				}
				OrderInfo orderinfo=new OrderInfo();
				orderinfo.setApp(apkInfo);
				orderinfo.setPayInfo(pinfo);
				orderinfo.setUser(users);
				if(paytype==2){
					if(!checkSignForUnionPay(pinfo, apkInfo.getProvider(), selectPaymentForm)){
						logger.debug("Fail to check sign.");
		                return new ServiceResult<OrderInfo>(CommonCode.SERVICE_FAIL);
					}
				}
				return new ServiceResult<OrderInfo>(CommonCode.SUCCESS,orderinfo);
            	
            }else{
            	
            	if(order.getStatus()==1 || order.getStatus()==2 || order.getStatus()==5){
             	  OrderInfo orderinfo=new OrderInfo();
                  orderinfo.setApp(apkInfo);
                  orderinfo.setPayInfo(order);
                  orderinfo.setUser(users);
                  return new ServiceResult<OrderInfo>(CommonCode.SUCCESS,orderinfo);
            	}
            	
            	 
            	
            }
           
           
    	}
    	return new ServiceResult<OrderInfo>(CommonCode.SERVICE_FAIL);
    }
    /**
     * 檢查是否超過每月最大金額5000元
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Boolean> isOverPaymentQuota(PaymentService self, String token,
        String iccid, String imei, String pkgId, String store) {

        try {
            // get package information
            Amount pkgAmount = applicationService.getAppAmount(pkgId, store);
            if (pkgAmount == null) {
                return new ServiceResult<Boolean>(OverPaymentQuotaCode.NO_APP, null);
            }

            // 取得使用者資訊
            Prosumer users = authenticationService.getProsumerByAccount(token);
            String userUid = users.getUserUid();
            String userId = users.getUserId();
            boolean isTestBlankType = blankListService.isMockPayment(blankListService, userId);
            // 計次APK，不管使用信用卡或Phone Bill 皆以 pay_time=當月 and (status=2 or status=5)作為判斷條件
            int onetime = 0, monthlyBill = 0, monthlyCC = 0;
            if (isTestBlankType) {// true:測試使用, false:實際使用)
                // 假付費
                onetime = self.getOnetimeAmountByTestList(self, userUid);
                monthlyCC = self.getCCAmountByTestList(self, userUid);
                monthlyBill = self.getBillAmountByTestList(self, userUid);
            } else {
                // 真付費
                onetime = self.getOnetimeAmount(self, userUid);
                monthlyCC = self.getCCAmount(self, userUid);
                monthlyBill = self.getBillAmount(self, userUid);
            }

            // 加總計次/帳單/信用卡消費金額
            int totalAmount = onetime + monthlyBill + monthlyCC;

            // 當消費總金額大於當月最高可消費金額，則回傳以超過可消費金額即 isOverQuota=true
            boolean isOverQuota = (pkgAmount.getPrice() + totalAmount) > IPAY_TOTAL_FEE_LIMIT;

            logger.info("IpayTotalFee total: " + totalAmount + ", onetime: " + onetime
                    + ", monthlyBill: " + monthlyBill + ", monthlyCC: " + monthlyCC
                    + ", pkgAmount:" + pkgAmount.getPrice());

            return new ServiceResult<Boolean>(CommonCode.SUCCESS, isOverQuota);
        } catch (Throwable e) {
            return new ServiceResult<Boolean>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * 檢查付款狀態
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PayStatus> checkPayStatus(PaymentService self, String token, String iccid,
        String imei, String pkgId, BigDecimal storeId) {

        ServiceResult<Boolean> serviceResult = null;
        PayStatus payStatus = new PayStatus();

        try {

            // 檢查是否有Apk
            if (!applicationService.hasApplicationByPkgId(pkgId))
                return new ServiceResult<PayStatus>(CheckPayStatusCode.NO_APP);

            // 取得使用者資訊
            Prosumer users = authenticationService.getProsumerByAccount(token);
            String userUid = users.getUserUid();
            String userId = users.getUserId();
            // 取得付費資訊
            PaymentInformation paymentInformation = self.getPaymentInfoByApkAndUserUid(self, pkgId, userUid);
            if (paymentInformation == null)
                return new ServiceResult<PayStatus>(CheckPayStatusCode.NO_PAID);

            // 只檢查付款中的付款狀態(STATUS_PAYMENT_TEMP_SUCCESS=1;)
            if (UserPayment.STATUS_PAYMENT_PROCESSING == paymentInformation.getStatus()) {

                String code = null;
                String exceptionName = null;

                // 取得serviceId
                String serviceId = applicationService.getServiceId(pkgId, paymentInformation.getLastVersionId());
                if (StringUtils.isBlank(serviceId))
                    return new ServiceResult<PayStatus>(CheckPayStatusCode.NO_SERVICE_ID);

                payStatus.priceType = paymentInformation.getMyPriceType();
                payStatus.version = paymentInformation.getVersionId();
                String myPriceType = payStatus.priceType = payStatus.priceType.trim();

                // 從CSP/Ipay取得付款資訊
                if (myPriceType != null
                        && myPriceType.equals(String.valueOf(Constants.PRICE_TYPE_MONTHLY))) { // 月租
                    // 從CSP取得付款資訊
                    serviceResult = cspService.checkPaimentInfoBySubscribe(userId, serviceId);
                    // 如果回傳的Result不為null，表示不成功，要記下是哪個API錯誤
                    if (serviceResult != null && serviceResult.getResult() != null)
                        exceptionName = CSP_API_NAME;
                } else if (myPriceType != null
                        && (myPriceType.equals(String.valueOf(Constants.PRICE_TYPE_ONE_TIME)) //
                        || myPriceType.equals(String.valueOf(Constants.PRICE_TYPE_IN_APP_PURCHASE)))) { // 計次或inAppPurchase
                    // 從Ipay取得付款資訊
                    serviceResult = ipayService.checkPaymentInfoByOneTime(ipayService, userId, serviceId, paymentInformation.getId());
                    // 如果回傳的Result不為null，表示不成功，要記下是哪個API錯誤
                    if (serviceResult != null && serviceResult.getResult() != null)
                        exceptionName = IPAY_API_NAME;
                }

                // 取得Call CSP/Ipay 回傳的Code/Msg
                if (serviceResult != null) {
                    code = serviceResult.getCode();
                    paymentInformation.setRetMsg(serviceResult.getDes());
                }

                // iPayAPI付款成功
                if (serviceResult != null && (serviceResult.getData() != null)
                        && serviceResult.getData().booleanValue()) {
                    try {
                        self.accessSuccessPayment(self, token, imei, userId, serviceId, paymentInformation, storeId);
                    } catch (Throwable e) {
                        return new ServiceResult<PayStatus>(CommonCode.SERVICE_FAIL, payStatus, e);
                    }
                    payStatus.payStatus = UserPayment.STATUS_PAYMENT_SUCCESS;
                } else {
                    paymentInformation.setApiName((exceptionName != null ? exceptionName
                            : CONTROLLER_API_NAME));
                    paymentInformation.setRetCode(code != null ? code
                            : BusinessCommonCode.ERROR_CODE_IPAY_OR_CSP_FAIL);
                    paymentInformation.setRetMsgWithThrowable(serviceResult != null ? (serviceResult.getThrowable())
                            : null);

                    if (serviceResult != null //
                            && (CspCode.CSP_NO_FEEDBACK.getCompleteCode().equals(serviceResult.getResult().getCompleteCode()) //
                            || IpayCode.IPAY_NO_FEEDBACK.getCompleteCode().equals(serviceResult.getResult().getCompleteCode()))) {
                        // 更新IpayUserPaymentLog，不包含Status
                        self.updateIpayUserPaymentLogNoStatus(self, paymentInformation);
                        payStatus.payStatus = UserPayment.STATUS_PAYMENT_PROCESSING; // 付款中
                    } else {
                        // STATUS_PAYMENT_FAIL=3; 付款失敗
                        paymentInformation.setStatus(UserPayment.STATUS_PAYMENT_FAIL);
                        // 更新IpayUserPaymentLog
                        self.updateIpayUserPaymentLog(self, paymentInformation);
                        payStatus.payStatus = UserPayment.STATUS_PAYMENT_NO_PAID; // 沒有付款(還沒有按確認付款按鈕之前的狀態)
                    }
                    ResultCode resultCode = CommonCode.SUCCESS.bindSource((serviceResult != null ? serviceResult.getResult()
                            : null));
                    return new ServiceResult<PayStatus>(resultCode, payStatus, (serviceResult != null ? serviceResult.getThrowable()
                            : null));
                }
            } else if (UserPayment.STATUS_PAYMENT_SUCCESS == paymentInformation.getStatus()) {
                payStatus.payStatus = UserPayment.STATUS_PAYMENT_SUCCESS; // 付款成功
            }

            return new ServiceResult<PayStatus>(CommonCode.SUCCESS, payStatus);
        } catch (Throwable e) {
            return new ServiceResult<PayStatus>(CommonCode.SERVICE_FAIL, payStatus, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void accessSuccessPayment(PaymentService self, String token, String imei, String userId,
        String serviceId, PaymentInformation paymentInformation, BigDecimal storeId) {

        Date endDateQuery = null;

        // PayAPI付款成功, STATUS_PAYMENT_SUCCESS=2
        int status = Constants.STATUS_PAYMENT_SUCCESS;
        boolean isTestBlankType = blankListService.isMockPayment(blankListService, userId);

        if (isTestBlankType) {
            // 測試用假付款成功, STATUS_PAYMENT_TEST_SUCCESS=6
            status = Constants.STATUS_PAYMENT_TEST_SUCCESS;
        }
        paymentInformation.setStatus(status);

        // 更新成功的IpayUserPaymentLog
        self.updateSuccessIpayUserPaymentLog(self, paymentInformation);

        // 更新user的最後付款方式
        sendLogService.updateUserApkBehavior(sendLogService, userId, paymentInformation.getUserUid(), paymentInformation.getPkg(), paymentInformation.getPaymentType());

        // 月租
        if (paymentInformation.getMyPriceType() != null
                && Application.PRICE_TYPE_MONTH.equals(paymentInformation.getMyPriceType().trim())) {
            boolean isOwn = true;
            UserSubscribe userSubscribe = subscribeService.getIsCanceledAndEndTime(paymentInformation.getPkg(), paymentInformation.getUserUid(), storeId);
            if (userSubscribe == null) {
                userSubscribe = new UserSubscribe();
                isOwn = false;
            }
            if (userSubscribe != null)
                endDateQuery = userSubscribe.getEndTime();

            userSubscribe.setAppId(paymentInformation.getPkg());
            userSubscribe.setImei(imei);
            userSubscribe.setLastVersionId(paymentInformation.getVersionId());
            userSubscribe.setOrderId(paymentInformation.getId());
            userSubscribe.setServiceId(serviceId);
            userSubscribe.setUserId(userId);
            userSubscribe.setUserUid(paymentInformation.getUserUid());
            userSubscribe.setVersionId(paymentInformation.getVersionId());

            if (isOwn) {
                // 更新Subscribe data
                subscribeService.updateSubcribe(subscribeService, userSubscribe);
            } else {
                // 新增Subscribe data
                subscribeService.insertSubcribe(subscribeService, userSubscribe);
            }
            long subcribeId = subscribeService.getSubcribeId();

            // 更新SubcribeLog
            UserSubscribeLog userSubscribeLog = new UserSubscribeLog();
            userSubscribeLog.setId(subcribeId);
            userSubscribeLog.setUserId(userSubscribe.getUserId());
            userSubscribeLog.setAppId(userSubscribe.getAppId());
            if (endDateQuery != null)
                userSubscribeLog.setLastEndTime(new Timestamp(endDateQuery.getTime()));

            userSubscribeLog.setVersionId(userSubscribe.getVersionId());
            userSubscribeLog.setSubscribId(0);
            userSubscribeLog.setOrderId(userSubscribe.getOrderId());
            userSubscribeLog.setUserUid(userSubscribe.getUserUid());
            userSubscribeLog.setServiceId(userSubscribe.getServiceId());
            userSubscribeLog.setImei(userSubscribe.getImei());
            subscribeService.insertSubcribeLog(subscribeService, userSubscribeLog);

            // 更新Subscribe Id
            subscribeService.updateSubcribeId(subscribeService, subcribeId, userSubscribe);
        }
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出付款資訊，條件apk/userUid
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PaymentInformation getPaymentInfoByApkAndUserUid(PaymentService self, String pkgId,
        String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", pkgId);
        map.put("userUid", userUid);
        return (PaymentInformation) sqlMapClientTemplate.queryForObject("Payment.getPaymentInfoByApkAndUserUid", map);
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出已成功付款資訊，取出ID,STATUS,PAYMENT_TYPE資訊
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PaymentInformation getSuccessPaidInfo(PaymentService self, String userUid, String appId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        map.put("appId", appId);
        return (PaymentInformation) sqlMapClientTemplate.queryForObject("Payment.getSuccessPaidInfo", map);
    }

    /**
     * 取出已成功付款資訊(假--測試用)，取出ID,STATUS,PAYMENT_TYPE資訊
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PaymentInformation getSuccessPaidInfoByTest(PaymentService self, String userUid, String appId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        map.put("appId", appId);
        return (PaymentInformation) sqlMapClientTemplate.queryForObject("Payment.getSuccessPaidInfoByTest", map);
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出付款金額 (計次)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getOnetimeAmount(PaymentService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("Payment.getOnetimeAmount", map);
        return amount != null ? amount.intValue() : 0;
    }

    /**
     * 取出付款金額 (計次)(假--測試用)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getOnetimeAmountByTestList(PaymentService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("Payment.getOnetimeAmountByTestList", map);
        return amount != null ? amount.intValue() : 0;
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出付款金額 (信用卡)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getCCAmount(PaymentService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("Payment.getCCAmount", map);
        return amount != null ? amount.intValue() : 0;
    }

    /**
     * 取出付款金額 (信用卡)(假--測試用)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getCCAmountByTestList(PaymentService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("Payment.getCCAmountByTestList", map);
        return amount != null ? amount.intValue() : 0;
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出帳單付款金額 (帳單)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getBillAmount(PaymentService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("Payment.getBillAmount", map);
        return amount != null ? amount.intValue() : 0;
    }

    /**
     * 取出帳單付款金額 (帳單)(假--CP測試用)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getBillAmountByTestList(PaymentService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("Payment.getBillAmountByTestList", map);
        return amount != null ? amount.intValue() : 0;
    }

    // ------------------------------------------------------------------------------------

    /**
     * 更新IpayUserPaymentLog
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIpayUserPaymentLog(PaymentService self, PaymentInformation paymentInformation) {
        sqlMapClientTemplate.update("Payment.updateIpayUserPaymentLog", paymentInformation);
    }

    /**
     * 更新IpayUserPaymentLog，不包含Status
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIpayUserPaymentLogNoStatus(PaymentService self, PaymentInformation paymentInformation) {
        sqlMapClientTemplate.update("Payment.updateIpayUserPaymentLogNoStatus", paymentInformation);
    }

    /**
     * 更新成功的IpayUserPaymentLog
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSuccessIpayUserPaymentLog(PaymentService self, PaymentInformation paymentInformation) {
        sqlMapClientTemplate.update("Payment.updateSuccessIpayUserPaymentLog", paymentInformation);
    }
    /**
     * 更新成功的IpayUserPaymentLog by orderNo
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSuccessIpayUserPaymentLogByOrderNo(PaymentService self, PaymentInformation paymentInformation) {
        sqlMapClientTemplate.update("Payment.updateSuccessIpayUserPaymentLogByOrderNo", paymentInformation);
    }

    // ------------------------------------------------------------------------------------

    /**
     * update IPAY_USER_PAYMENT_LOG 的 STATUS 設為失效
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFailurePaidLog(PaymentService self, PaymentInformation paymentInformation) {
        sqlMapClientTemplate.update("Payment.updateFailurePaidLog", paymentInformation);
    }

    /**
     * update IPAY_USER_PAYMENT_LOG 的 STATUS 設為失效，(假--測試用)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFailurePaidLogByTest(PaymentService self, PaymentInformation paymentInformation) {
        sqlMapClientTemplate.update("Payment.updateFailurePaidLogByTest", paymentInformation);
    }

    // ------------------------------------------------------------------------------------

    /**
     * update IPAY_USER_PAYMENT_LOG 的 LAST_VERSION_ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastVersionId(PaymentService self, String appId, String userUid, String priceType,
        int lastVersionId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("priceType", priceType);
        map.put("lastVersionId", Integer.valueOf(lastVersionId));
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("Payment.updateLastVersionId", map);
    }

    /**
     * update IPAY_USER_PAYMENT_LOG 的 LAST_VERSION_ID，(假--測試用)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastVersionIdByTest(PaymentService self, String appId, String userUid,
        String priceType, int lastVersionId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("priceType", priceType);
        map.put("lastVersionId", Integer.valueOf(lastVersionId));
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("Payment.updateLastVersionIdByTest", map);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public PaymentInformation getOrderByOrderNo(String orderNo){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("orderNo", orderNo);
    	return (PaymentInformation) sqlMapClientTemplate.queryForObject("Payment.getOrderByOrderNo", map);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public PaymentInformation getOrderByOrderId(Long orderId){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("orderId", orderId);
    	return (PaymentInformation) sqlMapClientTemplate.queryForObject("Payment.getOrderByOrderId", map);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean hasUnapprovedOrderRefundHistoryByOrderId(Long orderId){
    	boolean has=true;
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("orderId", orderId);
    	Long total= (Long) sqlMapClientTemplate.queryForObject("Payment.hasUnapprovedOrderRefundHistoryByOrderId", map);
    	if(total==0) has=false;
    	return has;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderRefundHistory(OrderRefundHistory refund){
    	sqlMapClientTemplate.insert("Payment.insertOrderRefundHistory", refund);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIpayUserPaymentLogByKeySelective(PaymentInformation order){
    	sqlMapClientTemplate.update("Payment.updateByKey", order);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public ServiceResult<OrderInfo> getOrderStatus(String token,BigDecimal storeId,String pkg){
    	Prosumer users = authenticationService.getProsumerByAccount(token);
    	if(users!=null){
            // 取出 Appliction，檢查是否有Apk, 條件為APP_ID
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", pkg);
            map.put("storeId", storeId);
            Application apkInfo = (Application) sqlMapClientTemplate.queryForObject("Application.getApplicationByAppId", map);
            if (apkInfo == null)
                return new ServiceResult<OrderInfo>(GetOrderStatus.INVALID_PKG);
            // url前加上hostPath, set icon and screen shots url
            String hostPath = constantService.getHostPath();
            if (StringUtils.isNotBlank(apkInfo.getIcon()) && apkInfo.getIcon().charAt(0) == '/')
            	apkInfo.setIcon(hostPath + apkInfo.getIcon());

            Map<String, Object> order_map = new HashMap<String, Object>();
            order_map.put("appId", pkg);
            order_map.put("userUid", users.getUserUid());
            order_map.put("storeId", storeId);
            //取得是否購買
            PaymentInformation order = (PaymentInformation) sqlMapClientTemplate.queryForObject("Application.getUserPaymentIsOrdered", order_map);
            //the order_id is exists  
        	if(order!=null){
        		OrderInfo orderinfo=new OrderInfo();
        		orderinfo.setApp(apkInfo);
        		orderinfo.setPayInfo(order);
        		orderinfo.setUser(users);
        		return new ServiceResult<OrderInfo>(CommonCode.SUCCESS,orderinfo);
        	}else{
        		return new ServiceResult<OrderInfo>(GetOrderStatus.INVALID_ORDER);
        	}
    	}else{
    		return new ServiceResult<OrderInfo>(CommonCode.INVALID_TOKEN);
    	}
    }
    
    private boolean checkSignForUnionPay(PaymentInformation pinfo, String merchantOrderDesc, SelectPaymentForm selectPaymentForm){
		//使用银联付费时，需将订单资讯送交银联前置进行验签
    	String merchantOrderId = pinfo.getOrderNo();
    	String merchantOrderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(pinfo.getPayTime());
    	String merchantOrderAmt = String.valueOf(Math.round(pinfo.getAmount()*100D));
		String originalsign7 = XmlDefinition.CreateOriginalSign7(
				selectPaymentForm.getMerchantName(), 
				selectPaymentForm.getMerchantId(), 
				merchantOrderId,
				merchantOrderTime, 
				merchantOrderAmt, 
				merchantOrderDesc,
				selectPaymentForm.getTransTimeout());
		logger.info("original string:" + originalsign7);
		SignBy getSign = new SignBy();
		String xmlSign7;
		//读取私钥档案
		try {
			InputStream PrivateSign = getClass().getClassLoader().getResourceAsStream(selectPaymentForm.getPrivateKeyFileName());
			xmlSign7 = getSign.createSign(originalsign7, selectPaymentForm.getPrivateKeyAlias(), selectPaymentForm.getPrivateKeyPassword(), PrivateSign);
			logger.info("signed string:" + xmlSign7);
		} catch (Exception e) {
			logger.debug("获取私钥加密过程异常，请检查私钥档案");
			e.printStackTrace();
			return false;
		}
		// 订单提交的报文
		String Submit = XmlDefinition.SubmitOrder(selectPaymentForm.getIsTest().equals("true"), selectPaymentForm.getMerchantName(),
				selectPaymentForm.getMerchantId(), merchantOrderId, merchantOrderTime,
				merchantOrderAmt, merchantOrderDesc, selectPaymentForm.getTransTimeout(),
				selectPaymentForm.getBackEndUrl(), xmlSign7, selectPaymentForm.getMerchantPublicCer());
		XmlHttpConnection Xml_SetHttp = new XmlHttpConnection(selectPaymentForm.getCheckSignUrl(), 6000);
		if(Xml_SetHttp.sendMsg(Submit) && // 提交报文成功
			Xml_SetHttp.getReMeg()!=null && // 成功收到验签结果报文
			Xml_SetHttp.getReMeg().indexOf("<respCode>0000</respCode>")!=-1){//验签成功
			return true;
		}else{
			return false;//验签失败
		}
		
	}
}