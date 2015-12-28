package cn.vstore.appserver.apilog;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.PaymentSession;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.service.AuthenticationService;
import cn.vstore.appserver.service.PaymentSessionService;
import cn.vstore.appserver.util.StringUtils;

/**
 * @version $Id: ApiLogService.java 7437 2011-03-03 06:26:58Z yellow $
 */
@Service("ApiLogService")
public class ApiLogService {

    private final Logger L = LoggerFactory.getLogger(ApiLogService.class);

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PaymentSessionService paymentSessionService;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void addApiLog(Map<String, String[]> parameterMap,
            long startMillSec, long endMillSec, String uri, String retCode) {
        if (startMillSec < 0 || endMillSec < 0
                || (endMillSec - startMillSec) < 0) {
            L.warn("startMillSec < 0 || endMillSec < 0 || (endMillSec - startMillSec) < 0");
        }

        ApiInfo apiInfo = ApiInfo.parseApiInfo(uri);
        if (apiInfo == null) {
            L.warn("API Name match fail for url: " + uri);
            return;
        }
        ApiLog apiLog = new ApiLog();
        String imei = getParameter(parameterMap, "imei");
        String token = getParameter(parameterMap, "token");
        String iccid = getParameter(parameterMap, "iccid");
        if (StringUtils.isBlankAny(imei, iccid, token) == -1) {
            if (apiInfo.type == ApiInfo.TYPE_IPAY) {
                PaymentSession ps = paymentSessionService.getPaymentSession(
                        token, imei, iccid);
                if (ps != null) {
                    apiLog.setUserId(ps.getUserId());
                    apiLog.setUserUid(ps.getUserUid());
                }
            } else {
                Prosumer user = authenticationService.getProsumerByAccount(token);
                if (user != null) {
                    apiLog.setUserId(user.getUserId());
                    apiLog.setUserUid(user.getUserUid());
                }
            }
        }
        apiLog.setImei(imei);
        apiLog.setIccid(iccid);
        apiLog.setToken(token);
        apiLog.setModel(getParameter(parameterMap, "dvc"));
        apiLog.setAndroidApiLevel(getParameterInt(parameterMap, "aver"));
        apiLog.setClientVersion(getParameterInt(parameterMap, "cver"));
        
        if(apiInfo.name.equals("GetCategoryList")){
        	//如果呼叫的是应用程序类别列表，则以参数pid来判断存入的API名称
        	String categoryLevelName = "";
        	if(getParameter(parameterMap, "pid")==null || !getParameter(parameterMap, "pid").equals("0")){
        		categoryLevelName = (String) sqlMapClientTemplate.queryForObject("ApiLog.getCategoryListApiLevelName", 
        																			"GetCategoryList-Second");
        	}else{
        		categoryLevelName = (String) sqlMapClientTemplate.queryForObject("ApiLog.getCategoryListApiLevelName", 
																					"GetCategoryList-First");
        	}
        	apiLog.setApiName(categoryLevelName);
        }else{
        	apiLog.setApiName(apiInfo.name);
        }
        apiLog.setChannel("VTION");
        apiLog.setInputParameters(getRequestParams(uri, parameterMap, apiInfo));
        apiLog.setReturnCode(retCode);
        apiLog.setResponseTime((int) Math.abs(endMillSec - startMillSec));
        apiLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        saveApiLog(apiLog);
    }

    private String getParameter(Map<String, String[]> parameterMap, String key) {
        String[] parameter = parameterMap.get(key);
        if (parameter != null)
            return parameter[0];
        return null;
    }

    private Integer getParameterInt(Map<String, String[]> parameterMap,
            String key) {
        String param = getParameter(parameterMap, key);
        if (StringUtils.isNotBlank(param)) {
            try {
                return new Integer(param);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private String getRequestParams(String uri,
            Map<String, String[]> paramsMap, ApiInfo apiInfo) {
        StringBuilder sb = new StringBuilder(512);
        sb.append(uri);
        if (paramsMap.size() > 0) {
            sb.append("?");
            boolean appendParam = false;
            Set<Entry<String, String[]>> entrys = paramsMap.entrySet();
            for (Entry<String, String[]> entry : entrys) {
                String key = entry.getKey();
                boolean secret = apiInfo.isSecretParam(key);
                String[] value = entry.getValue();
                for (String param : value) {
                    if (appendParam) {
                        sb.append("&");
                    }
                    sb.append(key).append("=");
                    if (secret && param.length() != 0)
                        sb.append("***");
                    else
                        sb.append(param);
                    appendParam = true;
                }
            }
        }

        return sb.toString();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveApiLog(ApiLog log) {
        sqlMapClientTemplate.insert("ApiLog.insertApiLog", log);
    }

}
