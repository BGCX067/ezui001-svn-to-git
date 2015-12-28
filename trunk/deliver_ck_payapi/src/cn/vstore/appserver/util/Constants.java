package cn.vstore.appserver.util;

public class Constants {

//	0=沒有付款 1=付款中 2=付款成功 3=付款失敗 4=訂單取消 5=一次性付費的反安裝或者月租取消

	//IPAY_USER_PAYMENT_LOG 內的 status 參數
	public static final int STATUS_PAYMENT_LOG = 0;          //沒有付款
	public static final int STATUS_PAYMENT_TEMP_SUCCESS = 1; //付款中
	public static final int STATUS_PAYMENT_SUCCESS = 2;      //付款成功
	public static final int STATUS_PAYMENT_FAIL = 3;         //付款失敗
	public static final int STATUS_PAYMENT_CANCEL = 4;       //訂單取消
	public static final int STATUS_PAYMENT_FAILURE = 5;      //一次性付費的反安裝或者月租取消
	public static final int STATUS_PAYMENT_TEST_SUCCESS = 6; //測試用假付款成功
	public static final int STATUS_PAYMENT_TEST_CANCEL = 7;  //測試用假訂單取消

	public static final int PRICE_TYPE_FREE=0;					//免費
	public static final int PRICE_TYPE_ONE_TIME=1;				//計次
	public static final int PRICE_TYPE_MONTHLY=2;				//月租
	public static final int PRICE_TYPE_IN_APP_PURCHASE=3;		//InAppPurchase
	public static final int PRICE_TYPE_SERVER_PRODUCT_PURCHASE=4;//INNAPP購物車
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
}
