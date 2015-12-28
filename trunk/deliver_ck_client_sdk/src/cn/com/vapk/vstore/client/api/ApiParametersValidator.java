package cn.com.vapk.vstore.client.api;

import tw.com.sti.store.api.util.LangUtils;

public final class ApiParametersValidator {

	public static final class Login {
		public static final boolean account(String account) {
			if (LangUtils.isBlank(account)) {
				return false;
			}
			if (!account.matches("^([\\w]+)(([-\\.][\\w]+)?)*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"))
			{
				return false;
			}

			return true;
		}

		public final static boolean password(String password) {
			return !LangUtils.isBlank(password);
		}
	}

}
