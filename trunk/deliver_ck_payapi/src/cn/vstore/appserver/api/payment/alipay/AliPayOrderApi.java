package cn.vstore.appserver.api.payment.alipay;

import java.util.Date;

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

import cn.vstore.appserver.api.payment.support.ConstantPayUrl;
import cn.vstore.appserver.api.payment.support.PayType;
import cn.vstore.appserver.form.payment.SelectOrderPayForm;
import cn.vstore.appserver.model.VMemberInfo;
import cn.vstore.appserver.model.VOrderPayInfo;
import cn.vstore.appserver.service.OrderPayService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.util.MessageTranslator;
import cn.vstore.appserver.util.OrderNoUtil;

@Controller
public class AliPayOrderApi {
	protected static final Logger logger = LoggerFactory.getLogger(AliPayOrderApi.class);
    // 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/gamepay/selectGamePay";

	public final static String RETRUN_PAGE_ERROR = "error";

	@Autowired
	protected OrderPayService orderPayService;

	@Autowired
	private MessageTranslator translator;

	@RequestMapping(value = "/api/alipay/order/{propsId:.+}", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(
			@ModelAttribute("SelectOrderPayForm") SelectOrderPayForm selectOrderPayForm,
			@PathVariable("propsId") String propsId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		
		String cardNo = request.getParameter("sn");//支付卡号"12312312123";//
		String cardPwd = request.getParameter("password");//支付卡密"12344123";//
		//String pkgName = request.getParameter("packageName");//Apk包名"com.cn.vstore.pkgd";//
		String cardType = request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信"1";//
		String denomination = request.getParameter("cardMoney");//卡面额"5000";//
		String amount = request.getParameter("price");//支付金额"100";//
		String userID = request.getParameter("userID");//渠道号"sdf324-234sd2-9984d-3f3";//
//		String userId = request.getParameter("userID");//购买用户ID系统唯一标识"zhangd@vtion.com.cn";//
//		String memId = request.getParameter("memId");//购买用户ID系统唯一标识"2";//
//		String cardNo = "12312312123";//request.getParameter("sn");//支付卡号
//		String cardPwd = "12344123";//request.getParameter("password");//支付卡密
		String pkgName = "com.cn.vstore.pkgd";//request.getParameter("packageName");//Apk包名
//		String cardType = "1";//request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
//		String denomination = "5000";//request.getParameter("cardMoney");//卡面额(单位：分)
//		String amount = "100";//request.getParameter("price");//支付金额(单位：分)
//		String snum = "sdf324-234sd2-9984d-3f3";//request.getParameter("snum");//渠道号
		String userId = "hhh@126.com";//request.getParameter("userID");//购买用户ID系统唯一标识
	//	String memId = "2";//request.getParameter("memId");//购买用户ID系统唯一标识(网讯会员)
//		String imei = request.getParameter("imei");
//		String iccid = request.getParameter("iccid");
//		String merchantOrderDesc=request.getParameter("merchantOrderDesc");
//		String checkSignUrl=request.getParameter("checkSignUrl");
//		String merchantPublicCe=request.getParameter("merchantPublicCe");
//		String backUrl=request.getParameter("backUrl");
//		String privateKeyFileName=request.getParameter("privateKeyFileName");
//		String privateKeyAlias=request.getParameter("privateKeyAlias");
//		String privateKeyPassword=request.getParameter("privateKeyPassword");
//		boolean test=Boolean.valueOf(request.getParameter("test"));
	//	String merchantOrderDesc = request.getParameter("merchantOrderDesc");
		String loginId=request.getParameter("loginId");
		System.out.println("cardNo=="+cardNo);
		System.out.println("cardPwd=="+cardPwd);
		System.out.println("pkgName=="+pkgName);
		System.out.println("cardType=="+cardType);
		System.out.println("denomination=="+denomination);
		System.out.println("amount=="+amount);
		System.out.println("userId=="+userId);
		if("".equals(pkgName) || pkgName == null){
			return RETRUN_PAGE_ERROR;
		}
		if("".equals(userId) || userId == null){
			return RETRUN_PAGE_ERROR;
		}
		else{
			//设置参数表单值
			SelectOrderPayForm payForm = initSelectOrderFormInfo(request,response);
			System.out.println("OrderNo===="+payForm.getOrderno());
			ServiceResult<VOrderPayInfo> serviceResult = null;
			long runstarttime = System.currentTimeMillis();
			logger.info("parameter : time={}, vsign={}, token={}, payeeInfo={}, imei={}",new Object[] {
				selectOrderPayForm.getTime(),
				selectOrderPayForm.getVsign(),
				selectOrderPayForm.getToken(),
				selectOrderPayForm.getImei()});
			try {
				System.out.println("entry try.....");
				VMemberInfo memberInfo = null;
				//发送订单信息给神州付服务器
					//生成本地订单
					//需要根据会员登录用户名查询主键ID
					if(!"".equals(loginId) && loginId != null){
						memberInfo = orderPayService.selectMemInfoByLoginId(loginId);
						if(memberInfo!=null)
						payForm.setMemid(Long.valueOf(memberInfo.getId()));//会员ID
					}
					if(!"".equals(propsId) && propsId != null){
						payForm.setItemid(propsId);//道具ID
						payForm.setBuytype("1");//0购买APP，1支付道具类型
					}else{
						payForm.setBuytype("0");//0购买APP，1支付道具类型
					}
					serviceResult  = orderPayService.getSZFNewOrderInfo(payForm);
				   	if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
		        		model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        		return RETRUN_PAGE_ERROR;
		        	}
		        	// 回傳成功訊息
		            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        //    model.addAttribute("orderInfo", serviceResult.getData());
		            model.addAttribute("orderInfo", serviceResult.getData());
		            String url=request.getScheme()+":"+request.getServerName()+request.getContextPath();
		            model.addAttribute("backUrl",ConstantPayUrl.getServerPath(request)+ConstantPayUrl.ALIPAY_RECEIVER);
		            logger.info(translator.getMessage(serviceResult.getResult()));
		            return RETRUN_PAGE_SUCCESS;
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
				System.out.println("return info and entry catch...");
				return RETRUN_PAGE_ERROR;
			} finally {
				logger.info("running:"+ (System.currentTimeMillis() - runstarttime));
			}
		}
	}
	
	
	private SelectOrderPayForm initSelectOrderFormInfo(HttpServletRequest request,HttpServletResponse response){
		SelectOrderPayForm orderPayForm = new SelectOrderPayForm();
//		String cardNo = request.getParameter("sn");//支付卡号
//		String cardPwd = request.getParameter("password");//支付卡密
		String pkgName = request.getParameter("packageName");//Apk包名
//		String cardType = request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
//		String denomination = request.getParameter("cardMoney");//卡面额
		String amount = request.getParameter("price");//支付金额
		String propsType=request.getParameter("propsType");
//		String snum = request.getParameter("snum");//渠道号
		String userId = request.getParameter("userID");//购买用户ID系统唯一标识
     	String memId = request.getParameter("memId");//购买用户ID系统唯一标识
     	String nums = request.getParameter("num");//道具ID
//		String merusername = request.getParameter("merusername");
//		String merchantsid = request.getParameter("merchantsid");
		String pcId = "CMJFK00010001";//request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
		String pmId = "CMJFK";
		String denomination = "10000";//request.getParameter("cardMoney");//卡面额(单位：分)
		String snum = "sdf324-234sd2-9984d-3f3";//request.getParameter("snum");//渠道号
	//	String userId = "hhh@126.com";//request.getParameter("userID");//购买用户ID系统唯一标识
		String merchantsid = "240009";	//商户ID, 分配的商户ID号
		String merusername="支付宝";
//		String merchantsid="898000000000002";
		
		orderPayForm.setMerusername(merusername);
		orderPayForm.setMerchantsid(merchantsid);
		orderPayForm.setCreatedate(new Date());
		orderPayForm.setPaytype(PayType.ZHIFUBAO);
		orderPayForm.setAmount(amount);
		orderPayForm.setPropsType(propsType);
		orderPayForm.setNums(nums);
		orderPayForm.setPackageName(pkgName);
		orderPayForm.setUserID(userId);
/*		orderPayForm.setVersion(URLUtil.SZF_VERSION);//版本
		orderPayForm.setMerchantsid(URLUtil.SZF_MERID);//商户ID
		orderPayForm.setMeruseremail(URLUtil.SZF_MERUSERMAIL);//商户email
		orderPayForm.setMerusername(URLUtil.SZF_MERUSERNAME);//商户名称
		orderPayForm.setPrivatefield(URLUtil.SZF_PRIVATEFIELD);//商户私有数据
		orderPayForm.setVerifytype(URLUtil.SZF_VERIFYTYPE);//加密类型
*/		
		//生成订单号
		String orderNo = OrderNoUtil.getOrderNoInfo();
		orderPayForm.setOrderno(orderNo);
/*		String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(denomination, cardNo, cardPwd, URLUtil.SZF_DESKEY);   //充值卡加密信息
	    String combineString = URLUtil.SZF_VERSION + URLUtil.SZF_MERID + amount + orderNo + URLUtil.SZF_RETURNURL + cardInfo + URLUtil.SZF_PRIVATEFIELD + URLUtil.SZF_VERIFYTYPE + URLUtil.SZF_PRIVATEKEY;
	    String md5String = DigestUtils.md5Hex(combineString); //md5加密串
	    orderPayForm.setVerifystr(md5String);//设置加密字符串
*/	    
		return orderPayForm;
	}
}
