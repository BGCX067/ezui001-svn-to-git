package cn.vstore.appserver.api.payment.feiliupay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

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

import cn.vstore.appserver.api.payment.gamepay.GPayGameReceiver;
import cn.vstore.appserver.form.GpayForm;
import cn.vstore.appserver.service.GamePayService;
import cn.vstore.appserver.util.RsaMessage;
import cn.vstore.appserver.util.URLUtil;

@Controller
public class FlPayReceiverApi {
	protected static final Logger logger = LoggerFactory
			.getLogger(GPayGameReceiver.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/flgpay/succpay";
	public final static String RETRUN_PAGE_ERROR = "payment/flgpay/failpay";

	@Autowired
	protected GamePayService gamepayService;

	@RequestMapping(value = "/api/integrate/flpaygame/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("GpayForm") GpayForm gpayForm,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		long runstarttime = System.currentTimeMillis();
		logger.info("parameter : notify_data={}, sign={}", new Object[] {
				gpayForm.getNotify_data(), gpayForm.getSign() });
			String reStr = null;
			PrintWriter out = null;
			StringBuffer stringBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			stringBuffer.append("<Response>");
		try {
			out = response.getWriter();
			int len = request.getContentLength();
			System.out.println("数据流长度===============" +len);
			if(len <= 0){
				stringBuffer.append("<Ret>0</Ret>");
				stringBuffer.append("</Response>");
		        out.write(stringBuffer.toString());
		        String res0 = Dom4jUtil.postSpancer(URLUtil.FL_VTION_POST_SP,Dom4jUtil.reqStr("0","noorderInfo"));
		        System.out.println("商户返回信息res0=="+res0);
		        //此时本地订单状态仍为0，临时订单
			}else{
				//获取HTTP请求的输入流
		        InputStream is = request.getInputStream();
		        //已HTTP请求输入流建立一个BufferedReader对象
		        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		        //读取HTTP请求内容
		        String buffer = null;
		        StringBuffer sb = new StringBuffer();
		        while ((buffer = br.readLine()) != null) {
		        //在页面中显示读取到的请求参数
		            sb.append(buffer+"\n");
		        }
		        System.out.println("接收飞流post发送数据流================\n"+sb.toString().trim());
		        reStr = sb.toString();
		        
		        //首先获取加密串
		        Map<String, String> map = Dom4jUtil.readFLStringXmlOut(reStr);
		        String rsaStr = map.get("verifyString");
		        
		        //获取解密后的明码
		        //需要先RSA解密验证数据是否和明码一致
		        String rsaStrs = RsaMessage.getStrDSADecInfo(rsaStr);
		        //rsaStrs = "2323|1000|100016|2323323|100|1|101|feiliupay";
		        //解密后的MAP信息
		        Map<String, String> rsaMap = Dom4jUtil.getRsaInfo(rsaStrs,rsaStr);
		        
		        //如果解密数据和铭文不一致
		        //如果支付状态不为1
		        if(!rsaMap.equals(map) || !"1".equals(map.get("ret"))){
		        	System.out.println("返回支付结果后map对比===="+rsaMap.equals(map));
		        	System.out.println("返回的ret==="+map.get("ret"));
		        	stringBuffer.append("<Ret>0</Ret>");
		        	//同时告诉私办事儿支付失败
		        	//不需要返回数据
		        	@SuppressWarnings("unused")
					String res1 = Dom4jUtil.postSpancer(URLUtil.FL_VTION_POST_SP,Dom4jUtil.reqStr("0",map.get("orderId")));
//		        	if("0".equals(Dom4jUtil.getRetMsg(res1))){
			    	gamepayService.updateFLorderStatusByOrderId(gamepayService, 1, map.get("flOrderId"), map.get("cardStatus"), map.get("orderId"));
//			    	}
		        }else{
		        	System.out.println("进入else，说明map值相等......");
		        	//发送支付结果请求到死板散而，看返回是否成功
			        String results = Dom4jUtil.postSpancer(URLUtil.FL_VTION_POST_SP,Dom4jUtil.reqStr("1",map.get("orderId")));
			        System.out.println("商户返回的通知结果===="+results);
			        //如果商户返回的为空与否
			        if(!"".equals(results) && results != null){
			        	System.out.println("商户返回信息不为空......");
			        	if("1".equals(Dom4jUtil.getRetMsg(results))){//1为成功,其他为失败
				        	//私办事儿返回成功与否支付标识(1成功，0失败)
				        	//成功再修改订单状态
				        	/**
							 * 参数说明
							 * 2,为订单状态，支付成功
							 * flOrderId飞流订单
							 * cardStatus卡支付状态
							 * orderId本地订单号
							 */
				        	System.out.println("商户返回的成功信息，开始修改订单.....");
							gamepayService.updateFLorderStatusByOrderId(gamepayService, 2, map.get("flOrderId"), map.get("cardStatus"), map.get("orderId"));
							System.out.println("支付成功订单更新完毕.....");
							stringBuffer.append("<Ret>1</Ret>");
				        }else{
				        	System.out.println("进入商户返回不成功标识......");
				        	stringBuffer.append("<Ret>0</Ret>");
				        	//私办事儿返回不成功支付标识
				        	//不需要返回数据,
				        	System.out.println("商户返回不成功标识，且更新订单信息为失败......");
					    	gamepayService.updateFLorderStatusByOrderId(gamepayService, 1, map.get("flOrderId"), map.get("cardStatus"), map.get("orderId"));
					    	System.out.println("支付失败订单更新完毕......");
				        }
			        }else{
			        	System.out.println("商户返回信息为空......");
			        	stringBuffer.append("<Ret>0</Ret>");
			        }
		        }
		        stringBuffer.append("</Response>");
		        out.write(stringBuffer.toString());
			}
		}catch (Throwable e) {
			logger.error(e.getMessage(), e);
			System.out.println("进入支付结果catch...");
			//发送给商户通知支付失败
			Dom4jUtil.postSpancer(URLUtil.FL_VTION_POST_SP,Dom4jUtil.reqStr("0","noorderInfo"));
			
			stringBuffer.append("<Ret>0</Ret>");
			stringBuffer.append("</Response>");
			out.write(stringBuffer.toString());
			System.out.println("response给飞流支付异常信息.....");
		} finally {
	        out.flush();
	        out.close();
			logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
		}
			System.out.println("the last response to feiliu info");
		return null;
	}
}
