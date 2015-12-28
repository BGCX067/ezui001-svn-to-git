package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DownloadAppForm extends TokenForm {

    private String vlogId;

    private String lappv;

    private String categoryId;

    private String range;//文件块的开始字节--断点续传所用
    
    public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

    public String getVlogId() {
        return vlogId;
    }

    public void setVlogId(String vlogId) {
        this.vlogId = vlogId;
    }

    public String getLappv() {
        return lappv;
    }

    public void setLappv(String lappv) {
        this.lappv = lappv;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
