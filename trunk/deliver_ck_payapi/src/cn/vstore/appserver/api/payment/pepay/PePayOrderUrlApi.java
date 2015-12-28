package cn.vstore.appserver.api.payment.pepay;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.api.support.PePayUtil;
import cn.vstore.appserver.api.support.paytype.PePay;
import cn.vstore.appserver.form.payment.SelectPaymentForm;
import cn.vstore.appserver.model.OrderInfo;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.PaymentService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.GetPePayOrderCode;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.util.MessageTranslator;

@Controller
public class PePayOrderUrlApi {

	protected static final Logger logger = LoggerFactory.getLogger(PePayOrderUrlApi.class);
	
	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/pepay/success";

	public final static String RETRUN_PAGE_ERROR = "payment/pepay/fail";

    @Autowired
    protected PaymentService paymentService;

	@Autowired
	private MessageTranslator translator;
	
	/**
	 * 建立新訂單並回傳 PePay 導頁資訊
	 * @param selectPaymentForm
	 * @param pkg
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/api/pepay/order/{pkg:.+}", method = {RequestMethod.POST, RequestMethod.GET})
	public String excuteApi(
			@ModelAttribute("SelectPaymentForm") SelectPaymentForm selectPaymentForm,
			@PathVariable("pkg") String pkg, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		
		if (StringUtils.isBlank(selectPaymentForm.getImei())) {
			model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
			logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
			return RETRUN_PAGE_ERROR;
		}
		
		long runstarttime = System.currentTimeMillis();
		
		logger.info("parameter : time={}, vsign={}, token={}, payeeInfo={}, imei={}", new Object[] { selectPaymentForm.getTime(), selectPaymentForm.getVsign(), selectPaymentForm.getToken(), selectPaymentForm.getPayeeInfo(), selectPaymentForm.getImei() });

		try {
			// 驗證輸入參數
			StoreInfo storeInfo = (StoreInfo) request.getAttribute("storeInfo");
			if (storeInfo == null) {
				model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
				logger.info(translator.getMessage(CommonCode.PARAMETER_ERROR), false);
//				return RETRUN_PAGE_ERROR;
			}
			
			// 取得訂單資訊
			ServiceResult<OrderInfo> serviceResult = paymentService.getNewOrder(pkg, selectPaymentForm, new BigDecimal(100), new PePay());
			if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
				model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
				return RETRUN_PAGE_ERROR;
			}

			OrderInfo oi = serviceResult.getData();
			PaymentInformation pi = oi.getPayInfo();
			logger.info(translator.getMessage(serviceResult.getResult()));
			// 驗證用戶是否購買過
			if(pi.getStatus() == 1) {
				model.addAttribute("ret", GetPePayOrderCode.USER_IN_PAY_PROCESS.getCompleteCode());
				return RETRUN_PAGE_ERROR;
			} else if(pi.getStatus() == 2) {
				model.addAttribute("ret", GetPePayOrderCode.USER_ALREADY_BUY.getCompleteCode());
				return RETRUN_PAGE_ERROR;
			}
			model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
			model.addAttribute("orderNo", pi.getOrderNo());
			model.addAttribute("pePayOrderUrl", PePayUtil.REDIRECT_URL);
			model.addAttribute("pePayBaseUrl", PePayUtil.BASE_URL);
			model.addAttribute("SHOP_ID", PePayUtil.SHOP_ID);
			model.addAttribute("ORDER_ID", pi.getOrderNo());
			model.addAttribute("ORDER_ITEM", java.net.URLEncoder.encode(oi.getApp().getTitle(), "big5"));
			model.addAttribute("AMOUNT", String.valueOf(pi.getAmount().intValue()));
			model.addAttribute("CURRENCY", PePayUtil.CURRENCY);
			if (!StringUtils.isBlank(selectPaymentForm.getPayType())) {
				model.addAttribute("PAY_TYPE", selectPaymentForm.getPayType());
			} else {
				model.addAttribute("PAY_TYPE", PePayUtil.PAY_TYPE_TY_BILL);
			}
			model.addAttribute("CHECK_CODE", PePayUtil.getPePayCheckCode(pi.getOrderNo(), String.valueOf(pi.getAmount().intValue())));
			
			return RETRUN_PAGE_SUCCESS;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
			return RETRUN_PAGE_ERROR;
		} finally {
			logger.info("Total Running Time : " + (System.currentTimeMillis() - runstarttime) + "ms");
		}
	}
}
