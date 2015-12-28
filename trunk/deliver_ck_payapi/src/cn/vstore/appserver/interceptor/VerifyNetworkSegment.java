package cn.vstore.appserver.interceptor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;
import cn.vstore.appserver.util.NetUtil;

/**
 * 驗證網段，內部IP才可以HTTP GET
 */
public class VerifyNetworkSegment extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VerifyNetworkSegment.class);

    @Autowired
    @Qualifier("configurationMessageSource")
    private MessageSource messageSource;

    @Autowired
    private MessageTranslator translator;

    // 回傳頁面
    private final String RETRUN_PAGE_ERROR = "/content/error.jsp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        try {
            // 檢查是否開啟IP區段的檢驗
            String apiTestIpEnable = messageSource.getMessage("config.apiIpBlankList.enable", null, Locale.ENGLISH);
            if ("false".equals(apiTestIpEnable))
                return true;

            if ("GET".equalsIgnoreCase(request.getMethod()) && !this.isBlankIp(getIpAddr(request))) {
                request.setAttribute("ret", CommonCode.NOT_IN_NETWORK_SEGMENT.getCompleteCode());
                request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
                logger.warn(translator.getMessage(CommonCode.NOT_IN_NETWORK_SEGMENT, false));
                return false;
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            request.setAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
            request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
            return false;
        }

        return true;
    }

    /**
     * 檢查是否符合網段內
     * 
     * @param ip : 使用者AddressIP
     * @return boolean : 是否符合網段內--true為網段內，false為不是網段內
     */
    boolean isBlankIp(String ip) {
        String ips = messageSource.getMessage("config.apiIpBlankList", null, Locale.ENGLISH);
        boolean ret = false;
        String[] ipArray = ips.split("\\,");
        for (int i = 0; i < ipArray.length; i++) {
            String wholeIp = NetUtil.getSubnet(ipArray[i]);
            String[] wholeIpArray = wholeIp.split("/");
            if (NetUtil.isInSubnet(ip, wholeIpArray[0], NetUtil.getMask(Integer.parseInt(wholeIpArray[1])))) {
                ret = true;
            }
        }
        logger.debug("is blank IP? " + ip + " in " + ips + " == " + ret);
        return ret;
    }

    /**
     * 取得客戶端IP
     * 
     * @param request : HttpServletRequest
     * @return String : IP
     */
    public String getIpAddr(HttpServletRequest request) {
        //       String ip = request.getHeader("x-forwarded-for");
        //       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        //           ip = request.getHeader("Proxy-Client-IP");
        //       }
        //       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        //           ip = request.getHeader("WL-Proxy-Client-IP");
        //       }
        //       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        String ip = request.getRemoteAddr();
        //       }
        if (StringUtils.isNotBlank(ip))
            logger.info("Current user IP : " + ip);
        return ip;
    }
}
