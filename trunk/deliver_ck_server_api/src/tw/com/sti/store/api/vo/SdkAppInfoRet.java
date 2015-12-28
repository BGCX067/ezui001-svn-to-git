package tw.com.sti.store.api.vo;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.vo.AppInfo.PayStatus;
import tw.com.sti.store.api.vo.AppInfo.PriceType;

public class SdkAppInfoRet extends BaseRet {
	private SdkApp application;
	public SdkAppInfoRet(JSONObject json) {
		super(json);
		if (isSuccess()) {
			try {
				JSONObject appData = json.getJSONObject("sdkApp");
				application = SdkApp.parseApplication(appData);
			} catch (JSONException e) {
				throw new ApiDataInvalidException(json, SdkAppInfoRet.class,
						"SdkAppRet JSON is invalid.", e);
			}
		}
	}
	
	public SdkApp getApplication() {
		return application;
	}

	final static public class SdkApp {

		private String pkg;
		private int version;
		private String title;
		private String icon;
		private String provider;
		private float rating;
		private float price;
		private String priceText;
		private PriceType priceType;
		private int appPriceType;
		private PayStatus payStatus = PayStatus.NO_PAID;
		private int paymentStatus = 0;
		private String subscribeExpDate;
		private String ratingTimes;
		private int onUse;
		private long logId;

		final static SdkApp parseApplication(JSONObject appData) {
			if (appData == null)
				return null;
			SdkApp ret = new SdkApp();
			try {
				ret.pkg = appData.getString("pkg");
				ret.version = appData.getInt("version");
				ret.title = appData.getString("title");
				ret.icon = appData.getString("icon");
				ret.provider = appData.getString("provider");
				ret.rating = (float) appData.getDouble("rating");
				ret.price = (float) appData.getDouble("price");
				ret.priceText = appData.getString("priceText");
				ret.appPriceType = appData.getInt("priceType");
				ret.priceType = PriceType.parse(ret.appPriceType);
				if(!appData.isNull("payStatus")){
					ret.paymentStatus = appData.getInt("payStatus");
					ret.payStatus = PayStatus.parse(ret.paymentStatus);
				}
				if(!appData.isNull("onUse")){
					ret.onUse=appData.getInt("onUse");
				}
				if(!appData.isNull("subscribeExpDate"))ret.subscribeExpDate = appData.optString("subscribeExpDate");
				ret.ratingTimes = appData.getString("ratingTimes");
				// screenShots
				if(!appData.isNull("logId")) ret.logId = appData.optLong("logId");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(appData, SdkApp.class,
						"Application JSON invalid.", e);
			}
			return ret;
		}

		private SdkApp() {
		}

		public boolean isPayProcessing() {
			return PayStatus.PAY_PROCESSING.equals(payStatus);
		}
		public String getPkg() {
			return pkg;
		}

		public int getVersion() {
			return version;
		}

		public String getTitle() {
			return title;
		}

		public String getIcon() {
			return icon;
		}

		public String getProvider() {
			return provider;
		}

		public float getRating() {
			return rating;
		}

		public float getPrice() {
			return price;
		}

		public String getPriceText() {
			return priceText;
		}

		public PriceType getPriceType() {
			return priceType;
		}

		public PayStatus getPayStatus() {
			return payStatus;
		}

		public String getSubscribeExpDate() {
			return subscribeExpDate;
		}

		public String getRatingTimes() {
			return ratingTimes;
		}

		public long getLogId() {
			return logId;
		}

		public int getPaymentStatus() {
			return paymentStatus;
		}

		public int getAppPriceType() {
			return appPriceType;
		}

		public int getOnUse() {
			return onUse;
		}
	};
}
