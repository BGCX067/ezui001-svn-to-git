package cn.vstore.appserver.api.payment.danglepay;

import java.io.PrintWriter;

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

import cn.vstore.appserver.api.payment.feiliupay.Dom4jUtil;
import cn.vstore.appserver.api.payment.gamepay.GPayGameReceiver;
import cn.vstore.appserver.form.GpayForm;
import cn.vstore.appserver.service.GamePayService;
import cn.vstore.appserver.util.URLUtil;

@Controller
public class DlPayReceiverApi {
	protected static final Logger logger = LoggerFactory
			.getLogger(GPayGameReceiver.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/dlgpay/succpay";
	public final static String RETRUN_PAGE_ERROR = "payment/dlgpay/failpay";

	@Autowired
	protected GamePayService gamepayService;

	@RequestMapping(value = "/api/integrate/dlpaygame/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("GpayForm") GpayForm gpayForm,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		long runstarttime = System.currentTimeMillis();
		logger.info("parameter : notify_data={}, sign={}", new Object[] {
				gpayForm.getNotify_data(), gpayForm.getSign() });
		//获取参数
		String orderno = null;
		String result = request.getParameter("result");
		String orderid = request.getParameter("orderid");
		String amount = request.getParameter("amount");//最终支付金额，实际支付金额
		String mid = request.getParameter("mid");
		String gid = request.getParameter("gid");
		String sid = request.getParameter("sid");
		String uif = request.getParameter("uif");
		String utp = request.getParameter("utp");
		String eif = request.getParameter("eif");//等价于orderNo
		String pcid = request.getParameter("pcid");
		String cardno = request.getParameter("cardno");
		String timestamp = request.getParameter("timestamp");
		String errorcode = request.getParameter("errorcode");
		String verstr = request.getParameter("verstring");

		StringBuilder sb = new StringBuilder();
		String reStr = "success";
		PrintWriter out = null;
		orderno = eif;//订单号
		try {
			out = response.getWriter();
			sb.append("result=").append(result);
			sb.append("&orderid=").append(orderid);
			sb.append("&amount=").append(amount);
			sb.append("&mid=").append(mid.toString());
			sb.append("&gid=").append(gid.toString());
			sb.append("&sid=").append(sid.toString());
			sb.append("&uif=").append(uif);//java.net.URLEncoder.encode(uif,"UTF-8")
			sb.append("&utp=").append(utp.toString());
			sb.append("&eif=").append(eif);
			sb.append("&pcid=").append(pcid);
			sb.append("&cardno=").append(cardno);
			sb.append("&timestamp=").append(timestamp);
			sb.append("&errorcode=").append(errorcode);
			sb.append("&merchantkey=").append(DandleUtil.MERCHANT_KEY);
			  
			System.out.println("当乐返回的支付结果信息的加密串verstr0==="+verstr);
			String str1 = sb.toString();
			System.out.println("dangle return data but need MD5:verstr==="+str1);
			String verifyString = MD5.MD5Encode(str1).toLowerCase();
			System.out.println("verstr2===" + verifyString);
			
			System.out.println("当乐返回的参数值中的result为===" + result+"如果为1则成功支付，否则支付失败~");
			//对比加密后的组装参数字符串与返回的加密字符串是否相等
			if(verstr != null && verstr.equals(verifyString)){
				System.out.println("说明当乐返回的支付结果不为空......");
				if("1".equals(result)){//支付成功
					System.out.println("当乐支付成功结果后返回的订单号==="+orderid+"===支付成功");
					//还需要给商户去处理订单成功与否后再返回给当乐
					//1.如果商户返回成功修改本地订单状态
					//2.如果商户返回失败，则不修改订单状态，直接返回给
				    //String urlStr = "";//需要商户提供url接口
					//杨小牧 15:34:36 
					//1.支付失败还是返回success
					//2.验证失败就是非法请求，不需返回了。
					//3.验证成功的都返回success
				    String results = Dom4jUtil.postSpancer(URLUtil.DL_VTION_POST_SP,Dom4jUtil.reqStr("1",eif));
				    System.out.println("商户第一次返回操作信息==="+results);
//				    if(!"".equals(results) && results != null){
				    	if("1".equals(Dom4jUtil.getRetMsg(results))){//1为成功,其他为失败(当乐意思是必须成功，不成功也得扣钱)
						    //更新订单
					    	System.out.println("商户返回成功......");
						    gamepayService.updateDLorderStatusByOrderId(2, orderid, eif);
						    System.out.println("当乐返回成功支付后，传送给商户，返回成功信息后更新订单信息成功...");
//						    out.write(reStr);
//				        	return null;
					    }else{
					    	//失败
					    	System.out.println("商户返回失败并修改订单信息......");
						    gamepayService.updateDLorderStatusByOrderId(1, orderid, orderno);
						    System.out.println("商户返回失败更新订单成功......");
//						    out.write(reStr);//同步数据标识，如果返回success则不会重传数据，否则会重传数据
//						    return null;
					    }
//				    }else{
//				    	System.out.println("商户在当乐给与支付成功信息后返回EMPTY信息,不需要通知当乐.....");
//				    	gamepayService.updateDLorderStatusByOrderId(1, orderid, orderno);
//				    	return null;
//				    }
			    }else{//支付失败
			    	//需给商户失败信息，不修改订单信息
			    	//不需要返回
			    	@SuppressWarnings("unused")
					String res1 = Dom4jUtil.postSpancer(URLUtil.DL_VTION_POST_SP,Dom4jUtil.reqStr("0",eif));
//			    	if("0".equals(Dom4jUtil.getRetMsg(res1))){
//			    	gamepayService.updateDLorderStatusByOrderId(1, orderid, orderno);
//			    	}
			    	System.out.println("当乐返回支付失败订单号==="+orderid+"===支付失败！");
//			    	out.write(reStr);
			    	System.out.println("返回当乐支付失败信息......");
			    }
			    //remark 是订单状态的中文说明，不需要加到MD5中验证。
			    System.out.print("签名验证成后返回的remark=" + request.getParameter("remark"));
			    //System.out.print("success");//同步数据标识，如果返回success则不会重传数据，否则会重传数据
			    out.write(reStr);
			    return null;
			 }else{
			    System.out.println("当乐返回支付结果签名验证失败......");
			    //需要给商户失败信息，不修改订单信息
			    //不需要私办事儿返回
			    @SuppressWarnings("unused")
				String res2 = Dom4jUtil.postSpancer(URLUtil.DL_VTION_POST_SP,Dom4jUtil.reqStr("0",eif));
//			    if("0".equals(Dom4jUtil.getRetMsg(res2))){
//		    		gamepayService.updateDLorderStatusByOrderId(1, orderid, orderno);
//		    	}
		    		return null;
			 }
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			//不需要返回当乐，但是需要告知商户订单支付失败
//			Dom4jUtil.postSpancer(URLUtil.DL_VTION_POST_SP,Dom4jUtil.reqStr("0",orderno));
		} finally {
			out.flush();
        	out.close();
			logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
		}
		return null;
	}
}
