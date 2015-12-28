/**
 *
 */
package cn.vstore.appserver.util;

import java.util.Locale;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import cn.vstore.appserver.service.ResultCode;
import cn.vstore.appserver.service.ServiceResult;

@Component
public class MessageTranslator {

    protected static final Logger logger = LoggerFactory.getLogger(MessageTranslator.class);

    public static final String WARN_MSG_PREFIX = "warnMessageFormat.";

    public static final String ERROR_MSG_PREFIX = "errorMessageFormat.";

    public static final String INFO_MSG_PREFIX = "infoMessageFormat.";

    @Autowired
    @Qualifier("messageSource")
    private MessageSource clientMessageSource;

    public String getMessage(ServiceResult<?> o) {
        return getMessage(o.getResult(), o.isError());
    }

    /**
     * 有錯誤code或exception的訊息ResultCode
     */
    public String getMessage(ResultCode code, boolean isError) {
        String clientMsg = clientMessageSource.getMessage(code.getClientMessageCodes(), null, Locale.ENGLISH);
        String sysMsg = clientMessageSource.getMessage(code.getSystemMessageCodes(), null, Locale.ENGLISH);

        if (isError) {
            return String.format(getErrorMessageFormat(code), code.getCompleteCode(), clientMsg, code.getDeepSouceCompleteCode(), sysMsg);
        } else {
            return String.format(getWarnMessageFormat(code), code.getCompleteCode(), clientMsg, code.getDeepSouceCompleteCode(), sysMsg);
        }

    }

    /**
     * 正確訊息的ResultCode
     */
    public String getMessage(ResultCode code) {
        String clientMsg = clientMessageSource.getMessage(code.getClientMessageCodes(), null, Locale.ENGLISH);
        String sysMsg = clientMessageSource.getMessage(code.getSystemMessageCodes(), null, Locale.ENGLISH);

        return String.format(getInfoMessageFormat(code), code.getCompleteCode(), clientMsg, code.getDeepSouceCompleteCode(), sysMsg);

    }

    /**
     * WARN訊息
     */
    protected String getWarnMessageFormat(Object o) {
        return clientMessageSource.getMessage(WARN_MSG_PREFIX + o.getClass().getSimpleName(), null, Locale.ENGLISH);
    }

    /**
     * ERROR訊息
     */
    protected String getErrorMessageFormat(Object o) {
        return clientMessageSource.getMessage(ERROR_MSG_PREFIX + o.getClass().getSimpleName(), null, Locale.ENGLISH);
    }

    /**
     * INFO訊息
     */
    protected String getInfoMessageFormat(Object o) {
        return clientMessageSource.getMessage(INFO_MSG_PREFIX + o.getClass().getSimpleName(), null, Locale.ENGLISH);
    }
}