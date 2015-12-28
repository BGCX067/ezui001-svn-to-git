package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SearchAppForm extends TokenForm {

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}