package tw.com.sti.store.api.vo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckAppStoreRet extends BaseRet{

	private Store[] stores;
	
	public CheckAppStoreRet(JSONObject json) {
		super(json);
		if(!isSuccess()){
			return;
		}
		stores = Store.parseStores(json.optJSONArray("storeList"));
	}

	public static final class Store{
//		private String pkg;
		private String id;
		private String name;
		private String downloadUrl;
		
		final static Store[] parseStores(JSONArray storesData){
			if(storesData==null || storesData.length()==0){
				return new Store[0];
			}
			Store[] stores;
			int count = storesData.length();
			ArrayList<Store> storeList = new ArrayList<CheckAppStoreRet.Store>();
			try {
				for(int i = 0 ; i < count ; i++){
					JSONObject storeData = storesData.getJSONObject(i);
					Store store = Store.parseStore(storeData);
					if(store != null){
						storeList.add(store);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stores = storeList.toArray(new Store[storeList.size()]);
			return stores;
		}
		
		final static Store parseStore(JSONObject storeData){
			if(storeData == null){
				return null;				
			}
			Store store=null;
			try {
				store = new Store();
				store.id = storeData.getString("id");
				store.name = storeData.getString("name");
				store.downloadUrl = storeData.getString("url");
//				store.pkg = storeData.getString("");
				return store;
			} catch (JSONException e) {
			}
			return null;
		}

//		public String getPkg() {
//			return pkg;
//		}
//
//		public void setPkg(String pkg) {
//			this.pkg = pkg;
//		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDownloadUrl() {
			return downloadUrl;
		}

		public void setDownloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
		}
	}

	public Store[] getStores() {
		return stores;
	}

	public void setStores(Store[] stores) {
		this.stores = stores;
	}
}
