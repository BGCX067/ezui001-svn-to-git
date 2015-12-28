package cn.vstore.appserver.form;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ReportAppForm extends TokenForm {

    private String lappv; // Local Application version code
    private String reportId;

    public String getLappv() {
        return lappv;
    }

    public void setLappv(String lappv) {
        this.lappv = lappv;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}