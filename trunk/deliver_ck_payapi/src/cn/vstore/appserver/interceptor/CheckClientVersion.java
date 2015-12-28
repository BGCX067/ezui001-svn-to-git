package cn.vstore.appserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vstore.appserver.model.ClinetVersion;
import cn.vstore.appserver.service.ClientService;
import cn.vstore.appserver.service.ServiceResult;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.util.MessageTranslator;

/**
 * 檢查Client版本
 */
public class CheckClientVersion extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CheckClientVersion.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private MessageTranslator translator;

    // 回傳頁面
    private final String RETRUN_PAGE_SUCCESS = "/content/newClient.jsp";
    private final String RETRUN_PAGE_ERROR = "/content/error.jsp";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String store = request.getParameter("store"); // store package name
        String cver = request.getParameter("cver");
        String snum = request.getParameter("snum");
//        String imei = request.getParameter("imei");
//        String imsi = request.getParameter("imsi");
//        String iccid = request.getParameter("iccid");

        int cverNo = NumberUtils.toInt(cver, 0);
        if (cverNo == 0) {
            logger.info("[CheckClinetVersion] cver = {} ...", new Object[] { cver });
//            request.setAttribute("ret", CommonCode.PARAMETER_ERROR.getCompleteCode());
//            request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
//            return false;
            return true;
        }

        int versionNumber = 0;

        try {
            // Check Client Version Service
            ServiceResult<ClinetVersion> serviceResult = clientService.checkClientVersion(cverNo,snum, store);

            if (serviceResult.getData() != null) {
                versionNumber = ((ClinetVersion) serviceResult.getData()).getVersionId();
            }

            // 比對版本，當手機版本小於server版本則回傳新版本資訊
            if (cverNo < versionNumber) {
//                logger.info("[CheckClinetVersion] cver = {}, imei = {}, imsi = {}, iccid = {} ...", new Object[] {
//                        cver, imei, imsi, iccid });
                request.setAttribute("ret", CommonCode.NEW_CLIENT.getCompleteCode());
                request.setAttribute("fileSize", new Integer(((ClinetVersion) serviceResult.getData()).getFileSize()));
                request.getRequestDispatcher(RETRUN_PAGE_SUCCESS).forward(request, response);
                logger.info(translator.getMessage(CommonCode.NEW_CLIENT));
                return false;
            }
        } catch (Throwable e) {
            logger.info("[CheckClinetVersion] cver = {} ...", new Object[] { cver });
            logger.error(e.getMessage(), e);
            request.setAttribute("ret", CommonCode.SERVICE_FAIL.getCompleteCode());
            request.getRequestDispatcher(RETRUN_PAGE_ERROR).forward(request, response);
            return false;
        }
        return true;
    }
}