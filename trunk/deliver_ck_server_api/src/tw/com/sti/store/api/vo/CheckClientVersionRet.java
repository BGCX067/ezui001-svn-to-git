package tw.com.sti.store.api.vo;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

final public class CheckClientVersionRet extends BaseRet {

	private static NewClient newClient;

	public CheckClientVersionRet(JSONObject json) {
		super(json);

		if (!json.isNull("newClient")) {
			newClient = NewClient.parseNewClient(json
					.optJSONObject("newClient"));
		}
	}

	public NewClient getNewClient() {
		return newClient;
	}

	public static final class NewClient {
		private int version;
		private int apkBytes;
		private String msg;
		private String title;
		private String notification;
		private String ticker;
		private String dltoken;

		static final NewClient parseNewClient(JSONObject json) {
			if (json == null) {
				return null;
			}
			NewClient newClient = new NewClient();
			try {
				newClient.version = json.getInt("version");
				newClient.apkBytes = json.getInt("apkbytes");
				newClient.msg = json.getString("msg");
				newClient.title = json.getString("title");
				newClient.notification = json.getString("notification");
				newClient.ticker = json.getString("ticker");
				newClient.dltoken = json.optString("dltoken");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(json, NewClient.class,
						"NewClient JSON invalid.", e);
			}
			return newClient;
		}

		private NewClient() {
		}

		public int getVersion() {
			return version;
		}

		public int getApkBytes() {
			return apkBytes;
		}

		public String getMsg() {
			return msg;
		}

		public String getTitle() {
			return title;
		}

		public String getNotification() {
			return notification;
		}

		public String getTicker() {
			return ticker;
		}

		public String getDownloadToken() {
			return dltoken;
		}
	}
}