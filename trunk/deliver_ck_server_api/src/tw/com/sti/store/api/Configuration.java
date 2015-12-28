package tw.com.sti.store.api;

public class Configuration {
	public static enum RUNTIME_SERVER_TYPE {
		PRODUCT,TEST_PRODUCT,STAGING,DEVELOPER
	}
	public Configuration(){
		
	}
	private RUNTIME_SERVER_TYPE serverType=RUNTIME_SERVER_TYPE.PRODUCT;
	private int apiHttpPort=80;
	private int apiHttpsPort=443;
	private boolean supportSSL=false;
	private String apiRootPath="/";
	private String apiHostname;
	private int apiTimeout=30000;
	private String apiPrivkey;
	private String snum;
	private String unionPayPrivateKeyFileName;
	private String unionPayAlias;
	private String unionPayPassword;
	private String unionPayMerchantId;
	private String unionPayMerchantName;
	private String unionPayTransTimeout;
	private String unionPayBackEndUrl;
	private String unionPayMerchant_public_cer;
	private String unionPayCheckSignUrl;
	
	private int apiPayHttpPort=80;
	private int apiPayHttpsPort=443;
	private String apiPayRootPath="/";
	private String apiPayHostname;
	
	public String getApiHttpURL(){
		return "http://" + this.getApiHostname() + ":" + this.getApiHttpPort() + this.getApiRootPath();
	}
	public String getApiHttpsURL(){
		return "http://" + this.getApiHostname() + ":" + this.getApiHttpsPort() + this.getApiRootPath();
	}
	
	public String getApiPayHttpURL(){
		return "http://" + this.getApiPayHostname() + ":" + this.getApiPayHttpPort() + this.getApiPayRootPath();
	}
	public String getApiPayHttpsURL(){
		return "http://" + this.getApiPayHostname() + ":" + this.getApiPayHttpsPort() + this.getApiPayRootPath();
	}
	
	public int getApiHttpPort() {
		return apiHttpPort;
	}
	public void setApiHttpPort(int apiHttpPort) {
		this.apiHttpPort = apiHttpPort;
	}
	public int getApiHttpsPort() {
		if(this.isSupportSSL()){
			return apiHttpsPort;
		}else{
			return apiHttpPort;
		}
	}
	public void setApiHttpsPort(int apiHttpsPort) {
		this.apiHttpsPort = apiHttpsPort;
	}
	public boolean isSupportSSL() {
		return supportSSL;
	}
	public void setSupportSSL(boolean supportSSL) {
		this.supportSSL = supportSSL;
	}
	public String getApiRootPath() {
		return apiRootPath;
	}
	public void setApiRootPath(String apiRootPath) {
		this.apiRootPath = apiRootPath;
	}
	public String getApiHostname() {
		return apiHostname;
	}
	public void setApiHostname(String apiHostname) {
		this.apiHostname = apiHostname;
	}
	public int getApiTimeout() {
		return apiTimeout;
	}
	public void setApiTimeout(int apiTimeout) {
		this.apiTimeout = apiTimeout;
	}
	public RUNTIME_SERVER_TYPE getServerType() {
		return serverType;
	}
	public void setServerType(RUNTIME_SERVER_TYPE serverType) {
		this.serverType = serverType;
	}
	public String getApiPrivkey() {
		return apiPrivkey;
	}
	public void setApiPrivkey(String apiPrivkey) {
		this.apiPrivkey = apiPrivkey;
	}
	public String getSnum() {
		return snum;
	}
	public void setSnum(String snum) {
		this.snum = snum;
	}
	public String getUnionPayPrivateKeyFileName() {
		return unionPayPrivateKeyFileName;
	}
	public void setUnionPayPrivateKeyFileName(String unionPayPrivateKeyFileName) {
		this.unionPayPrivateKeyFileName = unionPayPrivateKeyFileName;
	}
	public String getUnionPayAlias() {
		return unionPayAlias;
	}
	public void setUnionPayAlias(String unionPayAlias) {
		this.unionPayAlias = unionPayAlias;
	}
	public String getUnionPayPassword() {
		return unionPayPassword;
	}
	public void setUnionPayPassword(String unionPayPassword) {
		this.unionPayPassword = unionPayPassword;
	}
	public String getUnionPayMerchantId() {
		return unionPayMerchantId;
	}
	public void setUnionPayMerchantId(String unionPayMerchantId) {
		this.unionPayMerchantId = unionPayMerchantId;
	}
	public String getUnionPayMerchantName() {
		return unionPayMerchantName;
	}
	public void setUnionPayMerchantName(String unionPayMerchantName) {
		this.unionPayMerchantName = unionPayMerchantName;
	}

	public String getUnionPayTransTimeout() {
		return unionPayTransTimeout;
	}
	public void setUnionPayTransTimeout(String unionPayTransTimeout) {
		this.unionPayTransTimeout = unionPayTransTimeout;
	}
	public String getUnionPayBackEndUrl() {
		return unionPayBackEndUrl;
	}
	public void setUnionPayBackEndUrl(String unionPayBackEndUrl) {
		this.unionPayBackEndUrl = unionPayBackEndUrl;
	}
	public String getUnionPayMerchant_public_cer() {
		return unionPayMerchant_public_cer;
	}
	public void setUnionPayMerchant_public_cer(String unionPayMerchant_public_cer) {
		this.unionPayMerchant_public_cer = unionPayMerchant_public_cer;
	}
	public String getUnionPayCheckSignUrl() {
		return unionPayCheckSignUrl;
	}
	public void setUnionPayCheckSignUrl(String unionPayCheckSignUrl) {
		this.unionPayCheckSignUrl = unionPayCheckSignUrl;
	}
	public int getApiPayHttpPort() {
		return apiPayHttpPort;
	}
	public void setApiPayHttpPort(int apiPayHttpPort) {
		this.apiPayHttpPort = apiPayHttpPort;
	}
	public int getApiPayHttpsPort() {
		return apiPayHttpsPort;
	}
	public void setApiPayHttpsPort(int apiPayHttpsPort) {
		this.apiPayHttpsPort = apiPayHttpsPort;
	}
	public String getApiPayRootPath() {
		return apiPayRootPath;
	}
	public void setApiPayRootPath(String apiPayRootPath) {
		this.apiPayRootPath = apiPayRootPath;
	}
	public String getApiPayHostname() {
		return apiPayHostname;
	}
	public void setApiPayHostname(String apiPayHostname) {
		this.apiPayHostname = apiPayHostname;
	}
	
}
