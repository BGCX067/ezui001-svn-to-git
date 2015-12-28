package cn.vstore.appserver.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Account {

    private String token;
    private String iccid;
    private String imei;

    public Account() {
    }

    public Account(String token, String iccid, String imei) {
        this.token = token;
        this.iccid = iccid;
        this.imei = imei;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
