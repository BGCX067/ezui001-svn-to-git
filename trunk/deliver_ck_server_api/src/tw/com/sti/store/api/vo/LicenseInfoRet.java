package tw.com.sti.store.api.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LicenseInfoRet extends BaseRet {
	private List<LicenseInfo> application;
	public LicenseInfoRet(JSONObject json) {
		super(json);
		if(!isSuccess()){
			return;
		}
		application = LicenseInfo.parseApplication(json.optJSONArray("licenses"));
	}
	
	public List<LicenseInfo> getApplication() {
		return application;
	}

	final static public class LicenseInfo {

		private BigDecimal id;
		private String pkg;
		private String data;
		private String sign;

		final static List<LicenseInfo> parseApplication(JSONArray licenses) {
			if (licenses == null)
				return null;
			List<LicenseInfo> retList = new ArrayList<LicenseInfo>();
			try {
				for(int i=0; i<licenses.length(); i++) {
					LicenseInfo linfo = new LicenseInfo();
					JSONObject json = licenses.getJSONObject(i);
					linfo.id = new BigDecimal(json.getInt("id"));
					linfo.pkg = json.getString("pkg");
					linfo.data = json.getString("data");
					linfo.sign = json.getString("sign");
					retList.add(linfo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return retList;
		}

		private LicenseInfo() {
		}

		public BigDecimal getId() {
			return id;
		}

		public void setId(BigDecimal id) {
			this.id = id;
		}

		public String getPkg() {
			return pkg;
		}

		public void setPkg(String pkg) {
			this.pkg = pkg;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

	};
}
