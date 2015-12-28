package cn.vstore.appserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 共同參數驗證
 */
public class VerifyCommonParameters extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCommonParameters.class);

    @Autowired
    private MessageTranslator translator;

    // 回傳頁面
    private final String RETRUN_PAGE_ERROR = "/content/error.jsp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String store = request.getParameter("store"); // store package name
        String aver = request.getParameter("aver"); // Android SDK 版本
        String cver = request.getParameter("cver"); // Client版本
        String lang = request.getParameter("lang"); // 語系
        String time = request.getParameter("time"); // 手機端時間 yyyy/MM/dd HH:mm，如2010/08/01 04:00
        String imei = request.getParameter("imei");
        String imsi = request.getParameter("imsi");
        String iccid = request.getParameter("iccid");
        String dvc = request.getParameter("dvc"); // 手機型號
        String wpx = request.getParameter("wpx"); // 手機螢幕寬度(pixel)
        String hpx = request.getParameter("hpx"); // 手機螢幕高度(pixel)
        String appfilter = request.getParameter("appfilter"); // 0表示不Filter, 1表示要filter Feature Apps, Category Apps, CP’s Apps, Search Apps
        String token = request.getParameter("token");

        logger.info("aver = {}, cver = {}, lang = {}, time = {}, imei = {}, imsi = {}, iccid = {}, dvc = {}, wpx = {}, hpx = {}, token = {} ...", new Object[] {
                aver, cver, lang, time, imei, imsi, iccid, dvc, wpx, hpx, token });
        String ret = CommonCode.SUCCESS.getCompleteCode();

        if (StringUtils.isBlank(aver)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(cver)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(lang)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(time)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(imei)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(imsi)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(iccid)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(dvc)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(wpx)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(hpx)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(appfilter)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        } else if (StringUtils.isBlank(store)) {
            ret = CommonCode.PARAMETER_ERROR.getCompleteCode();
        }

        // 設定語系
        request.setAttribute("lang", lang);

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