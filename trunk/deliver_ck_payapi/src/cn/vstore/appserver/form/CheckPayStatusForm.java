package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CheckPayStatusForm extends TokenForm {

    private String lappv;

    public String getLappv() {
        return lappv;
    }

    public void setLappv(String lappv) {
        this.lappv = lappv;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
