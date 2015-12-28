package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @version $Id: PaymentSession.java 7437 2011-03-03 06:26:58Z yellow $
 */
public class PaymentSession implements Serializable {

    private static final long serialVersionUID = -5180867979926969166L;
    private Long id;
    private String token;
    private String userId;
    private String imei;
    private String iccid;
    private Date loginTime;
    private String ipayUserId;
    private Long sessionId;
    private Integer blankType;
    private String userUid;
    private Integer userType;
    private Integer sim;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getIpayUserId() {
        return ipayUserId;
    }

    public void setIpayUserId(String ipayUserId) {
        this.ipayUserId = ipayUserId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getBlankType() {
        return blankType;
    }

    public void setBlankType(Integer blankType) {
        this.blankType = blankType;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getSim() {
        return sim;
    }

    public void setSim(Integer sim) {
        this.sim = sim;
    }

}
