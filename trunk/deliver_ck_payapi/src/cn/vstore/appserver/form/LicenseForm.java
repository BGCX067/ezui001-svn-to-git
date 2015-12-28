package cn.vstore.appserver.form;

public class LicenseForm extends TokenForm{
	private String ver;
	private String pkg;
	
	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
}
