package cn.vstore.appserver.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id: SupportVersion.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class SupportVersion implements Serializable {

    private static final long serialVersionUID = 4950664385627188287L;
    private String param;
    private String value;

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
