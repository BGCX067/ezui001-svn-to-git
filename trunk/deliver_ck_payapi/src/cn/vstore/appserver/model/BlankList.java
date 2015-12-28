package cn.vstore.appserver.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BlankList {

    private int blankType;
    private boolean isNotInBlankList;

    public int getBlankType() {
        return blankType;
    }

    public void setBlankType(int blankType) {
        this.blankType = blankType;
    }

    public boolean isNotInBlankList() {
        return isNotInBlankList;
    }

    public void setNotInBlankList(boolean isNotInBlankList) {
        this.isNotInBlankList = isNotInBlankList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
