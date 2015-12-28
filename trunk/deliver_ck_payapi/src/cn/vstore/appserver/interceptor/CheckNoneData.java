package cn.vstore.appserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 檢查Client版本
 */
public class CheckNoneData extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CheckClientVersion.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
    	logger.info("用户行为反馈动作，不做拦截......");
        return true;
    }
}