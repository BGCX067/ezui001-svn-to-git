package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

public class UserDownloadTimes implements Serializable {
    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_download_times.id
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    private Long id;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_download_times.pkgName
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    private String pkgname;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_download_times.d_time
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    private Date dTime;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_download_times.action_id
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    private Long paraId;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database table v_user_download_times
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_download_times.id
     *
     * @return the value of v_user_download_times.id
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_download_times.id
     *
     * @param id the value for v_user_download_times.id
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_download_times.pkgName
     *
     * @return the value of v_user_download_times.pkgName
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public String getPkgname() {
        return pkgname;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_download_times.pkgName
     *
     * @param pkgname the value for v_user_download_times.pkgName
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public void setPkgname(String pkgname) {
        this.pkgname = pkgname == null ? null : pkgname.trim();
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_download_times.d_time
     *
     * @return the value of v_user_download_times.d_time
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public Date getdTime() {
        return dTime;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_download_times.d_time
     *
     * @param dTime the value for v_user_download_times.d_time
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public void setdTime(Date dTime) {
        this.dTime = dTime;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_download_times.action_id
     *
     * @return the value of v_user_download_times.action_id
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public Long getParaId() {
        return paraId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_download_times.action_id
     *
     * @param actionId the value for v_user_download_times.action_id
     *
     * @ibatorgenerated Tue Jun 26 20:58:29 CST 2012
     */
    public void setParaId(Long paraId) {
        this.paraId = paraId;
    }
}