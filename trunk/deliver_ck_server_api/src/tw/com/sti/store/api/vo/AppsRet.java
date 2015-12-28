package tw.com.sti.store.api.vo;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.vo.AppInfo.PayStatus;
import tw.com.sti.store.api.vo.AppInfo.PriceType;
import tw.com.sti.store.api.vo.ApplicationRet.Application;

public class AppsRet extends BaseRet {

	private boolean pageEnd;
	private App[] apps;

	public AppsRet(JSONObject json) {
		super(json);
		pageEnd = json.optBoolean("pageEnd");
		apps = App.parseApps(json.optJSONArray("apps"));
	}

	public boolean isPageEnd() {
		return pageEnd;
	}

	public App[] getApps() {
		return apps;
	}

	final static public class App implements Serializable {

		private static final long serialVersionUID = -7174127657368708686L;
		
		private String pkg;
		private int version;
		private String title;
		private String icon;
		private String provider;
		private float rating;
		private PriceType priceType;
		private String priceText;
		private PayStatus payStatus;
		private String subscribeExpDate;

		final static App[] parseApps(JSONArray appsData) {
			if (appsData == null || appsData.length() == 0) {
				return new App[0];
			}
			App[] apps;
			int count = appsData.length();
			ArrayList<App> appsList = new ArrayList<AppsRet.App>(count);
			for (int i = 0; i < count; i++) {
				JSONObject appData = appsData.optJSONObject(i);
				App app = App.parseApp(appData);
				if (app != null)
					appsList.add(app);
			}
			apps = appsList.toArray(new App[appsList.size()]);
			return apps;
		}

		final static App parseApp(JSONObject appData) {
			if (appData == null)
				return null;
			App app = new App();
			try {
				app.pkg = appData.getString("pkg");
				app.version = appData.getInt("version");
				app.title = appData.getString("title");
				app.icon = appData.getString("icon");
				app.provider = appData.getString("provider");
				app.rating = (float) appData.getDouble("rating");
				app.priceText = appData.getString("priceText");
				int priceType = appData.getInt("priceType");
				app.priceType = PriceType.parse(priceType);
				int payStatus = appData.getInt("payStatus");
				app.payStatus = PayStatus.parse(payStatus);
				app.subscribeExpDate = appData.optString("subscribeExpDate");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(appData, App.class,
						"App JSON invalid.", e);
			}
			return app;
		}
		
		public static App getAppfromApplication(Application application) {
			if (application == null) {
				return null;
			}
			App app = new App();
			app.pkg = application.getPkg();
			app.version = application.getVersion();
			app.title = application.getTitle();
			app.icon = application.getIcon();
			app.provider = application.getProvider();
			app.rating = application.getRating();
			app.priceText = application.getPriceText();
			app.priceType = application.getPriceType();
			app.payStatus = application.getPayStatus();
			app.subscribeExpDate = application.getSubscribeExpDate();
			return app;
		}

		private App() {
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

		public void setRating(float rating) {
			this.rating = rating;
		}

		public PayStatus getPayStatus() {
			return payStatus;
		}

		public void setPayStatus(PayStatus payStatus) {
			this.payStatus = payStatus;
		}

		public void setSubscribeExpDate(String subscribeExpDate) {
			this.subscribeExpDate = subscribeExpDate;
		}

		public PriceType getPriceType() {
			return priceType;
		}

		public String getPriceText() {
			return priceText;
		}

		public String getSubscribeExpDate() {
			return subscribeExpDate;
		}

		@Override
		public int hashCode() {
			if (pkg == null)
				return super.hashCode();
			return pkg.hashCode();
		}
	};

}
