package cn.vstore.appserver.api.payment.shenzhoufu;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.api.payment.support.PayType;
import cn.vstore.appserver.form.payment.SelectOrderPayForm;
import cn.vstore.appserver.model.VMemberInfo;
import cn.vstore.appserver.model.VOrderPayInfo;
import cn.vstore.appserver.service.OrderPayService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.DSAUtil;
import cn.vstore.appserver.util.MessageTranslator;
import cn.vstore.appserver.util.OrderNoUtil;
import cn.vstore.appserver.util.URLUtil;

@Controller
public class SelectSzfuPayApi {
	protected static final Logger logger = LoggerFactory.getLogger(SelectSzfuPayApi.class);

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/paysuc";
	public final static String RETRUN_PAGE_ERROR = "payment/payfail";

	@Autowired
	protected OrderPayService orderPayService;

	@Autowired
	private MessageTranslator translator;

	@RequestMapping(value = "/api/szfpay/order/{propsId:.+}", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(
			@ModelAttribute("SelectOrderPayForm") SelectOrderPayForm selectOrderPayForm,
			@PathVariable("propsId") String propsId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		
		String cardNo = request.getParameter("sn");//支付卡号"12312312123";//
		String cardPwd = request.getParameter("password");//支付卡密"12344123";//
		String pkgName = request.getParameter("packageName");//Apk包名"com.cn.vstore.pkgd";//
		String cardType = request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信"1";//
		String denomination = request.getParameter("cardMoney");//卡面额"5000";//
		String amount = request.getParameter("price");//支付金额"100";//
		String snum = request.getParameter("snum");//渠道号"sdf324-234sd2-9984d-3f3";//
		String userId = request.getParameter("userID");//购买用户ID系统唯一标识"zhangd@vtion.com.cn";//
		String loginId = request.getParameter("loginId");//购买用户ID系统唯一标识"2";//
		String nums = request.getParameter("num");//道具数量
		String propsType = request.getParameter("propsType");//道具类型
//		String imei = request.getParameter("imei");
//		String iccid = request.getParameter("iccid");
		System.out.println("cardNo=="+cardNo);
		System.out.println("cardPwd=="+cardPwd);
		System.out.println("pkgName=="+pkgName);
		System.out.println("cardType=="+cardType);
		System.out.println("denomination=="+denomination);
		System.out.println("amount=="+amount);
		System.out.println("snum=="+snum);
		System.out.println("propsId=="+propsId);
		System.out.println("userId=="+userId);
		System.out.println("loginId=="+loginId);
		System.out.println("nums=="+nums);
		System.out.println("propsType=="+propsType);
		if("".equals(cardNo) || cardNo == null){
			return RETRUN_PAGE_ERROR;//参数验证失败(有空的参数信息)
		}if("".equals(cardPwd) || cardPwd == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(pkgName) || pkgName == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(cardType) || cardType == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(denomination) || denomination == null){
			return RETRUN_PAGE_ERROR;
		}if("".equals(amount) || amount == null){
			return RETRUN_PAGE_ERROR;
		}
//		if("".equals(snum) || snum == null){
//			return RETRUN_PAGE_ERROR;
//		}
//		if("".equals(userId) || userId == null){
//			return RETRUN_PAGE_ERROR;
//		}
		else{
			//设置参数表单值
			SelectOrderPayForm payForm = initSelectOrderFormInfo(request,response);
			SZFOrderPayUtil szfOrderPayUtil = new SZFOrderPayUtil();
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
				int resultInt = 0;
				//判断是否是购买APP道具还是应用APP
				//购买道具(1)
				//发送订单信息给神州付服务器
				resultInt = szfOrderPayUtil.sendOrderInfoToSZF(payForm);
				logger.debug("daojuID_propsId="+propsId);
				System.out.println("神州付获取订单后返回结果状态=="+resultInt);
				if(resultInt != 200){//失败
					model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
					return RETRUN_PAGE_ERROR;
				}
				//生成本地订单
				//需要根据会员登录用户名查询主键ID
				if(!"".equals(loginId) && loginId != null){
					memberInfo = orderPayService.selectMemInfoByLoginId(loginId);
					if(memberInfo != null){
						payForm.setMemid(Long.valueOf(memberInfo.getId()));//会员ID
					}
				}
				if(!"".equals(propsId) && propsId != null){
					payForm.setItemid(propsId);//道具ID
					payForm.setPropsType(propsType);//道具类型
					payForm.setBuytype("1");//0购买APP，1支付道具类型
					if(!"".equals(nums) && nums != null){
						payForm.setNums(nums);//道具数量
					}else{
						payForm.setAccounts("1");//道具数量
					}
				}else{
					payForm.setBuytype("0");//0购买APP，1支付道具类型
					payForm.setAccounts("0");//道具数量
				}
				payForm.setSnum(snum);//SDK渠道号
				serviceResult  = orderPayService.getSZFNewOrderInfo(payForm);
		        // 回傳成功訊息
		        model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        model.addAttribute("orderNo", serviceResult.getData().getOrderno());
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
	
	/**
	 * 获取参数
	 * @param request
	 * @param response
	 * @return
	 */
	private SelectOrderPayForm initSelectOrderFormInfo(HttpServletRequest request,HttpServletResponse response){
		SelectOrderPayForm orderPayForm = new SelectOrderPayForm();
		String cardNo = request.getParameter("sn");//支付卡号
		String cardPwd = request.getParameter("password");//支付卡密
		String pkgName = request.getParameter("packageName");//Apk包名
		String cardType = request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
		String denomination = request.getParameter("cardMoney");//卡面额
		String amount = request.getParameter("price");//支付金额
		String snum = request.getParameter("snum");//渠道号
		String userId = request.getParameter("userID");//购买用户ID系统唯一标识
//		String memId = request.getParameter("memId");//购买用户ID系统唯一标识
//		String cardNo = "12312312123";//request.getParameter("sn");//支付卡号
//		String cardPwd = "12344123";//request.getParameter("password");//支付卡密
//		String pkgName = "com.cn.vstore.pkgd";//request.getParameter("packageName");//Apk包名
//		String cardType = "1";//request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
//		String denomination = "5000";//request.getParameter("cardMoney");//卡面额(单位：分)
//		String amount = "100";//request.getParameter("price");//支付金额(单位：分)
//		String snum = "sdf324-234sd2-9984d-3f3";//request.getParameter("snum");//渠道号
//		String userId = "zhangd@vtion.com.cn";//request.getParameter("userID");//购买用户ID系统唯一标识
//		String memId = "2";//request.getParameter("memId");//购买用户ID系统唯一标识(网讯会员)
		
		orderPayForm.setCardno(cardNo);
		orderPayForm.setCardpwd(cardPwd);
		orderPayForm.setPackageName(pkgName);
		orderPayForm.setCardtype(cardType);
		orderPayForm.setDenomination(denomination);
		orderPayForm.setAmount(amount);
		orderPayForm.setSnum(snum);
		orderPayForm.setUserID(userId);
		orderPayForm.setPaytype(PayType.SHENZHOUFU);//表示神州付
		orderPayForm.setVersion(URLUtil.SZF_VERSION);//版本
		orderPayForm.setMerchantsid(URLUtil.SZF_MERID);//商户ID
		orderPayForm.setMeruseremail(URLUtil.SZF_MERUSERMAIL);//商户email
		orderPayForm.setMerusername(URLUtil.SZF_MERUSERNAME);//商户名称
		orderPayForm.setPrivatefield(URLUtil.SZF_PRIVATEFIELD);//商户私有数据
		orderPayForm.setVerifytype(URLUtil.SZF_VERIFYTYPE);//加密类型
		orderPayForm.setProductid(URLUtil.SZF_PRIVATEKEY);//设置神州付的privatekey
		orderPayForm.setChannelid(URLUtil.SZF_DESKEY);//设置神州付的deskey
		orderPayForm.setCreatedate(new Date());//订单日期
		
		//生成订单号
		String orderNo = OrderNoUtil.getOrderNoInfo();
		orderPayForm.setOrderno(orderNo);
		String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(denomination, cardNo, cardPwd, URLUtil.SZF_DESKEY);   //充值卡加密信息
	    String combineString = URLUtil.SZF_VERSION + URLUtil.SZF_MERID + amount + orderNo + URLUtil.SZF_RETURNURL + cardInfo + URLUtil.SZF_PRIVATEFIELD + URLUtil.SZF_VERIFYTYPE + URLUtil.SZF_PRIVATEKEY;
	    String md5String = DigestUtils.md5Hex(combineString); //md5加密串
	    orderPayForm.setVerifystr(md5String);//设置加密字符串
	    
		return orderPayForm;
	}
	
	 public static void main(String[] args) {
		System.out.println("100016"+ new DSAUtil().splits());
	}
}