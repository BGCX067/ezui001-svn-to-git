package cn.vstore.appserver.form.payment;


import org.apache.commons.lang.builder.ToStringBuilder;

import cn.vstore.appserver.form.TokenForm;

public class SelectPaymentForm extends TokenForm {

	private String storeOid;
	private String payeeInfo;
	
	private String isTest;
	private String checkSignUrl;
	private String merchantId;
	private String merchantName;
	private String merchantPublicCer;
	private String privateKeyFileName;
	private String privateKeyAlias;
	private String privateKeyPassword;
	private String transTimeout;
	private String backEndUrl;
	private String payType;

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public String getStoreOid() {
		return storeOid;
	}

	public void setStoreOid(String storeOid) {
		this.storeOid = storeOid;
	}

	public String getPayeeInfo() {
		return payeeInfo;
	}

	public void setPayeeInfo(String payeeInfo) {
		this.payeeInfo = payeeInfo;
	}

	public String getCheckSignUrl() {
		return checkSignUrl;
	}

	public void setCheckSignUrl(String checkSignUrl) {
		this.checkSignUrl = checkSignUrl;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantPublicCer() {
		return merchantPublicCer;
	}

	public void setMerchantPublicCer(String merchantPublicCer) {
		this.merchantPublicCer = merchantPublicCer;
	}

	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	public String getPrivateKeyAlias() {
		return privateKeyAlias;
	}

	public void setPrivateKeyAlias(String privateKeyAlias) {
		this.privateKeyAlias = privateKeyAlias;
	}

	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	public String getTransTimeout() {
		return transTimeout;
	}

	public void setTransTimeout(String transTimeout) {
		this.transTimeout = transTimeout;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String getBackEndUrl() {
		return backEndUrl;
	}

	public void setBackEndUrl(String backEndUrl) {
		this.backEndUrl = backEndUrl;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
}
