package cn.vstore.appserver.apilog;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id: ApiLog.java 7437 2011-03-03 06:26:58Z yellow $
 */
public class ApiLog implements Serializable {

    private static final long serialVersionUID = 2452167677897127400L;

    private Long id;
    private String userId;
    private String userUid;
    private String token;
    private String imei;
    private String iccid;
    private String model;
    private Integer androidApiLevel;
    private Integer clientVersion;
    private String apiName;
    private String channel;
    private String inputParameters;
    private String returnCode;
    /**
     * API回應所需要的時間，單位毫秒
     */
    private Integer responseTime;
    private Timestamp createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getAndroidApiLevel() {
        return androidApiLevel;
    }

    public void setAndroidApiLevel(Integer androidApiLevel) {
        this.androidApiLevel = androidApiLevel;
    }

    public Integer getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(String inputParameters) {
        if (inputParameters == null || inputParameters.length() < 4000)
            this.inputParameters = inputParameters;
        else
            this.inputParameters = inputParameters.substring(0, 4000);
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
