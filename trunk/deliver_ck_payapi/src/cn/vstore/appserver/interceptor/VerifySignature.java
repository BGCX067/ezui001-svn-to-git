package cn.vstore.appserver.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.AuthenticationService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

import com.sti.security.Dsa;

/**
 * 共同參數驗證
 */
public class VerifySignature extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VerifySignature.class);

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private MessageTranslator translator;

    // 回傳頁面
    private final String RETRUN_PAGE_ERROR = "/content/error.jsp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String store = request.getParameter("store"); // store package name
        String time = request.getParameter("time"); // 手機端時間millisecond
        String imei = request.getParameter("imei");
        String imsi = request.getParameter("imsi");
        String iccid = request.getParameter("iccid");
        String userId = request.getParameter("userId"); // 帳號
        String pwd = request.getParameter("pwd"); // 密碼
        String token = request.getParameter("token"); // token
        String vsign = request.getParameter("vsign");

        String ret = CommonCode.SUCCESS.getCompleteCode();
        
        long n=new Date().getTime();
//        System.out.println("n===="+n);
        long t=0;
    	try{
    		t=new Long(time);
//    		System.out.println("t===="+t);
    	}catch(Throwable e){
    	}
    	long stime = 100*365*24*60*60*1000*1000l+5000000000000l;
        StoreInfo storeInfo=null;
        if (t<=0) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if(Math.abs(n-t)> stime){ //else if(Math.abs(n-t)>(48*60*60*1000)){\
//        	System.out.println("48*60*60*1000===" +(48*60*60*1000));
//        	System.out.println("Math.abs(n-t)===="+Math.abs(n-t));
        	ret = CommonCode.TIME_ZONE_48HOURS_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(store)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(vsign)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        }else{
        	storeInfo=authenticationService.getStore(store);
        	if(storeInfo==null){
        		logger.debug("cann't find store="+store);
        		ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        	}else{
        		request.setAttribute("storeInfo", storeInfo);
                String vstring=store+"|"+time;
                if(userId!=null&&pwd!=null){
                	vstring=store+"|"+time+"|"+userId+"|"+pwd;
                }else if(token!=null){
                	vstring=store+"|"+time+"|"+token;
                }else if(imei!=null&&imsi!=null&&iccid!=null){
                	vstring=store+"|"+time+"|"+imei+"|"+imsi+"|"+iccid;
                }
                logger.debug("vstring="+vstring);
                logger.info("vstring==="+vstring);
                logger.info("vsign==="+vsign);
        		if(storeInfo.getPubKeyBase64()==null||!Dsa.verify(vstring,vsign,storeInfo.getPubKeyBase64())){
        			 ret=CommonCode.PARAMETER_ERROR.getCompleteCode();
        			 logger.info("sign verify failed.");
        		}
        	}
        }

        if (!ret.equals(CommonCode.SUCCESS.getCompleteCode())) {
            request.setAttribute("ret", ret);
            request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
            logger.warn(translator.getMessage(CommonCode.PARAMETER_ERROR, false));
            return false;
        } else {
            return true;
        }
    }
}