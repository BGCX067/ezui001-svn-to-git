package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class TokenForm extends BaseForm {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
