package cn.vstore.appserver.api.payment.unionpay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.mifiance.unionpay.mer.api.Constant;
import cn.mifiance.unionpay.mer.api.entity.NotifyBean;
import cn.mifiance.unionpay.mer.api.services.NotifyServices;
import cn.vstore.appserver.api.payment.UnionPayRet;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.service.OrderPayService;
import cn.vstore.appserver.service.PaymentService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.Constants;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 处理银联支付结果
 * 
 * @version $Id$
 */

@Controller
public class UnionPayReceiver {

	protected static final Logger logger = LoggerFactory
			.getLogger(UnionPayReceiver.class);
	

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/gpay/success";
	public final static String RETRUN_PAGE_ERROR = "payment/gpay/fail";

	@Autowired
	protected OrderPayService orderPayService;


	@RequestMapping(value = "/api/integrate/unionpay/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(Model model,HttpServletRequest request,HttpServletResponse response) {
		
		
		long runstarttime = System.currentTimeMillis();


		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()),8192);
			StringBuilder sb = new StringBuilder();   
            String inputLine;
            while ((inputLine = reader.readLine()) != null)
            {   
                sb.append(inputLine);   
            }  
			            
            String responseRet = new String(URLDecoder.decode(sb.toString(), "UTF-8"));
            logger.info("original responseRet:\n\n" + responseRet);
            UnionPayRet ret = UnionPayRet.parseXml(responseRet);
            //银联使用PHP送出签名时，签名中的"+"会被自动转成" "，此处需手动还原
            String realSign = ret.getSign();
            responseRet = responseRet.replaceAll("<sign>[^<]*</sign>", "<sign>" + realSign + "</sign>");
            logger.info("responseRet:\n\n" + responseRet);
            //进行验签
            NotifyBean recvCheck=NotifyServices.checkSign(responseRet);
			if (recvCheck.getCheckSign().equals(Constant.CHECK_RESULT_SUCESS)) {
				logger.debug("success");
				String result = "";
				  if ((ret.getTradeStatus()==1||ret.getTradeStatus()==2)) {
			            //消费成功
			            result = "消费成功";
			            System.out.print("消费成功......" + "<br/>");
			            //更新订单支付状态
			            orderPayService.updateOrderStatusByOrderId(2, ret.getMerchantOrderId());//支付成功
			        } else {
			            //消费失败
			            result = "消费失败";
			            System.out.print("消费失败....." + "<br/>");
			            //todo 商户处理网站业务逻辑代码.
			            orderPayService.updateOrderStatusByOrderId(1, ret.getMerchantOrderId());//支付失败
			        }
				 	response.getWriter().write(result + ", orderId=" + ret.getMerchantOrderId());
			//	gamepayService.updateSuccessIpayUserPaymentLogByOrderNo(gamepayService,paymentInformation);
			return RETRUN_PAGE_SUCCESS;
				
			} else {
				logger.debug("fail");
				return RETRUN_PAGE_ERROR;
			}

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
			return RETRUN_PAGE_ERROR;
		} finally {
			logger.info("running:"
					+ (System.currentTimeMillis() - runstarttime));
		}
	}

}