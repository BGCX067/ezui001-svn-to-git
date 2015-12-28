package cn.vstore.appserver.service;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.form.BaseForm;
import cn.vstore.appserver.model.ClinetVersion;
import cn.vstore.appserver.model.StoreClientInstallLog;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.ResultCode.CommonCode;

/**
 * @version $Id$
 */
@Service("ClientService")
public class ClientService {

//    private static ClinetVersion clinetVersion = new ClinetVersion();
//    private static final long EXPIRED_TIME = 60 * 30 * 1000; //30分鐘
//    private static long cacheTime;

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
    
    /**
     * FeedbackOfClient : 客户端反馈
     */
    public ServiceResult<Integer> feedbackOfClient(String feedbackText,String contactText,String imei, BigDecimal storeId) {
        try {
        	 Map<String, Object> map = new HashMap<String, Object>();
             map.put("feedbackText", feedbackText);
             map.put("contactText", contactText);
             map.put("imei", imei);
             map.put("storeId", storeId);
             sqlMapClientTemplate.insert("ClinetVersion.insertClientFeedback", map);
            return new ServiceResult<Integer>(ResultCode.CommonCode.SUCCESS, 1);
        } catch (Throwable e) {
            return new ServiceResult<Integer>(ResultCode.CommonCode.SUCCESS.bindSource(ResultCode.CommonCode.SERVICE_FAIL), 1, e);
        }
    }

    /**
     * CheckNewClient : 18.檢查Client有沒有新版本
     */
    public ServiceResult<ClinetVersion> checkNewClient(int cverNo,String snum, String store) {
        try {
            // 取得StoreClinet版本。(有catch30分鐘機制)
            ClinetVersion clinetVersion = getStoreClinetVersion(store,snum);
            // 當StoreClinet版本為null，則回傳查無最新版本
            if (clinetVersion == null)
                return new ServiceResult<ClinetVersion>(ResultCode.CommonCode.SUCCESS.bindSource(ResultCode.CheckNewClientCode.NO_NEW_CLIENT_CODE), null);

            int versionNumber = clinetVersion.getVersionId();

            // 比對版本，當手機版本小於server版本則回傳有新版本
            if (cverNo < versionNumber) {
                return new ServiceResult<ClinetVersion>(ResultCode.CommonCode.NEW_CLIENT, clinetVersion);
            }

            return new ServiceResult<ClinetVersion>(ResultCode.CommonCode.SUCCESS, null);
        } catch (Throwable e) {
            return new ServiceResult<ClinetVersion>(ResultCode.CommonCode.SUCCESS.bindSource(ResultCode.CommonCode.SERVICE_FAIL), null, e);
        }
    }

    /**
     * DownloadClient : 19.下載Client
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<ClinetVersion> downloadClient(BaseForm baseForm, BigDecimal storeId, String IP) {
        ClinetVersion clinetVersion = null;
        try{
        	
        	if(baseForm.getSnum()!=null && baseForm.getSnum().length()>0){
        		//尝试取得指定渠道號碼的client
        		clinetVersion = (ClinetVersion) sqlMapClientTemplate.queryForObject("ClinetVersion.getClinetVersionBySNum", baseForm.getSnum());
        	}
        	if (clinetVersion == null){
	            // 取得StoreClinet版本。(即時撈DB版本資訊)
	            clinetVersion = (ClinetVersion) sqlMapClientTemplate.queryForObject("ClinetVersion.getClinetVersion", baseForm.getStore());
        	}
            if (clinetVersion == null){
                return new ServiceResult<ClinetVersion>(ResultCode.DownloadClientCode.NO_CLIENT_APK, clinetVersion);
            }
            
            StoreClientInstallLog storeClientInstallLog = new StoreClientInstallLog(baseForm, clinetVersion.getVersionId(), storeId, IP);
            sqlMapClientTemplate.insert("ClinetVersion.insertStoreClientInstallLog", storeClientInstallLog);
            return new ServiceResult<ClinetVersion>(ResultCode.CommonCode.SUCCESS, clinetVersion);
        } catch (Throwable e) {
            return new ServiceResult<ClinetVersion>(ResultCode.CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * 檢查Client版本
     */
    public ServiceResult<ClinetVersion> checkClientVersion(int cver,String snum, String store) {
        ClinetVersion clinetVersion = getStoreClinetVersion(store,snum);
        return new ServiceResult<ClinetVersion>(CommonCode.NEW_CLIENT, clinetVersion);
    }

    /**
     * 取得StoreClinet最新版本，catch30分鐘機制
     */
    ClinetVersion getStoreClinetVersion(String store,String snum) {
    	System.out.println("---------------------檢查版本");
    	 Map<String, Object> map = new HashMap<String, Object>();
         map.put("store", store);
         map.put("snum", snum);
    	return (ClinetVersion) sqlMapClientTemplate.queryForObject("ClinetVersion.getClinetVersion", map);
//        long currentTime = System.currentTimeMillis();
//        if (currentTime > (cacheTime + EXPIRED_TIME) || clinetVersion == null) {
//            clinetVersion = (ClinetVersion) sqlMapClientTemplate.queryForObject("ClinetVersion.getClinetVersion", store);
//            cacheTime = currentTime;
//        }
//        return clinetVersion;
    }
    /**
     * 取得APP在那些STORE上架
     * */
    public ServiceResult<List<StoreInfo>> getStoreByPKG(String pkg, String ver, String dvc){
    	List<StoreInfo> storeInfo = null;
    	if(pkg==null || pkg.trim().length()==0){
    		return null;
    	}
    	if(ver==null || ver.trim().length()==0){
    		return null;
    	}
    	if(dvc==null || dvc.trim().length()==0){
    		return null;
    	}
    	
    	int version = 0;
    	if(ver != null){
    		version = Integer.parseInt(ver);    		
    	}
    	
    	try {
    		Map<String, String> appInfo = new HashMap<String, String>();
    		appInfo.put("PACKAGE_NAME", pkg);
    		appInfo.put("VERSION_ID", ver);
    		
    		if(version != 0 && version == Integer.MIN_VALUE){
    			try {
					storeInfo = sqlMapClientTemplate.queryForList("Auth.getAllStore");
				} catch (Throwable e) {
					return new ServiceResult<List<StoreInfo>>(ResultCode.CommonCode.SERVICE_FAIL, null, e);
				}
    			return new ServiceResult<List<StoreInfo>>(ResultCode.CommonCode.SUCCESS, storeInfo);
    		}
    		
			storeInfo = sqlMapClientTemplate.queryForList("Auth.getStoreByPkgVer", appInfo);
			if(storeInfo==null || storeInfo.isEmpty()){
				return new ServiceResult<List<StoreInfo>>(ResultCode.CheckAppStoreCode.NO_APP, storeInfo);
			}
			
			return new ServiceResult<List<StoreInfo>>(ResultCode.CommonCode.SUCCESS, storeInfo);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ServiceResult<List<StoreInfo>>(ResultCode.CommonCode.SERVICE_FAIL, null, e);
		}
    }
}