package tw.com.sti.store.api.vo;

//import tw.com.sti.store.api.util.LangUtils;

public class Credential {
	private final String token;
	private final String uid;
	private final String userId;

	public Credential(String token, String uid, String userId) {
		this.token = token;
		this.uid = uid;
		this.userId = userId;
//		if (LangUtils.isBlankAny(token, uid, userId) > -1) {
//			throw new RuntimeException(
//					"Credential's token, uid, userId is blank any.");
//		}
	}

	public String getToken() {
		return token;
	}

	public String getUid() {
		return uid;
	}

	public String getUserId() {
		return userId;
	}
	
}
