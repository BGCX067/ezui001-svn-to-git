package tw.com.sti.store.api.vo;

public interface AppInfo {

	public static enum PayStatus {
		NO_PAID, PAY_PROCESSING, PAID, UN_SUBSCRIBE;

		static PayStatus parse(int payStatus) {
			switch (payStatus) {
			case 0:
				return NO_PAID;
			case 1: // 付款處理中
				return PAY_PROCESSING;
			case 2:
				return PAID;
			case 3: // 付款失敗
				return NO_PAID;
			case 4: // 付款成功的訂單被取消
				return NO_PAID;
			case 5: // 付款成功的訂單被取消
				return UN_SUBSCRIBE;
			default:
				return null;
			}
		}
	}

	public static enum PriceType {
		FREE, ONE_TIME, MONTHLY, IN_APP_PURCHASE, SERVER_PRODUCT_PURCHASE;

		static PriceType parse(int priceType) {
			switch (priceType) {
			case 0:
				return FREE;
			case 1:
				return ONE_TIME;
			case 2:
				return MONTHLY;
			case 3:
				return IN_APP_PURCHASE;
			case 4:
				return SERVER_PRODUCT_PURCHASE;
			default:
				return null;
			}
		}
	}

	public final static class Status {
		public static final int TYPE_FREE = 0x010;
		public static final int TYPE_ONE_TIME = 0x020;
		public static final int TYPE_MONTHLY = 0x040;
		public static final int TYPE_IN_APP_PURCHASE = 0x080;
		public static final int TYPE_SERVER_PRODUCT_PURCHASE = 0x100;

		public static final int INSTALLED = 0x2;
		public static final int NEW_VERSION = 0x4;

		public static final int PAID = 0x1000;
		public static final int SUBSCRIBE = 0x2000;
		public static final int UN_SUBSCRIBE_NOT_EXP = 0x4000;

		public static int getStatus(PriceType priceType, boolean installed,
				boolean newVersion, boolean paid, boolean unSubscribeNotExp) {
			int result = 0;
			switch (priceType) {
			case FREE:
				result |= TYPE_FREE;
				break;
			case ONE_TIME:
				result |= TYPE_ONE_TIME;
				if (paid)
					result |= PAID;
				break;
			case MONTHLY:
				result |= TYPE_MONTHLY;
				if (paid)
					result |= SUBSCRIBE;
				else if (unSubscribeNotExp)
					result |= UN_SUBSCRIBE_NOT_EXP;
				break;
			case IN_APP_PURCHASE:
				result |= TYPE_IN_APP_PURCHASE;
				if (paid)
					result |= PAID;
				break;
			case SERVER_PRODUCT_PURCHASE:
				result |= TYPE_SERVER_PRODUCT_PURCHASE;
				break;
			default:
				return result;
			}

			if (installed)
				result |= INSTALLED;

			if (newVersion)
				result |= NEW_VERSION;

			return result;
		}
	}
	public static enum AppPaidType {
		FREE_OR_PAID, PAY_PROCESSING, NEED_PAY;
		public final static AppPaidType getPaymentRequired(PriceType myPriceType,PayStatus payStatus,PriceType appPriceType) {
			AppPaidType isRequired = FREE_OR_PAID; // 0=no need 1=need -1=order unknown
	
			if (appPriceType == PriceType.FREE) {
				isRequired = FREE_OR_PAID;
			} else if (myPriceType != PriceType.ONE_TIME && myPriceType != PriceType.MONTHLY) {
				// not paid or not installed
				if (appPriceType == PriceType.ONE_TIME || appPriceType == PriceType.MONTHLY) {
					isRequired = NEED_PAY;
				} else {
					isRequired = FREE_OR_PAID;
				}
			} else if (myPriceType == PriceType.ONE_TIME) {
				if (appPriceType == PriceType.MONTHLY) {
					isRequired = NEED_PAY;
				} else if (appPriceType == PriceType.ONE_TIME) {
					switch (payStatus) {
					case NO_PAID:
						isRequired = NEED_PAY;
						break;
					case PAY_PROCESSING:
						isRequired = PAY_PROCESSING;
						break;
					case PAID:
						isRequired = FREE_OR_PAID;
						break;
					case UN_SUBSCRIBE:
						isRequired = FREE_OR_PAID;
						break;
					default:
						isRequired = NEED_PAY;
						break;
					}
				} else {
					isRequired = FREE_OR_PAID;
				}
			} else if (myPriceType == PriceType.MONTHLY) {
				if (appPriceType == PriceType.MONTHLY) {
					switch (payStatus) {
					case NO_PAID:
						isRequired = NEED_PAY;
						break;
					case PAY_PROCESSING:
						isRequired = PAY_PROCESSING;
						break;
					case PAID:
						isRequired = FREE_OR_PAID;
						break;
					case UN_SUBSCRIBE:
						isRequired = FREE_OR_PAID;
						break;
					default:
						isRequired = NEED_PAY;
						break;
					}
				} else {
					isRequired = FREE_OR_PAID;
				}
			}
			return isRequired;
	
		}
	}
}
