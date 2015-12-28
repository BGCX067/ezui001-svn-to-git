package cn.vstore.appserver.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 */
public class ApplictionDetail extends Application {

    private static final long serialVersionUID = -1037271111743636604L;
    private int downloadTimes;
    private int ratingTimes;
    private String introduction;
    private String webPic1;
    private String webPic2;
    private String webPic3;
    private String webPic4;
    private String webPic5;
    private String versionName;
    private String lastUpdate;
    private int apkSize;
    private String supportLang;
    private String supportSDK;
    private String supportScreen;
    private String providerID;
    private String providerSite;
    private String providerEmail;
    private List<PosterComment> commentList;
    private boolean reportable;
    private boolean commentable;
    private boolean ratingable;
    private Comment myComment;

    
    public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(int downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public int getRatingTimes() {
        return ratingTimes;
    }

    public void setRatingTimes(int ratingTimes) {
        this.ratingTimes = ratingTimes;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getWebPic1() {
        return webPic1;
    }

    public void setWebPic1(String webPic1) {
        this.webPic1 = webPic1;
    }

    public String getWebPic2() {
        return webPic2;
    }

    public void setWebPic2(String webPic2) {
        this.webPic2 = webPic2;
    }

    public String getWebPic3() {
        return webPic3;
    }

    public void setWebPic3(String webPic3) {
        this.webPic3 = webPic3;
    }

    public String getWebPic4() {
        return webPic4;
    }

    public void setWebPic4(String webPic4) {
        this.webPic4 = webPic4;
    }

    public String getWebPic5() {
        return webPic5;
    }

    public void setWebPic5(String webPic5) {
        this.webPic5 = webPic5;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getApkSize() {
        return apkSize;
    }

    public void setApkSize(int apkSize) {
        this.apkSize = apkSize;
    }

    public String getSupportLang() {
        return supportLang;
    }

    public void setSupportLang(String supportLang) {
        this.supportLang = supportLang;
    }

    public String getSupportSDK() {
        return supportSDK;
    }

    public void setSupportSDK(String supportSDK) {
        this.supportSDK = supportSDK;
    }

    public String getSupportScreen() {
        return supportScreen;
    }

    public void setSupportScreen(String supportScreen) {
        this.supportScreen = supportScreen;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getProviderSite() {
        if (providerSite != null && providerSite.toLowerCase().indexOf("http://") == -1) {
            providerSite = "http://" + providerSite;
        }
        return providerSite;
    }

    /**
     * 把語文字串已逗號文分隔轉為陣列
     * 
     * @return
     */
    public String[] getSupportLangArray() {
        String[] value = null;
        if (supportLang != null) {
            value = supportLang.replaceAll("\\s+", "").split(",");
        }
        return value;
    }

    public void setProviderSite(String providerSite) {
        this.providerSite = providerSite;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public List<PosterComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<PosterComment> commentList) {
        this.commentList = commentList;
    }

    public boolean isReportable() {
        return reportable;
    }

    public void setReportable(boolean reportable) {
        this.reportable = reportable;
    }

    public boolean isCommentable() {
        return commentable;
    }

    public void setCommentable(boolean commentable) {
        this.commentable = commentable;
    }

    public boolean isRatingable() {
        return ratingable;
    }

    public void setRatingable(boolean ratingable) {
        this.ratingable = ratingable;
    }

    public Comment getMyComment() {
        return myComment;
    }

    public void setMyComment(Comment myComment) {
        this.myComment = myComment;
    }

    public String[] getWebPicList() {
        List<String> webPicList = new ArrayList<String>();
        if (StringUtils.isNotBlank(getWebPic1())) {
            webPicList.add(getWebPic1());
        }
        if (StringUtils.isNotBlank(getWebPic2())) {
            webPicList.add(getWebPic2());
        }
        if (StringUtils.isNotBlank(getWebPic3())) {
            webPicList.add(getWebPic3());
        }
        if (StringUtils.isNotBlank(getWebPic4())) {
            webPicList.add(getWebPic4());
        }
        if (StringUtils.isNotBlank(getWebPic5())) {
            webPicList.add(getWebPic5());
        }
        return webPicList.toArray(new String[webPicList.size()]);
    }

    public String getDownloadTimesRegion() {
        String dTimeRegin = "";
        int dTimes = getDownloadTimes();
        if (dTimes <= 100) {
            dTimeRegin = "< 100";
        } else if (dTimes > 100 && dTimes < 501) {
            dTimeRegin = "101 ~ 500";
        } else if (dTimes > 500 && dTimes < 1001) {
            dTimeRegin = "501 ~ 1000";
        } else if (dTimes > 1000 && dTimes < 5001) {
            dTimeRegin = "1001 ~ 5000";
        } else if (dTimes > 5000 && dTimes < 10001) {
            dTimeRegin = "5001 ~ 10000";
        } else if (dTimes > 10000 && dTimes < 25001) {
            dTimeRegin = "10001 ~ 25000";
        } else if (dTimes > 25000) {
            dTimeRegin = "> 25000";
        }
        return dTimeRegin;
    }

    public String getApkSizeRegion() {
        String apkSizeRegion = "";
        NumberFormat formatter = new DecimalFormat("0.0");

        if (apkSize < 1024) {
            apkSizeRegion = apkSize + "byte";
        } else if (apkSize >= 1024 && apkSize < (1024 * 1024)) {
            apkSizeRegion = formatter.format((float) ((float) apkSize / 1024)) + "KB";
        } else if (apkSize >= (1024 * 1024)) {
            apkSizeRegion = formatter.format((float) ((float) apkSize / (1024 * 1024))) + "MB";
        }
        return apkSizeRegion;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
