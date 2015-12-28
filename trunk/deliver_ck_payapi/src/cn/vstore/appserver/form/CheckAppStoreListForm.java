package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CheckAppStoreListForm extends BaseForm {

	private String ver;
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ToStringBuilder.reflectionToString(this).toString();
	}


	public String getVer() {
		return ver;
	}


	public void setVer(String ver) {
		this.ver = ver;
	}

}
