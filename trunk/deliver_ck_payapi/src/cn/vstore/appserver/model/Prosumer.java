package cn.vstore.appserver.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 */
public class Prosumer extends Account implements Serializable {

    private static final long serialVersionUID = 2083193250221984279L;
    public static final int IS_SIM_TRUE = 1; // 是SIM用戶
    public static final int IS_SIM_FALSE = 0; // 不是SIM用戶
    public static final int ZERO_SERVICE_REGISTERED = 1; // 已註冊0元service
    public static final int ZERO_SERVICE_UNREGISTER = 0; // 未註冊0元service

    private String userId;
    private String userUid;
    private String userName;
    private String password;
    private Integer blankType;
    private Integer isSim;
    private int registerService;
    private Date loginTime;
    private String nickName;
    private BigDecimal storeId;
    private String signature;
    private String pwdResetToken;
    private Long id;
    private Integer status;
    private String snum;
    private String IP;

	public String getPwdResetToken() {
		return pwdResetToken;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPwdResetToken(String pwdResetToken) {
		this.pwdResetToken = pwdResetToken;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.isSim = Integer.valueOf(checkIsSim(userId));
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getBlankType() {
        return blankType;
    }

    public void setBlankType(Integer blankType) {
        this.blankType = blankType;
    }

    public Integer getIsSim() {
        return isSim;
    }

    public void setIsSim(Integer isSim) {
        this.isSim = isSim;
    }

    public int getRegisterService() {
        return registerService;
    }

    public void setRegisterService(int registerService) {
        this.registerService = registerService;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public void setAccount(Account account) {
        setToken(account.getToken());
        setIccid(account.getIccid());
        setImei(account.getImei());
    }

    public Account getAccount(String token, String iccid, String imei) {
        Account account = new Account();
        account.setIccid(iccid);
        account.setImei(imei);
        account.setToken(token);
        return account;
    }

    public Credential getCredential() {
        Credential credential = new Credential();
        credential.setToken(getToken());
        credential.setUid(getUserUid());
        credential.setUserId(getUserId());
        credential.setNickName(getNickName());
        return credential;
    }

    public String getNewToken() {
        return DigestUtils.md5Hex(getUserId() + getIccid() + getImei() + new Date().getTime());
    }

    // 判斷是否為SIM用戶
    private static int checkIsSim(String userId) {
        int isSim = IS_SIM_FALSE;
        if (userId != null && userId.trim().length() == 10
                && userId.trim().substring(0, 2).equals("09")) {
            isSim = IS_SIM_TRUE;
        }
        return isSim;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public BigDecimal getStoreId() {
		return storeId;
	}

	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getSnum() {
		return snum;
	}

	public void setSnum(String snum) {
		this.snum = snum;
	}
	
}
