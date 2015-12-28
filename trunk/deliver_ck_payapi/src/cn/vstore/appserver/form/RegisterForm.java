package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class RegisterForm extends BaseForm {

    private String userId;
    private String pwd;
    private String nickname;
    private String signature;
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    
    public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
