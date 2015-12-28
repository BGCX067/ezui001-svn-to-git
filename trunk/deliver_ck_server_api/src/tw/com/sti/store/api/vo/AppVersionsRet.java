package tw.com.sti.store.api.vo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

public class AppVersionsRet extends BaseRet {

	private boolean pageEnd;
	private AppVersion[] appVersions;

	public AppVersionsRet(JSONObject json) {
		super(json);
		if (!isSuccess())
			return;
		pageEnd = json.optBoolean("pageEnd");
		appVersions = AppVersion.parseAppVersions(json.optJSONArray("appvs"));
	}

	public boolean isPageEnd() {
		return pageEnd;
	}

	public AppVersion[] getApps() {
		return appVersions;
	}

	final static public class AppVersion {

		private String pkg;
		private int version;

		final static AppVersion[] parseAppVersions(JSONArray appsData) {
			if (appsData == null || appsData.length() == 0) {
				return new AppVersion[0];
			}
			AppVersion[] apps;
			int count = appsData.length();
			ArrayList<AppVersion> appsList = new ArrayList<AppVersionsRet.AppVersion>(
					count);
			for (int i = 0; i < count; i++) {
				JSONObject appData = appsData.optJSONObject(i);
				AppVersion app = AppVersion.parseApp(appData);
				if (app != null)
					appsList.add(app);
			}
			apps = appsList.toArray(new AppVersion[appsList.size()]);
			return apps;
		}

		final static AppVersion parseApp(JSONObject appData) {
			if (appData == null)
				return null;
			AppVersion app = new AppVersion();
			try {
				app.pkg = appData.getString("pkg");
				app.version = appData.getInt("version");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(appData, AppVersion.class,
						"App JSON invalid.", e);
			}
			return app;
		}

		private AppVersion() {
		}

		public String getPkg() {
			return pkg;
		}

		public int getVersion() {
			return version;
		}

		@Override
		public int hashCode() {
			if (pkg == null)
				return super.hashCode();
			return pkg.hashCode();
		}
	};

}
