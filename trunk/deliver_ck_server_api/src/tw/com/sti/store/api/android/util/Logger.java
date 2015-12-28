package tw.com.sti.store.api.android.util;

import android.util.Log;

public final class Logger {

	private static final String DEFAULT_TAG = "STORE_API";
	private static boolean logable = true;
	public static final boolean DEBUG = true & logable;
	public static final boolean INFO = true & logable;
	public static final boolean WARN = true & logable;
	public static final boolean ERROR = true & logable;

	public static boolean isLogable() {
		return logable;
	}

	public static void setLogable(boolean logable) {
		Logger.logable = logable;
	}

	private String clazz;
	private String tag;

	private Logger() {
	}

	public static final Logger getLogger(Class<?> clazz) {
		return getLogger(clazz, DEFAULT_TAG);
	}

	private static final Logger getLogger(Class<?> clazz, String tag) {
		Logger log = new Logger();
		log.clazz = clazz.getSimpleName();
		log.tag = tag;
		return log;
	}

	private static final String headString(String clazz) {
		return clazz + " [" + Thread.currentThread().getId() + "]# \t";
	}

	public static final void logd(Class<?> clazz, String msg) {
		if (DEBUG)
			Log.d(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg);
	}

	public static final void loge(Class<?> clazz, String msg, Throwable e) {
		if (ERROR)
			Log.e(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg, e);
	}

	public final void i(String msg) {
		if (INFO)
			Log.i(tag, headString(clazz) + msg);
	}

	public final void i(String msg, Throwable e) {
		if (INFO)
			Log.i(tag, headString(clazz) + msg, e);
	}

	public final void d(String msg) {
		if (DEBUG)
			Log.d(tag, headString(clazz) + msg);
	}

	public final void d(String msg, Throwable e) {
		if (DEBUG)
			Log.d(tag, headString(clazz) + msg, e);
	}

	public final void w(String msg) {
		if (WARN)
			Log.w(tag, headString(clazz) + msg);
	}

	public final void w(String msg, Throwable e) {
		if (WARN)
			Log.w(tag, headString(clazz) + msg, e);
	}

	public final void e(String msg) {
		if (ERROR)
			Log.e(tag, headString(clazz) + msg);
	}

	public final void e(String msg, Throwable e) {
		if (ERROR)
			Log.e(tag, headString(clazz) + msg, e);
	}

	// static
	public static final void i(String msg, Class<?> clazz) {
		if (INFO)
			Log.i(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg);
	}

	public static final void i(String msg, Throwable e, Class<?> clazz) {
		if (INFO)
			Log.i(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg, e);
	}

	public static final void d(String msg, Class<?> clazz) {
		if (DEBUG)
			Log.d(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg);
	}

	public static final void d(String msg, Throwable e, Class<?> clazz) {
		if (DEBUG)
			Log.d(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg, e);
	}

	public static final void w(String msg, Class<?> clazz) {
		if (WARN)
			Log.w(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg);
	}

	public static final void w(String msg, Throwable e, Class<?> clazz) {
		if (WARN)
			Log.w(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg, e);
	}

	public static final void e(String msg, Class<?> clazz) {
		if (ERROR)
			Log.e(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg);
	}

	public static final void e(String msg, Throwable e, Class<?> clazz) {
		if (ERROR)
			Log.e(DEFAULT_TAG, headString(clazz.getSimpleName()) + msg, e);
	}
}
