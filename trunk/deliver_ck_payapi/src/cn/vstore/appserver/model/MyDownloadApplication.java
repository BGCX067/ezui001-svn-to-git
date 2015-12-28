/**
 *
 */
package cn.vstore.appserver.model;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id$
 */
public class MyDownloadApplication extends Application {

    private static final long serialVersionUID = -5173476392682818616L;
    private int isEndDate;
    private int isCanceled;
    private Date endTime;
    private Date sortTime;

    public int getIsEndDate() {
        return isEndDate;
    }

    public void setIsEndDate(int isEndDate) {
        this.isEndDate = isEndDate;
    }

    public int getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(int isCanceled) {
        this.isCanceled = isCanceled;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getSortTime() {
        return sortTime;
    }

    public void setSortTime(Date sortTime) {
        this.sortTime = sortTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}
