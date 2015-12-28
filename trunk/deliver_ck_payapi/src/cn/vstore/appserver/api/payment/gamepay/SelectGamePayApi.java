package cn.vstore.appserver.api.payment.gamepay;



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

import cn.vstore.appserver.api.support.PayeeInfoFactory;
import cn.vstore.appserver.form.payment.SelectGamePayForm;
import cn.vstore.appserver.model.GameOrderInfo;
import cn.vstore.appserver.service.GamePayService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 21.	新增訂單，並獲取定單號碼(23)
 * 
 * @version $Id$
 */
@Controller
public class SelectGamePayApi {

    protected static final Logger logger = LoggerFactory.getLogger(SelectGamePayApi.class);

    // 回傳頁面
    public final static String RETRUN_PAGE_SUCCESS = "payment/gamepay/selectGamePay";
    public final static String RETRUN_PAGE_ERROR = "error";

    @Autowired
    protected GamePayService gamePayService;

    @Autowired
    private MessageTranslator translator;

    @RequestMapping(value = "/api/gamepay/order/{propsId:.+}", method = { RequestMethod.GET, RequestMethod.POST })
    public String excuteApi(@ModelAttribute("SelectPaymentForm") SelectGamePayForm selectGamePayForm,
    		@PathVariable("propsId") String propsId, Model model,
    		HttpServletRequest request, HttpServletResponse response) {
    //测试数据
    	/*	
    	selectGamePayForm.setPackageName("cn.com.vtion.sample");	
    	selectGamePayForm.setAver("10");
    	selectGamePayForm.setArel("2.3.3");
    	selectGamePayForm.setCver("1");
    	selectGamePayForm.setLang("zh_CN");
    	selectGamePayForm.setImei("000000000000000");
    	selectGamePayForm.setImsi("310260000000000");
    	selectGamePayForm.setIccid("89014103211118510720"); 
    	selectGamePayForm.setDvc("sdk");
    	selectGamePayForm.setWpx("320");
    	selectGamePayForm.setHpx("480");
    	selectGamePayForm.setPropsId("54321");
    	selectGamePayForm.setPropsType("1");
    	selectGamePayForm.setNum("5");
    	selectGamePayForm.setPrice("2.5");
    	selectGamePayForm.setUserID("Song");
    	selectGamePayForm.setSnum("0374cbe1-0d22-4a72-9e49-a0f9fb2a1d06");
    	  //测试旧数据
    	selectGamePayForm.setAppfilter("appfilter");
    	selectGamePayForm.setAver("aver");
    	selectGamePayForm.setBackEndUrl("backEndUrl");
    	selectGamePayForm.setCheckSignUrl("checkSignUrl");
    	selectGamePayForm.setCver("cver");
    	selectGamePayForm.setDvc("dvc");
    	selectGamePayForm.setHpx("hpx");
    	selectGamePayForm.setIccid("iccid");
    	selectGamePayForm.setImei("imei");
    	selectGamePayForm.setImsi("imsi");
    	selectGamePayForm.setIsTest("isTest");
    	selectGamePayForm.setLang("lang");
    	selectGamePayForm.setMac("mac");
    	selectGamePayForm.setMerchantId("merchantId");
    	selectGamePayForm.setMerchantName("merchantName");
    	selectGamePayForm.setMerchantPublicCer("merchantPublicCer");
    	selectGamePayForm.setPayeeInfo("<gpay><partner>2088501902645082</partner><seller>alipay@vtion.com.cn</seller></gpay>");
    	selectGamePayForm.setPrivateKeyAlias("privateKeyAlias");
    	selectGamePayForm.setPrivateKeyFileName("privateKeyFileName");
    	selectGamePayForm.setPrivateKeyPassword("privateKeyPassword");
    	selectGamePayForm.setPsize("psize");
    	selectGamePayForm.setSnum("snum");
    	selectGamePayForm.setStore("store");
    	selectGamePayForm.setStoreId(new BigDecimal("15"));
    	selectGamePayForm.setStoreOid("storeOid");
    	selectGamePayForm.setTime("time");
    	selectGamePayForm.setToken("f85aa5a5c4c00945f1d7e674c2be7408");//wg4213
    	selectGamePayForm.setTransTimeout("transTimeout");
    	selectGamePayForm.setVsign("vsign");
    	selectGamePayForm.setWpx("wpx");*/
      if (StringUtils.isBlank(selectGamePayForm.getImei())) {
      model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
      logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
      return RETRUN_PAGE_ERROR;
      }
        long runstarttime = System.currentTimeMillis();
        logger.info("parameter : time={}, vsign={}, token={}, payeeInfo={}, imei={}", new Object[] { selectGamePayForm.getTime(),selectGamePayForm.getVsign(),
        		selectGamePayForm.getToken(),selectGamePayForm.getPayeeInfo(), selectGamePayForm.getImei() });
        
        try {
//        	StoreInfo storeInfo = new StoreInfo();
//        	storeInfo.setClientDownloadUrl("clientDownloadUrl");
//        	storeInfo.setId(new BigDecimal("111"));
//        	storeInfo.setIsApproved(5);
//        	storeInfo.setIsDownload(1);
//        	storeInfo.setIsLogin(2);
//        	storeInfo.setPubKeyBase64("pubKeyBase64");
//        	storeInfo.setPwdResetUrl("pwdResetUrl");
//        	storeInfo.setStoreName("storeName");
//        	storeInfo.setStorePkgName("storePkgName");
            // 驗證輸入參數
//        	StoreInfo storeInfo = (StoreInfo)request.getAttribute("storeInfo");
//        	if(storeInfo==null){
//        		model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
//        		logger.info(translator.getMessage(CommonCode.PARAMETER_ERROR), false);
//        		return RETRUN_PAGE_ERROR;
//        	}
        	Object payeeInfo=null;
        	if (!StringUtils.isBlank(selectGamePayForm.getPayeeInfo())){
        		payeeInfo=PayeeInfoFactory.parse(selectGamePayForm.getPayeeInfo());
        	}
        	
        	ServiceResult<GameOrderInfo> serviceResult = gamePayService.getNewOrder(propsId,selectGamePayForm, selectGamePayForm.getStoreId(),payeeInfo);
//        	ServiceResult<OrderInfo> serviceResult = gamePayService.getNewOrder(propsId,selectGamePayForm, storeInfo.getId(),payeeInfo);
        	if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
        		model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
        		return RETRUN_PAGE_ERROR;
        	}
        	// 回傳成功訊息
            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
            model.addAttribute("orderInfo", serviceResult.getData());
            logger.info(translator.getMessage(serviceResult.getResult()));
            return RETRUN_PAGE_SUCCESS;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
            return RETRUN_PAGE_ERROR;
        } finally {
            logger.info("running:" + (System.currentTimeMillis() - runstarttime));
        }
    }
}