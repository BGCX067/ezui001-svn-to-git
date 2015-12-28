package cn.vstore.appserver.api.payment;


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

import cn.vstore.appserver.api.support.PayeeInfoFactory;
import cn.vstore.appserver.form.payment.SelectPaymentForm;
import cn.vstore.appserver.model.OrderInfo;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.PaymentService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 21.	新增訂單，並獲取定單號碼(23)
 * 
 * @version $Id$
 */
@Controller
public class SelectPaymentApi {

    protected static final Logger logger = LoggerFactory.getLogger(SelectPaymentApi.class);

    // 回傳頁面
    public final static String RETRUN_PAGE_SUCCESS = "payment/selectPayment";
    public final static String RETRUN_PAGE_ERROR = "error";

    @Autowired
    protected PaymentService paymentService;

    @Autowired
    private MessageTranslator translator;

    @RequestMapping(value = "/api/payment/order/{pkg:.+}", method = { RequestMethod.GET, RequestMethod.POST })
    public String excuteApi(@ModelAttribute("SelectPaymentForm") SelectPaymentForm selectPaymentForm,
    		@PathVariable("pkg") String pkg, Model model,
    		HttpServletRequest request, HttpServletResponse response) {
      if (StringUtils.isBlank(selectPaymentForm.getImei())) {
      model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
      logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
      return RETRUN_PAGE_ERROR;
      }
        long runstarttime = System.currentTimeMillis();
        logger.info("parameter : time={}, vsign={}, token={}, payeeInfo={}, imei={}", new Object[] { selectPaymentForm.getTime(),selectPaymentForm.getVsign(),
        		selectPaymentForm.getToken(),selectPaymentForm.getPayeeInfo(), selectPaymentForm.getImei() });
        
        try {
            // 驗證輸入參數
        	StoreInfo storeInfo = (StoreInfo)request.getAttribute("storeInfo");
        	if(storeInfo==null){
        		model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
        		logger.info(translator.getMessage(CommonCode.PARAMETER_ERROR), false);
        		return RETRUN_PAGE_ERROR;
        	}
        	Object payeeInfo=null;
        	if (!StringUtils.isBlank(selectPaymentForm.getPayeeInfo())){
        		payeeInfo=PayeeInfoFactory.parse(selectPaymentForm.getPayeeInfo());
        	}
        	
        	ServiceResult<OrderInfo> serviceResult = paymentService.getNewOrder(pkg,selectPaymentForm, storeInfo.getId(),payeeInfo);
        	if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
        		model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
        		return RETRUN_PAGE_ERROR;
        	}
        	// 回傳成功訊息
            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
            model.addAttribute("orderInfo", serviceResult.getData());
            logger.info(translator.getMessage(serviceResult.getResult()));
            return RETRUN_PAGE_SUCCESS;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
            return RETRUN_PAGE_ERROR;
        } finally {
            logger.info("running:" + (System.currentTimeMillis() - runstarttime));
        }
    }
}