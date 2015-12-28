package cn.vstore.appserver.apilog;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Id: ApiLogHandler.java 7437 2011-03-03 06:26:58Z yellow $
 */
public class ApiLogHandler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(ApiLogHandler.class);
    final private ApiLogService apiLogService;
    final private String retCode;
    final private long startMillSec;
    final private long endMillSec;
    final private String uri;
    final private Map<String, String[]> parameterMap;

    public ApiLogHandler(ApiLogService apiLogService,
            HttpServletRequest request, long startMillSec, long endMillSec) {
        this.apiLogService = apiLogService;
        this.startMillSec = startMillSec;
        this.endMillSec = endMillSec;
        this.uri = request.getRequestURI()
                .replace(request.getContextPath(), "");
        Object ret = request.getAttribute("ret");
        this.retCode = ret != null ? ret.toString() : null;
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = request.getParameterMap();
        this.parameterMap = new HashMap<String, String[]>(parameterMap);
    }

    @Override
    public void run() {
    	try{
    		apiLogService.addApiLog(parameterMap, startMillSec, endMillSec, uri,
                retCode);
    	}catch(Throwable e){
    		logger.error(e.getMessage(),e);
    	}
    }

}
