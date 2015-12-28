package cn.com.vapk.vstore.client.ui;

import android.text.InputFilter;
import android.text.Spanned;

class LoginAccountTextInputFilter implements InputFilter {

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		if (source.toString()
				.matches("[\\[\\].@a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]*")) {
			return source;
		}

		int count = source.length();
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			String s = String.valueOf(source.charAt(i));
			if (s.matches("[\\[\\].@a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]"))
				sb.append(s);
		}
		return sb.toString();
	}

}
