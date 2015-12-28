package cn.vstore.appserver.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 * 2012-06-06 update
 * add filePath,fileSize,totalDownloadTimes
 * by Raymond
 */
public class FeatureApplication extends Application {

    private static final long serialVersionUID = -5255955087896831934L;
    private Integer fSort;

    private String supportSDK;
    private String supportScreen;
    

	public Integer getFSort() {
        return fSort;
    }

    public void setFSort(Integer fSort) {
        this.fSort = fSort;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
