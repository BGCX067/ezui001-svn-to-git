package cn.vstore.appserver.model;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UserSubscribeLog {

    private long id;
    private String userId;
    private String appId;
    private Timestamp lastEndTime;
    private Integer versionId;
    private long subscribId;
    private long orderId;
    private String userUid;
    private String serviceId;
    private String imei;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getLastEndTime() {
        return lastEndTime;
    }

    public void setLastEndTime(Timestamp lastEndTime) {
        this.lastEndTime = lastEndTime;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public long getSubscribId() {
        return subscribId;
    }

    public void setSubscribId(long subscribId) {
        this.subscribId = subscribId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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
