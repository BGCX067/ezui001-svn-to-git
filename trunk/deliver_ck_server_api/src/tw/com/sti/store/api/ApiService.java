package tw.com.sti.store.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import tw.com.sti.security.Dsa;

import tw.com.sti.store.api.vo.LoginRet;

public class ApiService {
	public static enum AppsType {
		FREE, PAID, ALL;
	};
	private Configuration config;
	private ApiUrl apiUrl;
	private static ApiService service;
	private ApiService(Configuration config) {
		this.config=config;
		this.apiUrl=new ApiUrl(config);
	}
	public static synchronized ApiService getInstance(Configuration config) {
		if (service != null)
			return service;
		service = new ApiService(config);
		return service;
	}
	public List<NameValuePair> addRequestParams(List<NameValuePair> nvps,CommonParameter common, boolean withToken, String userId, String pwd) {
		if(common.getSdkVer()!=null) nvps.add(new BasicNameValuePair("aver", common.getSdkVer()));
		if(common.getSdkRel()!=null) nvps.add(new BasicNameValuePair("arel", common.getSdkRel()));
		if(common.getClientVer()!=null) nvps.add(new BasicNameValuePair("cver", common.getClientVer()));
//		nvps.add(new BasicNameValuePair("lang", Locale.getDefault().toString()));
//		TimeZone tz = TimeZone.getDefault();
//		nvps.add(new BasicNameValuePair("tzid", tz.getID()));
//		nvps.add(new BasicNameValuePair("tzrawoffset", "" + tz.getRawOffset()));
		if(common.getDeviceId()!=null) nvps.add(new BasicNameValuePair("imei", common.getDeviceId()));
		if(common.getSimSerialNumber()!=null) nvps.add(new BasicNameValuePair("imsi", common.getSimSerialNumber()));
		if(common.getSimSerialNumber()!=null) nvps.add(new BasicNameValuePair("iccid", common.getSimSerialNumber()));
//		nvps.add(new BasicNameValuePair("dvc", Build.MODEL));
		if(common.getWpx()!=null) nvps.add(new BasicNameValuePair("wpx", common.getWpx()));
		if(common.getHpx()!=null) nvps.add(new BasicNameValuePair("hpx", common.getHpx()));
		nvps.add(new BasicNameValuePair("appfilter", "" + common.getAppFilter()));
		if (withToken&&common.getToken()!=null&&common.getToken().length()>0) {
			nvps.add(new BasicNameValuePair("token", common.getToken()));
		}
		if(userId!=null){
			nvps.add(new BasicNameValuePair("userId", userId));
		}
		if(pwd!=null){
			nvps.add(new BasicNameValuePair("pwd", pwd));
		}

		nvps.add(new BasicNameValuePair("store", common.getStoreId()));
		String time=""+System.currentTimeMillis();
		nvps.add(new BasicNameValuePair("time", time));
		String vstring=common.getStoreId()+"|"+time;
		if(userId!=null&&pwd!=null){
        	vstring=common.getStoreId()+"|"+time+"|"+userId+"|"+pwd;
        }else if(withToken&&common.getToken()!=null&&common.getToken().length()>0){
        	vstring=common.getStoreId()+"|"+time+"|"+common.getToken();
        }else if(common.getDeviceId()!=null&&common.getSubscriberId()!=null&&common.getSimSerialNumber()!=null){
        	vstring=common.getStoreId()+"|"+time+"|"+common.getDeviceId()+"|"+common.getSubscriberId()+"|"+common.getSimSerialNumber();
        }
        String vsign=Dsa.sign(vstring,config.getApiPrivkey());
        nvps.add(new BasicNameValuePair("vsign", vsign));
        return nvps;
	}
	public List<NameValuePair> addRequestParams(List<NameValuePair> nvps,String[] paramNames,String[] paramValues) {
		if (paramNames == null || paramValues == null) {
			return nvps;
		}

		if (paramNames.length != paramValues.length) {
			throw new IndexOutOfBoundsException(
					"paramNames.length != paramValues.length");
		}

		int count = paramNames.length;
		for (int i = 0; i < count; i++) {
			nvps.add(new BasicNameValuePair(paramNames[i], paramValues[i]));
		}

		return nvps;
	}
	public ApiInvoker<LoginRet> login(CommonParameter common,String userId, String password) {
		String url = apiUrl.getLoginUrl();
		ApiDataParseHandler<LoginRet> handler = ApiDataParseHandler.LOGIN_RET_PARSE_HANDLER;
		List<NameValuePair> nvps = addRequestParams(new ArrayList<NameValuePair>(),common,false,userId,password);
		return new ApiInvoker<LoginRet>(this.config,handler, url, nvps);
	}
}
