package cn.vstore.appserver.api.payment.yijiupay;

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
import cn.vstore.appserver.service.OrderPayService;
import cn.vstore.appserver.util.MessageTranslator;

@Controller
public class YijiuOrderPayReceiver {
	protected static final Logger logger = LoggerFactory
			.getLogger(YijiuOrderPayReceiver.class);
	

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/gpay/success";
	public final static String RETRUN_PAGE_ERROR = "payment/gpay/fail";


	@Autowired
	protected OrderPayService orderPayService;

	@Autowired
	private MessageTranslator translator;
	@RequestMapping(value = "/api/integrate/19pay/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("SelectOrderPayForm") SelectOrderPayForm selectOrderPayForm,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		long runstarttime = System.currentTimeMillis();
	/*	version_id	版本号	非空	本接口版本号:3.00
		merchant_id	商户代码	非空	19PAY统一分配的商户代码
		verifystring	验证摘要串	非空	32位小写详见接后描述
		order_date	订单日期	可空	格式为‘YYYYMMDD’
		order_id	商户订单号	非空	
		result	支付结果	非空	Y成功  F失败 
		amount	金额	非空	
		currency	币种	非空	
		pay_sq	支付流水号	非空	
		pay_date	支付时间	非空	格式为‘YYYYMMDDHHMMSS’
		count	卡支付次数	可空	
		card_num1	卡号	非空	卡号DES加密
		card_pwd1	卡密码	非空	卡密码DES加密
		pm_id1	支付方式	非空	
		pc_id1	支付通道编号	非空	
		card_status1	卡支付状态	非空	0 成功 1失败
		card_code1	卡支付错误码	非空	详见错误返回码
		card_date1	卡支付完成时间	非空	格式为‘YYYYMMDDHHMMSS
		r1	扩展字段	非空	扩展字段
*/
		String version_id = request.getParameter("version_id");
		String merchant_id = request.getParameter("merchant_id");
		String verifystring = request.getParameter("verifystring");
		String order_date = request.getParameter("order_date");
		String order_id = request.getParameter("order_id");
		String amount = request.getParameter("amount");
		String currency = request.getParameter("currency");
		String pay_sq = request.getParameter("pay_sq");
		String pay_date = request.getParameter("pay_date");
		String pc_id1 =request.getParameter("pc_id1");
		String result = request.getParameter("result");
		String count= request.getParameter("count");
		String card_num1= request.getParameter("card_num1");
		String card_pwd1= request.getParameter("card_pwd1");
		String card_code1= request.getParameter("card_code1");
		String card_status1= request.getParameter("card_status1");
		String card_date1= request.getParameter("card_date1");
		String r1= request.getParameter("r1");
		String merchant_key = "123456789";
		
		try{
		    ///生成加密串,注意顺序  下面的if else判断如果采用返回模式1请用不加竖线的，如果是返回模式2请用加竖线的
			String ori="version_id="+ version_id+ "&merchant_id=" + merchant_id+ "&order_id="+ order_id + "&result=" + result + "&order_date="
					+ order_date+ "&amount=" + amount + "&currency=" + currency + "&pay_sq=" + pay_sq + "&pay_date=" + pay_date + "&count=" + count + "&card_num1=" + card_num1 + "&card_pwd1=" 
						+ card_pwd1+ "&pc_id1="+ pc_id1+ "&card_status1="+ card_status1+ "&card_code1="+ card_code1+ "&card_date1="+ card_date1+ "&r1="+ r1+ "&merchant_key=" + merchant_key;
	//md5 验证
		ori = KeyedDigestMD5.getKeyedDigest(ori,"");
	if(!verifystring.equals(ori)){
		 System.out.print("MD5验证失败！" + "<br/>");
    }
	else
	{
	      //用于服务器地址返回时,回复91pay消费平台:
        if ("Y".equals(result)) {
            //消费成功
            result = "消费成功";
            System.out.print("消费成功......" + "<br/>");
            //更新订单支付状态
            orderPayService.updateOrderStatusByOrderId(2, order_id);//支付成功
        } else {
            //消费失败
            result = "消费失败";
            System.out.print("消费失败....." + "<br/>");
            //todo 商户处理网站业务逻辑代码.
            orderPayService.updateOrderStatusByOrderId(1, order_id);//支付失败
        }
	}
		    //System.out.println("神州付网关返回数据：combineString=" + combineString);
		  
		    	response.getWriter().write(result + ", orderId=" + order_id);
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			logger.debug("进入支付结果catch...");
			orderPayService.updateOrderStatusByOrderId(1, order_id);//支付失败
		} finally {
			logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
		}
			logger.debug("the last response to shenzhoufu info");
		return null;
	}
}
