package cn.vstore.appserver.form;

public class SdkAppInfoForm extends TokenForm{

	private String ver;
	private String First;
	private String dlog;

	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getFirst() {
		return First;
	}
	public void setFirst(String first) {
		First = first;
	}
	public String getDlog() {
		return dlog;
	}
	public void setDlog(String dlog) {
		this.dlog = dlog;
	}
}
