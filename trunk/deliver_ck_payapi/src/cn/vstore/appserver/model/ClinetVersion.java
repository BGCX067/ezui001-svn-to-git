package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 */
public class ClinetVersion implements Serializable {

    private static final long serialVersionUID = -4756332181476980416L;
    private int versionId;
    private String versionName;
    private String release_desc;
    private int fileSize;
    private String download;
    private String versionIdBySnum;

    
    public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getRelease_desc() {
		return release_desc;
	}

	public void setRelease_desc(String release_desc) {
		this.release_desc = release_desc;
	}

	public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getNewToken(String iccid, String imei) {
        return DigestUtils.md5Hex(iccid + imei + new Date().getTime());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public String getVersionIdBySnum() {
		return versionIdBySnum;
	}

	public void setVersionIdBySnum(String versionIdBySnum) {
		this.versionIdBySnum = versionIdBySnum;
	}
}
