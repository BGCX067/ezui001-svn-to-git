package cn.com.vapk.vstore.client.util;

import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.util.Logger;

public class ConfigurationFactory {
	private static Configuration config;
	private static String apiPrivkey = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUJ2I8LbNBCrUgom4gZPZ9JgPy0QA=";
	private static String apiRootPath = "/ckapi";
	private static String apiPayRootPath = "/payapi";
//	private static String apiPayHostname = "172.20.10.4";
	private static String apiPayHostname = "210.202.111.104";
	private static String apiHostname = "210.202.111.104";
	private static int apiHttpPort;
	private static int apiPayHttpPort = 8080;

	public static Configuration getInstance(){
		if(config != null) {
			return config;
		}
		config = new Configuration();
		init();
		return config;
	}
	
	private static void init(){
		Logger.setLogable(true);
		config.setSupportSSL(false);
		config.setApiPrivkey(apiPrivkey);
		
		if(!"".equals(apiPrivkey) && apiPrivkey != null) {
			config.setApiPrivkey(apiPrivkey);
		} else {
			config.setApiPrivkey("MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUJ2I8LbNBCrUgom4gZPZ9JgPy0QA=");
		}
		
		if(!"".equals(apiHostname) && apiHostname != null) {
			config.setApiHostname(apiHostname);
		} else {
			config.setApiHostname("ckapi.51anzhuo.com.cn");
		}
		
		if(apiHttpPort > 0) {
			config.setApiHttpPort(apiHttpPort);
		} else {
			config.setApiHttpPort(80);
		}
		
		if(apiRootPath != null) {
			config.setApiRootPath(apiRootPath);
		} else {
			config.setApiRootPath("");
		}
		
		if(apiPayRootPath != null) {
			config.setApiPayRootPath(apiPayRootPath);
		} else {
			config.setApiPayRootPath(config.getApiRootPath());
		}
		
		if(apiPayHostname != null) {
			config.setApiPayHostname(apiPayHostname);
		} else {
			config.setApiPayHostname(config.getApiHostname());
		}
		
		if(apiPayHttpPort > 0) {
			config.setApiPayHttpPort(apiPayHttpPort);
		} else {
			config.setApiPayHttpPort(config.getApiHttpPort());
		}
		
		//设定渠道号码
		config.setSnum("03191715");
	}
}
