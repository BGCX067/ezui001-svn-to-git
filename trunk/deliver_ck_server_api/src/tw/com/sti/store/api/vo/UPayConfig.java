package tw.com.sti.store.api.vo;

public class UPayConfig {
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
	
	public UPayConfig(String isTest, String checkSignUrl, String merchantId, String merchantName, 
			String merchantPublicCer, String privateKeyFileName, String privateKeyAlias, 
			String privateKeyPassword, String transTimeout, String backEndUrl
			){
		this.isTest = isTest;
		this.checkSignUrl = checkSignUrl;
		this.merchantId = merchantId;
		this.merchantName = merchantName;
		this.merchantPublicCer = merchantPublicCer;
		this.privateKeyFileName = privateKeyFileName;
		this.privateKeyAlias = privateKeyAlias;
		this.privateKeyPassword = privateKeyPassword;
		this.transTimeout = transTimeout;
		this.backEndUrl = backEndUrl;
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
	public String getBackEndUrl() {
		return backEndUrl;
	}
	public void setBackEndUrl(String backEndUrl) {
		this.backEndUrl = backEndUrl;
	}
	public String getIsTest() {
		return isTest;
	}
	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}
}
