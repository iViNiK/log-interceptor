package it.vinicioflamini.sharedlib.loginterceptor.log;

public class RequestCorrelation {

	public static final String USERNAME_HEADER = "userName";
	public static final String CORRELATION_ID_HEADER = "correlationId";
	public static final String CORRELATION_ID_PREFIX = "CID-";

	private static final ThreadLocal<String> id = new ThreadLocal<>();
	private static final ThreadLocal<String> user = new ThreadLocal<>();

	private RequestCorrelation() {
	}

	public static void setId(String correlationId) {
		id.set(correlationId);
	}

	public static String getId() {
		return id.get();
	}

	public static void setUserName(String userName) {
		user.set(userName);
	}

	public static String getUserName() {
		return user.get();
	}
}