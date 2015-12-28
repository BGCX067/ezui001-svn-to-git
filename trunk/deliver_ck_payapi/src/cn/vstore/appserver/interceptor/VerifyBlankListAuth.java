package cn.vstore.appserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vstore.appserver.model.BlankList;
import cn.vstore.appserver.service.BlankListService;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 白名單檢查
 */
public class VerifyBlankListAuth extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptorAdapter.class);

    @Autowired
    private BlankListService blankListService;

    @Autowired
    private MessageTranslator translator;

    // 回傳頁面
    private final String RETRUN_PAGE_ERROR = "/content/error.jsp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        // 檢查是否開啟白名單控制
        if (blankListService.isBlankListEnable()) {

            String userId = request.getParameter("userId");
            String token = request.getParameter("token");

            try {

                // 白名單檢查，當blankList有開啟時，blankType的值必須大於0，即白名單內
                BlankList blankList = null;
                if (StringUtils.isNotBlank(userId)) {
                    blankList = blankListService.userBlankListCredentialByUserId(userId);
                } else if (StringUtils.isNotBlank(token)) {
                    blankList = blankListService.userBlankListCredentialByToken(token);
                }

                // 不在白名單內
                if (blankList == null || blankList.isNotInBlankList()) {
                    logger.info("[VerifyBlankListAuth] userId = {}, token = {} ...", new Object[] {
                            userId, token });
                    request.setAttribute("ret", CommonCode.NOT_IN_BLANK_LIST.getCompleteCode());
                    request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
                    logger.warn(translator.getMessage(CommonCode.NOT_IN_BLANK_LIST, false));
                    return false;
                }

            } catch (Throwable e) {
                logger.info("[VerifyBlankListAuth] userId = {}, token = {} ...", new Object[] {
                        userId, token });
                logger.error(e.getMessage(), e);
                request.setAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
                request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
                return false;
            }
        }
        return true;
    }
}