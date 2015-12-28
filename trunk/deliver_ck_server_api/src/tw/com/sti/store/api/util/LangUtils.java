package tw.com.sti.store.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LangUtils {

	public static final int isBlankAny(String... str) {
		for (int i = 0; i < str.length; i++) {
			if (isBlank(str[i])) {
				return i;
			}
		}
		return -1;
	}

	public static final boolean isBlank(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	public static final int parseInt(String string, int def) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
			return def;
		}
	}

	public static final float parseFloat(String string, float def) {
		try {
			return Float.parseFloat(string);
		} catch (Exception e) {
			return def;
		}
	}

	public static final Date parseDate(String value, String pattern) {
		SimpleDateFormat fmt = new SimpleDateFormat(pattern);
		try {
			return fmt.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}

	public static final String formatDate(Date date, String pattern) {
		SimpleDateFormat fmt = new SimpleDateFormat(pattern);
		return fmt.format(date);
	}

	public static final String formatDate(long milliSeconds, String pattern) {
		SimpleDateFormat fmt = new SimpleDateFormat(pattern);
		Date date = new Date(milliSeconds);
		return fmt.format(date);
	}
}