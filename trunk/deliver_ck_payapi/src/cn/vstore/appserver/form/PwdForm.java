package cn.vstore.appserver.form;

public class PwdForm extends BaseForm {
	
	private String resetToken;
	private String pwd;
	
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
