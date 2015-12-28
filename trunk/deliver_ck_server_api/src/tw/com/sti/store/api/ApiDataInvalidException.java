package tw.com.sti.store.api;

import org.json.JSONObject;

import tw.com.sti.store.api.vo.BaseRet;

public class ApiDataInvalidException extends RuntimeException {

	private static final long serialVersionUID = -7553060060114570146L;

	private JSONObject json;

	private Class<? extends BaseRet> clazz;

	public ApiDataInvalidException(JSONObject json,
			Class<? extends Object> clazz) {
	}

	public ApiDataInvalidException(JSONObject json,
			Class<? extends Object> clazz, String detailMessage) {
		super(detailMessage);
	}

	public ApiDataInvalidException(JSONObject json,
			Class<? extends Object> clazz, String detailMessage, Throwable e) {
		super(detailMessage, e);
	}

	public JSONObject getJson() {
		return json;
	}

	public Class<? extends BaseRet> getClazz() {
		return clazz;
	}

	// public RetDataInvalidException(Throwable throwable) {
	// super(throwable);
	// }
	// public RetDataInvalidException(String detailMessage, Throwable throwable)
	// {
	// super(detailMessage, throwable);
	// }

}
