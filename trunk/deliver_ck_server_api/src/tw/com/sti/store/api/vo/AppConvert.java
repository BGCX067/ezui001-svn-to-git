package tw.com.sti.store.api.vo;

import tw.com.sti.store.api.vo.ApplicationRet.Application;
import tw.com.sti.store.api.vo.AppsRet.App;

public class AppConvert {
	public static App getAppFromApplication(Application application) {
		return AppsRet.App.getAppfromApplication(application);
	}
}
