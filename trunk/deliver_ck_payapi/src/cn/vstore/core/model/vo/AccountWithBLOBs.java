package cn.vstore.core.model.vo;

import java.io.Serializable;

public class AccountWithBLOBs extends Account implements Serializable {
    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column account.name
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    private String name;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database column account.pwd_reset_token
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    private String pwdResetToken;

    /**
     * This field was generated by Apache iBATIS ibator.
     * This field corresponds to the database table account
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column account.name
     *
     * @return the value of account.name
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column account.name
     *
     * @param name the value for account.name
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method returns the value of the database column account.pwd_reset_token
     *
     * @return the value of account.pwd_reset_token
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    public String getPwdResetToken() {
        return pwdResetToken;
    }

    /**
     * This method was generated by Apache iBATIS ibator.
     * This method sets the value of the database column account.pwd_reset_token
     *
     * @param pwdResetToken the value for account.pwd_reset_token
     *
     * @ibatorgenerated Tue Dec 13 18:13:54 CST 2011
     */
    public void setPwdResetToken(String pwdResetToken) {
        this.pwdResetToken = pwdResetToken == null ? null : pwdResetToken.trim();
    }
}