package tw.com.sti.store.api.util;

import java.util.ArrayList;

import org.json.JSONArray;

public class JSONUtils {

	public static final String[] parseStringArrayNoBlank(JSONArray strJA) {
		if (strJA == null || strJA.length() == 0)
			return new String[0];

		int count = strJA.length();
		ArrayList<String> strList = new ArrayList<String>(count);
		for (int i = 0; i < count; i++) {
			String str = strJA.optString(i);
			if (!LangUtils.isBlank(str))
				strList.add(str);
		}

		return strList.toArray(new String[strList.size()]);
	}

}
