package cn.vstore.appserver.model;

import java.io.Serializable;

public class SubscribeExpDate implements Serializable {

    private static final long serialVersionUID = 1067131977123763897L;
    private String month;
    private String date;

    public SubscribeExpDate() {
    }

    public SubscribeExpDate(String month, String date) {
        this.month = month;
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
