package cn.vstore.appserver.apilog;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @version $Id: ApiInfo.java 7437 2011-03-03 06:26:58Z yellow $
 */
class ApiInfo {

    static final int TYPE_IPAY = 1;
    static final int TYPE_NORMAL = 0;

    private static final ApiInfo[] API_INFOS = new ApiInfo[] {
    		new ApiInfo("getAppUp","/api/checkappsup/all"),
    		new ApiInfo("appAddCollect", "/api/apps/collect"),
    		new ApiInfo("appDelCollect", "/api/apps/delCollect"),
    		new ApiInfo("appIsCollect", "/api/apps/isCollect"),
    		new ApiInfo("getAppCollectList", "/api/apps/collectAppList"),
    		new ApiInfo("getAppCommentsList", "/api/apps/commentsAppList"),
    		
    		new ApiInfo("getNewSpaces","/api/space/newSpace"),
    		new ApiInfo("getFeatureSpaces","/api/space/featureSpace"),
    		new ApiInfo("getAllapps","/api/ranking/[\\w]+/allapps/page/[0-9]"),
    		new ApiInfo("getGameranking","/api/ranking/[\\w]+/gameranking/page/[0-9]"),
    		new ApiInfo("getAppRanking","/api/ranking/[\\w]+/appranking/page/[0-9]"),
    		new ApiInfo("getGuessYourLike","/api/application/guess"),
    		new ApiInfo("getHomeAdvertisement","/api/home/advertisement"),
            new ApiInfo("Login", "/api/login").setSecretParams("pwd"),
            new ApiInfo("Login", "/doLogin.do").setSecretParams("passwd"),
            new ApiInfo("VpadLogin", "/api/vpadlogin"),
            new ApiInfo("AutoLogin", "/api/autologin"),
            new ApiInfo("Register", "/api/register"),
            new ApiInfo("AutoLogin", "/reLogin.do"),
            new ApiInfo("IPLogin", "/api/iplogin"),
            new ApiInfo("GetFeatureApps", "/api/feature/apps"),
            new ApiInfo("GetFeatureApps", "/app/featurelist.do"),
            new ApiInfo("GetCategoryList", "/api/category"),
            new ApiInfo("GetAppsOfCategory-All",
                    "/api/category/[\\w]+/apps/page/[0-9]+"),
            new ApiInfo("GetAppsOfCategory-Paid",
                    "/api/category/[\\w]+/paidapps/page/[0-9]+"),
            new ApiInfo("GetAppsOfCategory-Free",
                    "/api/category/[\\w]+/freeapps/page/[0-9]+"),
            new ApiInfo("GetAppDetail", "/api/application/[\\w\\.]+"),
            new ApiInfo("GetAppDetail", "/app/appdetail.do"),
            new ApiInfo("GetComments",
                    "/api/application/[\\w\\.]+/comments/page/[0-9]+"),
            new ApiInfo("GetComments", "/app/commentList.do"),
            new ApiInfo("ReportApp", "/api/application/[\\w\\.]+/report"),
            new ApiInfo("ReportApp", "/api/application/[\\w\\.]+/report/"),
            new ApiInfo("ReportApp", "/report/send.do"),
            new ApiInfo("RateApp", "/api/application/[\\w\\.]+/rating"),
            new ApiInfo("RateApp", "/api/application/[\\w\\.]+/rating/"),
            new ApiInfo("RateApp", "/rank/send.do"),
            new ApiInfo("SearchApps-All", "/api/search/apps/page/[0-9]+"),
            new ApiInfo("SearchApps-Paid", "/api/search/paidapps/page/[0-9]+"),
            new ApiInfo("SearchApps-Free", "/api/search/freeapps/page/[0-9]+"),
            new ApiInfo("GetCPApps-All", "/api/cp/[\\w\\.-]+@([\\w-]+\\.)+\\w{2,4}/apps/page/[0-9]+"),
            new ApiInfo("GetCPApps-Paid",
                    "/api/cp/[\\w\\.-]+@([\\w-]+\\.)+\\w{2,4}/paidapps/page/[0-9]+"),
            new ApiInfo("GetCPApps-Free",
                    "/api/cp/[\\w\\.-]+@([\\w-]+\\.)+\\w{2,4}/freeapps/page/[0-9]+"),
            new ApiInfo("GetMyApps-All", "/api/mydonwload/apps/page/[0-9]+"),
            new ApiInfo("GetMyApps-Paid",
                    "/api/mydonwload/paidapps/page/[0-9]+"),
            new ApiInfo("GetMyApps-Free",
                    "/api/mydonwload/freeapps/page/[0-9]+"),
            new ApiInfo("ClientUsageLog", "/api/client/usage"),
            new ApiInfo("CheckMonthlyQuota", "/api/payment/overQuota/[\\w\\.]+"),
            new ApiInfo("IpayCheckAppOrderStatus",
                    "/api/payment/checkPayStatus/[\\w\\.]+"),
            new ApiInfo("IpayCheckAppOrderStatus",
                    "/ipay/checkApkOrderStatus.do"),
            new ApiInfo("Unsubscribe", "/api/payment/unsubscribe/[\\w\\.]+"),
            new ApiInfo("Unsubscribe", "/subscribe/doUnsubscribe.do"),
            new ApiInfo("CheckVersionNotice-Client", "/api/client/version"),
            new ApiInfo("DownloadClient", "/api/client/download"),
            new ApiInfo("DownloadClient", "/app/getStoreClientApk.do"),
            new ApiInfo("CheckVersionNotice-App",
                    "/api/my/appversions/page/[0-9]+"),
            new ApiInfo("DownloadApp", "/api/app/download/[\\w\\.]+"),
            new ApiInfo("DownloadApp", "/app/download.do"),
            new ApiInfo("GetNewOrder", "/api/payment/order/[\\w\\.]+"),
            new ApiInfo("GetCatOrAppList", "/app/catlist.do"),
            new ApiInfo("CheckNewVersion-Client", "/app/commentList.do"),
            new ApiInfo("GetLicense", "/app/getLicense.do"),
            new ApiInfo("SendLog", "/api/ck/logs"),
            new ApiInfo("SendLog", "/log/send.do"),
            new ApiInfo("SubstoreAddOrder", "/api/substore/order/add"),
            new ApiInfo("SubstoreDeleteOrder", "/api/substore/order/del"),
            new ApiInfo("SubstoreSyncToken", "/api/substore/user/syncToken"),
            new ApiInfo("GetOrderStatus", "/api/ck/info/order/[\\w\\.]+"),
            new ApiInfo("CancelOrder", "/api/ck/order/user/cancel"),
            new ApiInfo("GetSdkAppInfo", "/api/ck/sdk/appinfo/[\\w\\.]+"),
            new ApiInfo("GetAllAppList", "/app/allApplist.do"),
            new ApiInfo("SearchApps", "/app/search.do"),
            new ApiInfo("IpayLogin", "/ipay/doLogin.do", TYPE_IPAY)
                    .setSecretParams("passwd"),
            new ApiInfo("IpayGetUserPayInfo", "/ipay/getUserPayDetail.do",
                    TYPE_IPAY),
            new ApiInfo("IpayGetUserPayMethod", "/ipay/getUserPayMethod.do",
                    TYPE_IPAY),
            new ApiInfo("IpayAddCreditCardNo", "/ipay/addCreditCardNo.do",
                    TYPE_IPAY).setSecretParams("cardno", "securityCode",
                    "expYear", "expMonth"),
            new ApiInfo("IpayDoTransaction", "/ipay/doTransaction.do",
                    TYPE_IPAY),
            new ApiInfo("GetMyApps-Monthly", "/subscribe/getUserSubscribe.do"),
            new ApiInfo("RecommenderAppList", "/recommend/getApkList.do"),
            new ApiInfo("RecommenderDetail",
                    "/recommend/getRecommenderDetail.do"),
            new ApiInfo("GetCPAppList", "/cp/getApkList.do"),
            new ApiInfo("GetMyApps-OnetimeFree", "/app/getOnetimeFreeList.do"),
            new ApiInfo("CheckNewVersion-App", "/sdk/getAppVersion.do"),
            new ApiInfo("GetTopCatList", "/app/getTopCatList.do"),
            new ApiInfo("DownloadAppWeb-OneTime", "/app/dapk.do"),
            new ApiInfo("SendUserEmail", "/info/sendInfo.do", TYPE_IPAY),
            new ApiInfo("IpayGetMonthlyTotalFee", "/ipay/getTotalFee.do"),
            new ApiInfo("DownloadAppWeb-NoAuth", "/dapk.do"),
            new ApiInfo("GetLicense-AppSelfPurchase",
                    "/app/getAppSelfPurchaseLicense.do"),
            new ApiInfo("IpayServerProductPurchaseGetInfo",
                    "/ipay/serverProductPurchaseGetInfo.do", TYPE_IPAY),
            new ApiInfo("IpayServerProductPurchaseDoTransaction",
                    "/ipay/serverProductPurchaseDoTransaction.do", TYPE_IPAY),
            new ApiInfo("CPServerProductPurchaseResult",
                    "/cp/serverProductPurchaseResult.do") };

    final String name;
    final int type;
    private String[] secretParams;
    private final Pattern pattern;

    private ApiInfo(String name, String regExp) {
        this.name = name;
        this.pattern = Pattern.compile(regExp);
        this.type = TYPE_NORMAL;
    }

    private ApiInfo(String name, String regExp, int type) {
        this.name = name;
        this.pattern = Pattern.compile(regExp);
        this.type = type;
    }

    private ApiInfo setSecretParams(String... secretParams) {
        this.secretParams = secretParams;
        return this;
    }

    boolean isSecretParam(String pname) {
        if (secretParams == null || StringUtils.isBlank(pname)
                || secretParams.length == 0)
            return false;

        return Arrays.binarySearch(secretParams, pname) > -1;
    }

    private boolean match(String uri) {
        return pattern.matcher(uri).matches();
    }

    static final ApiInfo parseApiInfo(String uri) {
        for (ApiInfo apiName : API_INFOS) {
            if (apiName.match(uri))
                return apiName;
        }
        return null;
    }

}
