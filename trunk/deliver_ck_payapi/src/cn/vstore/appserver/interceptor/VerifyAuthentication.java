package cn.vstore.appserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vstore.appserver.model.Account;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.service.AuthenticationService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 權限驗證
 */
public class VerifyAuthentication extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VerifyAuthentication.class);

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MessageTranslator translator;

    // 回傳頁面
    private final String RETRUN_PAGE_ERROR = "/content/error.jsp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String imei = request.getParameter("imei");
        String token = request.getParameter("token");
        String iccid = request.getParameter("iccid");
        logger.info("[VerifyAuthentication] imei = {}, iccid = {}, token = {} ...", new Object[] {
              imei, iccid, token });
        if (StringUtils.isBlank(token)) {
            logger.info("[VerifyAuthentication] imei = {}, iccid = {}, token = {} ...", new Object[] {
                    imei, iccid, token });
            request.setAttribute("ret", CommonCode.TOKEN_PARAMETER_ERROR.getCompleteCode());
            request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
            return false;
        }

        try {
            // 取得使用者資訊
            Prosumer users = authenticationService.getProsumerByAccount(token);
            if (users == null) {
                logger.info("[VerifyAuthentication] imei = {}, iccid = {}, token = {} ...", new Object[] {
                        imei, iccid, token });
                request.setAttribute("ret", CommonCode.INVALID_TOKEN.getCompleteCode());
                request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
                logger.warn(translator.getMessage(CommonCode.INVALID_TOKEN, false));
                return false;
            }
        } catch (Throwable e) {
            logger.info("[VerifyAuthentication] imei = {}, iccid = {}, token = {} ...", new Object[] {
                    imei, iccid, token });
            logger.error(e.getMessage(), e);
            request.setAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
            request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
            return false;
        }

        return true;
    }
}