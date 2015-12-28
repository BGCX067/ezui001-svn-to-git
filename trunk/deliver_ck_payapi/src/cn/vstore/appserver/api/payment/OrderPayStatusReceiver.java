package cn.vstore.appserver.api.payment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cn.vstore.appserver.form.payment.SelectOrderPayForm;
import cn.vstore.appserver.model.VOrderPayInfo;
import cn.vstore.appserver.service.OrderPayService;
import cn.vstore.appserver.service.ResultCode.CommonCode;

/**
 * 获取订单支付信息
 */

@Controller
public class OrderPayStatusReceiver {

	protected static final Logger logger = LoggerFactory.getLogger(OrderPayStatusReceiver.class);
	

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/paysuc";
	public final static String RETRUN_PAGE_ERROR = "payment/payfail";

	@Autowired
	protected OrderPayService orderPayService;
	
	@RequestMapping(value = "/api/integrate/orderpay/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("SelectOrderPayForm") SelectOrderPayForm selectOrderPayForm,
			Model model,HttpServletRequest request,HttpServletResponse response) {
		
		VOrderPayInfo orderPayInfo = null;
		String orderNo = request.getParameter("orderNo");
		System.out.println("orderNo=="+orderNo);
		long runstarttime = System.currentTimeMillis();
		try{
			//获取订单支付信息
			if(!"".equals(orderNo) && orderNo != null){
				orderPayInfo = orderPayService.selectOrderStatusByOrderId(orderNo);
				if(orderPayInfo != null){
					if("2".equals(orderPayInfo.getOrderstatus())){
						model.addAttribute("ret", CommonCode.SUCCESS.getCompleteCode());
						model.addAttribute("orderNo", orderPayInfo.getOrderno());
					}else{
						model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
						model.addAttribute("orderNo", orderNo);
						return RETRUN_PAGE_ERROR;
					}
				}else{
					model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
					model.addAttribute("msg", "订单号码不存在");
					return RETRUN_PAGE_ERROR;
				}
			}else{
				model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
				model.addAttribute("msg", "订单号码为空");
				return RETRUN_PAGE_ERROR;
			}
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