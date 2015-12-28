package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.ApkInfo;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.DownloadAppCode;
/**
 * 37.	Web Download App(39) PC Web通用
 * @author user
 *
 */
@Service("WebDownloadAppService")
public class WebDownloadAppService {
	
	private static final Logger logger = LoggerFactory.getLogger(WebDownloadAppService.class);
	
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Transactional(propagation = Propagation.NEVER)
	public ServiceResult<DownloadAppInfo> downloadAppInfo(String pkg, String ver, BigDecimal storeId, String auditStatus){
		try {
			logger.info("WebDownloadAppService parameter : pkg = " + pkg + ", ver = " + ver + ", auditStatus = " + auditStatus);
			
			Map<String, Object> map = new HashMap<String, Object>();
			Integer appVersion = null;
			
//			如果参数有auditStatus = 1 ， 则表示后台待审核app呼叫该api
			if(auditStatus != null && !auditStatus.trim().equals("") && auditStatus.trim().equals("1") ){
				if(ver!=null && !ver.trim().equals("")){
					logger.info("auditStatus = 1, ver is not null , ver is " + ver);
					try {
						appVersion = Integer.parseInt(ver);
					} catch (Exception e) {
						return new ServiceResult<DownloadAppInfo>(DownloadAppCode.NO_APP);
					}
				}
			}else{
//				如果有指定ver，就直接使用指定的ver；没有指定ver，则找出最后修改版本
				if(ver!=null && !ver.trim().equals("")){
					logger.info("ver is not null , ver is " + ver);
					
					try {
						appVersion = Integer.parseInt(ver);
					} catch (Exception e) {
						
						if(appVersion == null || appVersion <= 0){
							map.put("appId", pkg);
							map.put("storeId", storeId);
							appVersion = (Integer) sqlMapClientTemplate.queryForObject("Application.getAppVersion", map);
						}
						
					}
					
					logger.info("ver is not null , Integer.parseInt(ver) = " + appVersion);
					
				}else{
					// 取出 AppVersion，檢查是否有Apk, 條件為APP_ID
					map.put("appId", pkg);
					map.put("storeId", storeId);
					appVersion = (Integer) sqlMapClientTemplate.queryForObject("Application.getAppVersion", map);				
				}
			}
			logger.info("appVersion = " + appVersion);
			
    // 取得下載APK資訊, 取出ApplictionDetail的下載用的檔案資訊，條件為APP_ID/VERSION_ID
			map = new HashMap<String, Object>();
			map.put("appId", pkg);
			map.put("versionId", appVersion);
			map.put("storeId", storeId);
			
			List<ApkInfo> apkInfo = null;
//			如果auditStatus = 1，从application_temp捞出数据 ；否则从application_versions捞出数据
			if(auditStatus != null && !auditStatus.trim().equals("") && auditStatus.trim().equals("1") ){
				
				apkInfo = sqlMapClientTemplate.queryForList("Application.getApkInfo2ForWebDownload", map);
				
			}else{
				
//			如果 application_temp 沒有該 APK，抓最新版本
				apkInfo = sqlMapClientTemplate.queryForList("Application.getApkInfo2ForWebDownload", map);
				
			}
			
			logger.info("aplinfo is null = " + (apkInfo==null));
			if (apkInfo == null){
				logger.info("apkInfo == null");
				return new ServiceResult<DownloadAppInfo>(DownloadAppCode.NO_APP);
			}
			
			if(apkInfo != null && apkInfo.isEmpty()){
				logger.info("apkInfo : filePath = " + apkInfo.iterator().next().getFilepath());				
			}
    // 回傳頁面資料
			DownloadAppInfo downloadAppInfo = new DownloadAppInfo();
			
				logger.info("apkInfo isEmpty ? " + apkInfo.isEmpty());
			
			if(apkInfo != null && !apkInfo.isEmpty()){
				
				downloadAppInfo.apkInfo = apkInfo.iterator().next();	
				
					logger.info("downloadAppInfo apkInfo = " +  downloadAppInfo.apkInfo);
			}
				logger.info("apkinfo.iterator.next = " + apkInfo.iterator().next());
			
			downloadAppInfo.appId = pkg;
			
				logger.info("downloadAppInfo appId = " + downloadAppInfo.appId);

			downloadAppInfo.appVersion = appVersion;
			
				logger.info("downloadAppInfo appVersion = " + downloadAppInfo.appVersion);
				logger.info("downloadAppInfo.apkInfo.getFilepath : " + downloadAppInfo.apkInfo.getFilepath());
			
			return new ServiceResult<DownloadAppInfo>(CommonCode.SUCCESS, downloadAppInfo);
		} catch (Exception e) {		
			return new ServiceResult<DownloadAppInfo>(CommonCode.SERVICE_FAIL, null, e);
		}
	}
}
