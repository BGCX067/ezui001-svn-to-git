package cn.vstore.appserver.api.payment.shenzhoufu;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.form.payment.SelectOrderPayForm;
import cn.vstore.appserver.service.OrderPayService;
import cn.vstore.appserver.util.URLUtil;

@Controller
public class SZFPayReceiverApi {
	protected static final Logger logger = LoggerFactory.getLogger(SZFPayReceiverApi.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/szfgpay/succpay";
	public final static String RETRUN_PAGE_ERROR = "payment/faiorder";

	@Autowired
	protected OrderPayService orderPayService;

	@RequestMapping(value = "/api/integrate/szfpay/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("SelectOrderPayForm") SelectOrderPayForm selectOrderPayForm,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		long runstarttime = System.currentTimeMillis();
		String version = request.getParameter("version");            		//获取神州付消费接口的版本号
	    String merId = request.getParameter("merId");        				//获取商户ID
	    String payMoney = request.getParameter("payMoney");    				//获取消费金额
	    String orderId = request.getParameter("orderId");        			//获取商户订单号
	    String payResult = request.getParameter("payResult");    			//获取交易结果,1 成功 0 失败
	    String privateField = request.getParameter("privateField");        	//获取商户私有数据
	    String payDetails = request.getParameter("payDetails"); 			//获取消费详情
	    String returnMd5String = request.getParameter("md5String");        	//获取MD5加密串
	    String signString = request.getParameter("signString");            	//神州付证书签名
	    String cardMoney = request.getParameter("cardMoney");            	//卡面额
	    String privateKey = URLUtil.SZF_PRIVATEKEY;							//商户在神州付的私钥
		
		try{
		    System.out.println("version=" + version + "<br/>");
		    System.out.println("merId=" + merId + "<br/>");
		    System.out.println("payMoney=" + payMoney + "<br/>");
		    System.out.println("orderId=" + orderId + "<br/>");
		    System.out.println("payResult=" + payResult + "<br/>");
		    System.out.println("privateField=" + privateField + "<br/>");
		    System.out.println("returnMd5String=" + returnMd5String + "<br/>");
		    System.out.println("signString=" + signString + "<br/>");
		    System.out.println("cardMoney=" + cardMoney + "<br/>");

		    ///生成加密串,注意顺序  下面的if else判断如果采用返回模式1请用不加竖线的，如果是返回模式2请用加竖线的
		    String combineString;
		    if (cardMoney != null) {
		        combineString = version + "|" + merId + "|" + payMoney + "|" + cardMoney + "|" + orderId + "|" + payResult + "|" + privateField + "|" + payDetails + "|" + privateKey;
		    } else {
		        combineString = version + merId + payMoney + orderId + payResult + privateField + payDetails + privateKey;
		    }
		    //System.out.println("神州付网关返回数据：combineString=" + combineString);
		    String md5String = DigestUtils.md5Hex(combineString);
		    //（1）进行 MD5 校验
		    String result = null;
		    if (md5String.equals(returnMd5String)) {
		        //System.out.print("MD5验证成功！");
		        //用于服务器地址返回时,回复神州付消费平台:
		            if ("1".equals(payResult)) {
		                //消费成功
		                result = "消费成功";
		                System.out.print("消费成功......" + "<br/>");
		                //todo 商户处理网站业务逻辑代码.
		                //更新订单支付状态
		                orderPayService.updateOrderStatusByOrderId(2, orderId);//支付成功
		            } else {
		                //消费失败
		                result = "消费失败";
		                System.out.print("消费失败....." + "<br/>");
		                //todo 商户处理网站业务逻辑代码.
		                orderPayService.updateOrderStatusByOrderId(1, orderId);//支付失败
		            }
		     }else {
		    	 result = "MD5验证失败";
		         System.out.print("MD5验证失败！" + "<br/>");
		     }
		    	response.getWriter().write(result + ", orderId=" + orderId);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			logger.debug("进入支付结果catch...");
			orderPayService.updateOrderStatusByOrderId(1, orderId);//支付失败
		} finally {
			logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
		}
			logger.debug("the last response to shenzhoufu info");
		return null;
	}
}
