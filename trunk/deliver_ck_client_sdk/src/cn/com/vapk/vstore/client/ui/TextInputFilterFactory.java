package cn.com.vapk.vstore.client.ui;

import android.text.InputFilter;

public final class TextInputFilterFactory {

	private TextInputFilterFactory() {
	}

	public static final InputFilter[] loginAccount() {
		return new InputFilter[] { new LoginAccountTextInputFilter() };
	}

	public static final InputFilter[] loginPassword() {
		return new InputFilter[] { new LoginPasswordTextInputFilter() };
	}

}
