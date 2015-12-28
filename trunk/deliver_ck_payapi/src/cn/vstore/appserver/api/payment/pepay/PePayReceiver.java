package cn.vstore.appserver.api.payment.pepay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.api.support.PePayUtil;
import cn.vstore.appserver.form.PePayForm;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.UserPayment;
import cn.vstore.appserver.service.PaymentService;

@Controller
public class PePayReceiver {

	protected static final Logger logger = LoggerFactory.getLogger(PePayReceiver.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_RESULT_01 = "payment/pepay/result01";
	
	public final static String RETRUN_PAGE_RESULT_02 = "payment/pepay/result02";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
    protected PaymentService paymentService;
	
	/**
	 * 接受 PePay 第一階段回傳訊息 - 使用者選擇付款方式
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/integrate/pepay/receive01", method = {RequestMethod.GET, RequestMethod.POST })
	public String executeApiReceive01(Model model, HttpServletRequest request) {
		logger.info("executeApiReceive01 starts ...");
		
		long runstarttime = System.currentTimeMillis();
		PePayForm pePayForm = new PePayForm();
		pePayForm.setShopId(request.getParameter("SHOP_ID"));
		pePayForm.setOrderId(request.getParameter("ORDER_ID"));
		pePayForm.setAmount(request.getParameter("AMOUNT"));
		pePayForm.setCurrency(request.getParameter("CURRENCY"));
		pePayForm.setSessId(request.getParameter("SESS_ID"));
		pePayForm.setProdId(request.getParameter("PROD_ID"));
		pePayForm.setCheckCode(request.getParameter("CHECK_CODE"));
		pePayForm.setShopPara(request.getParameter("SHOP_PARA"));
		
		logger.info("parameter : shopId={}, orderId={}, amount={}, currency={}, sessId={}, prodId={}, checkCode={}, shopPara={}", new Object[] {
				pePayForm.getShopId(), pePayForm.getOrderId(), pePayForm.getAmount(), pePayForm.getCurrency(), 
				pePayForm.getSessId(), pePayForm.getProdId(), pePayForm.getCheckCode(), pePayForm.getShopPara() });
		
		try {
			pePayForm.setResCode(PePayUtil.PePayResCode.SUCCESS);
			// 驗證輸入參數
			if (StringUtils.isBlank(pePayForm.getShopId()) || !PePayUtil.SHOP_ID.equals(pePayForm.getShopId()) ||
					StringUtils.isBlank(pePayForm.getOrderId()) ||
					StringUtils.isBlank(pePayForm.getAmount()) || 
					StringUtils.isBlank(pePayForm.getCurrency()) ||
					StringUtils.isBlank(pePayForm.getSessId()) ||
					StringUtils.isBlank(pePayForm.getProdId()) || 
					StringUtils.isBlank(pePayForm.getCheckCode())) {
				
				pePayForm.setResCode(PePayUtil.PePayResCode.PARAMS_FAIL_RECEIVE_01);
				model.addAttribute("pePayForm", pePayForm);
				logger.info("Some parameters are invalid!");
				return RETRUN_PAGE_RESULT_01;
			}
			
			// 驗證 CheckCode
			String checkCode = PePayUtil.getPePayCheckCode(pePayForm.getOrderId(), pePayForm.getAmount(), pePayForm.getSessId(), pePayForm.getProdId());
			if (!checkCode.equals(pePayForm.getCheckCode())) {
				pePayForm.setResCode(PePayUtil.PePayResCode.CHECK_CODE_MISMATCH_RECEIVE_01);
				model.addAttribute("pePayForm", pePayForm);
				logger.info("Check code mismatch!");
				return RETRUN_PAGE_RESULT_01;
			}
			
			
			// 找出對應的訂單資料
			PaymentInformation pi = paymentService.getOrderByOrderNo(pePayForm.getOrderId());
			// 確認訂單狀態
			if(pi.getStatus() == UserPayment.STATUS_PAYMENT_NO_PAID) {
				// 訂單狀態為"未付款"
				String retMsg = this.getAllParams(request);
				
				//將PePay回傳參數逐次累加記錄
				if(StringUtils.isBlank(pi.getRetMsg())){
					pi.setRetMsg(retMsg);
				} else {
					pi.setRetMsg(pi.getRetMsg() + "\n" + retMsg);
				}
				
				// 修改訂單狀態為付款中
				pi.setStatus(UserPayment.STATUS_PAYMENT_PROCESSING);
				
				logger.info("Order id:{} starts payment progress and changes to new status:{}", new Object[]{ pi.getId(), pi.getStatus()});
			} else {
				// 訂單狀態為"付款中"等其他狀態 -> 理論上不應該出現
				pePayForm.setResCode(PePayUtil.PePayResCode.TRANSACTION_SUCCESS_RECEIVE_01);
				logger.info("Invalid status transition! order id:{}, status:{}", new Object[]{ pi.getId(), pi.getStatus()});
			}
			
			paymentService.updateSuccessIpayUserPaymentLog(paymentService, pi);
			pePayForm.setUserId(pi.getUserUid());
			model.addAttribute("pePayForm", pePayForm);
			return RETRUN_PAGE_RESULT_01;
		} catch (Throwable e) {
			pePayForm.setResCode(PePayUtil.PePayResCode.OTHER_FAIL_RECEIVE_01);
			model.addAttribute("pePayForm", pePayForm);
			logger.error(e.getMessage(), e);
			return RETRUN_PAGE_RESULT_01;
		} finally {
			logger.info("PePayReceiver ResCode : {}, ResCodeDesc : {}", new Object[]{ pePayForm.getResCode().getCode(), pePayForm.getResCode().getDesc()});
			logger.info("Total Running Time : " + (System.currentTimeMillis() - runstarttime) + "ms");
		}
		
	}
	
	/**
	 * 接受 PePay 交易結果回傳訊息
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/integrate/pepay/receive02", method = {RequestMethod.GET, RequestMethod.POST })
	public String executeApiReceive02(Model model, HttpServletRequest request) {
		logger.info("executeApiReceive02 start...");
		long runstarttime = System.currentTimeMillis();
		PePayForm pePayForm = new PePayForm();
		
		pePayForm.setSessId(request.getParameter("SESS_ID"));
		pePayForm.setOrderId(request.getParameter("ORDER_ID"));
		pePayForm.setBillId(request.getParameter("BILL_ID"));
		pePayForm.setDataId(request.getParameter("DATA_ID"));
		pePayForm.setShopId(request.getParameter("SHOP_ID"));
		pePayForm.setPayType(request.getParameter("PAY_TYPE"));
		pePayForm.setProdId(request.getParameter("PROD_ID"));
		pePayForm.setUserId(request.getParameter("USER_ID"));
		pePayForm.setSourceAmount(request.getParameter("SOURCE_AMOUNT"));
		pePayForm.setAmount(request.getParameter("AMOUNT"));
		pePayForm.setCurrency(request.getParameter("CURRENCY"));
		pePayForm.setDataCode(request.getParameter("DATA_CODE"));
		pePayForm.setTradeCode(request.getParameter("TRADE_CODE"));
		pePayForm.setShopPara(request.getParameter("SHOP_PARA"));
		pePayForm.setcDate(request.getParameter("CDATE"));
		pePayForm.setcTime(request.getParameter("CTIME"));
		pePayForm.setBillDate(request.getParameter("BILL_DATE"));
		pePayForm.setBillTime(request.getParameter("BILL_TIME"));
		pePayForm.setDate(request.getParameter("DATE"));
		pePayForm.setTime(request.getParameter("TIME"));
		pePayForm.setCheckCode(request.getParameter("CHECK_CODE"));
		
		logger.info("parameter : sessId={}, orderId={}, billId={}, dataId={}, shopId={}, payType={}, prodId={}, userId={}, " + 
					"sourceAmount={}, amount={}, currency={}, dataCode={}, tradeCode={}, shopPara={}, cDate={}, cTime={}, " + 
					"billDate={}, billTime={}, date={}, time={}, checkCode={}", new Object[] { 
				pePayForm.getSessId(), pePayForm.getOrderId(), pePayForm.getBillId(), pePayForm.getDataId(),
				pePayForm.getShopId(), pePayForm.getPayType(), pePayForm.getProdId(), pePayForm.getUserId(),
				pePayForm.getSourceAmount(), pePayForm.getAmount(), pePayForm.getCurrency(), pePayForm.getDataCode(),
				pePayForm.getTradeCode(), pePayForm.getShopPara(), pePayForm.getcDate(), pePayForm.getcTime(),
				pePayForm.getBillDate(), pePayForm.getBillTime(), pePayForm.getDate(), pePayForm.getTime(), pePayForm.getCheckCode() });
		
		// 驗證輸入參數
		if (StringUtils.isBlank(pePayForm.getSessId())
				|| StringUtils.isBlank(pePayForm.getOrderId())
				|| StringUtils.isBlank(pePayForm.getBillId())
				|| StringUtils.isBlank(pePayForm.getDataId())
				|| StringUtils.isBlank(pePayForm.getShopId())
				|| !PePayUtil.SHOP_ID.equals(pePayForm.getShopId())
				|| StringUtils.isBlank(pePayForm.getPayType())
				|| StringUtils.isBlank(pePayForm.getProdId())
				|| StringUtils.isBlank(pePayForm.getUserId())
				|| StringUtils.isBlank(pePayForm.getSourceAmount())
				|| StringUtils.isBlank(pePayForm.getAmount())
				|| StringUtils.isBlank(pePayForm.getCurrency())
				|| StringUtils.isBlank(pePayForm.getDataCode())
				|| StringUtils.isBlank(pePayForm.getTradeCode())
				|| StringUtils.isBlank(pePayForm.getcDate())
				|| StringUtils.isBlank(pePayForm.getcTime())
				|| StringUtils.isBlank(pePayForm.getDate())
				|| StringUtils.isBlank(pePayForm.getTime())
				|| StringUtils.isBlank(pePayForm.getCheckCode())) {

			pePayForm.setResCode(PePayUtil.PePayResCode.PARAMS_FAIL_RECEIVE_02);
			model.addAttribute("pePayForm", pePayForm);
			logger.info("Some parameters are invalid!");
			return RETRUN_PAGE_RESULT_02;
		}
		
		try {
			// 驗證 CheckCode
			String checkCode = PePayUtil.getPePayCheckCode(pePayForm.getOrderId(), pePayForm.getAmount(), pePayForm.getSessId(), pePayForm.getProdId(), pePayForm.getUserId());
			if (!checkCode.equals(pePayForm.getCheckCode())) {
				pePayForm.setResCode(PePayUtil.PePayResCode.CHECK_CODE_MISMATCH_RECEIVE_02);
				model.addAttribute("pePayForm", pePayForm);
				logger.info("Check code mismatch!");
				return RETRUN_PAGE_RESULT_02;
			}
						
			// 找出對應的訂單資料
			PaymentInformation pi = paymentService.getOrderByOrderNo(pePayForm.getOrderId());
			if(pi.getStatus() == UserPayment.STATUS_PAYMENT_SUCCESS) {//訂單已付款
				pePayForm.setResCode(PePayUtil.PePayResCode.TRANSACTION_SUCCESS_RECEIVE_02);
				model.addAttribute("pePayForm", pePayForm);
				logger.info("The payment of order id:{} is completed", pi.getId());
			} else if(pi.getStatus() == UserPayment.STATUS_PAYMENT_PROCESSING){
				//處理訂單
				Date transactionDate = sdf.parse(pePayForm.getDate() + pePayForm.getTime());//完成交易日期
				
				//2012.12.19 討論結果以下列方式儲存PePay各項回傳資訊：
				pi.setRetCode(pePayForm.getTradeCode());
				
				String retMsg = this.getAllParams(request);
				//將PePay回傳參數逐次累加記錄
				if(StringUtils.isBlank(pi.getRetMsg())){
					pi.setRetMsg(retMsg);
				} else {
					pi.setRetMsg(pi.getRetMsg() + "\n" + retMsg);
				}
				
				//PePay付款ID: "<SESS_ID>-<BILL_ID>"
				pi.setPaymentId(pePayForm.getSessId() + "-" + pePayForm.getBillId());
				
				//交易類型ID: "<PAY_TYPE>-<PROD_ID>"
				pi.setTransactionTypeId(pePayForm.getPayType() + "-" + pePayForm.getProdId());
				
				//記錄原始輸入金額與完成交易日期
				pi.setAmount(Double.parseDouble(pePayForm.getSourceAmount()));
				pi.setPayTime(transactionDate);
				
				//記錄其餘資訊
				String transactionData = "";
				transactionData += "DATA_ID:" + pePayForm.getDataId() + ";";
				transactionData += "AMOUNT:" + pePayForm.getAmount() + ";";
				transactionData += "CURRENCY:" + pePayForm.getCurrency() + ";";
				transactionData += "DATA_CODE:" + pePayForm.getDataCode() + ";";
				transactionData += "CDATE:" + pePayForm.getcDate() + ";";
				transactionData += "CTIME:" + pePayForm.getcTime() + ";";
				transactionData += "BILL_DATE:" + pePayForm.getBillDate() + ";";
				transactionData += "BILL_TIME:" + pePayForm.getBillTime() + ";";
				pi.setTransactionData(transactionData);
				
				if(PePayUtil.PEPAY_TRADE_CODE_SUCCESS.equals(pePayForm.getTradeCode())) { 
					pi.setStatus(UserPayment.STATUS_PAYMENT_SUCCESS);//金流交易成功
				} else {
					pi.setStatus(UserPayment.STATUS_PAYMENT_FAIL);//金流交易失敗
				}
				
				// 存入資料庫
				paymentService.updateSuccessIpayUserPaymentLog(paymentService, pi);
				pePayForm.setResCode(PePayUtil.PePayResCode.SUCCESS);
				model.addAttribute("pePayForm", pePayForm);
			} else {
				// 訂單狀態不是"付款中"也不是"已付款" -> 理論上不應該出現
				logger.info("Invalid status transition! order id:{}, status:{}", new Object[]{ pi.getId(), pi.getStatus()});
				pePayForm.setResCode(PePayUtil.PePayResCode.OTHER_FAIL_RECEIVE_02);
				model.addAttribute("pePayForm", pePayForm);
			}
			
			logger.info("executeApiReceive02 finish");
			return RETRUN_PAGE_RESULT_02;
		} catch (Throwable e) {
			pePayForm.setResCode(PePayUtil.PePayResCode.OTHER_FAIL_RECEIVE_02);
			model.addAttribute("pePayForm", pePayForm);
			logger.error(e.getMessage(), e);
			return RETRUN_PAGE_RESULT_02;
		} finally {
			logger.info("PePayReceiver ResCode : {}, ResCodeDesc : {}", new Object[]{ pePayForm.getResCode().getCode(), pePayForm.getResCode().getDesc()});
			logger.info("Total Running Time : " + (System.currentTimeMillis() - runstarttime) + "ms");
		}
		
	}
	
	@RequestMapping(value = "/api/integrate/pepay/return", method = {RequestMethod.GET, RequestMethod.POST })
	public String excuteApiReturn(Model model, HttpServletRequest request) {

		long runstarttime = System.currentTimeMillis();
		
		try {
			
			return null;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			logger.info("Total Running Time : " + (System.currentTimeMillis() - runstarttime) + "ms");
		}
	}
	
	/**
	 * 取得 HttpRequest 參數轉換字串
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getAllParams(HttpServletRequest request) {
		Map<String, Object[]> paramsMap = request.getParameterMap();
		String params = "";
		for(String k : paramsMap.keySet()) {
			if("".equals(params)) {
				params = k + "=" + (String) paramsMap.get(k)[0];
			} else {
				params += "&" + k + "=" + (String) paramsMap.get(k)[0];
			}
		}
		return params;
	}
}