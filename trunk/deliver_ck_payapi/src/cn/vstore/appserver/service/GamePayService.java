package cn.vstore.appserver.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import cn.vstore.appserver.api.support.paytype.UnionPay;
import cn.vstore.appserver.form.payment.SelectGamePayForm;
import cn.vstore.appserver.model.GameOrderInfo;
import cn.vstore.appserver.model.GamePayInformation;
import cn.vstore.appserver.model.OrderRefundHistory;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.Constants;
import cn.vstore.core.model.vo.OrderPayInfoLog;
import cn.vstore.core.model.vo.StorePayeeInfo;
import cn.vstore.core.model.vo.StorePayeeInfoExample;
import cn.vstore.core.model.vo.StorePayeeInfoExample.Criteria;
/**
 * @version $Id$
 */
@Service("GamePayService")
public class GamePayService {

    private static final Logger logger = LoggerFactory.getLogger(GamePayService.class);

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
    
    /**
     * 当乐订单
     * @param pkg
     * @param selectGamePayForm
     * @param storeId
     * @param payeeInfo
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ServiceResult<GameOrderInfo> getDLNewOrder(SelectGamePayForm selectGamePayForm, BigDecimal storeId,Object payeeInfo){
    		int paytype=0;
    		if(payeeInfo!=null) 
    			paytype = PayeeInfoFactory.getPaymentType(payeeInfo);

            Map<String, Object> order_map = new HashMap<String, Object>();
            	order_map.put("appId", selectGamePayForm.getPackageName());
            	order_map.put("eimi", selectGamePayForm.getImei());
//            	order_map.put("userUid", users.getUserUid());
            	order_map.put("storeId", storeId);
            
            	logger.info("appId : " + order_map.get("appId") +" "+ (order_map.get("appId")==selectGamePayForm.getPackageName()));
            	logger.info("eimi : " + order_map.get("eimi") +" "+ (order_map.get("eimi")==selectGamePayForm.getImei()));
//            	logger.info("userUid : " + order_map.get("userUid") +" "+ (order_map.get("userUid")==users.getUserUid()));
            	logger.info("storeId : " + order_map.get("storeId") +" "+ (order_map.get("storeId")==storeId));
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
	            	}
	            	if(find){
	                	List<StorePayeeInfo> spilist=sqlMapClientTemplate.queryForList("store_payee_info.ibatorgenerated_selectByExample",spie);
	            		if(spilist!=null&&spilist.size()>0){
	            			payeeStoreId=new BigDecimal(spilist.get(0).getStoreId());
	            		}
	            	}
            	}
            	Date payTime = new Date();
            	GamePayInformation pinfo = new GamePayInformation();
            	//新增加字段
            	pinfo.setUserID(selectGamePayForm.getUserID());
            	pinfo.setPropsId(selectGamePayForm.getPropsId());
            	pinfo.setPropsType(selectGamePayForm.getPropsType());
            	pinfo.setNum(selectGamePayForm.getNum());
            	pinfo.setSnum(selectGamePayForm.getSnum());
            	pinfo.setPrice(selectGamePayForm.getPrice());
            	
				pinfo.setPayTime(payTime);
				pinfo.setPkg(selectGamePayForm.getPackageName());
				pinfo.setImei(selectGamePayForm.getImei());
				pinfo.setPaymentType(4);//当乐支付渠道
				pinfo.setToken(selectGamePayForm.getToken());
				pinfo.setMyPriceType("0");
				pinfo.setStatus(Constants.STATUS_PAYMENT_LOG);//STATUS_PAYMENT_LOG=0  沒有付款
				pinfo.setStoreId(storeId);
				pinfo.setRightStartDate(payTime);
				pinfo.setPayeeStoreId(payeeStoreId);
				
				//当乐
				pinfo.setAmount( Double.valueOf(selectGamePayForm.getAmount()));
				pinfo.setDenomination(selectGamePayForm.getDenomination());//卡面额
				pinfo.setProductId(selectGamePayForm.getProductId());//商户编号
				pinfo.setCompanyId(selectGamePayForm.getCompanyId());//游戏编号
				pinfo.setChannelId(selectGamePayForm.getChannelId());//服务器编号
				pinfo.setCardNo(selectGamePayForm.getCardNo());//支付卡号
				pinfo.setCardPwd(selectGamePayForm.getCardPwd());//支付卡密码
				pinfo.setOrderNo(selectGamePayForm.getOrderNo());//订单号
				pinfo.setOrderType(selectGamePayForm.getOrderType());//卡号类型
				pinfo.setMerPriv(selectGamePayForm.getMerPriv());//保留字段
				pinfo.setVervifystring(selectGamePayForm.getVerStr());//加密字符串
				pinfo.setOrderTime(selectGamePayForm.getOrderTime());//当乐需要的字符串日期
				pinfo.setUserIpAddr(selectGamePayForm.getUserIpAddr());//ip地址
				pinfo.setPayUserId(selectGamePayForm.getPayUserId());//固定值189用户填1，非189用户填0
				
				if(!StringUtils.isBlank(selectGamePayForm.getStoreOid())){
					pinfo.setStoreOid(selectGamePayForm.getStoreOid());
				}
				sqlMapClientTemplate.insert("GamePayment.insertDLOrder", pinfo);
				logger.debug("insert order id="+pinfo.getId()+" amount="+pinfo.getAmount());

				GameOrderInfo orderinfo=new GameOrderInfo();
				orderinfo.setPayInfo(pinfo);
				return new ServiceResult<GameOrderInfo>(CommonCode.SUCCESS,orderinfo);
    }

    /**
     * 飞流订单
     * @param pkg
     * @param selectGamePayForm
     * @param storeId
     * @param payeeInfo
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ServiceResult<GameOrderInfo> getFLNewOrder(String pkg,SelectGamePayForm selectGamePayForm, BigDecimal storeId,Object payeeInfo){
    	//用户验证，暂时去掉
//    	Prosumer users = authenticationService.getProsumerByAccount(selectGamePayForm.getToken());
//    	if(users!=null){
    		int paytype=0;
    		if(payeeInfo!=null) 
    			paytype = PayeeInfoFactory.getPaymentType(payeeInfo);

            Map<String, Object> order_map = new HashMap<String, Object>();
            	order_map.put("appId", pkg);
            	order_map.put("eimi", selectGamePayForm.getImei());
//            	order_map.put("userUid", users.getUserUid());
            	order_map.put("storeId", storeId);
            
            	logger.info("appId : " + order_map.get("appId") +" "+ (order_map.get("appId")==pkg));
            	logger.info("eimi : " + order_map.get("eimi") +" "+ (order_map.get("eimi")==selectGamePayForm.getImei()));
//            	logger.info("userUid : " + order_map.get("userUid") +" "+ (order_map.get("userUid")==users.getUserUid()));
            	logger.info("storeId : " + order_map.get("storeId") +" "+ (order_map.get("storeId")==storeId));
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
	            	}
	            	if(find){
	                	List<StorePayeeInfo> spilist=sqlMapClientTemplate.queryForList("store_payee_info.ibatorgenerated_selectByExample",spie);
	            		if(spilist!=null&&spilist.size()>0){
	            			payeeStoreId=new BigDecimal(spilist.get(0).getStoreId());
	            		}
	            	}
            	}
            	Date payTime = new Date();
            	GamePayInformation pinfo=new GamePayInformation();
            	//新增加字段
            	pinfo.setUserID(selectGamePayForm.getUserID());
            	pinfo.setPropsId(selectGamePayForm.getPropsId());
            	pinfo.setPropsType(selectGamePayForm.getPropsType());
            	pinfo.setNum(selectGamePayForm.getNum());
            	pinfo.setSnum(selectGamePayForm.getSnum());
            	pinfo.setPrice(selectGamePayForm.getPrice());
            	
				pinfo.setPayTime(payTime);
				pinfo.setPkg(selectGamePayForm.getPackageName());
				pinfo.setImei(selectGamePayForm.getImei());
				pinfo.setPaymentType(3);//飞流支付渠道
				pinfo.setToken(selectGamePayForm.getToken());
				pinfo.setMyPriceType("0");
				pinfo.setStatus(Constants.STATUS_PAYMENT_LOG);//STATUS_PAYMENT_LOG=0  沒有付款
				pinfo.setStoreId(storeId);
				pinfo.setRightStartDate(payTime);
				pinfo.setPayeeStoreId(payeeStoreId);
				
				//飞流
				pinfo.setAmount( Double.valueOf(selectGamePayForm.getAmount()));
				pinfo.setDenomination(selectGamePayForm.getDenomination());//卡面额
				pinfo.setProductId(selectGamePayForm.getProductId());//产品ID
				pinfo.setCompanyId(selectGamePayForm.getCompanyId());//公司ID
				pinfo.setChannelId(selectGamePayForm.getChannelId());//渠道ID
				pinfo.setCardNo(selectGamePayForm.getCardNo());//支付卡号
				pinfo.setCardPwd(selectGamePayForm.getCardPwd());//支付卡密码
				pinfo.setOrderNo(selectGamePayForm.getOrderNo());//订单号
				pinfo.setOrderType(selectGamePayForm.getOrderType());//卡号类型
				pinfo.setMerPriv(selectGamePayForm.getMerPriv());//保留字段
				pinfo.setVervifystring(selectGamePayForm.getVerStr());
				
				if(!StringUtils.isBlank(selectGamePayForm.getStoreOid())){
					pinfo.setStoreOid(selectGamePayForm.getStoreOid());
				}
				sqlMapClientTemplate.insert("GamePayment.insertFLOrder", pinfo);
				logger.debug("insert order id="+pinfo.getId()+" amount="+pinfo.getAmount());

				GameOrderInfo orderinfo=new GameOrderInfo();
				orderinfo.setPayInfo(pinfo);
				return new ServiceResult<GameOrderInfo>(CommonCode.SUCCESS,orderinfo);
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public ServiceResult<GameOrderInfo> getNewOrder(String pkg,SelectGamePayForm selectGamePayForm, BigDecimal storeId,Object payeeInfo){
    	//用户验证，暂时去掉
//    	Prosumer users = authenticationService.getProsumerByAccount(selectGamePayForm.getToken());
//    	if(users!=null){
    		int paytype=0;
    		if(payeeInfo!=null) 
    			paytype = PayeeInfoFactory.getPaymentType(payeeInfo);
/*            // 取出 Appliction，檢查是否有Apk, 條件為APP_ID
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
*/
            Map<String, Object> order_map = new HashMap<String, Object>();
            order_map.put("appId", pkg);
            order_map.put("eimi", selectGamePayForm.getImei());
//            order_map.put("userUid", users.getUserUid());
            order_map.put("storeId", storeId);
            
            logger.info("appId : " + order_map.get("appId") +" "+ (order_map.get("appId")==pkg));
            logger.info("eimi : " + order_map.get("eimi") +" "+ (order_map.get("eimi")==selectGamePayForm.getImei()));
//            logger.info("userUid : " + order_map.get("userUid") +" "+ (order_map.get("userUid")==users.getUserUid()));
            logger.info("storeId : " + order_map.get("storeId") +" "+ (order_map.get("storeId")==storeId));
//            取得是否購買
//            GamePayInformation order = (GamePayInformation) sqlMapClientTemplate.queryForObject("Application.getUserPaymentIsOrdered", order_map);
            
//            logger.info("order 等於 " + (order==null));
//            logger.info("IPAY_TOKEN : " + order.getId());
            //the order_id is exists  
//            if(order==null || 0==order.getStatus()){
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
	            	}
	            	if(find){
	                	List<StorePayeeInfo> spilist=sqlMapClientTemplate.queryForList("store_payee_info.ibatorgenerated_selectByExample",spie);
	            		if(spilist!=null&&spilist.size()>0){
	            			payeeStoreId=new BigDecimal(spilist.get(0).getStoreId());
	            		}
	            	}
            	}
            	Date payTime = new Date();
            	GamePayInformation pinfo=new GamePayInformation();
            	//新增加字段
            	pinfo.setUserID(selectGamePayForm.getUserID());
            	pinfo.setPropsId(selectGamePayForm.getPropsId());
            	pinfo.setPropsType(selectGamePayForm.getPropsType());
            	pinfo.setNum(selectGamePayForm.getNum());
            	pinfo.setSnum(selectGamePayForm.getSnum());
            	pinfo.setPrice(selectGamePayForm.getPrice());
            	
				pinfo.setPayTime(payTime);
				pinfo.setPkg(selectGamePayForm.getPackageName());
				pinfo.setImei(selectGamePayForm.getImei());
//				pinfo.setUserUid(users.getUserUid());
				pinfo.setPaymentType(paytype);
				pinfo.setToken(selectGamePayForm.getToken());
				pinfo.setMyPriceType("0");
//				pinfo.setAmount(apkInfo.getPrice());
//				pinfo.setVersionId(apkInfo.getVersion());
				pinfo.setStatus(Constants.STATUS_PAYMENT_LOG);//STATUS_PAYMENT_LOG=0  沒有付款
				pinfo.setStoreId(storeId);
//				pinfo.setUserId(users.getUserId());
				pinfo.setRightStartDate(payTime);
/*				if((Constants.PRICE_TYPE_MONTHLY+"").equals(apkInfo.getPriceType())){
					Calendar cal=Calendar.getInstance();
					int amount=1;
					if(apkInfo.getMonthlyCycle()>=1){
						amount=apkInfo.getMonthlyCycle();
					}
					//每月以30日計算
					cal.add(Calendar.DAY_OF_MONTH, amount*30);
					pinfo.setRightEndDate(cal.getTime());
				}
				pinfo.setMonthlyCycle(apkInfo.getMonthlyCycle());*/
				pinfo.setPayeeStoreId(payeeStoreId);
				if(!StringUtils.isBlank(selectGamePayForm.getStoreOid())){
					pinfo.setStoreOid(selectGamePayForm.getStoreOid());
				}
				sqlMapClientTemplate.insert("GamePayment.insertOrder", pinfo);
				logger.debug("insert order id="+pinfo.getId()+" amount="+pinfo.getAmount());
				String orderId="00000000"+pinfo.getId();
//				String orderNo=storeId+new SimpleDateFormat("yyyyMMdd").format(payTime)+orderId.substring(orderId.length()-8);
				String orderNo=new SimpleDateFormat("yyyyMMdd").format(payTime)+orderId.substring(orderId.length()-8);//去掉storeId
				pinfo.setOrderNo(orderNo);
				sqlMapClientTemplate.update("GamePayment.updateIpayUserPaymentLogOrderNoById",pinfo);
				logger.debug("update order id="+pinfo.getId()+" orderNo="+pinfo.getOrderNo());
				//如果有payeeInfo，保存log
				if (!StringUtils.isBlank(selectGamePayForm.getPayeeInfo())){
					OrderPayInfoLog pl=new OrderPayInfoLog();
					pl.setOrderId(pinfo.getId());
					pl.setPayeeInfo(selectGamePayForm.getPayeeInfo());
					sqlMapClientTemplate.insert("order_gamepay_info_log.ibatorgenerated_insert", pl);
				}
				GameOrderInfo orderinfo=new GameOrderInfo();
//				orderinfo.setApp(apkInfo);
				orderinfo.setPayInfo(pinfo);
//				orderinfo.setUser(users);
//				if(paytype==2){
//					if(!checkSignForUnionPay(pinfo, apkInfo.getProvider(), selectGamePayForm)){
//						logger.debug("Fail to check sign.");
//		                return new ServiceResult<OrderInfo>(CommonCode.SERVICE_FAIL);
//					}
//				}
				return new ServiceResult<GameOrderInfo>(CommonCode.SUCCESS,orderinfo);
            	
//            }else{
//            	
//            	if(order.getStatus()==1 || order.getStatus()==2 || order.getStatus()==5){
//             	  OrderInfo orderinfo=new OrderInfo();
//                  orderinfo.setApp(apkInfo);
//                  orderinfo.setPayInfo(order);
//                  orderinfo.setUser(users);
//                  return new ServiceResult<OrderInfo>(CommonCode.SUCCESS,orderinfo);
//            	}
            	
            	 
            	
//            }
           
           
//    	}
//    	return new ServiceResult<OrderInfo>(CommonCode.SERVICE_FAIL);
    }

    /**
     * 取出付款資訊，條件apk/userUid
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public GamePayInformation getPaymentInfoByApkAndUserUid(GamePayService self, String pkgId,
        String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", pkgId);
        map.put("userUid", userUid);
        return (GamePayInformation) sqlMapClientTemplate.queryForObject("GamePayment.getPaymentInfoByApkAndUserUid", map);
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出已成功付款資訊，取出ID,STATUS,PAYMENT_TYPE資訊
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public GamePayInformation getSuccessPaidInfo(GamePayService self, String userUid, String appId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        map.put("appId", appId);
        return (GamePayInformation) sqlMapClientTemplate.queryForObject("GamePayment.getSuccessPaidInfo", map);
    }

    /**
     * 取出已成功付款資訊(假--測試用)，取出ID,STATUS,PAYMENT_TYPE資訊
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public GamePayInformation getSuccessPaidInfoByTest(GamePayService self, String userUid, String appId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        map.put("appId", appId);
        return (GamePayInformation) sqlMapClientTemplate.queryForObject("GamePayment.getSuccessPaidInfoByTest", map);
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出付款金額 (計次)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getOnetimeAmount(GamePayService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("GamePayment.getOnetimeAmount", map);
        return amount != null ? amount.intValue() : 0;
    }

    /**
     * 取出付款金額 (計次)(假--測試用)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getOnetimeAmountByTestList(GamePayService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("GamePayment.getOnetimeAmountByTestList", map);
        return amount != null ? amount.intValue() : 0;
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出付款金額 (信用卡)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getCCAmount(GamePayService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("GamePayment.getCCAmount", map);
        return amount != null ? amount.intValue() : 0;
    }

    /**
     * 取出付款金額 (信用卡)(假--測試用)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getCCAmountByTestList(GamePayService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("GamePayment.getCCAmountByTestList", map);
        return amount != null ? amount.intValue() : 0;
    }

    // ------------------------------------------------------------------------------------

    /**
     * 取出帳單付款金額 (帳單)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getBillAmount(GamePayService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("GamePayment.getBillAmount", map);
        return amount != null ? amount.intValue() : 0;
    }

    /**
     * 取出帳單付款金額 (帳單)(假--CP測試用)
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public int getBillAmountByTestList(GamePayService self, String userUid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        Integer amount = (Integer) sqlMapClientTemplate.queryForObject("GamePayment.getBillAmountByTestList", map);
        return amount != null ? amount.intValue() : 0;
    }

    // ------------------------------------------------------------------------------------

    /**
     * 更新IpayUserPaymentLog
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIpayUserPaymentLog(GamePayService self, GamePayInformation paymentInformation) {
        sqlMapClientTemplate.update("GamePayment.updateIpayUserPaymentLog", paymentInformation);
    }

    /**
     * 更新IpayUserPaymentLog，不包含Status
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIpayUserPaymentLogNoStatus(GamePayService self, GamePayInformation paymentInformation) {
        sqlMapClientTemplate.update("GamePayment.updateIpayUserPaymentLogNoStatus", paymentInformation);
    }

    /**
     * 更新成功的IpayUserPaymentLog
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSuccessIpayUserPaymentLog(GamePayService self, GamePayInformation paymentInformation) {
        sqlMapClientTemplate.update("GamePayment.updateSuccessIpayUserPaymentLog", paymentInformation);
    }
    /**
     * 更新成功的IpayUserPaymentLog by orderNo
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSuccessIpayUserPaymentLogByOrderNo(GamePayService self, GamePayInformation paymentInformation) {
        sqlMapClientTemplate.update("GamePayment.updateSuccessIpayUserPaymentLogByOrderNo", paymentInformation);
    }

    // ------------------------------------------------------------------------------------

    /**
     * update IPAY_USER_PAYMENT_LOG 的 STATUS 設為失效
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFailurePaidLog(GamePayService self, GamePayInformation paymentInformation) {
        sqlMapClientTemplate.update("GamePayment.updateFailurePaidLog", paymentInformation);
    }

    /**
     * update IPAY_USER_PAYMENT_LOG 的 STATUS 設為失效，(假--測試用)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFailurePaidLogByTest(GamePayService self, GamePayInformation paymentInformation) {
        sqlMapClientTemplate.update("GamePayment.updateFailurePaidLogByTest", paymentInformation);
    }

    // ------------------------------------------------------------------------------------

    /**
     * update IPAY_USER_PAYMENT_LOG 的 LAST_VERSION_ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastVersionId(GamePayService self, String appId, String userUid, String priceType,
        int lastVersionId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("priceType", priceType);
        map.put("lastVersionId", Integer.valueOf(lastVersionId));
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("GamePayment.updateLastVersionId", map);
    }

    /**
     * update IPAY_USER_PAYMENT_LOG 的 LAST_VERSION_ID，(假--測試用)
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastVersionIdByTest(GamePayService self, String appId, String userUid,
        String priceType, int lastVersionId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("priceType", priceType);
        map.put("lastVersionId", Integer.valueOf(lastVersionId));
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("GamePayment.updateLastVersionIdByTest", map);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public GamePayInformation getOrderByOrderNo(String orderNo){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("orderNo", orderNo);
    	return (GamePayInformation) sqlMapClientTemplate.queryForObject("GamePayment.getOrderByOrderNo", map);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public GamePayInformation getOrderByOrderId(Long orderId){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("orderId", orderId);
    	return (GamePayInformation) sqlMapClientTemplate.queryForObject("GamePayment.getOrderByOrderId", map);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean hasUnapprovedOrderRefundHistoryByOrderId(Long orderId){
    	boolean has=true;
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("orderId", orderId);
    	Long total= (Long) sqlMapClientTemplate.queryForObject("GamePayment.hasUnapprovedOrderRefundHistoryByOrderId", map);
    	if(total==0) has=false;
    	return has;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrderRefundHistory(OrderRefundHistory refund){
    	sqlMapClientTemplate.insert("GamePayment.insertOrderRefundHistory", refund);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIpayUserPaymentLogByKeySelective(GamePayInformation order){
    	sqlMapClientTemplate.update("GamePayment.updateByKey", order);
    }
    
    private boolean checkSignForUnionPay(GamePayInformation pinfo, String merchantOrderDesc, SelectGamePayForm selectGamePayForm){
		//使用银联付费时，需将订单资讯送交银联前置进行验签
    	String merchantOrderId = pinfo.getOrderNo();
    	String merchantOrderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(pinfo.getPayTime());
    	String merchantOrderAmt = String.valueOf(Math.round(pinfo.getAmount()*100D));
		String originalsign7 = XmlDefinition.CreateOriginalSign7(
				selectGamePayForm.getMerchantName(), 
				selectGamePayForm.getMerchantId(), 
				merchantOrderId,
				merchantOrderTime, 
				merchantOrderAmt, 
				merchantOrderDesc,
				selectGamePayForm.getTransTimeout());
		logger.info("original string:" + originalsign7);
		SignBy getSign = new SignBy();
		String xmlSign7;
		//读取私钥档案
		try {
			InputStream PrivateSign = getClass().getClassLoader().getResourceAsStream(selectGamePayForm.getPrivateKeyFileName());
			xmlSign7 = getSign.createSign(originalsign7, selectGamePayForm.getPrivateKeyAlias(), selectGamePayForm.getPrivateKeyPassword(), PrivateSign);
			logger.info("signed string:" + xmlSign7);
		} catch (Exception e) {
			logger.debug("获取私钥加密过程异常，请检查私钥档案");
			e.printStackTrace();
			return false;
		}
		// 订单提交的报文
		String Submit = XmlDefinition.SubmitOrder(selectGamePayForm.getIsTest().equals("true"), selectGamePayForm.getMerchantName(),
				selectGamePayForm.getMerchantId(), merchantOrderId, merchantOrderTime,
				merchantOrderAmt, merchantOrderDesc, selectGamePayForm.getTransTimeout(),
				selectGamePayForm.getBackEndUrl(), xmlSign7, selectGamePayForm.getMerchantPublicCer());
		XmlHttpConnection Xml_SetHttp = new XmlHttpConnection(selectGamePayForm.getCheckSignUrl(), 6000);
		if(Xml_SetHttp.sendMsg(Submit) && // 提交报文成功
			Xml_SetHttp.getReMeg()!=null && // 成功收到验签结果报文
			Xml_SetHttp.getReMeg().indexOf("<respCode>0000</respCode>")!=-1){//验签成功
			return true;
		}else{
			return false;//验签失败
		}
	}
    
    // ====================================== update feiliu order status ===================================//
    
    /**
     * 更新飞流付费成功后的订单信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateFLorderStatusByOrderId(GamePayService self, int status, String florderId, String cardStatus, String orderId) {
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("status", status);
    	hm.put("orderId", orderId);
    	hm.put("florderId", florderId);
    	hm.put("cardStatus", cardStatus);
        sqlMapClientTemplate.update("GamePayment.updateFLOrderNoById", hm);
    }
    
    /**
     * 更新当乐付费成功后的订单信息
     * @param self
     * @param status
     * @param florderId
     * @param cardStatus
     * @param orderId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateDLorderStatusByOrderId(int status, String dlorderId, String merpriv) {
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("status", status);
    	hm.put("dlorderId", dlorderId);
    	hm.put("merpriv", merpriv);
        sqlMapClientTemplate.update("GamePayment.updateDLOrderNoById", hm);
    }
}