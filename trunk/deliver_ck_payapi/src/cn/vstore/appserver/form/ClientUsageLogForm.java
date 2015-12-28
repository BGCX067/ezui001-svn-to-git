package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ClientUsageLogForm extends TokenForm {

    private String l_uid;
    private String l_userId;
    private String l_iccid;
    private String l_imei;
    private String l_msisdn;
    private String l_cver;
    private String l_time;
    private String l_dvc;
    private String l_isFirstTime;

    public String getL_uid() {
        return l_uid;
    }

    public void setL_uid(String l_uid) {
        this.l_uid = l_uid;
    }

    public String getL_userId() {
        return l_userId;
    }

    public void setL_userId(String l_userId) {
        this.l_userId = l_userId;
    }

    public String getL_iccid() {
        return l_iccid;
    }

    public void setL_iccid(String l_iccid) {
        this.l_iccid = l_iccid;
    }

    public String getL_imei() {
        return l_imei;
    }

    public void setL_imei(String l_imei) {
        this.l_imei = l_imei;
    }

    public String getL_msisdn() {
        return l_msisdn;
    }

    public void setL_msisdn(String l_msisdn) {
        this.l_msisdn = l_msisdn;
    }

    public String getL_cver() {
        return l_cver;
    }

    public void setL_cver(String l_cver) {
        this.l_cver = l_cver;
    }

    public String getL_time() {
        return l_time;
    }

    public void setL_time(String l_time) {
        this.l_time = l_time;
    }

    public String getL_dvc() {
        return l_dvc;
    }

    public void setL_dvc(String l_dvc) {
        this.l_dvc = l_dvc;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public String getL_isFirstTime() {
		return l_isFirstTime;
	}

	public void setL_isFirstTime(String l_isFirstTime) {
		this.l_isFirstTime = l_isFirstTime;
	}

}
