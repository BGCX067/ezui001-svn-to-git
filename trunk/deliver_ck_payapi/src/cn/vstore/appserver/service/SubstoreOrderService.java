package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.form.SubstoreOrderForm;
import cn.vstore.appserver.model.Application;
import cn.vstore.appserver.model.OrderRefundHistory;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.SubstoreCancelOrderCode;
import cn.vstore.appserver.service.ResultCode.SubstoreOkOrderCode;
import cn.vstore.appserver.util.Constants;
import cn.vstore.appserver.util.NextMonthEndDay;
import cn.vstore.appserver.util.StringUtils;

@Service("SubstoreOrderService")
public class SubstoreOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubstoreOrderService.class);
	
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private PaymentService paymentService;
	
	/**
	 * 34.	SubStore將交易成功的訂單傳給CK(36) substore自己的付款方式適用
	 * @param userId
	 * @param pkg
	 * @param storeId
	 * @param substoreOrderForm
	 * @param storeOid
	 * @return
	 */
	public ServiceResult<PaymentInformation> orderOk(String userId, String pkg, BigDecimal storeId, SubstoreOrderForm substoreOrderForm, String storeOid){
		logger.info("paremeter : userId={}, pkg={}, storeId={}, storeOid={}", new Object[]{userId, pkg, storeId, storeOid});
		try{
			Prosumer users = authenticationService.getProsumerByUserId(userId);
			if(users!=null){
				logger.info("users = " + users.getUserId() + " " + users.getUserName() + " userUid = " + users.getUserUid());
	
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userUid", users.getUserUid());
				map.put("appId", pkg);
				map.put("storeId", storeId);
				Application apkInfo = (Application) sqlMapClientTemplate.queryForObject("Application.getApplicationByAppId", map);
				if(apkInfo == null){
					return new ServiceResult<PaymentInformation>(SubstoreOkOrderCode.INVALID_PKG);
				}
				//检查此订单是否已经存在
				map = new HashMap<String, Object>();
				map.put("storeId", storeId);
				map.put("storeOid", storeOid);
				List<PaymentInformation> count=(List<PaymentInformation>)sqlMapClientTemplate.queryForList("Payment.getOrderBySubStoreOrderNo", map);
				if(count!=null&&count.size()>0){
					return new ServiceResult<PaymentInformation>(SubstoreOkOrderCode.EXISTED_ORDER);
				}
				PaymentInformation paymentInfo = new PaymentInformation();
				paymentInfo.setStatus(2);
				paymentInfo.setPkg(pkg);
				paymentInfo.setImei(substoreOrderForm.getImei());
				paymentInfo.setUserId(users.getUserId());
				paymentInfo.setUserUid(users.getUserUid());
				paymentInfo.setAmount(apkInfo.getPrice());
				paymentInfo.setMyPriceType(apkInfo.getPriceType());
				paymentInfo.setVersionId(apkInfo.getVersion());
				paymentInfo.setStoreId(storeId);
				paymentInfo.setStoreOid(storeOid);
				paymentInfo.setToken(substoreOrderForm.getToken());
				paymentInfo.setRightStartDate(new Date());
				paymentInfo.setPayeeStoreId(storeId);
				if((Constants.PRICE_TYPE_MONTHLY+"").equals(apkInfo.getPriceType())){
					Calendar cal=Calendar.getInstance();
					int amount=1;
					if(apkInfo.getMonthlyCycle()>=1){
						amount=apkInfo.getMonthlyCycle();
					}
					//每月以30日計算
					cal.add(Calendar.DAY_OF_MONTH, amount*30);
					paymentInfo.setRightEndDate(cal.getTime());
					paymentInfo.setMonthlyCycle(apkInfo.getMonthlyCycle());
				}
				//未知的付款方式
				paymentInfo.setPaymentType(0);
				logger.info("paymentInfomation status : " + paymentInfo.getStatus() + " pkg = " + paymentInfo.getPkg());
				sqlMapClientTemplate.insert("Payment.insertOrder", paymentInfo);
                String orderId="00000000"+paymentInfo.getId();
                String orderNo=storeId+new SimpleDateFormat("yyyyMMdd").format(new Date())+orderId.substring(orderId.length()-8);
                paymentInfo.setOrderNo(orderNo);
                sqlMapClientTemplate.update("Payment.updateIpayUserPaymentLogOrderNoById",paymentInfo);
                logger.debug("update order id="+paymentInfo.getId()+" orderNo="+paymentInfo.getOrderNo());
				return new ServiceResult<PaymentInformation>(CommonCode.SUCCESS, paymentInfo);
			}else{
				return new ServiceResult<PaymentInformation>(CommonCode.INVALID_TOKEN);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			return new ServiceResult<PaymentInformation>(CommonCode.SERVICE_FAIL);
		}
	}
	
	/**
	 * 35.	SubStore通知CK將交易的訂單取消(37) substore自己的付款方式適用
	 * @param storeOid
	 * @param reason
	 * @return
	 */
	public ServiceResult<String> orderCancel(String storeOid, String reason, BigDecimal storeId){
		try{
			//检查此订单是否已经存在
			Map map = new HashMap<String, Object>();
			map.put("storeId", storeId);
			map.put("storeOid", storeOid);
			List<PaymentInformation> count=(List<PaymentInformation>)sqlMapClientTemplate.queryForList("Payment.getOrderBySubStoreOrderNo", map);
			if(count==null||count.size()==0){
				return new ServiceResult<String>(SubstoreCancelOrderCode.INVALID_ORDER);
			}
			PaymentInformation order=count.get(0);
			boolean has=this.paymentService.hasUnapprovedOrderRefundHistoryByOrderId(order.getId());
			//預設到期日為明日
			Calendar yes=Calendar.getInstance();
			yes.add(Calendar.DAY_OF_MONTH, -1);
			Date newRightEnddate=yes.getTime();
			double amount=order.getAmount()*95/100;
			int refundMonth=0;
			int d=NextMonthEndDay.CompareDay(order.getRightStartDate(),new Date());
			if((Constants.PRICE_TYPE_MONTHLY+"").equals(order.getMyPriceType())){
				//是否為3日內退款
				if(d<3){
					//使用預設值
				}else{
					//计算月租的租期
					Calendar cal=Calendar.getInstance();
					if(order.getRightStartDate()!=null) cal.setTime(order.getRightStartDate());
					NextMonthEndDay n=NextMonthEndDay.nextMonthlyEndDay(cal.getTime(),false);
					newRightEnddate=n.getEndDay();
					if(n.getMonth()>0&&order.getMonthlyCycle()!=null&&order.getMonthlyCycle()>0&&order.getMonthlyCycle()>=n.getMonth()){
						refundMonth=order.getMonthlyCycle()-n.getMonth();
						amount=order.getAmount()/order.getMonthlyCycle()*refundMonth;
					}
				}
			}else{
				//是否為3日內退款
				if(d<3){
					//使用預設值
				}else{
					//超過3天不可退款
					return new ServiceResult<String>(SubstoreCancelOrderCode.CANCEL_REJECT);
				}
			}
			OrderRefundHistory refund=null;
			if(!has){
				refund=new OrderRefundHistory();
				refund.setOrderId(order.getId());
				refund.setApplyRefundDate(new Date());
				refund.setApplyRefundReason(reason);
				//預設為審核通过
				refund.setApprovedStatus(1);
				refund.setRefundAmount(amount);
				refund.setApprovedDate(new Date());
				refund.setApprovedReason(reason);
				refund.setApprovedUid(0l);
				refund.setOldRightEnddate(order.getRightEndDate());
				refund.setNewRightEnddate(newRightEnddate);
				refund.setTransferAmount(amount);
				refund.setTransferDate(new Date());
				refund.setTransferLogDate(new Date());
				refund.setRefundMonth(refundMonth);
				this.paymentService.saveOrderRefundHistory(refund);
				//寫入訂單取消記錄和原因
			}else{
				return new ServiceResult<String>(SubstoreCancelOrderCode.HAVE_REFUND_HISTORY);
			}
			if(StringUtils.isBlank(reason)){
				reason = null;
			}
			//设置为取消退款
			order.setStatus(5);
			order.setRightEndDate(newRightEnddate);
			order.setCancelTime(new Date());
			order.setOrderRefundHistoryId(new BigDecimal(refund.getId()));
			sqlMapClientTemplate.update("Payment.updateByKey", order);
			return new ServiceResult<String>(CommonCode.SUCCESS);
		} catch (Throwable e) {
			logger.info(e.getMessage(),e);
			return new ServiceResult<String>(CommonCode.SERVICE_FAIL);
		}
		
	}
	
}
