package cn.vstore.appserver.web.api;

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

import cn.vstore.appserver.form.RegisterForm;
import cn.vstore.appserver.model.Credential;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.ApplicationService;
import cn.vstore.appserver.service.ResultCode.Web_RegisterCode;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 *  註冊流程
 * 
 * @version $Id$
 */
@Controller("webRegisterApi")
public class RegisterApi {

    private static final Logger logger = LoggerFactory.getLogger(RegisterApi.class);

    // 回傳頁面
    private final static String RETRUN_PAGE_SUCCESS = "web/register";
    private final static String RETRUN_PAGE_ERROR = "error";

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private MessageTranslator translator;

    @RequestMapping(value = "/api/web/register", method = { RequestMethod.GET, RequestMethod.POST })
    public String excuteApi(@ModelAttribute("RegisterForm") RegisterForm registerForm, Model model,HttpServletRequest request, HttpServletResponse response) {

        long runstarttime = System.currentTimeMillis();
        logger.info("RegisterApi excuteApi....................");
        try {
        	//驗證註冊資料　
        	String re = validate(registerForm,model);
        	if( re!=null )
        		return re;

            // 驗證輸入參數
        	StoreInfo storeInfo=(StoreInfo)request.getAttribute("storeInfo");
        	if(storeInfo==null){
        		model.addAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
                logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
                return RETRUN_PAGE_ERROR;
        	}
            // registerAccount Service
        	ServiceResult<Credential> serviceResult = applicationService.registerAccountForWeb
        		(storeInfo.getId(),registerForm.getUserId(),registerForm.getPwd(),"0","0",registerForm.getNickname(),registerForm.getSignature());
        	
        	if (!CommonCode.SUCCESS.getCompleteCode().equals(serviceResult.getResult().getCompleteCode())) {
                // 回傳錯誤訊息
                model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
                // 印出Warn Log
                if (serviceResult.isError()) {
                    logger.error(translator.getMessage(serviceResult), serviceResult.getThrowable());
                } else {
                    logger.warn(translator.getMessage(serviceResult));
                }
                return RETRUN_PAGE_ERROR;
            }
            // 回傳成功訊息
           
            model.addAttribute("ret", serviceResult.getResult().getCompleteCode());
            model.addAttribute("registerData", (Credential) serviceResult.getData());
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

	private String validate(RegisterForm registerForm, Model model) {
		//驗證帳號
		String regex = "^([\\w]+)(([-\\.][\\w]+)?)*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		System.out.println(" registerForm.getUserId() : "+registerForm.getUserId());
		if( registerForm.getUserId()==null ){
			//1.是否為null
			model.addAttribute("ret", Web_RegisterCode.INVALID_USER_ID.getCompleteCode());
            return RETRUN_PAGE_ERROR;
		}else if( !registerForm.getUserId().matches(regex) ){
			//2.是否符合E-mail格式
			model.addAttribute("ret", Web_RegisterCode.INVALID_USER_ID.getCompleteCode());
            return RETRUN_PAGE_ERROR;
		}else if( registerForm.getUserId().length()>100 ){
			//2.是否長度大於100
			model.addAttribute("ret", Web_RegisterCode.INVALID_ACCOUNT_LENGTH.getCompleteCode());
            return RETRUN_PAGE_ERROR;
		}
		
		//驗證密碼
		if( registerForm.getPwd().length()<6 || registerForm.getPwd().length()>20 ){
			model.addAttribute("ret", Web_RegisterCode.INVALID_PASSWORD.getCompleteCode());
            return RETRUN_PAGE_ERROR;
		}
		
		//驗證暱稱
		if( registerForm.getNickname().length()<=0 || registerForm.getNickname().length()>20 ){
			model.addAttribute("ret", Web_RegisterCode.INVALID_NICKNAME.getCompleteCode());
            return RETRUN_PAGE_ERROR;
		}
		
		//驗證個性簽名
		if( registerForm.getSignature().length()>10 ){
			model.addAttribute("ret", Web_RegisterCode.INVALID_SIGNATURE.getCompleteCode());
            return RETRUN_PAGE_ERROR;
		}
		
		return null;
	}
}