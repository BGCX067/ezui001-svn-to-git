package cn.vstore.appserver.api.payment.unionpay;

import java.io.InputStream;
import java.text.SimpleDateFormat;
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
public class UnionOrderPayApi {
    protected static final Logger logger = LoggerFactory.getLogger(UnionOrderPayApi.class);

    // 回傳頁面
    public final static String RETRUN_PAGE_SUCCESS = "payment/unionPayment";
    public final static String RETRUN_PAGE_ERROR = "error";
	static final String G_PAYMENT_PUBLIC_KEY = "MIIDuDCCAyGgAwIBAgIQaOXUUCzukC6m5EAlw0LdZjANBgkqhkiG9w0BAQUFADAkMQswCQYDVQQGEwJDTjEVMBMGA1UEChMMQ0ZDQSBURVNUIENBMB4XDTExMDgxNzAyNDU1M1oXDTEyMDgxNzAyNDU1M1owfjELMAkGA1UEBhMCQ04xFTATBgNVBAoTDENGQ0EgVEVTVCBDQTERMA8GA1UECxMITG9jYWwgUkExFDASBgNVBAsTC0VudGVycHJpc2VzMS8wLQYDVQQDFCYwNDFAWjIwMTEwODE3QDg5ODAwMDAwMDAwMDAwMkAwMDAwMDAwMzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAzD7Xy03ptoXR7jx3BxGD5GN2Fsivu/QprnYZF+Axby8LjVNGs97tHn8CHfXzvFMqAvsd4dkKzKrTG+dOmrlunYLGFrntIHl8Mx3liFkGLYFuJUy1+HF/hIRAMPIkDux6AAhbbCZlawdx5faHkM5OQg2KGeBcD+8NUJA6IYOunIUCAwEAAaOCAY8wggGLMB8GA1UdIwQYMBaAFEZy3CVynwJOVYO1gPkL2+mTs/RFMB0GA1UdDgQWBBSrHyJHSuW1lYnmxBBh6ulilF4xSTALBgNVHQ8EBAMCBPAwDAYDVR0TBAUwAwEBADA7BgNVHSUENDAyBggrBgEFBQcDAQYIKwYBBQUHAwIGCCsGAQUFBwMDBggrBgEFBQcDBAYIKwYBBQUHAwgwgfAGA1UdHwSB6DCB5TBPoE2gS6RJMEcxCzAJBgNVBAYTAkNOMRUwEwYDVQQKEwxDRkNBIFRFU1QgQ0ExDDAKBgNVBAsTA0NSTDETMBEGA1UEAxMKY3JsMTI3XzE1MzCBkaCBjqCBi4aBiGxkYXA6Ly90ZXN0bGRhcC5jZmNhLmNvbS5jbjozODkvQ049Y3JsMTI3XzE1MyxPVT1DUkwsTz1DRkNBIFRFU1QgQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDQYJKoZIhvcNAQEFBQADgYEARfg4YNGNETrJx+gy74UmPJ326T7H2hIE/lRcTyonq4NFpXmssau+TDV7btLUuhDxBGF1JysknFjeNAKMl9ZFGjKbOGGpQ7nfnEC8HIM7cp2n+gSlADRZbc8PHrqxLbjxsKoSUUFfh3PhfNXtWLfTxi5TT+hm6coV1K/EUX4t0AY=";
	@Autowired
	protected OrderPayService orderPayService;
	
    @Autowired
	private MessageTranslator translator;
	
	@RequestMapping(value = "/api/unionpay/order/{propsId:.+}", method = {
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
//		String cardNo = "12312312123";//request.getParameter("sn");//支付卡号
//		String cardPwd = "12344123";//request.getParameter("password");//支付卡密
//		String pkgName = "com.cn.vstore.pkgd";//request.getParameter("packageName");//Apk包名
//		String cardType = "1";//request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
//		String denomination = "5000";//request.getParameter("cardMoney");//卡面额(单位：分)
//		String amount = "100";//request.getParameter("price");//支付金额(单位：分)
//		String snum = "sdf324-234sd2-9984d-3f3";//request.getParameter("snum");//渠道号
//		String userId = "hhh@126.com";//request.getParameter("userID");//购买用户ID系统唯一标识
//		String memId = "2";//request.getParameter("memId");//购买用户ID系统唯一标识(网讯会员)
//		String imei = request.getParameter("imei");
//		String iccid = request.getParameter("iccid");
		String merchantOrderDesc=request.getParameter("propDesc");
//		String checkSignUrl=request.getParameter("checkSignUrl");
//		String merchantPublicCe=request.getParameter("merchantPublicCe");
//		String backUrl=request.getParameter("backUrl");
//		String privateKeyFileName=request.getParameter("privateKeyFileName");
//		String privateKeyAlias=request.getParameter("privateKeyAlias");
//		String privateKeyPassword=request.getParameter("privateKeyPassword");
//		boolean test=Boolean.valueOf(request.getParameter("test"));
	//	String merchantOrderDesc="abcd";
	//	String merchantOrderDesc = request.getParameter("merchantOrderDesc");
		String checkSignUrl="http://211.154.166.219/qzjy/MerOrderAction/deal.action";
		String merchantPublicCe=G_PAYMENT_PUBLIC_KEY;
		String backUrl=ConstantPayUrl.getServerPath(request)+ConstantPayUrl.UNION_RECEIVER;
		String privateKeyFileName="898000000000002.p12";
		String privateKeyAlias="889ce7a52067a87f905c91f502c69644_d1cba47d-cbb1-4e29-9d77-8d1fe1b0dccd";
		String privateKeyPassword="898000000000002";
		boolean test=true;
		System.out.println("cardNo=="+cardNo);
		System.out.println("cardPwd=="+cardPwd);
		System.out.println("pkgName=="+pkgName);
		System.out.println("cardType=="+cardType);
		System.out.println("denomination=="+denomination);
		System.out.println("amount=="+amount);
		System.out.println("snum=="+snum);
		System.out.println("userId=="+userId);
		if("".equals(pkgName) || pkgName == null){
			return RETRUN_PAGE_ERROR;
		}
		if("".equals(amount) || amount == null){
			return RETRUN_PAGE_ERROR;
		}
//		if("".equals(snum) || snum == null){
//			return RETRUN_PAGE_ERROR;
//		}
//		if("".equals(propsId) || propsId == null){
//			return RETRUN_PAGE_ERROR;
//		}
		if("".equals(userId) || userId == null){
			return RETRUN_PAGE_ERROR;
		}
//		if("".equals(memId) || memId == null){
//			return RETRUN_PAGE_ERROR;
//		}
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
					serviceResult  = orderPayService.getNewOrder(merchantOrderDesc, checkSignUrl, merchantPublicCe, backUrl, payForm, privateKeyFileName, privateKeyAlias, privateKeyPassword, test);
				   	if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
		        		model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        		return RETRUN_PAGE_ERROR;
		        	}
		        	// 回傳成功訊息
		            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
		        //    model.addAttribute("orderInfo", serviceResult.getData());
		            model.addAttribute("orderNo", serviceResult.getData().getOrderno());
		            model.addAttribute("merchantsId", serviceResult.getData().getMerchantsid());
		        	String merchantOrderTime = new SimpleDateFormat("yyyyMMddHHmmss").format( serviceResult.getData().getCreatedate());
		        	 model.addAttribute("time", merchantOrderTime);
			       String xmlSign3=     XmlDefinition.CreateOriginalSign3(serviceResult.getData().getMerchantsid(), serviceResult.getData().getOrderno(),merchantOrderTime);
			       InputStream PrivateSign = getClass().getClassLoader().getResourceAsStream(privateKeyFileName);
			      	SignBy getSign = new SignBy();
				    String xmlSign3Res = getSign.createSign(xmlSign3, privateKeyAlias, privateKeyPassword, PrivateSign);
				    model.addAttribute("sign", xmlSign3Res);
				    model.addAttribute("backUrl", backUrl);
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
/*		String cardNo = request.getParameter("sn");//支付卡号
		String cardPwd = request.getParameter("password");//支付卡密
		String pkgName = request.getParameter("packageName");//Apk包名
		String cardType = request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
		String denomination = request.getParameter("cardMoney");//卡面额
		String amount = request.getParameter("price");//支付金额
		String snum = request.getParameter("snum");//渠道号
		String userId = request.getParameter("userID");//购买用户ID系统唯一标识
		String merusername = request.getParameter("merusername");
		String merchantsid = request.getParameter("merchantsid");*/
		String pkgName = request.getParameter("packageName");//Apk包名
//		String cardType = request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
//		String denomination = request.getParameter("cardMoney");//卡面额
		String amount = request.getParameter("price");//支付金额
		String propsType=request.getParameter("propsType");
//		String snum = request.getParameter("snum");//渠道号
		String userId = request.getParameter("userID");//购买用户ID系统唯一标识
     	String memId = request.getParameter("memId");//购买用户ID系统唯一标识
     	String num = request.getParameter("num");//购买用户ID系统唯一标识
//		String cardNo = "12312312123";//request.getParameter("sn");//支付卡号
//		String cardPwd = "12344123";//request.getParameter("password");//支付卡密
//		String pkgName = "com.cn.vstore.pkgd";//request.getParameter("packageName");//Apk包名
//		String cardType = "1";//request.getParameter("cardTypeCombine");//支付卡类型0：移动 1：联通 2：电信
//		String denomination = "5000";//request.getParameter("cardMoney");//卡面额(单位：分)
//		String amount = "100";//request.getParameter("price");//支付金额(单位：分)
//		String snum = "sdf324-234sd2-9984d-3f3";//request.getParameter("snum");//渠道号
//		String userId = "zhangd@vtion.com.cn";//request.getParameter("userID");//购买用户ID系统唯一标识
//		String memId = "2";//request.getParameter("memId");//购买用户ID系统唯一标识(网讯会员)
		String merusername="联通华建";
		String merchantsid="898000000000002";
		orderPayForm.setUserID(userId);
		orderPayForm.setMerusername(merusername);
		orderPayForm.setMerchantsid(merchantsid);
		orderPayForm.setCreatedate(new Date());
		orderPayForm.setAmount(amount);
		orderPayForm.setPropsType(propsType);
		orderPayForm.setAccounts(num);
		orderPayForm.setPackageName(pkgName);
		orderPayForm.setUserID(userId);
		orderPayForm.setPaytype(PayType.UNION);
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
