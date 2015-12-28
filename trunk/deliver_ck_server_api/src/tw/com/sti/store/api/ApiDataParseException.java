package tw.com.sti.store.api;

public class ApiDataParseException extends Exception {

	private static final long serialVersionUID = 8847946270853055303L;

	public ApiDataParseException() {
		super();
	}

	public ApiDataParseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ApiDataParseException(Throwable throwable) {
		super(throwable);
	}

}
