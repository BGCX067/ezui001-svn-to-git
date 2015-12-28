package cn.vstore.appserver.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElFunction {
    protected static final Logger logger = LoggerFactory.getLogger(ElFunction.class);

    protected static final boolean filterBlank = "true".equals(ResourceBundle.getBundle("configuration", Locale.ENGLISH).getString("config.filterBlank.json"));

    /**
     * Escape JavaScript
     * 
     * @param str
     * @return
     */
    public static String escapeJavaScript(String str) {
        return StringEscapeUtils.escapeJavaScript(str);
    }

    /**
     * Filter Blank For Json Data
     * 
     * @param str
     * @return
     */
    public static String filterBlankForJsonData(String str) {
        String returnResult = str;
        try {
            if (filterBlank) {
                returnResult = new JSONObject(str).toString();
            }
        } catch (Exception e) {
            returnResult = str;
        }
        return returnResult;
    }

    /**
     * 檢查有沒有值，若沒有整個tag都不顯示
     * 
     * @param tagName : Json 的 tag 名稱
     * @param str : Json 的 tag 值
     * @param isComma : 最後是否加上逗號，true為要加上， false為不要加上
     * @param isComma : 是否秀出tag，true為要秀出， false為不要秀出
     * @return
     */
    public static String isBlank(String tagName, String str, boolean isComma, boolean showTag) {
        if (StringUtils.isBlank(str)) {
            str = (str == null) ? "null" : "'" + str + "'";
            if (showTag)
                return tagName + ": " + str + (isComma == true ? ", " : "");
            return "";
        } else {
            return tagName + ": '" + str + "'" + (isComma == true ? ", " : "");
        }

    }

    /**
     * 檢查有沒有值，若沒有整個tag都不顯示，For Number
     * 
     * @param tagName : Json 的 tag 名稱
     * @param str : Json 的 tag 值
     * @param isComma : 最後是否加上逗號，true為要加上， false為不要加上
     * @param isComma : 是否秀出tag，true為要秀出， false為不要秀出
     * @return
     */
    public static String isBlankForNumber(String tagName, String str, boolean isComma,
        boolean showTag) {
        if (StringUtils.isBlank(str)) {
            if (showTag)
                return tagName + ": 0" + (isComma == true ? ", " : "");
            return "";
        } else {
            try {
                Double.parseDouble(str);
            } catch (Exception e) {
                str = "0";
            }
            return tagName + ": " + str + (isComma == true ? ", " : "");
        }

    }

    /**
     * 檢查有沒有值，若沒有整個tag都不顯示，For Boolean
     * 
     * @param tagName : Json 的 tag 名稱
     * @param str : Json 的 tag 值
     * @param isComma : 最後是否加上逗號，true為要加上， false為不要加上
     * @param isComma : 是否秀出tag，true為要秀出， false為不要秀出
     * @return
     */
    public static String isBlankForBoolean(String tagName, String str, boolean isComma,
        boolean showTag) {
        if (StringUtils.isBlank(str)) {
            if (showTag)
                return tagName + ": 0" + (isComma == true ? ", " : "");
            return "";
        } else {
            return tagName + ": " + str + (isComma == true ? ", " : "");
        }

    }

    /**
     * 數值格式轉換並檢查有沒有值
     * 
     * @param tagName : Json 的 tag 名稱
     * @param str : Json 的 tag 值
     * @param pattern : 數值格式顯示
     * @param isComma : 最後是否加上逗號，true為要加上， false為不要加上
     * @param isComma : 是否秀出tag，true為要秀出， false為不要秀出
     * @return
     */
    public static String formatNumber(String tagName, String str, String pattern, boolean isComma,
        boolean showTag) {
        if (StringUtils.isBlank(str)) {
            if (showTag)
                return tagName + ": 0" + (isComma == true ? ", " : "");
            return "";
        } else {
            try {
                float strNo = NumberUtils.toFloat(str, 0);
                NumberFormat formatter = new DecimalFormat(pattern);
                str = formatter.format(strNo);
            } catch (Exception e) {
                str = "0";
            }
            return tagName + ": " + str + (isComma == true ? ", " : "");
        }
    }
}
