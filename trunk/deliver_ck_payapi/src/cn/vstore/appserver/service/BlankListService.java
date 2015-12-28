package cn.vstore.appserver.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.BlankList;

/**
 * @version $Id$
 */
@Service("BlankListService")
public class BlankListService {

    protected static final Logger logger = LoggerFactory.getLogger(BlankListService.class);

    // 白名單
    public final static String USER_BLANK_LIST_ENABLE = "userBlankListEnable";

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    @Qualifier("configurationMessageSource")
    private MessageSource configuration;

    /**
     * 取得是否開啟白名單設定
     * 
     * @return boolean : true為開啟， false為沒有開啟
     */
    public boolean isBlankListEnable() {
        boolean isBlankList = false;
        String blankListEnableValue = configuration.getMessage("config.userBlankListEnable", null, Locale.ENGLISH);
        isBlankList = "true".equals(blankListEnableValue);
        return isBlankList;
    }

    /**
     * 取得是否為假付費機制
     * 
     * @return boolean : true為開啟假付費機制， false為沒有開啟假付費機制
     */
    boolean isMockPayment(BlankListService self, String userId) {
        boolean isBlank = false;
        int blankType = 0;
        // 取得是否開啟白名單設定
        if (isBlankListEnable()) {
            blankType = getBlankTypeValueByUserId(userId);
            // 當blankType>=30，則為測試用假付費機制，反之為正常付費機制。
            if (blankType >= 30) {
                isBlank = true;
                logger.info(" USER_ID = " + userId
                        + " . BLANK_TYPE >= 30, This is test, not real payment to ipay.");
            } else {
                logger.info(" USER_ID = " + userId
                        + " . BLANK_TYPE < 30, This is the actual operation. Actual payments.");
            }
        }
        return isBlank;
    }

    /**
     * 取得白名單中blankType的值，以UserId為條件
     */
    int getBlankTypeValueByUserId(String userId) {

        int blankType = 100;// 預設100，即白名單內

        // 如果開啟白名單，回傳DB內PROSUMER_BLANKLIST的BLANK_TYPE值，如搜尋不到則回傳0(非白名單內)
        if (isBlankListEnable()) {
            Integer blankTypeObject = (Integer) sqlMapClientTemplate.queryForObject("Auth.getUserBlankListByUserId", userId);
            blankType = blankTypeObject == null ? 0 : blankTypeObject.intValue();
            logger.debug("blankType: " + blankType);
        }

        return blankType;
    }

    /**
     * 取得白名單中blankType的值，以Token為條件
     * 
     * @return int : blankType
     */
    int getBlankTypeValueByToken(String token) {

        int blankType = 100;// 預設100，即白名單內

        // 如果開啟白名單，回傳DB內PROSUMER_BLANKLIST的BLANK_TYPE值，如搜尋不到則回傳0(非白名單內)
        if (isBlankListEnable()) {
            Integer blankTypeObject = (Integer) sqlMapClientTemplate.queryForObject("Auth.getUserBlankListByToken", token);
            blankType = blankTypeObject == null ? 0 : blankTypeObject.intValue();
            logger.debug("blankType: " + blankType);
        }

        return blankType;
    }

    /**
     * 白名單驗證，以UserId為條件
     * 
     * @return int : blankType
     */
    public BlankList userBlankListCredentialByUserId(String userId) {

        BlankList blankList = new BlankList();
        int blankType = 100;// 預設100，即白名單內
        boolean isNotInBlankList = true;

        blankType = getBlankTypeValueByUserId(userId);

        // blankType > 0， 即白名單內
        if (blankType > 0) {
            isNotInBlankList = false;
        }

        blankList.setBlankType(blankType);
        blankList.setNotInBlankList(isNotInBlankList);

        return blankList;
    }

    /**
     * 白名單驗證，以Token為條件
     * 
     * @return int : blankType
     */
    public BlankList userBlankListCredentialByToken(String token) {

        BlankList blankList = new BlankList();
        int blankType = 100;// 預設100，即白名單內
        boolean isNotInBlankList = true;

        blankType = getBlankTypeValueByToken(token);

        // blankType > 0， 即白名單內
        if (blankType > 0) {
            isNotInBlankList = false;
        }

        blankList.setBlankType(blankType);
        blankList.setNotInBlankList(isNotInBlankList);

        return blankList;
    }
}