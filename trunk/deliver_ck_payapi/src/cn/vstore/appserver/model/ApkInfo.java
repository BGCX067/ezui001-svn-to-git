package cn.vstore.appserver.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ApkInfo implements Serializable {

    private static final long serialVersionUID = 4152173016913488259L;
    private String filepath;
    private String priceType;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
