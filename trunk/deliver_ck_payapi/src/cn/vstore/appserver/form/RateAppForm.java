package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class RateAppForm extends TokenForm {

    private String rating;
    private String lappv;
    private String comment;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLappv() {
        return lappv;
    }

    public void setLappv(String lappv) {
        this.lappv = lappv;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}