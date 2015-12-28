package cn.com.vapk.vstore.client.receiver.listener;

public class LoginEvent {
	public static String DOWNLOAD="DOWNLOAD";
	public static String PAY="PAY";
	
	private String eventType;

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

}
