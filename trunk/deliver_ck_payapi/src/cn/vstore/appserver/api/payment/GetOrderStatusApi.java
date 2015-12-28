package cn.vstore.appserver.api.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.form.TokenForm;
import cn.vstore.appserver.model.OrderInfo;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.PaymentService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

@Controller
public class GetOrderStatusApi {
	protected static final Logger logger = LoggerFactory.getLogger(GetOrderStatusApi.class);
    // 回傳頁面
    public final static String RETRUN_PAGE_SUCCESS = "payment/getOrderStatus";
    public final static String RETRUN_PAGE_ERROR = "error";
    @Autowired
    protected PaymentService paymentService;
    @Autowired
    private MessageTranslator translator;
    @RequestMapping(value = "/api/ck/info/order/{pkg:.+}", method = { RequestMethod.GET, RequestMethod.POST })
    public String excuteApi(@ModelAttribute("SelectPaymentForm") TokenForm selectPaymentForm,
    		@PathVariable("pkg") String pkg, Model model,
    		HttpServletRequest request, HttpServletResponse response) {
        long runstarttime = System.currentTimeMillis();
        logger.info("parameter : store={}, pkg={}, token={}, iccid={}, imei={}", new Object[] { selectPaymentForm.getStore(),pkg,
        		selectPaymentForm.getToken(),selectPaymentForm.getIccid(), selectPaymentForm.getImei() });
        try{
            // 驗證輸入參數
        	StoreInfo storeInfo = (StoreInfo)request.getAttribute("storeInfo");
        	if(storeInfo==null){
        		model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
        		logger.info(translator.getMessage(CommonCode.PARAMETER_ERROR), false);
        		return RETRUN_PAGE_ERROR;
        	}
        	
        	ServiceResult<OrderInfo> serviceResult = paymentService.getOrderStatus(selectPaymentForm.getToken(),storeInfo.getId(),pkg);        	
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
