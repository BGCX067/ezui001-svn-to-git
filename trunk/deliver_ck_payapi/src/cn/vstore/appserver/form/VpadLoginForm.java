package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class VpadLoginForm extends BaseForm {


	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
