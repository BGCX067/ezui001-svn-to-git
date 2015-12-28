package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

public class UserMenuFeed implements Serializable {
    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_menu_feed.id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    private Long id;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_menu_feed.menu_id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    private Long menuId;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_menu_feed.para_id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    private Long paraId;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column v_user_menu_feed.m_time
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    private Date mTime;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database table v_user_menu_feed
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_menu_feed.id
     *
     * @return the value of v_user_menu_feed.id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_menu_feed.id
     *
     * @param id the value for v_user_menu_feed.id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_menu_feed.menu_id
     *
     * @return the value of v_user_menu_feed.menu_id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_menu_feed.menu_id
     *
     * @param menuId the value for v_user_menu_feed.menu_id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_menu_feed.para_id
     *
     * @return the value of v_user_menu_feed.para_id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public Long getParaId() {
        return paraId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_menu_feed.para_id
     *
     * @param paraId the value for v_user_menu_feed.para_id
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public void setParaId(Long paraId) {
        this.paraId = paraId;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column v_user_menu_feed.m_time
     *
     * @return the value of v_user_menu_feed.m_time
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public Date getmTime() {
        return mTime;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column v_user_menu_feed.m_time
     *
     * @param mTime the value for v_user_menu_feed.m_time
     *
     * @ibatorgenerated Thu Jun 28 11:25:57 CST 2012
     */
    public void setmTime(Date mTime) {
        this.mTime = mTime;
    }
}