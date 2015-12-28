package cn.com.vapk.vstore.client.sdk;

import cn.com.vapk.vstore.client.util.ConfigurationFactory;
import tw.com.sti.clientsdk.provider.AppSdkProvider;
import tw.com.sti.store.api.Configuration;

public class StoreProvider extends AppSdkProvider {

	@Override
	protected Configuration getConfiguration() {
		return ConfigurationFactory.getInstance();
	}

}
