package tw.com.sti.store.api.vo;

import org.json.JSONObject;

final public class NewClientInfo {

	private String msg;
	private int apkBytes;

	final static NewClientInfo createInstance(JSONObject json) {
		NewClientInfo ret = new NewClientInfo();
		ret.msg = json.optString("msg");
		ret.apkBytes = json.optInt("apkbytes");
//		if (LangUtils.isBlank(ret.msg)) {
//			if (Logger.DEBUG)
//				L.d("NewClientInfo.msg is blank.");
//		}
//		if (ret.apkBytes == 0) {
//			if (Logger.DEBUG)
//				L.d("NewClientInfo.fileSize is zero.");
//		}
		return ret;
	}

	private NewClientInfo() {
	}

	final public String getMsg() {
		return msg;
	}

	public int getApkBytes() {
		return apkBytes;
	}
}
