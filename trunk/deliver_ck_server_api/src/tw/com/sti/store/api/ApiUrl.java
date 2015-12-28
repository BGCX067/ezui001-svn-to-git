package tw.com.sti.store.api;

import tw.com.sti.store.api.ApiService.AppsType;

public class ApiUrl {
	private Configuration config;
	public ApiUrl(Configuration config){
		this.config=config;
	}
	public String getRegisterUrl(){
		return config.getApiHttpsURL()+"/api/register";
	}
	public String getLoginUrl(){
		return config.getApiHttpsURL()+"/api/login";
	}
	public String getAutoLoginUrl(){
		return config.getApiHttpsURL()+"/api/autologin";
	}
	public String getIPLoginUrl(){
		return config.getApiHttpsURL()+"/api/iplogin";
	}
	public String getFeatureAppsUrl(){
		return config.getApiHttpURL()+"/api/feature/apps";
	}
	public String getCategoryUrl(){
		return config.getApiHttpURL()+"/api/category";
	}
	public String getClientUsageUrl(){
		return config.getApiHttpsURL()+"/api/client/usage";
	}
	public String getLicenseUrl(){
		return config.getApiHttpsURL()+"/app/getLicense.do";
	}
	public String getLicenseUrl(String pkg){
		return config.getApiHttpsURL() + "/api/application/" + pkg + "/license";
	}
	public String getCheckNewClientUrl(){
		return config.getApiHttpURL()+"/api/client/version";
	}
	public String getDownloadClientUrl(){
		return config.getApiHttpURL()+"/api/client/download";
	}
	public String getCategoryAppsUrl(String categoryId, AppsType appType, int page) {
		String appTypeUrl = convertApplicationListTypeToUrl(appType);
		return config.getApiHttpURL()+"/api/category/" + categoryId + "/" + appTypeUrl + "/page/" + page;
	}
	public String getPaymentOrderNumtUrl(String packageName) {
		return config.getApiHttpURL()+"/api/payment/order/" + packageName;
	}

	public String getApplicationDetailUrl(String packageName) {
		return config.getApiHttpURL()+"/api/application/" + packageName;
	}

	public String getApplicationCommentsUrl(String packageName, int pageNumber) {
		return config.getApiHttpURL()+"/api/application/" + packageName + "/comments/page/" + pageNumber;
	}

	public String getApplicationReportUrl(String packageName) {
		return config.getApiHttpURL()+"/api/application/" + packageName + "/report";
	}

	public String getApplicationRateUrl(String packageName) {
		return config.getApiHttpURL()+"/api/application/" + packageName + "/rating";
	}

	public String getCPAppsUrl(String cpId, AppsType appType, int page) {
		String appTypeUrl = convertApplicationListTypeToUrl(appType);
		return config.getApiHttpURL()+"/api/cp/" + cpId + "/" + appTypeUrl + "/page/" + page;
	}

	public String getSearchAppsUrl(AppsType appType, int page) {
		String appTypeUrl = convertApplicationListTypeToUrl(appType);
		return config.getApiHttpURL()+"/api/search/" + appTypeUrl + "/page/" + page;
	}

	public String getMyDownloadAppsUrl(AppsType appType, int page) {
		String appTypeUrl = convertApplicationListTypeToUrl(appType);
		return config.getApiHttpURL()+"/api/mydonwload/" + appTypeUrl + "/page/" + page;
	}

	public String getOverPaymentQuotaUrl(String pakcageName) {
		return config.getApiHttpURL()+"/api/payment/overQuota/" + pakcageName;
	}

	public String getCheckPayStatusUrl(String packageName) {
		return config.getApiHttpURL()+"/api/payment/checkPayStatus/" + packageName;
	}

	public String getUnsubscribeUrl(String packageName) {
		return config.getApiHttpURL()+"/api/payment/unsubscribe/" + packageName;
	}

	public String getMyAppsVersionUrl(int page) {
		return config.getApiHttpURL()+"/api/my/appversions/page/" + page;
	}

	public String getDownloadAppUrl(String pakcageName) {
		return config.getApiHttpURL()+"/api/app/download/" + pakcageName;
	}

	public String getCheckAppStoreListUrl(String pkg){
		return config.getApiHttpURL() + "/api/ck/listStore/" + pkg;
	}
	public String getGPaymentReceiverUrl(){
		return config.getApiHttpURL() + "/api/integrate/gpay/receiver";
	}
	public String getUnionPaymentReceiverUrl(){
		return config.getApiHttpURL() + "/api/integrate/unionpay/receiver";
	}
	public String getSdkAppInfoUrl(String pkg){
		return config.getApiHttpURL() + "/api/ck/sdk/appinfo/" + pkg;
	}
	public String getSendLogUrl(){
		return config.getApiHttpURL() + "/api/ck/logs";
	}
	public String getOrderStatusUrl(String pkg){
		return config.getApiHttpURL() + "/api/ck/info/order/" + pkg;
	}
	public String getOrderRefundUrl(){
		return config.getApiHttpURL() + "/api/ck/order/user/cancel";
	}
	
	/**
	 * 取得PePayOrder呼叫的URL
	 * @param pkg
	 * @return
	 */
	public String getPePayOrderUrl(String pkg) {
		return config.getApiPayHttpURL() + "/api/pepay/order/" + pkg;
	}
	private static String convertApplicationListTypeToUrl(AppsType appType) {
		switch (appType) {
		case FREE:
			return "freeapps";
		case PAID:
			return "paidapps";
		case ALL:
			return "apps";
		}
		return "apps";
	}
}
