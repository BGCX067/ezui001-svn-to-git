package cn.vstore.appserver.api.payment.danglepay;


import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

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
import cn.vstore.appserver.api.payment.gamepay.SelectGamePayApi;
import cn.vstore.appserver.api.support.PayeeInfoFactory;
import cn.vstore.appserver.form.payment.SelectGamePayForm;
import cn.vstore.appserver.model.GameOrderInfo;
import cn.vstore.appserver.service.GamePayService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.DSAUtil;
import cn.vstore.appserver.util.MessageTranslator;
import cn.vstore.appserver.util.StringUtils;
import cn.vstore.appserver.util.URLUtil;

@Controller
public class SelectDanglePayApi {
	protected static final Logger logger = LoggerFactory.getLogger(SelectGamePayApi.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/dlgpay/succpay";
	public final static String RETRUN_PAGE_ERROR = "payment/gpay/fail";

	@Autowired
	protected GamePayService gamePayService;

	@Autowired
	private MessageTranslator translator;

	@RequestMapping(value = "/api/dlepay/order/{propsId:.+}", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(
			@ModelAttribute("SelectPaymentForm") SelectGamePayForm selectGamePayForm,
			@PathVariable("propsId") String propsId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		//获取IP地址
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		String ipaddr = addr.getHostAddress().toString();
		System.out.println("IP地址==="+ipaddr);
		String cardNo = request.getParameter("cardNo");//支付卡号
		String cardPwd = request.getParameter("cardPwd");//支付卡密
		String orderType = request.getParameter("orderType");//支付卡类型
		String denomination = request.getParameter("denomination");//卡面额
		String pkgName = request.getParameter("pkgName");//Apk包名
		String amount = request.getParameter("amount");//支付金额
		String snum = request.getParameter("snum");//渠道号
		String uif = request.getParameter("userId");//用户ID系统唯一标识
		String imei = request.getParameter("imei");
		String iccid = request.getParameter("iccid");
		
		System.out.println("cardNo==="+cardNo);
		System.out.println("cardPwd==="+cardPwd);
		System.out.println("orderType==="+orderType);
		System.out.println("denomination==="+denomination);
		System.out.println("pkgName==="+pkgName);
		System.out.println("amount==="+amount);
		System.out.println("snum==="+snum);
		System.out.println("uif==="+uif);
		
		if("".equals(cardNo) || cardNo == null){
			return RETRUN_PAGE_ERROR;//参数验证失败(有空的参数信息)
		}if("".equals(cardPwd) || cardPwd == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(orderType) || orderType == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(denomination) || denomination == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(pkgName) || pkgName == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(amount) || amount == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(snum) || snum == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(propsId) || propsId == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(uif) || uif == null){
			return RETRUN_PAGE_ERROR;
    	}else{
    	//当乐
			selectGamePayForm.setProductId(DandleUtil.MERCHANT_ID);//商户编号
			selectGamePayForm.setCompanyId(DandleUtil.GAME_ID);//游戏编号
			selectGamePayForm.setChannelId(DandleUtil.SERVER_ID);//服务器编号
	    	selectGamePayForm.setCardNo(cardNo);//卡号"0101001207430162809"
	    	selectGamePayForm.setCardPwd(cardPwd);//卡密"100704562511274917"
	    	selectGamePayForm.setAmount(amount);//支付金额"50"
	    	selectGamePayForm.setSnum(snum);//渠道号"cbea4ea2-0710-414c-8386-ee8ff46f9524"
	    	selectGamePayForm.setPackageName(pkgName);//apk包名"super.power"
	    	selectGamePayForm.setUserID(uif);//用户ID"sdfsdfsdf234"
	    	selectGamePayForm.setPayUserId("0");//固定值189用户填1，非189用户填0
	    	selectGamePayForm.setUserIpAddr(ipaddr);//IP地址
	    	selectGamePayForm.setOrderTime(StringUtils.getDataStr());//日期字符串StringUtils.getDataStr()
	    	selectGamePayForm.setDenomination(Double.valueOf(denomination));//卡面额
	    	selectGamePayForm.setOrderType(orderType);//卡支付的类型
	    	if(!"".equals(imei) && imei != null){
	    		selectGamePayForm.setImei(imei);
	    	}
	    	if(!"".equals(iccid) && iccid != null){
	    		selectGamePayForm.setIccid(iccid);
	    	}
	    	selectGamePayForm.setStoreId(new BigDecimal(100));//商城ID
	    	selectGamePayForm.setPayeeInfo(URLUtil.DLGPAY_INFO);
			
	    	//道具ID
	    	selectGamePayForm.setPropsId(propsId);
			//首先生成本地订单号
			String orderNo = DandleUtil.MERCHANT_ID + DandleUtil.GAME_ID + DandleUtil.SERVER_ID + new DSAUtil().splits();//订单号生成
			selectGamePayForm.setOrderNo(orderNo);
			selectGamePayForm.setMerPriv(orderNo);//唯一标识且为了修改订单支付状态
			
			ServiceResult<GameOrderInfo> serviceResult = null;
			long runstarttime = System.currentTimeMillis();
			logger.info("parameter : time={}, vsign={}, token={}, payeeInfo={}, imei={}",new Object[] {
				selectGamePayForm.getTime(),
				selectGamePayForm.getVsign(),
				selectGamePayForm.getToken(),
				selectGamePayForm.getPayeeInfo(),
				selectGamePayForm.getImei()});
			
			try{
				//发送订单信息给当乐服务器
				Object payeeInfo = null;
	        	if (!StringUtils.isBlank(selectGamePayForm.getPayeeInfo())){
	        		payeeInfo = PayeeInfoFactory.parse(selectGamePayForm.getPayeeInfo());
	        	}
	        	//需要组装数据
	        	String postData = DandleUtil.getMD5Str(selectGamePayForm);
	        	//发送订单请求到当乐服务器
	        	System.out.println("调用支付URL==="+getHostUrl(orderType));
	        	String res = DandleUtil.httpPost(getHostUrl(orderType), postData, "UTF-8");
	        	Map<String, String> map = DandleUtil.retOrderInfo(res);
	        	System.out.println("当乐返回订单信息result=="+map.get("result")+"===AND===msg=="+map.get("msg"));
	        	if("1".equals(map.get("result"))){//result=1表示订单下单成功，但需等待支付结果//支付金额必须是面额数值
	        		//本地记录临时订单
	        		System.out.println("当乐验证订单信息成功返回值是1，则增加一条订单信息......");
	        		serviceResult  = gamePayService.getDLNewOrder(selectGamePayForm, selectGamePayForm.getStoreId(), payeeInfo);
					if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
		        		model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        		return RETRUN_PAGE_ERROR;
		        	}
					// 回傳成功訊息
		            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		            model.addAttribute("orderNo", serviceResult.getData().getPayInfo().getOrderNo());
		            logger.info(translator.getMessage(serviceResult.getResult()));
		            return RETRUN_PAGE_SUCCESS;
	        	}else{
	        		//当乐返回订单状态为失败
	        		//还需要记录本地订单信息
	        		System.out.println("当乐验证订单信息失败返回值是非1，则增加一条订单信息......");
	        		serviceResult  = gamePayService.getDLNewOrder(selectGamePayForm, selectGamePayForm.getStoreId(), payeeInfo);
	        		return RETRUN_PAGE_ERROR;
	        	}
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
				return RETRUN_PAGE_ERROR;
			} finally {
				logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
			}
		}
	}
	
	/**
	 * 获取IP地址
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getIpAddr(HttpServletRequest request) {
	     String ipAddress = null;
	     //ipAddress = request.getRemoteAddr();
	     ipAddress = request.getHeader("x-forwarded-for");
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
	      ipAddress = request.getHeader("Proxy-Client-IP");
	     }
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
	         ipAddress = request.getHeader("WL-Proxy-Client-IP");
	     }
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
	    	 ipAddress = request.getRemoteAddr();
	    	 if(ipAddress.equals("127.0.0.1")){
	    		 //根据网卡取本机配置的IP
	    		 InetAddress inet=null;
	    		 try {
	    			 inet = InetAddress.getLocalHost();
	    		 } catch (UnknownHostException e) {
	    			 e.printStackTrace();
	    		 }
	    		 ipAddress= inet.getHostAddress();
	    	 }
	     }
	     //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
	     if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
	         if(ipAddress.indexOf(",")>0){
	             ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
	         }
	     }
	     return ipAddress;
	}
	
	/**
	 * 获取支付订单的URL(当乐提供)
	 * @param orderType
	 * @return
	 */
	private static String getHostUrl(String orderType){
		String dlPayUrl = null;
		if("001".equals(orderType)){//神州付
			dlPayUrl = "http://189hi.cn/189pay2/shenzhoufujava_feed.do";
		}else if("005".equals(orderType)){//易宝盛大点卡
			dlPayUrl = "http://189hi.cn/189pay2/yeepaysndajava_feed.do";
		}else if("006".equals(orderType)){//易宝神州行
			dlPayUrl = "http://189hi.cn/189pay2/yeepayszxjava_feed.do";
		}else if("007".equals(orderType)){//易宝联通卡
			dlPayUrl = "http://189hi.cn/189pay2/yeepayunicomjava_feed.do";
		}else if("010".equals(orderType)){//易宝骏网一卡通
			dlPayUrl = "http://189hi.cn/189pay2/yeepayjunnetjava_feed.do";
		}else if("012".equals(orderType)){//易宝征途卡
			dlPayUrl = "http://189hi.cn/189pay2/yeepayzhengtujava_feed.do";
		}else if("016".equals(orderType)){//易宝电信卡
			dlPayUrl = "http://189hi.cn/189pay2/yeepaytelecomjava_feed.do";
		}
		return dlPayUrl;
	}
	
	 public static void main(String[] args) {
		System.out.println("100016"+ new DSAUtil().splits());
	}
}