package cn.vstore.appserver.api.payment;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.form.payment.CancelOrderForm;
import cn.vstore.appserver.model.OrderRefundHistory;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.PaymentService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CancelOrderCode;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.SubstoreCancelOrderCode;
import cn.vstore.appserver.util.Constants;
import cn.vstore.appserver.util.MessageTranslator;
import cn.vstore.appserver.util.NextMonthEndDay;
@Controller
public class CancelOrderApi {
	protected static final Logger logger = LoggerFactory.getLogger(CancelOrderApi.class);
    // 回傳頁面
    public final static String RETRUN_PAGE_SUCCESS = "payment/cancelOrder";
    public final static String RETRUN_PAGE_ERROR = "error";
	@Autowired
    private PaymentService paymentService;
	@Autowired
    private MessageTranslator translator;
	@RequestMapping(value = "/api/ck/order/user/cancel", method = { RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("CancelOrderForm") CancelOrderForm cancelOrderForm,
	    		Model model,HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isBlank(cancelOrderForm.getOrderNo())) {
			model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
			logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
			return RETRUN_PAGE_ERROR;
		}
        // 驗證輸入參數
    	StoreInfo storeInfo = (StoreInfo)request.getAttribute("storeInfo");
    	if(storeInfo==null){
    		model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
    		logger.info(translator.getMessage(CommonCode.PARAMETER_ERROR), false);
    		return RETRUN_PAGE_ERROR;
    	}
		long runstarttime = System.currentTimeMillis();
		try {
			//查詢訂單是否存在
			PaymentInformation order=this.paymentService.getOrderByOrderNo(cancelOrderForm.getOrderNo());
			if(order!=null&&storeInfo.getId().equals(order.getStoreId())){
				boolean has=this.paymentService.hasUnapprovedOrderRefundHistoryByOrderId(order.getId());
				if(!has){
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
							refundMonth=order.getMonthlyCycle();
						}else{
							amount=0;
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
							model.addAttribute("ret", CancelOrderCode.CANCEL_REJECT.getCompleteCode());
							return RETRUN_PAGE_ERROR;
						}
					}
					//寫入訂單取消記錄和原因
					OrderRefundHistory refund=new OrderRefundHistory();
					refund.setOrderId(order.getId());
					refund.setApplyRefundDate(new Date());
					refund.setApplyRefundReason(cancelOrderForm.getReason());
					//預設為等待審核
					refund.setApprovedStatus(0);
					refund.setRefundAmount(amount);
					refund.setOldRightEnddate(order.getRightEndDate());
					refund.setNewRightEnddate(newRightEnddate);
					refund.setRefundMonth(refundMonth);
					this.paymentService.saveOrderRefundHistory(refund);
					//更新订单，将退款记录的id写入订单
					order.setOrderRefundHistoryId(new BigDecimal(refund.getId()));
					this.paymentService.updateIpayUserPaymentLogByKeySelective(order);
		            model.addAttribute("ret", CommonCode.SUCCESS.getCompleteCode());
					return RETRUN_PAGE_SUCCESS;
				}else{
		            model.addAttribute("ret", CancelOrderCode.HAVE_REFUND_HISTORY.getCompleteCode());
		            return RETRUN_PAGE_ERROR;
				}
			}else{
	            model.addAttribute("ret", CancelOrderCode.INVALID_ORDER_NO.getCompleteCode());
	            return RETRUN_PAGE_ERROR;
			}
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
            return RETRUN_PAGE_ERROR;
        } finally {
            logger.info("running:" + (System.currentTimeMillis() - runstarttime));
        }
	}
}
