package cn.vstore.appserver.api.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PePayUtil {

	protected static final Logger logger = LoggerFactory.getLogger(PePayUtil.class);

	// PEPAY 提供之廠商代碼
	public final static String SHOP_ID = "PPS_140270";

	// 系統交易信任碼
	public final static String SYS_TRUST_CODE = "KZn3H4YH7t";

	// 廠商交易信任碼
	public final static String SHOP_TRUST_CODE = "48Q7nOiial";

	// 幣別
	public final static String CURRENCY = "TWD";

	// 付款類型 - 預設電信帳單
	public final static String PAY_TYPE_TY_BILL = "TY-BILL";

	// 參數分割符號
	public final static String splitSign = "#";

	// PePay 選擇付款畫面
	public final static String REDIRECT_URL = "http://gate.pepay.com.tw/pepay/payselect_amt.php";

	// PePay 套用 CSS, JS 基礎位置
	public final static String BASE_URL = "http://gate.pepay.com.tw/pepay/";
	
	public final static String PEPAY_TRADE_CODE_SUCCESS = "0";

	/**
	 * 產生 PePay 的 CheckCode <br /> 
	 * CHECK_CODE = md5 (SYS_TRUST_CODE + "#" + SHOP_ID + "#" + ORDER_ID + "#" + AMOUNT + "#" + SHOP_TRUST_CODE) <br />
	 * "#"為一字元
	 * @return
	 */
	public static String getPePayCheckCode(String orderId, String amount) {
		// 驗證參數
		if (StringUtils.isBlank(orderId) || StringUtils.isBlank(amount)) {
			return null;
		} else {
			String str = SYS_TRUST_CODE + splitSign + SHOP_ID + splitSign + orderId + splitSign + amount + splitSign + SHOP_TRUST_CODE;
			logger.debug("Before MD5 : {}", str);
			String checkCode = null;
			try {
				checkCode = encodeMD5WithBytes(str.getBytes("utf-8"));
				logger.debug("After MD5 : {}", checkCode);
			} catch (Exception e) {
				logger.error("Generate PePay CheckCode Error : {}", e);
			}
			return checkCode;
		}
	}
	
	/**
	 * 產生 PePay 的 CheckCode <br /> 
	 * CHECK_CODE = md5 (SYS_TRUST_CODE + ”#” + SHOP_ID + ”#” + ORDER_ID + ”#” + AMOUNT + ”#” + SESS_ID + ”#” + PROD_ID + ”#” + SHOP_TRUST_CODE) <br />
	 * "#"為一字元
	 * @param orderId
	 * @param amount
	 * @param sessionId
	 * @param productId
	 * @return
	 */
	public static String getPePayCheckCode(String orderId, String amount, String sessionId, String productId) {
		// 驗證參數
		if (StringUtils.isBlank(orderId) || StringUtils.isBlank(amount) || 
				StringUtils.isBlank(sessionId) || StringUtils.isBlank(productId)) {
			return null;
		} else {
			String str = SYS_TRUST_CODE + splitSign + SHOP_ID + splitSign + orderId + splitSign + amount + splitSign + sessionId + splitSign + productId + splitSign + SHOP_TRUST_CODE;
			logger.debug("Before MD5 : {}", str);
			String checkCode = null;
			try {
				checkCode = encodeMD5WithBytes(str.getBytes("utf-8"));
				logger.debug("After MD5 : {}", checkCode);
			} catch (Exception e) {
				logger.error("Generate PePay CheckCode Error : {}", e);
			}
			return checkCode;
		}
	}
	
	/**
	 * 產生 PePay 的 CheckCode <br /> 
	 * CHECK_CODE = md5 (SYS_TRUST_CODE + ”#” + SHOP_ID + ”#” + ORDER_ID + ”#” + AMOUNT + ”#” + SESS_ID + ”#” + PROD_ID + ”#” + USER_ID + ”#” + SHOP_TRUST_CODE) <br />
	 * "#"為一字元
	 * @param orderId
	 * @param amount
	 * @param sessionId
	 * @param productId
	 * @return
	 */
	public static String getPePayCheckCode(String orderId, String amount, String sessionId, String productId, String userId) {
		// 驗證參數
		if (StringUtils.isBlank(orderId) || StringUtils.isBlank(amount) || 
				StringUtils.isBlank(sessionId) || StringUtils.isBlank(productId) || StringUtils.isBlank(userId)) {
			return null;
		} else {
			String str = SYS_TRUST_CODE + splitSign + SHOP_ID + splitSign + orderId + splitSign + amount + splitSign + sessionId + splitSign + productId + splitSign + userId + splitSign + SHOP_TRUST_CODE;
			logger.debug("Before MD5 : {}", str);
			String checkCode = null;
			try {
				checkCode = encodeMD5WithBytes(str.getBytes("utf-8"));
				logger.debug("After MD5 : {}", checkCode);
			} catch (Exception e) {
				logger.error("Generate PePay CheckCode Error : {}", e);
			}
			return checkCode;
		}
	}

	private static String encodeMD5WithBytes(byte[] bytes) {
		String md5code = "";
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] digest = md5.digest();

			final StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < digest.length; ++i) {
				final byte b = digest[i];
				final int value = (b & 0x7F) + (b < 0 ? 128 : 0);
				buffer.append(value < 16 ? "0" : "");
				buffer.append(Integer.toHexString(value));
			}
			md5code = buffer.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5code;
	}

	public enum PePayResCode {
		SUCCESS {
			public int getCode() {
				return 0;
			}
			public String getDesc() {
				return "成功";
			}
		},
		TRANSACTION_SUCCESS_RECEIVE_01 {
			public int getCode() {
				return 20099;
			}
			public String getDesc() {
				return "其他錯誤";
			}
		},
		PARAMS_FAIL_RECEIVE_01 {
			public int getCode() {
				return 20001;
			}
			public String getDesc() {
				return "接收參數錯誤";
			}
		},
		CHECK_CODE_MISMATCH_RECEIVE_01 {
			public int getCode() {
				return 20002;
			}
			public String getDesc() {
				return "比對 CHECK_CODE 錯誤";
			}
		},
		DB_CONN_FAIL_RECEIVE_01 {
			public int getCode() {
				return 20003;
			}
			public String getDesc() {
				return "資料庫連接失敗";
			}
		},
		DB_FAIL_RECEIVE_01 {
			public int getCode() {
				return 20004;
			}
			public String getDesc() {
				return "資料庫寫入失敗";
			}
		},
		OTHER_FAIL_RECEIVE_01 {
			public int getCode() {
				return 20099;
			}
			public String getDesc() {
				return "其他錯誤";
			}
		},
		TRANSACTION_SUCCESS_RECEIVE_02 {
			public int getCode() {
				return 20290;
			}
			public String getDesc() {
				return "資料已接收過";
			}
		},
		PARAMS_FAIL_RECEIVE_02 {
			public int getCode() {
				return 20201;
			}
			public String getDesc() {
				return "接收參數錯誤";
			}
		},
		CHECK_CODE_MISMATCH_RECEIVE_02 {
			public int getCode() {
				return 20299;
			}
			public String getDesc() {
				return "其他錯誤";
			}
		},
		DB_CONN_FAIL_RECEIVE_02 {
			public int getCode() {
				return 20202;
			}
			public String getDesc() {
				return "資料庫連接失敗";
			}
		},
		DB_FAIL_RECEIVE_02 {
			public int getCode() {
				return 20203;
			}
			public String getDesc() {
				return "資料庫寫入失敗";
			}
		},
		OTHER_FAIL_RECEIVE_02 {
			public int getCode() {
				return 20299;
			}
			public String getDesc() {
				return "其他錯誤";
			}
		};
		public abstract int getCode();
		public abstract String getDesc();
	};

	public static void main(String args[]) {
		String orderId = "665434567890";
		String amount = "1";
		System.out.println("CheckCode : " + PePayUtil.getPePayCheckCode(orderId, amount));
	}
}
