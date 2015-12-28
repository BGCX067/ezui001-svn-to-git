package tw.com.sti.store.api.vo;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.util.JSONUtils;
import tw.com.sti.store.api.vo.AppInfo.PayStatus;
import tw.com.sti.store.api.vo.AppInfo.PriceType;
import tw.com.sti.store.api.vo.CommentsRet.Comment;

final public class ApplicationRet extends BaseRet {

	private Application application;

	public ApplicationRet(JSONObject json) {
		super(json);
		if (isSuccess()) {
			try {
				JSONObject appData = json.getJSONObject("app");
				application = Application.parseApplication(appData);
			} catch (JSONException e) {
				throw new ApiDataInvalidException(json, ApplicationRet.class,
						"ApplicationRet JSON is invalid.", e);
			}
		}
	}

	public Application getApplication() {
		return application;
	}

	final static public class Application implements Serializable {

		private static final long serialVersionUID = 3253812587696258870L;

		public static enum Action {
			INSTALL, OPEN, UNINSTALL, UPDATE, BUY, SUBSCRIBE, UN_SUBSCRIBE, CANCEL;
		}

		private String pkg;
		private int version;
		private int apkBytes;
		private String title;
		private String icon;
		private String provider;
		private float rating;
		private float price;
		private String priceText;
		private PriceType priceType;
		private PayStatus payStatus;
		private String subscribeExpDate;
		private String downloadTimes;
		private String ratingTimes;
		private String introduction;
		private String[] screenShots;
		private Comment[] comments;
		private Integer myRating;
		private String myComment;
		private String about;
		private String providerID;
		private String providerSite;
		private String providerEmail;
		private boolean reportable;
		private boolean commentable;
		private boolean ratingable;
		private String alertInstallMsg;
		private String alertUnsubscribeMsg;
		private String alertUninstallMsg;

		final static Application parseApplication(JSONObject appData) {
			if (appData == null)
				return null;
			Application ret = new Application();
			try {
				ret.pkg = appData.getString("pkg");
				ret.version = appData.getInt("version");
				ret.apkBytes = appData.optInt("apkBytes");
				ret.title = appData.getString("title");
				ret.icon = appData.getString("icon");
				ret.provider = appData.getString("provider");
				ret.rating = (float) appData.getDouble("rating");
				ret.price = (float) appData.getDouble("price");
				ret.priceText = appData.getString("priceText");
				int priceType = appData.getInt("priceType");
				ret.priceType = PriceType.parse(priceType);
				int payStatus = appData.getInt("payStatus");
				ret.payStatus = PayStatus.parse(payStatus);
				ret.subscribeExpDate = appData.optString("subscribeExpDate");
				ret.downloadTimes = appData.getString("downloadTimes");
				ret.ratingTimes = appData.getString("ratingTimes");
				ret.introduction = appData.getString("introduction");
				ret.introduction = ret.introduction.replace("\r", "");
				// screenShots
				JSONArray scrShots = appData.getJSONArray("screenShots");
				ret.screenShots = JSONUtils.parseStringArrayNoBlank(scrShots);
				if (appData.has("comments")) {
					JSONArray cmtsData = appData.optJSONArray("comments");
					ret.comments = Comment.parseComments(cmtsData);
				}
				if (appData.has("myRating"))
					ret.myRating = appData.optInt("myRating");
				if (appData.has("myComment"))
					ret.myComment = appData.optString("myComment");
				ret.about = appData.getString("about");
				ret.providerID = appData.getString("providerID");
				ret.providerSite = appData.getString("providerSite");
				ret.providerEmail = appData.getString("providerEmail");
				ret.reportable = appData.getBoolean("reportable");
				ret.commentable = appData.getBoolean("commentable");
				ret.ratingable = appData.getBoolean("ratingable");
				ret.alertInstallMsg = appData.optString("alertInstallMsg");
				ret.alertUnsubscribeMsg = appData
						.optString("alertUnsubscribeMsg");
				ret.alertUninstallMsg = appData.optString("alertUninstallMsg");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(appData, Application.class,
						"Application JSON invalid.", e);
			}
			return ret;
		}

		private Application() {
		}

		public String getPkg() {
			return pkg;
		}

		public int getVersion() {
			return version;
		}

		public int getApkBytes() {
			return apkBytes;
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

		 public PriceType getPriceType() {
			 return priceType;
		 }

		public String getPriceText() {
			return priceText;
		}

		 public PayStatus getPayStatus() {
		 return payStatus;
		 }

		public boolean isPayProcessing() {
			return PayStatus.PAY_PROCESSING.equals(payStatus);
		}

		public String getDownloadTimes() {
			return downloadTimes;
		}

		public String getRatingTimes() {
			return ratingTimes;
		}

		public String getIntroduction() {
			return introduction;
		}

		public String[] getScreenShots() {
			return screenShots;
		}

		public Comment[] getComments() {
			return comments;
		}

		public Integer getMyRating() {
			return myRating;
		}

		public String getMyComment() {
			return myComment;
		}

		public String getAbout() {
			return about;
		}

		public String getProviderID() {
			return providerID;
		}

		public String getProviderSite() {
			return providerSite;
		}

		public String getProviderEmail() {
			return providerEmail;
		}

		public boolean isReportable() {
			return reportable;
		}

		public boolean isCommentable() {
			return commentable;
		}

		public boolean isRatingable() {
			return ratingable;
		}

		public String getAlertInstallMsg() {
			return alertInstallMsg;
		}

		public String getAlertUnsubscribeMsg() {
			return alertUnsubscribeMsg;
		}

		public String getAlertUninstallMsg() {
			return alertUninstallMsg;
		}

		public String getSubscribeExpDate() {
			return subscribeExpDate;
		}
	};

}
