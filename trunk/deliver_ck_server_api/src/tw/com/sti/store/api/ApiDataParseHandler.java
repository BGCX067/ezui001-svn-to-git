package tw.com.sti.store.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.json.JSONObject;

import tw.com.sti.store.api.vo.AppVersionsRet;
import tw.com.sti.store.api.vo.ApplicationRet;
import tw.com.sti.store.api.vo.AppsRet;
import tw.com.sti.store.api.vo.CPAppsRet;
import tw.com.sti.store.api.vo.CategoriesRet;
import tw.com.sti.store.api.vo.CategoryAppsRet;
import tw.com.sti.store.api.vo.CheckAppStoreRet;
import tw.com.sti.store.api.vo.CheckClientVersionRet;
import tw.com.sti.store.api.vo.CheckPayStatusRet;
import tw.com.sti.store.api.vo.CommentsRet;
import tw.com.sti.store.api.vo.CommonRet;
import tw.com.sti.store.api.vo.IPLoginRet;
import tw.com.sti.store.api.vo.LicenseInfoRet;
import tw.com.sti.store.api.vo.LoginRet;
import tw.com.sti.store.api.vo.OrderRet;
import tw.com.sti.store.api.vo.OverPaymentQuotaRet;
import tw.com.sti.store.api.vo.PePayOrderRet;
import tw.com.sti.store.api.vo.SdkAppInfoRet;
import tw.com.sti.store.api.vo.UnsubscribeRet;

public interface ApiDataParseHandler<T> {

	public T parseRet(String data) throws ApiDataParseException;

	abstract class JSONApiDataParseHandler<T> implements ApiDataParseHandler<T> {
		@Override
		public T parseRet(String data) throws ApiDataParseException {
			try {
				JSONObject json = new JSONObject(data);
				return parseRet(json);
			} catch (Exception e) {
				throw new ApiDataParseException(e);
			}
		}

		abstract T parseRet(JSONObject json);
	}

	public static final ApiDataParseHandler<LoginRet> LOGIN_RET_PARSE_HANDLER = new JSONApiDataParseHandler<LoginRet>() {
		public LoginRet parseRet(JSONObject json) {
			return new LoginRet(json);
		}
	};

	public static final ApiDataParseHandler<IPLoginRet> IP_LOGIN_RET_PARSE_HANDLER = new JSONApiDataParseHandler<IPLoginRet>() {
		public IPLoginRet parseRet(JSONObject json) {
			return new IPLoginRet(json);
		}
	};

	public static final ApiDataParseHandler<AppsRet> APPS_RET_PARSE_HANDLER = new JSONApiDataParseHandler<AppsRet>() {
		public AppsRet parseRet(JSONObject json) {
			return new AppsRet(json);
		}
	};

    public static final ApiDataParseHandler<CategoryAppsRet> CATEGORY_APPS_RET_PARSE_HANDLER = new JSONApiDataParseHandler<CategoryAppsRet>() {
        public CategoryAppsRet parseRet(JSONObject json) {
            return new CategoryAppsRet(json);
        }
    };

	public static final ApiDataParseHandler<CPAppsRet> CP_APPS_RET_PARSE_HANDLER = new JSONApiDataParseHandler<CPAppsRet>() {
		public CPAppsRet parseRet(JSONObject json) {
			return new CPAppsRet(json);
		}
	};

	public static final ApiDataParseHandler<CategoriesRet> CATEGORIES_PARSE_HANDLER = new JSONApiDataParseHandler<CategoriesRet>() {
		public CategoriesRet parseRet(JSONObject json) {
			return new CategoriesRet(json);
		}
	};

	public static final ApiDataParseHandler<ApplicationRet> APPLICATION_PARSE_HANDLER = new JSONApiDataParseHandler<ApplicationRet>() {
		public ApplicationRet parseRet(JSONObject json) {
			return new ApplicationRet(json);
		}
	};

	public static final ApiDataParseHandler<CommentsRet> COMMENTS_RET_PARSE_HANDLER = new JSONApiDataParseHandler<CommentsRet>() {
		public CommentsRet parseRet(JSONObject json) {
			return new CommentsRet(json);
		}
	};

	public static final ApiDataParseHandler<CommonRet> COMMON_RET_PARSE_HANDLER = new JSONApiDataParseHandler<CommonRet>() {
		public CommonRet parseRet(JSONObject json) {
			return new CommonRet(json);
		}
	};

	public static final ApiDataParseHandler<CheckClientVersionRet> CHECK_CLIENT_VERSION_HANDLER = new JSONApiDataParseHandler<CheckClientVersionRet>() {
		public CheckClientVersionRet parseRet(JSONObject json) {
			return new CheckClientVersionRet(json);
		}
	};

	public static final ApiDataParseHandler<OverPaymentQuotaRet> OVER_PAYMENT_QUOTA_RET_PARSE_HANDLER = new JSONApiDataParseHandler<OverPaymentQuotaRet>() {
		@Override
		public OverPaymentQuotaRet parseRet(JSONObject json) {
			return new OverPaymentQuotaRet(json);
		}
	};

	public static final ApiDataParseHandler<CheckPayStatusRet> CHECK_PAY_STATUS_RET_PARSE_HANDLER = new JSONApiDataParseHandler<CheckPayStatusRet>() {
		@Override
		public CheckPayStatusRet parseRet(JSONObject json) {
			return new CheckPayStatusRet(json);
		}
	};

	public static final ApiDataParseHandler<UnsubscribeRet> UNSUBSCRIBE_RET_PARSE_HANDLER = new JSONApiDataParseHandler<UnsubscribeRet>() {
		@Override
		public UnsubscribeRet parseRet(JSONObject json) {
			return new UnsubscribeRet(json);
		}
	};

	public static final ApiDataParseHandler<AppVersionsRet> MY_APP_VERSIONS_PARSE_HANDLER = new JSONApiDataParseHandler<AppVersionsRet>() {
		@Override
		public AppVersionsRet parseRet(JSONObject json) {
			return new AppVersionsRet(json);
		}
	};
	
	public static final ApiDataParseHandler<OrderRet> G_PAYMENT_RET_PARSE_HANDLER = new JSONApiDataParseHandler<OrderRet>() {
		public OrderRet parseRet(JSONObject json) {
			return new OrderRet(json);
		}
	};
	public static final ApiDataParseHandler<CheckAppStoreRet> APP_STORE_PARSE_HANDLER = new JSONApiDataParseHandler<CheckAppStoreRet>() {
		public CheckAppStoreRet parseRet(JSONObject json){
			return new CheckAppStoreRet(json);
		}
	};

	abstract class SAXApiDataParseHandler<T> implements ApiDataParseHandler<T> {
		@Override
		public T parseRet(String data) throws ApiDataParseException {
			try {
				return parseRet(new ByteArrayInputStream(data.getBytes("UTF-8")));
			} catch (Exception e) {
				throw new ApiDataParseException(e);
			}
		}

		abstract T parseRet(InputStream inputStream);
	}

	public static final ApiDataParseHandler<LicenseInfoRet> LICENSE_RET_PARSE_HANDLER = new JSONApiDataParseHandler<LicenseInfoRet>() {
		public LicenseInfoRet parseRet(JSONObject json){
			return new LicenseInfoRet(json);
		}
	};
	
	public static final ApiDataParseHandler<SdkAppInfoRet> SDK_APP_INFO_PARSE_HANDLER = new JSONApiDataParseHandler<SdkAppInfoRet>() {
		public SdkAppInfoRet parseRet(JSONObject json){
			return new SdkAppInfoRet(json);
		}
	};
	
	/**
	 * 處理 PePay 回應的 JSON 格式內容，並存放於 PePayOrderRet 物件中
	 */
	public static final ApiDataParseHandler<PePayOrderRet> PEPAYORDER_RET_PARSE_HANDLER = new JSONApiDataParseHandler<PePayOrderRet>() {
		public PePayOrderRet parseRet(JSONObject json){
			return new PePayOrderRet(json);
		}
	};
}
