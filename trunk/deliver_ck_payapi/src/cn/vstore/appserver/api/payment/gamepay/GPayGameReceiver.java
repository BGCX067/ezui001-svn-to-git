package cn.vstore.appserver.api.payment.gamepay;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.vstore.appserver.form.GpayForm;
import cn.vstore.appserver.model.GamePayInformation;
import cn.vstore.appserver.service.GamePayService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 支付宝通信(接受支付结果)
 * 
 * @version $Id$
 */

@Controller
public class GPayGameReceiver {

	protected static final Logger logger = LoggerFactory
			.getLogger(GPayGameReceiver.class);
	

	// 回傳頁面
	public final static String RETRUN_PAGE_SUCCESS = "payment/gpay/success";
	public final static String RETRUN_PAGE_ERROR = "payment/gpay/fail";

	@Autowired
	protected GamePayService gamepayService;

	@Autowired
	private MessageTranslator translator;

	@RequestMapping(value = "/api/integrate/gpaygame/receiver", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String excuteApi(@ModelAttribute("GpayForm") GpayForm gpayForm,
			Model model,HttpServletRequest request) {
		
		
		long runstarttime = System.currentTimeMillis();
		logger.info("parameter : notify_data={}, sign={}", new Object[] {
				gpayForm.getNotify_data(), gpayForm.getSign() });

		try {
			// 驗證輸入參數
			if (StringUtils.isBlank(gpayForm.getSign())) {
				model.addAttribute("ret",
						CommonCode.PARAMETER_ERROR.getCompleteCode());
				return RETRUN_PAGE_ERROR;
			}

			if (StringUtils.isBlank(gpayForm.getNotify_data())) {
				model.addAttribute("ret",
						CommonCode.PARAMETER_ERROR.getCompleteCode());
				return RETRUN_PAGE_ERROR;
			}

			// 檢查sign是否正確

			
			int status=0;
			boolean b = doCheck("notify_data=" + gpayForm.getNotify_data(), gpayForm.getSign(),PaymentKey.G_PAYMENT_PUBLIC_KEY);
			if (b) {
				logger.debug("success");
				
				
				GPayRet _gpayRet=GPayRet.parseXml(gpayForm.getNotify_data());
				
				logger.info("trade_no:"+_gpayRet.getTrade_no());
				logger.info("Out_trade_no:"+_gpayRet.getOut_trade_no());
				logger.info("Pament_type:"+_gpayRet.getPament_type());
				logger.info("trade_status:"+_gpayRet.getTrade_status());
				logger.info("total_fee:"+_gpayRet.getTotal_fee());
				
				GamePayInformation paymentInformation=new GamePayInformation();
				paymentInformation.setAmount(_gpayRet.getTotal_fee());
				paymentInformation.setRetMsg(gpayForm.getNotify_data());
				paymentInformation.setStatus((_gpayRet.getTrade_status()==1||_gpayRet.getTrade_status()==2)?_gpayRet.getTrade_status():3);
				paymentInformation.setPaymentType(_gpayRet.getPament_type());
				paymentInformation.setPaymentId(_gpayRet.getTrade_no());
//				if(_gpayRet.getOut_trade_no().length()>16){
					paymentInformation.setOrderNo(_gpayRet.getOut_trade_no());
//					GamePayInformation pi=gamepayService.getOrderByOrderNo(paymentInformation.getOrderNo());
//					if((Constants.PRICE_TYPE_MONTHLY+"").equals(pi.getMyPriceType())){
//						Calendar cal=Calendar.getInstance();
//						int amount=1;
//						if(pi.getMonthlyCycle()!=null&&pi.getMonthlyCycle()>=1){
//							amount=pi.getMonthlyCycle();
//						}
//						//每月以30日計算
//						cal.add(Calendar.DAY_OF_MONTH, amount*30);
//						paymentInformation.setRightEndDate(cal.getTime());
//					}
					gamepayService.updateSuccessIpayUserPaymentLogByOrderNo(gamepayService,paymentInformation);
//				}else{				
//					paymentInformation.setId(Long.parseLong(_gpayRet.getOut_trade_no()));
//					GamePayInformation pi=gamepayService.getOrderByOrderId(paymentInformation.getId());
//					if((Constants.PRICE_TYPE_MONTHLY+"").equals(pi.getMyPriceType())){
//						Calendar cal=Calendar.getInstance();
//						int amount=1;
//						if(pi.getMonthlyCycle()!=null&&pi.getMonthlyCycle()>=1){
//							amount=pi.getMonthlyCycle();
//						}
//						//每月以30日計算
//						cal.add(Calendar.DAY_OF_MONTH, amount*30);
//						paymentInformation.setRightEndDate(cal.getTime());
//					}
//					gamepayService.updateSuccessIpayUserPaymentLog(gamepayService,paymentInformation);
//				}
				return RETRUN_PAGE_SUCCESS;
				
			} else {
				logger.debug("fail");
				return RETRUN_PAGE_ERROR;
			}
			

			
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
			return RETRUN_PAGE_ERROR;
		} finally {
			logger.info("running:"
					+ (System.currentTimeMillis() - runstarttime));
		}
	}
	public static boolean doCheck(String content, String sign, String publicKey) {
		try {
			PublicKey pubKey = getPublicKeyFromX509("RSA",
					new ByteArrayInputStream(publicKey.getBytes()));
			Signature signature = Signature.getInstance("SHA1WithRSA");
			signature.initVerify(pubKey);
			signature.update(content.getBytes("UTF-8"));
			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception e) {
			return false;
		}
	}
	
    private static void io(Reader in, Writer out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = 8192 >> 1;
        }
        char[] buffer = new char[bufferSize];
        int amount;

        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }
    }

	private static PublicKey getPublicKeyFromX509(String algorithm,
			InputStream ins) throws NoSuchAlgorithmException {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

			StringWriter writer = new StringWriter();
			io(new InputStreamReader(ins), writer, -1);

			byte[] encodedKey = writer.toString().getBytes();

			// 先base64解码
			encodedKey = Base64.decodeBase64(encodedKey);

			return keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));
		} catch (IOException ex) {

		} catch (InvalidKeySpecException ex) {

		}

		return null;
	}

}