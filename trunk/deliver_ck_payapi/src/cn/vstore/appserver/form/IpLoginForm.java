package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class IpLoginForm extends BaseForm {

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
