package cn.vstore.appserver.api.payment.feiliupay;


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
import cn.vstore.appserver.api.payment.gamepay.SelectGamePayApi;
import cn.vstore.appserver.api.support.PayeeInfoFactory;
import cn.vstore.appserver.form.payment.SelectGamePayForm;
import cn.vstore.appserver.model.GameOrderInfo;
import cn.vstore.appserver.service.GamePayService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.DSAUtil;
import cn.vstore.appserver.util.MessageTranslator;
import cn.vstore.appserver.util.URLUtil;

@Controller
public class SelectFeiliuPayApi {
	protected static final Logger logger = LoggerFactory.getLogger(SelectGamePayApi.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/flgpay/succpay";
	public final static String RETRUN_PAGE_ERROR = "payment/gpay/fail";

	@Autowired
	protected GamePayService gamePayService;

	@Autowired
	private MessageTranslator translator;

	@RequestMapping(value = "/api/flpay/order/{propsId:.+}", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(
			@ModelAttribute("SelectPaymentForm") SelectGamePayForm selectGamePayForm,
			@PathVariable("propsId") String propsId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		String cardNo = request.getParameter("cardNo");//支付卡号
		String cardPwd = request.getParameter("cardPwd");//支付卡密
		String pkgName = request.getParameter("pkgName");//Apk包名
		String orderType = request.getParameter("orderType");//支付卡类型
		String denomination = request.getParameter("denomination");//卡面额
		String amount = request.getParameter("amount");//支付金额
		String snum = request.getParameter("snum");//渠道号
		String userId = request.getParameter("userId");//用户ID系统唯一标识
		String imei = request.getParameter("imei");
		String iccid = request.getParameter("iccid");
		System.out.println("cardNo=="+cardNo);
		System.out.println("cardPwd=="+cardPwd);
		System.out.println("pkgName=="+pkgName);
		System.out.println("orderType=="+orderType);
		System.out.println("denomination=="+denomination);
		System.out.println("amount=="+amount);
		System.out.println("snum=="+snum);
		System.out.println("userId=="+userId);
		if("".equals(cardNo) || cardNo == null){
			return RETRUN_PAGE_ERROR;//参数验证失败(有空的参数信息)
		}if("".equals(cardPwd) || cardPwd == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(pkgName) || pkgName == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(orderType) || orderType == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(denomination) || denomination == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(amount) || amount == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(snum) || snum == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(propsId) || propsId == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(userId) || userId == null){
			return RETRUN_PAGE_ERROR;
		}else{
    	//飞流
			selectGamePayForm.setProductId("100016");
			selectGamePayForm.setCompanyId("100016");
			selectGamePayForm.setChannelId("100043");
	    	selectGamePayForm.setDenomination(Double.valueOf(denomination));//(denomination)20
	    	selectGamePayForm.setCardNo(cardNo);//cardNo"811201087243031"
	    	selectGamePayForm.setCardPwd(cardPwd);//cardPwd"8105360813072536466"
	    	selectGamePayForm.setAmount(amount);//支付金额amount"1"
	    	selectGamePayForm.setOrderType(orderType);//支付卡类型orderType"106"
	    	selectGamePayForm.setMerPriv("feiliuinterface");
	    	selectGamePayForm.setSnum(snum);//snum"cbea4ea2-0710-414c-8386-ee8ff46f9524"
	    	selectGamePayForm.setPackageName(pkgName);//pkgName"com.ipandalab.superpower"
	    	selectGamePayForm.setUserID(userId);//userId"B16911a2ca9dc89f4077b05c53df1a21cc9d7fd892"
	    	if(!"".equals(imei) && imei != null){
	    		selectGamePayForm.setImei(imei);
	    	}
	    	if(!"".equals(iccid) && iccid != null){
	    		selectGamePayForm.setIccid(iccid);
	    	}
	    	selectGamePayForm.setStoreId(new BigDecimal(100));//商城ID
	    	selectGamePayForm.setPayeeInfo(URLUtil.FLGPAY_INFO);
			
	    	//道具ID
	    	selectGamePayForm.setPropsId(propsId);//propsId"524611467"
			//首先生成订单号
			String orderNo = selectGamePayForm.getCompanyId() + new DSAUtil().splits();//订单号生成
			selectGamePayForm.setOrderNo(orderNo);
			String verifyStr = Dom4jUtil.getDSAStr(selectGamePayForm);//公钥签章
			selectGamePayForm.setVerStr(verifyStr);
//			if (StringUtils.isBlank(selectGamePayForm.getImei())) {
//				model.addAttribute("ret",CommonCode.PARAMETER_ERROR.getCompleteCode());
//				logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
//				return RETRUN_PAGE_ERROR;
//			}
			ServiceResult<GameOrderInfo> serviceResult = null;
			long runstarttime = System.currentTimeMillis();
			logger.info("parameter : time={}, vsign={}, token={}, payeeInfo={}, imei={}",new Object[] {
				selectGamePayForm.getTime(),
				selectGamePayForm.getVsign(),
				selectGamePayForm.getToken(),
				selectGamePayForm.getPayeeInfo(),
				selectGamePayForm.getImei()});
			try {
				System.out.println("进入try.....");
				//发送订单信息给飞流服务器
				Object payeeInfo=null;
	        	if (!StringUtils.isBlank(selectGamePayForm.getPayeeInfo())){
	        		payeeInfo=PayeeInfoFactory.parse(selectGamePayForm.getPayeeInfo());
	        	}
				String resp = Dom4jUtil.post(URLUtil.FEILIU_ORDER_PAY_URL, Dom4jUtil.getDomStr(selectGamePayForm, verifyStr));
				
				System.out.println("飞流获取订单后返回结果=="+resp);
				String respons = Dom4jUtil.getRetMsg(resp);
				
				if(respons.equals("0")){//成功
					//生成本地订单
					serviceResult  = gamePayService.getFLNewOrder(selectGamePayForm.getPackageName(), selectGamePayForm, selectGamePayForm.getStoreId(), payeeInfo);
					if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
		        		model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        		return RETRUN_PAGE_ERROR;
		        	}
					//更新订单支付状态
					//gamePayService.updateFLorderStatusByOrderId(gamePayService, 2,orderNo);
		        	// 回傳成功訊息
		            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
	//	            model.addAttribute("orderInfo", serviceResult.getData());
		            model.addAttribute("orderNo", serviceResult.getData().getPayInfo().getOrderNo());
		            logger.info(translator.getMessage(serviceResult.getResult()));
		            return RETRUN_PAGE_SUCCESS;
				}
					return RETRUN_PAGE_ERROR;
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
				System.out.println("返回客户端信息并进入catch...");
				return RETRUN_PAGE_ERROR;
			} finally {
				logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
			}
		}
	}
	
	/**
	 * 参数验证
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean getPramas(HttpServletRequest request, HttpServletResponse response){
		boolean f = false;
		String cardNo = request.getParameter("cardNo");//支付卡号
		String cardPwd = request.getParameter("cardPwd");//支付卡密
		String pkgName = request.getParameter("pkgName");//Apk包名
		String orderType = request.getParameter("orderType");//支付卡类型
		String denomination = request.getParameter("denomination");//卡面额
		String amount = request.getParameter("amount");//支付金额
		String snum = request.getParameter("snum");//渠道号
		
		if(!"".equals(cardNo) && cardNo != null){
			return true;
		}
		if(!"".equals(cardPwd) && cardPwd != null){
			return true;
		}
		if(!"".equals(pkgName) && pkgName != null){
			return true;
		}
		if(!"".equals(orderType) && orderType != null){
			return true;
		}
		if(!"".equals(denomination) && denomination != null){
			return true;
		}
		if(!"".equals(amount) && amount != null){
			return true;
		}
		if(!"".equals(snum) && snum != null){
			return true;
		}
		return f;
	}
	
	 public static void main(String[] args) {
		System.out.println("100016"+ new DSAUtil().splits());
	}
}