package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cn.vstore.appserver.model.Amount;
import cn.vstore.appserver.model.ApkInfo;
import cn.vstore.appserver.model.Application;
import cn.vstore.appserver.model.ApplicationCategory;
import cn.vstore.appserver.model.ApplictionDetail;
import cn.vstore.appserver.model.Comment;
import cn.vstore.appserver.model.Credential;
import cn.vstore.appserver.model.FeatureApplication;
import cn.vstore.appserver.model.MyDownloadApplication;
import cn.vstore.appserver.model.MyDownloadComparator;
import cn.vstore.appserver.model.PaymentInformation;
import cn.vstore.appserver.model.PosterComment;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.model.RateApp;
import cn.vstore.appserver.model.ReportApp;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.model.Top3OfCategory;
import cn.vstore.appserver.model.UpAppInfo;
import cn.vstore.appserver.model.UserInstallApp;
import cn.vstore.appserver.model.UserSearchLog;
import cn.vstore.appserver.model.VMemDownloadInfo;
import cn.vstore.appserver.model.VMemberInfo;
import cn.vstore.appserver.service.ResultCode.AppDetailCode;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.CpAppsCode;
import cn.vstore.appserver.service.ResultCode.DownloadAppCode;
import cn.vstore.appserver.service.ResultCode.RateAppCode;
import cn.vstore.appserver.service.ResultCode.RegisterCode;
import cn.vstore.appserver.service.ResultCode.ReportAppCode;
import cn.vstore.appserver.service.ResultCode.Web_RegisterCode;
import cn.vstore.appserver.util.MemberCacheUtil;
import cn.vstore.core.model.vo.Account;
import cn.vstore.core.model.vo.AccountExample;
import cn.vstore.core.model.vo.AccountWithBLOBs;

import com.meetup.memcached.MemcachedClient;
import com.sti.util.StringUtil;

/**
 * @version $Id$
 */
@Service("ApplicationService")
public class ApplicationService {

    public static class AppType {
        public static final AppType FREE_APPS = new AppType("freeapps");
        public static final AppType PAID_APPS = new AppType("paidapps");
        public static final AppType LEGAL_APPS = new AppType("legalapps");//20121016 增加正版对外接口，比如海信，排除盗版和ASC
        public static final AppType ALL_APPS = new AppType("allapps");
        public static final AppType HOT_APPS = new AppType("hotapps");
        public static final AppType APP_APPS = new AppType("appranking");
        public static final AppType GAME_APPS = new AppType("gameranking");
        

        final String value;

        private AppType(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (!(obj instanceof AppType))
                return false;

            AppType o = (AppType) obj;
            return this.value.equals(o.value);
        }
    }

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private BlankListService blankListService;

    
    
    /**
     * UpInfoList:获得所有需要更新的app
     */
    public List<UpAppInfo> getUpAppInfoList(ApplicationService self,BigDecimal storeId,String pkg){
    	
    	 //声明可更新应用应用集合
        @SuppressWarnings("unchecked")
        List<UpAppInfo> upAppInfo;
        
        //声明返回可更新应用应用集合
        @SuppressWarnings("unchecked")
        List<UpAppInfo> upAppInfos = new ArrayList<UpAppInfo>();
        
        //取得hostpath
        String hostPath = constantService.getHostPath();
        
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("pkg", pkg);
    	map.put("storeId", storeId);
    	upAppInfo= sqlMapClientTemplate.queryForList("Application.getUpAppInfo",map);
    	if(upAppInfo!=null&&!"".equals(upAppInfo)&&upAppInfo.size()>0){
	    	for(int j = 0 ;j<upAppInfo.size();j++){
	    		 // icon url前加上hostPath
	            if (StringUtils.isNotBlank(upAppInfo.get(j).getIconPath()) && upAppInfo.get(j).getIconPath().charAt(0) == '/')
	            	upAppInfo.get(j).setIconPath(hostPath + upAppInfo.get(j).getIconPath());
	            //去掉所有空格
	            if(!"".equals(upAppInfo.get(j))&&upAppInfo.get(j).getVersionName()!=null&&upAppInfo.get(j).getTitle()!=null){
	            	
	            	upAppInfo.get(j).setVersionName(upAppInfo.get(j).getVersionName().replace(" ",""));
	            	upAppInfo.get(j).setTitle(upAppInfo.get(j).getTitle().replace(" ",""));
	            	
	            	
	            	
	            }
	            upAppInfos.add(upAppInfo.get(j));
	    	}
    	}
    	return upAppInfos;
    	
    	 
    }

    /**
     * FeatureList : 04. 取得featured applications service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<FeatureApplication>> featureApps(ApplicationService self,
    		String imei,String token, boolean appfilter, int aver, int wpx, int hpx, BigDecimal storeId) {
    	//实例memcatched 
        new MemberCacheUtil(); 
        //实例memcache
        MemcachedClient mcc = new MemcachedClient();
        //---------------------清除缓存------------------------------
        // mcc.flushAll();
        //---------------------清除缓存------------------------------
        
        try {
            // 當appfilter == 1表示要做Filter
            String deviceSDK = null;
            String deviceDPI = null;
            if (appfilter) {
                // 依照aver取得支援的版本
                deviceSDK = constantService.getAndroidSdkVersionByLevel(aver);
                if (StringUtils.isBlank(deviceSDK)) {
                    return new ServiceResult<List<FeatureApplication>>(CommonCode.SUCCESS, new ArrayList<FeatureApplication>());
                }
                // 組解析度，(寬*高)
                deviceDPI = getSupportDPI(wpx, hpx);
            }

            // 找出1-30的排名
            Map<Integer, FeatureApplication> mappingApps = new HashMap<Integer, FeatureApplication>();
            final int FEATURE_MAX_NUMBER = constantService.getFeatureAppsShowCount();
            // 取出 Fsort 1-30 的排名
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("plots", new Integer(FEATURE_MAX_NUMBER));
            map.put("storeID", storeId);
           
            //声明首页应用集合
            @SuppressWarnings("unchecked")
            List<FeatureApplication> fsortApps;
           
            
            if(mcc.get(storeId+"plotsstoreIDlimit30")==null||"".equals(mcc.get(storeId+"plotsstoreIDlimit30"))){
                fsortApps = sqlMapClientTemplate.queryForList("FeatureApps.getFeatureAppsByFsort", map);
            	mcc.set(storeId+"plotsstoreIDlimit30", fsortApps,new Date(8*60*60*1000));
            }else{
            	fsortApps=(List<FeatureApplication>)mcc.get(storeId+"plotsstoreIDlimit30");
        	}
           
            for (FeatureApplication fsortApp : fsortApps) {
                if ((fsortApp.getFSort() != null)
                        && (fsortApp.getFSort().intValue() <= FEATURE_MAX_NUMBER)) {
                    mappingApps.put(fsortApp.getFSort(), fsortApp);
                }
            }

            // 排名不滿 30，則找出下載次數最大前 30 筆
            if (mappingApps.size() < FEATURE_MAX_NUMBER) {
                map = new HashMap<String, Object>();
                map.put("plots", new Integer(FEATURE_MAX_NUMBER));
                map.put("storeID", storeId);
                @SuppressWarnings("unchecked")
                List<FeatureApplication> latestApps;
                if(mcc.get(storeId+"plotsstoreIDmax30")==null||"".equals(mcc.get(storeId+"plotsstoreIDmax30"))){
                	latestApps = sqlMapClientTemplate.queryForList("FeatureApps.getFeatureAppsByLatest", map);
                	mcc.set(storeId+"plotsstoreIDmax30",latestApps,new Date(8*60*60*1000));
                }else{
                	latestApps=(List<FeatureApplication>)mcc.get(storeId+"plotsstoreIDmax30");
            	}
                int i = 1;
                for (FeatureApplication latestApp : latestApps) {
                    for (; i <= FEATURE_MAX_NUMBER; i++) {
                        if (!mappingApps.containsKey(i)) {
                            mappingApps.put(i, latestApp);
                            break;
                        }
                    }
                    if (mappingApps.size() >= FEATURE_MAX_NUMBER)
                        break;
                }
            }

            // 取得hostpath
            String hostPath = constantService.getHostPath();
            // 取得付款資訊並存入List
            List<FeatureApplication> appList = new ArrayList<FeatureApplication>();
            List<String> appPkgNames = new ArrayList<String>();
            for (int j = 1; j <= FEATURE_MAX_NUMBER; j++) {
            
            	//获得首页应用对象
                FeatureApplication featureApp = mappingApps.get(j);
                if (featureApp == null)
                    continue;
                if (appfilter) {
                    if (StringUtils.isBlank(featureApp.getSupportSDK())
                            || StringUtils.isBlank(featureApp.getSupportScreen())
                            || deviceSDK.compareTo(featureApp.getSupportSDK()) < 0
                            || featureApp.getSupportScreen().indexOf(deviceDPI) < 0)
                        continue;
                }
                appList.add(featureApp);
                appPkgNames.add(featureApp.getPkg());
                // icon url前加上hostPath
                if (StringUtils.isNotBlank(featureApp.getIcon()) && featureApp.getIcon().charAt(0) == '/')
                    featureApp.setIcon(hostPath + featureApp.getIcon());
                //去掉所有空格
                if(!"".equals(featureApp)&&featureApp.getVersionName()!=null){
                	featureApp.setVersionName(featureApp.getVersionName().replace(" ",""));
                }
            }

            // 取得使用者資訊
            //&TODO
            if(!StringUtils.isBlank(token)&&imei!=null){
            	Prosumer users = authenticationService.getProsumerByAccount(token);
            	if(users!=null){
	            	String userUid = users.getUserUid();
	            	String userId = users.getUserId();
	            	List<PaymentInformation> paymentInformations = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
	            	for (FeatureApplication featureApplication : appList) {
	            		// 存入PaymentInformationList使取得PayStatus
	            		featureApplication.setPaymentInformationList(paymentInformations);
	            	}
            	}
            }
            return new ServiceResult<List<FeatureApplication>>(CommonCode.SUCCESS, appList);
        } catch (Throwable e) {
            return new ServiceResult<List<FeatureApplication>>(CommonCode.SERVICE_FAIL, null, e);
        }

    }

    /**
     * categoryList : 05. 取得應用程式類別清單 service
     */
    public ServiceResult<List<ApplicationCategory>> applicationCategories(BigDecimal storeId,Long parentCategoryId) {

        List<ApplicationCategory> categoryList = new ArrayList<ApplicationCategory>();

        MemcachedClient mcc = new MemcachedClient();
        
        // 取得hostpath
        String hostPath = constantService.getHostPath();

        try {
            // 取出Category列表
            @SuppressWarnings("unchecked")
            Map args=new HashMap();
            args.put("storeId", storeId);
            args.put("parentCategoryId", parentCategoryId);
            @SuppressWarnings("unchecked")
            List<ApplicationCategory> categorys;
            
            if(mcc.get(storeId+""+parentCategoryId+"category")==null||"".equals(mcc.get(storeId+""+parentCategoryId+"category"))){
            	categorys = sqlMapClientTemplate.queryForList("ApplicationCategory.getCategories",args);
            	mcc.set(storeId+""+parentCategoryId+"category", categorys,new Date(8*60*60*1000));
            }else{
            	categorys=(List<ApplicationCategory>)mcc.get(storeId+""+parentCategoryId+"category");
        	}
            
            for (ApplicationCategory applicationCategory : categorys) {
                //设置对象中分类的前三个好应用
                @SuppressWarnings("unchecked")
                Map category = new HashMap();
                category.put("categoryId", applicationCategory.getId());
                category.put("storeId", storeId);
                //声明前三名的集合
                @SuppressWarnings("unchecked")
                List<Top3OfCategory> top3 ; 
                
                if(mcc.get(storeId+""+applicationCategory.getId()+"categoryTop3")==null||"".equals(mcc.get(storeId+""+applicationCategory.getId()+"categoryTop3"))){
                	top3 =sqlMapClientTemplate.queryForList("Application.getTop3OfCategory",category);
                	mcc.set(storeId+""+applicationCategory.getId()+"categoryTop3", top3,new Date(8*60*60*1000));
                }else{
                	top3=(List<Top3OfCategory>)mcc.get(storeId+""+applicationCategory.getId()+"categoryTop3");
            	}
                
                if(top3.size()<1){
                	 // icon url前加上hostPath
                    if (StringUtils.isNotBlank(applicationCategory.getIcon()))
                        applicationCategory.setIcon(hostPath + applicationCategory.getIcon());
                	//设置到list
                	categoryList.add(applicationCategory);
                }else{
                	 // icon url前加上hostPath
                    if (StringUtils.isNotBlank(top3.get(0).getIcon()))
                        applicationCategory.setIcon(hostPath + top3.get(0).getIcon());
                	//设置前三名的数据
                	for(int i = 0; i < top3.size();i++){
                		if(i==0){
                			applicationCategory.setTop1(top3.get(i).getAppTitle());
                		}else if(i==1){
                			applicationCategory.setTop2(top3.get(i).getAppTitle());
                		}else if(i==2){
                			applicationCategory.setTop3(top3.get(i).getAppTitle());
                		}
                	}
                	//设置到list
                    categoryList.add(applicationCategory);
                }
            }

        } catch (Throwable e) {
            return new ServiceResult<List<ApplicationCategory>>(CommonCode.SERVICE_FAIL, categoryList, e);
        }

        return new ServiceResult<List<ApplicationCategory>>(CommonCode.SUCCESS, categoryList);
    }
    
    /**
     * 获得分类
     * @param storeId
     * @param parentCategoryId
     * @return
     */
    public List<ApplicationCategory> getApplicationCategory(BigDecimal storeId,Long parentCategoryId){
        Map args=new HashMap();
        args.put("storeId", storeId);
        args.put("parentCategoryId", parentCategoryId);
        return sqlMapClientTemplate.queryForList("ApplicationCategory.getCategories",args);
    }

    /**
     * applicationsOfCategory : 06. 某一類別的應用程式清單 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PagedApplications> applicationsOfCategory(ApplicationService self,
        String token, String imei, int categoryId, int pageNo, AppType appType,
        boolean appfilter, int aver, int wpx, int hpx, BigDecimal storeId, int psize) {

        try {
            String supportVersion = null;
            String supportDPI = null;

            // 當appfilter == 1表示要做Filter
            if (appfilter) {
                // 依照aver取得支援的版本
                supportVersion = constantService.getAndroidSdkVersionByLevel(aver);
                if (supportVersion == null) {
                    PagedApplications pagedApplication = new PagedApplications(true, new Application[0], 0);
                    return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
                }
                // 組解析度，(寬*高)
                supportDPI = getSupportDPI(wpx, hpx);
            }

            // 取出Application總比數，以categoryId為條件
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(appType.value, appType.value);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("categoryId", new Integer(categoryId));
            map.put("storeId", storeId);
            Integer returnCount = new Integer(0);
            if(appType.value.equals("allapps")||appType.value.equals("hotapps")) {
            	 returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getApplicationCount", map);
            }else if(appType.value.equals("legalapps")){
            	 returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getLegalApplicationCount", map);
            }
            int appsCount = returnCount != null ? returnCount.intValue() : 0;
            if (appsCount == 0) {
                PagedApplications pagedApplication = new PagedApplications(true, new Application[0], appsCount);
                return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
            }

            // 取出Application列表，以categoryId為條件
            int pageSize = 0; 
            if(psize!=0){
            	pageSize = psize; // 每頁app的最大筆數
            }else{
            	pageSize = constantService.getApplicationPageSize(); // 每頁app的最大筆數
            }
            int startNo = (pageNo - 1) * pageSize;
            int endNo = pageNo * pageSize;
            map = new HashMap<String, Object>();
            map.put(appType.value, appType.value);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("categoryId", new Integer(categoryId));
            map.put("startNo", new Integer(startNo));
            map.put("endNo", new Integer(pageSize));
            map.put("storeId", storeId);
            @SuppressWarnings("unchecked")
            List<Application> appsOfCategory =null;
            if(appType.value.equals("allapps")){
                appsOfCategory = (List<Application>) sqlMapClientTemplate.queryForList("Application.getApplications", map);
            }else if(appType.value.equals("hotapps")){
            	appsOfCategory = (List<Application>) sqlMapClientTemplate.queryForList("Application.getHotApplications", map);
            }else if(appType.value.equals("legalapps")){//getLegalApplications
            	appsOfCategory = (List<Application>) sqlMapClientTemplate.queryForList("Application.getLegalApplications", map);
            }
            String hostPath = constantService.getHostPath();
            List<String> appPkgNames = new ArrayList<String>();
            for (Application application : appsOfCategory) {
                // icon url前加上hostPath
                if (StringUtils.isNotBlank(application.getIcon()) && application.getIcon().charAt(0) == '/')
                    application.setIcon(hostPath + application.getIcon());
                appPkgNames.add(application.getPkg());
                if(application.getVersionName()!=null&&!"".equals(application.getVersionName())){
                	application.setVersionName(application.getVersionName().replace(" ", ""));
                }
                
            }
            // 取得使用者資訊
            if (!StringUtils.isBlank(token)&&imei!=null) {
	            Prosumer users = authenticationService.getProsumerByAccount(token);
	            String userUid = "";
	            String userId = "";
	            
	            if(users != null){
	            	userUid = users.getUserUid();
	            	userId = users.getUserId();
	            
		            // 取得付費資訊
		            List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
		            for (Application application : appsOfCategory) {
		                // 存入PaymentInformationList使取得PayStatus
		                application.setPaymentInformationList(paymentInformationList);
		            }
	            }
            }

            boolean pageEnd = pageNo * pageSize >= appsCount || appsOfCategory.size() == 0;
            Application[] apps = appsOfCategory.toArray(new Application[appsOfCategory.size()]);
            PagedApplications pagedApplication = new PagedApplications(pageEnd, apps, appsCount);
            return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
        } catch (Throwable e) {
            return new ServiceResult<PagedApplications>(CommonCode.SERVICE_FAIL, null, e);
        }
    }
    
    
    /**
     * applicationsOfCategory : 00new. 某一類別的排行 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PagedApplications> rankingOfCategoryForPage(ApplicationService self,
        String token, String imei, int categoryId, int pageNo, AppType appType,
        boolean appfilter, int aver, int wpx, int hpx, BigDecimal storeId, int psize) {
    	
    	MemcachedClient mcc = new MemcachedClient();
        try { 
        	
    	 String supportVersion = null;
         String supportDPI = null;
         int epNo = 20;//每頁條數
         int appsCount= 0 ;//設置應用總數
         boolean pageEnd= false;//設置頁數

         // 當appfilter == 1表示要做Filter
         if (appfilter) {
             // 依照aver取得支援的版本
             supportVersion = constantService.getAndroidSdkVersionByLevel(aver);
             if (supportVersion == null) {
                 PagedApplications pagedApplication = new PagedApplications(true, new Application[0], 0);
                 return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
             }
             // 組解析度，(寬*高)
             supportDPI = getSupportDPI(wpx, hpx);
         }
    	
        //实例memcatched 
        //new MemberCacheUtil(); 
    	//实例memcache
                
        Map map = new HashMap<String, Object>();
        map.put(appType.value, appType.value);
        //map.put("supportVersion", supportVersion);
        //map.put("supportDPI", supportDPI);
        map.put("categoryId", new Integer(categoryId));
        map.put("storeId", storeId);
        @SuppressWarnings("unchecked")
        List<Application> appsOfCategory = new ArrayList<Application>();
        
        @SuppressWarnings("unchecked")
        List<Application> appsOfCategoryForAll =new ArrayList<Application>();
      
        if(appType.value.equals("allapps")){

        	if((mcc.get(storeId+""+(pageNo-1)+"allranking")==null||"".equals(mcc.get(storeId+""+(pageNo-1)+"allranking")))){
        		//TODO 
            	//需要更改SQL語句
            	appsOfCategoryForAll = (List<Application>) sqlMapClientTemplate.queryForList("Application.getAllRanking", map);
                
                if(appsOfCategoryForAll.size()<=0){
                	 PagedApplications pagedApplication = new PagedApplications(true, new Application[0], 0);
                     return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
                }
                
                //TODO 加入memcache,目前這種方法會丟掉最後小於20的頁數，從業務角度講不嚴重，等待有更好的辦法
                for(int i=0;i<appsOfCategoryForAll.size()/epNo;i++){
                	
                	@SuppressWarnings("unchecked")
                    List<Application> appsOfCategoryForPage =new ArrayList<Application>();
                	
                		for(int j=i*epNo;j<epNo*(i+1);j++){
        	 				appsOfCategoryForPage.add(appsOfCategoryForAll.get(j));
        	 			}
                		if(i == (pageNo-1)){
    	 					appsOfCategory = appsOfCategoryForPage;
    	 				}
        	 			//設置每頁的值對象
        	 			mcc.set(storeId+""+i+"allranking", appsOfCategoryForPage,new Date(8*60*60*1000));
        	 			//判斷是否為最後一頁
        	 			if(i==appsOfCategoryForAll.size()-1){
        	 				//設置是否為頁結尾
            	 			mcc.set(storeId+""+i+"allranking"+"pageEnd", true,new Date(8*60*60*1000));
        	 			}
        	 			//設置是否為頁結尾
        	 			mcc.set(storeId+""+i+"allranking"+"pageEnd", false,new Date(8*60*60*1000));
        	 			
        	 	}
	                appsCount=(appsOfCategoryForAll.size()/epNo)*epNo;
	            	//設置應用總數
	            	mcc.set(storeId+"allranking"+"appsCount", appsCount,new Date(8*60*60*1000));
	            	//appsOfCategory=(List<Application>)mcc.get(categoryId+""+storeId+""+(pageNo-1)+"ranking");
	            	pageEnd = false;
            }else{
            	//如果有memcache直接獲得
            	appsOfCategory=(List<Application>)mcc.get(storeId+""+(pageNo-1)+"allranking");
            	appsCount =Integer.parseInt(mcc.get(storeId+"allranking"+"appsCount").toString());
            	pageEnd = Boolean.parseBoolean(mcc.get(storeId+""+(pageNo-1)+"allranking"+"pageEnd").toString());
            }
        	
        }else if(("appranking").equals(appType.value)||("gameranking").equals(appType.value)){
        	if((mcc.get(categoryId+""+storeId+""+(pageNo-1)+"ranking")==null||"".equals(mcc.get(categoryId+""+storeId+""+(pageNo-1)+"ranking")))){
            	//TODO 
            	//需要更改SQL語句
            	appsOfCategoryForAll = (List<Application>) sqlMapClientTemplate.queryForList("Application.getAPPRanking", map);
                
            	if(appsOfCategoryForAll.size()<=0){
                	 PagedApplications pagedApplication = new PagedApplications(true, new Application[0], 0);
                     return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
                }
                
                //TODO 加入memcache,目前這種方法會丟掉最後小於20的頁數，從業務角度講不嚴重，等待有更好的辦法
                for(int i=0;i<appsOfCategoryForAll.size()/epNo;i++){

                	@SuppressWarnings("unchecked")
                    List<Application> appsOfCategoryForPage =new ArrayList<Application>();
        	 			
                		for(int j=i*epNo;j<epNo*(i+1);j++){
        	 				appsOfCategoryForPage.add(appsOfCategoryForAll.get(j));
        	 			}
                		if(i ==(pageNo-1)){
    	 					appsOfCategory = appsOfCategoryForPage;
    	 				}
        	 			//設置每頁的值對象
        	 			mcc.set(categoryId+""+storeId+""+i+"ranking", appsOfCategoryForPage,new Date(8*60*60*1000));
        	 			//判斷是否為最後一頁
        	 			if(i==appsOfCategoryForAll.size()-1){
        	 				//設置是否為頁結尾
            	 			mcc.set(categoryId+""+storeId+""+i+"ranking"+"pageEnd", true,new Date(8*60*60*1000));
        	 			}else{
        	 				//設置是否為頁結尾
        	 				mcc.set(categoryId+""+storeId+""+i+"ranking"+"pageEnd", false,new Date(8*60*60*1000));
        	 			}
        	 			
        	 	}
                	appsCount=(appsOfCategoryForAll.size()/epNo)*epNo;
                	//設置應用總數
                	mcc.set(categoryId+""+storeId+"ranking"+"appsCount", appsCount,new Date(8*60*60*1000));
                	//appsOfCategory=(List<Application>)mcc.get(categoryId+""+storeId+""+(pageNo-1)+"ranking");
                	pageEnd = false;
            }else{
            	//如果有memcache直接獲得
            	appsOfCategory=(List<Application>)mcc.get(categoryId+""+storeId+""+(pageNo-1)+"ranking");
            	appsCount =Integer.parseInt(mcc.get(categoryId+""+storeId+"ranking"+"appsCount").toString());
            	pageEnd = Boolean.parseBoolean(mcc.get(categoryId+""+storeId+""+(pageNo-1)+"ranking"+"pageEnd").toString());
            }
        }
        
        
    
	        @SuppressWarnings("unchecked")
	        List<Application> appsOfCategorys =new ArrayList<Application>();
	        String hostPath = constantService.getHostPath();
	        List<String> appPkgNames = new ArrayList<String>();
	        //判断appsOfCategory不能为空
	        for (Application application : appsOfCategory) {
	            // icon url前加上hostPath
	            if (StringUtils.isNotBlank(application.getIcon()) && application.getIcon().charAt(0) == '/')
	                application.setIcon(hostPath + application.getIcon());
	            appPkgNames.add(application.getPkg());
	            //去掉versionName的空格
	            if(application.getVersionName()!=null&&!"".equals(application.getVersionName())){
	            	application.setVersionName(application.getVersionName().replace(" ",""));
	            }
	            if(application.getTitle()!=null&&!"".equals(application.getTitle())){
	            	application.setTitle(application.getTitle().replace(" ",""));
	            }
	            
	            appsOfCategorys.add(application);
	        }
	        // 取得使用者資訊
	        if (!StringUtils.isBlank(token)&&imei!=null) {
	            Prosumer users = authenticationService.getProsumerByAccount(token);
	            String userUid = "";
	            String userId = "";
	            
	            if(users != null){
	            	userUid = users.getUserUid();
	            	userId = users.getUserId();
	            
		            // 取得付費資訊
		            List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
		            for (Application application : appsOfCategory) {
		                // 存入PaymentInformationList使取得PayStatus
		                application.setPaymentInformationList(paymentInformationList);
		            }
	            }
	        }
	
	        Application[] apps = appsOfCategorys.toArray(new Application[appsOfCategorys.size()]);
	        PagedApplications pagedApplication = new PagedApplications(pageEnd, apps, appsCount);
	        return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
	    } catch (Throwable e) {
	        return new ServiceResult<PagedApplications>(CommonCode.SERVICE_FAIL, null, e);
	    }
    }
    
    /**
     * applicationsOfCategory : 00new. 某一類別的排行 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PagedApplications> rankingOfCategory(ApplicationService self,
        String token, String imei, int categoryId, int pageNo, AppType appType,
        boolean appfilter, int aver, int wpx, int hpx, BigDecimal storeId, int psize) {
        //实例memcache
        MemcachedClient mcc = new MemcachedClient();
        try {
            String supportVersion = null;
            String supportDPI = null;

            // 當appfilter == 1表示要做Filter
            if (appfilter) {
                // 依照aver取得支援的版本
                supportVersion = constantService.getAndroidSdkVersionByLevel(aver);
                if (supportVersion == null) {
                    PagedApplications pagedApplication = new PagedApplications(true, new Application[0], 0);
                    return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
                }
                // 組解析度，(寬*高)
                supportDPI = getSupportDPI(wpx, hpx);
            }

            // 取出Application總比數，以categoryId為條件
            Map<String, Object> map = new HashMap<String, Object>();
             map.put(appType.value, appType.value);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("categoryId", new Integer(categoryId));
            map.put("storeId", storeId);
            Integer returnCount= null;
            if(("allapps").equals(appType.value)){
           	 	if(mcc.get("allappscount"+storeId)==null||"".equals(mcc.get("allappscount"+storeId))){
  
                	returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getAllApplicationCount", map);
                	mcc.set("allappscount"+storeId, returnCount,new Date(8*60*60*1000));
           	 	}else{
           	 		returnCount=(Integer)mcc.get("allappscount"+storeId);
           	 	}
            }else{
            	if(mcc.get(categoryId+"topappscount"+storeId)==null||"".equals(mcc.get(categoryId+"topappscount"+storeId))){
                	returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getTopApplicationCount", map);
                	mcc.set(categoryId+"topappscount"+storeId, returnCount,new Date(8*60*60*1000));
           	 	}else{
           	 		returnCount=(Integer)mcc.get(categoryId+"topappscount"+storeId);
           	 	}
            }
            int appsCount = returnCount != null ? returnCount.intValue() : 0;
            if (appsCount == 0) {
                PagedApplications pagedApplication = new PagedApplications(true, new Application[0], appsCount);
                return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
            }

            // 取出Application列表，以categoryId為條件
            int pageSize = 0; 
            if(psize!=0){
            	pageSize = psize; // 每頁app的最大筆數
            }else{
            	pageSize = constantService.getApplicationPageSize(); // 每頁app的最大筆數
            }
            int startNo = (pageNo - 1) * pageSize;
            int endNo = pageNo * pageSize;
            Map map1 = new HashMap<String, Object>();
            map1.put(appType.value, appType.value);
            map1.put("supportVersion", supportVersion);
            map1.put("supportDPI", supportDPI);
            map1.put("categoryId", new Integer(categoryId));
            map1.put("startNo", new Integer(startNo));
            map1.put("endNo", new Integer(pageSize));
            map1.put("storeId", storeId);
            @SuppressWarnings("unchecked")
            List<Application> appsOfCategory =null;
            
            @SuppressWarnings("unchecked")
            List<Application> appsOfCategorys =new ArrayList<Application>();
        
            if(appType.value.equals("allapps")){
            	 if(mcc.get(startNo+pageSize+"allappsranking"+storeId)==null||"".equals(mcc.get(startNo+pageSize+"allappsranking"+storeId))){
                 	appsOfCategory = (List<Application>) sqlMapClientTemplate.queryForList("Application.getAllRanking", map1);
                 	mcc.set(startNo+pageSize+"allappsranking"+storeId, appsOfCategory,new Date(8*60*60*1000));
                 }else{
                 	appsOfCategory=(List<Application>)mcc.get(startNo+pageSize+"allappsranking"+storeId);
             	}
            }else if(("appranking").equals(appType.value)||("gameranking").equals(appType.value)){
            	if(mcc.get(categoryId+startNo+pageSize+"gameranking"+storeId)==null||"".equals(mcc.get(categoryId+startNo+pageSize+"gameranking"+storeId))){
  
                 	appsOfCategory = (List<Application>) sqlMapClientTemplate.queryForList("Application.getAPPRanking", map1);
                 	mcc.set(categoryId+startNo+pageSize+"gameranking"+storeId, appsOfCategory,new Date(8*60*60*1000));
            	}else{
                 	appsOfCategory=(List<Application>)mcc.get(categoryId+startNo+pageSize+"gameranking"+storeId);
             	}
            }
            String hostPath = constantService.getHostPath();
            List<String> appPkgNames = new ArrayList<String>();
            for (Application application : appsOfCategory) {
                // icon url前加上hostPath
                if (StringUtils.isNotBlank(application.getIcon()) && application.getIcon().charAt(0) == '/')
                    application.setIcon(hostPath + application.getIcon());
                appPkgNames.add(application.getPkg());
                //去掉versionName的空格
                if(application.getVersionName()!=null&&!"".equals(application.getVersionName())){
                	application.setVersionName(application.getVersionName().replace(" ",""));
                }
                if(application.getTitle()!=null&&!"".equals(application.getTitle())){
                	 application.setTitle(application.getTitle().replace(" ",""));
                }
               
                appsOfCategorys.add(application);
            }
            // 取得使用者資訊
            if (!StringUtils.isBlank(token)&&imei!=null) {
	            Prosumer users = authenticationService.getProsumerByAccount(token);
	            String userUid = "";
	            String userId = "";
	            
	            if(users != null){
	            	userUid = users.getUserUid();
	            	userId = users.getUserId();
	            
		            // 取得付費資訊
		            List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
		            for (Application application : appsOfCategory) {
		                // 存入PaymentInformationList使取得PayStatus
		                application.setPaymentInformationList(paymentInformationList);
		            }
	            }
            }

            boolean pageEnd = pageNo * pageSize >= appsCount || appsOfCategorys.size() == 0;
            Application[] apps = appsOfCategorys.toArray(new Application[appsOfCategorys.size()]);
            PagedApplications pagedApplication = new PagedApplications(pageEnd, apps, appsCount);
            return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
        } catch (Throwable e) {
            return new ServiceResult<PagedApplications>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * applicationDetail : 07.ApplicationDetailApi 應用程式詳細內容 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<ApplictionDetail> applicationDetail(ApplicationService self, String token,
        String imei, String mac, String pkgId, StoreInfo storeInfo) {
    	
    	MemcachedClient mcc = new MemcachedClient();
    	BigDecimal storeId = storeInfo.getId();
    	boolean isApproved = storeInfo.getIsApproved()==1? true:false;
    	boolean isLogin = storeInfo.getIsLogin()==1? true:false;
    	boolean isDownload = storeInfo.getIsDownload()==1? true:false;
        try {
        	// 取得使用者資訊
            Prosumer users=null;
            String userUid = "";
            String userId = "";
            if (!StringUtils.isBlank(token)&&imei!=null) {
	            users = authenticationService.getProsumerByAccount(token);
	            if(users != null){
	            	userUid = users.getUserUid();
	                userId = users.getUserId();
	            }
            }
            ApplictionDetail applictionDetail = null;
            // 取出ApplicationDetail資訊
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", pkgId);
            map.put("storeId", storeId);
            if(mcc.get(pkgId+storeId+"appDetail")==null||"".equals(mcc.get(pkgId+storeId+"appDetail"))){
            	applictionDetail = (ApplictionDetail) sqlMapClientTemplate.queryForObject("Application.getApplicationDetail", map);
            	mcc.set(pkgId+storeId+"appDetail", applictionDetail,new Date(8*60*60*1000));
            }
            else{
            	applictionDetail = (ApplictionDetail)mcc.get(pkgId+storeId+"appDetail");
            }
            

            if (applictionDetail == null)
                return new ServiceResult<ApplictionDetail>(AppDetailCode.NO_APP, null);

            // 取得APK最後3筆評論評分資訊
            map = new HashMap<String, Object>();
            map.put("pkgId", pkgId);
            map.put("startNo", new Integer(0));
            map.put("endNo", new Integer(3));
            map.put("storeId", storeId);
            if(isApproved){
            	map.put("isApproved", storeInfo.getIsApproved());
            	if(users != null){
            		map.put("userId", userId);
            	}else{
            		if(imei!=null && imei.length()>0){
                		map.put("imei", imei);
                	}else if(mac!=null && mac.length()>0){
                		map.put("mac", mac);
                	}
            	}
            }
            @SuppressWarnings("unchecked")
            List<PosterComment> commentList = sqlMapClientTemplate.queryForList("CommentOfApp.getComments", map);
            applictionDetail.setCommentList(commentList); // 存入評論評分資訊列表            
            
            // 檢查是否有安裝
            boolean isReportable = false;
            boolean isCommentable = false;
            boolean isRatingable = false;
            int installAppCount = 0;
            Comment comment = null;
            
            if(users != null){
	            // 取得付費資訊
	            List<String> appPkgList = new ArrayList<String>();
	            appPkgList.add(applictionDetail.getPkg());
	            List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgList, storeId);

	            // 存入PaymentInformationList使取得PayStatus
	            if(paymentInformationList != null && paymentInformationList.size() >0){
	            	applictionDetail.setPaymentInformationList(paymentInformationList);
	            }

	            // 取得使用者評論評分資訊，以pkg/userId為條件
	            map = new HashMap<String,Object>();
	            map.put("pkgId", pkgId);
	            map.put("userId", userId);
	            map.put("storeId", storeId);
	            comment = (Comment) sqlMapClientTemplate.queryForObject("CommentOfApp.getCommentByUserId", map);
	            // 存入使用者評論評分資訊
	            if (comment != null) {
	                applictionDetail.setMyComment(comment);
	            }
	            
	            // 取出安裝App總比數，以pkg/userId為條件
	            Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("UserInstallApp.getInstallAppCount", map);
	            installAppCount = returnCount != null ? returnCount.intValue() : 0;
	            
	            if(!isDownload || installAppCount > 0){
	            	if (comment == null || comment.getRating() == null)
	            		isRatingable = true;
	            }
            }
            if(users==null&&(!"0".equals(imei)||!"0".equals(mac))){
	            map = new HashMap<String, Object>();
	            map.put("pkgId", pkgId);
	            map.put("storeId", storeId);
            	if(!"0".equals(imei)){
    	            map.put("imei", imei);
            	}else if(!"0".equals(mac)){
    	            map.put("mac", mac);
            	}
	            comment = (Comment) sqlMapClientTemplate.queryForObject("CommentOfApp.getCommentByImeiMac", map);
	            Integer reportId = (Integer) sqlMapClientTemplate.queryForObject("CommentOfApp.getReportByImeiMac", map);
	            // 存入使用者評論評分資訊
	            if (comment != null) {
	            	comment.setReportId(reportId);
	                applictionDetail.setMyComment(comment);
	            }else if(reportId!=null){
	            	comment=new Comment();
	            	comment.setReportId(reportId);
	            	applictionDetail.setMyComment(comment);
	            }
	            
	            // 取出安裝App總比數，以pkg/imei/mac為條件
	            Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("UserInstallApp.getInstallAppCountByImeiMac", map);
	            installAppCount = returnCount != null ? returnCount.intValue() : 0;

	            if(!isLogin){
	            	if(!isDownload || installAppCount > 0){
		            	if (comment == null || comment.getRating() == null)
		            		isRatingable = true;
		            }
	            }
            }
            
            //曾下载过才可举报
            if (installAppCount > 0) {
                if (!(comment != null && comment.getReportId() != null))
                    isReportable = true;
            }
            
            //如果判断为可以評分；或者曾经评过分，则可以评论
            if(isRatingable || applictionDetail.getMyComment() != null){
            	isCommentable = true;
            }
            
            applictionDetail.setReportable(isReportable);
            applictionDetail.setCommentable(isCommentable);
            applictionDetail.setRatingable(isRatingable);
            
            // url前加上hostPath, set icon and screen shots url
            String hostPath = constantService.getHostPath();
            if (!"".equals(applictionDetail.getVersionName())||applictionDetail.getVersionName()!=null)
                applictionDetail.setVersionName(applictionDetail.getVersionName());
            if (StringUtils.isNotBlank(applictionDetail.getIcon()) && applictionDetail.getIcon().charAt(0) == '/')
                applictionDetail.setIcon(hostPath + applictionDetail.getIcon());
            if (StringUtils.isNotBlank(applictionDetail.getWebPic1()) && applictionDetail.getWebPic1().charAt(0) == '/') {
                applictionDetail.setWebPic1(hostPath + applictionDetail.getWebPic1());
            }
            if (StringUtils.isNotBlank(applictionDetail.getWebPic2()) && applictionDetail.getWebPic2().charAt(0) == '/') {
                applictionDetail.setWebPic2(hostPath + applictionDetail.getWebPic2());
            }
            if (StringUtils.isNotBlank(applictionDetail.getWebPic3()) && applictionDetail.getWebPic3().charAt(0) == '/') {
                applictionDetail.setWebPic3(hostPath + applictionDetail.getWebPic3());
            }
            if (StringUtils.isNotBlank(applictionDetail.getWebPic4()) && applictionDetail.getWebPic4().charAt(0) == '/') {
                applictionDetail.setWebPic4(hostPath + applictionDetail.getWebPic4());
            }
            if (StringUtils.isNotBlank(applictionDetail.getWebPic5()) && applictionDetail.getWebPic5().charAt(0) == '/') {
                applictionDetail.setWebPic5(hostPath + applictionDetail.getWebPic5());
            }
           
            return new ServiceResult<ApplictionDetail>(CommonCode.SUCCESS, applictionDetail);
        } catch (Throwable e) {
            return new ServiceResult<ApplictionDetail>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * CommentsOfApp : 08. 應用程式評論清單 service
     */
    public ServiceResult<PagedComments> commentsOfApplication(ApplicationService self, String token, String imei,
    	String mac, String pkgId, int page, StoreInfo storeInfo, int psize) {
    	BigDecimal storeId = storeInfo.getId();
    	int commentPageSize = 0;
        try {
        	// 取得使用者資訊
            Prosumer users=null;
            String userId = "";
            if (!StringUtils.isBlank(token)&&imei!=null) {
	            users = authenticationService.getProsumerByAccount(token);
	            if(users != null){
	                userId = users.getUserId();
	            }
            }
        	
            // 每頁評論的最大筆數
            if(psize!=0){
            	commentPageSize = psize;
            }else{
            	commentPageSize = constantService.getCommentPageSize();            	
            }
            // 取出CommentOfApp總筆數
            int startNo = (page - 1) * commentPageSize;
            int endNo = page * commentPageSize;

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pkgId", pkgId);
            map.put("startNo", new Integer(startNo));
            map.put("endNo", new Integer(commentPageSize));
            map.put("storeId", storeId);
        /*    if(storeInfo.getIsApproved()==1){
            	map.put("isApproved", storeInfo.getIsApproved());
            	if(users != null){
            		map.put("userId", userId);
            	}else{
            		if(imei!=null && imei.length()>0){
                		map.put("imei", imei);
                	}else if(mac!=null && mac.length()>0){
                		map.put("mac", mac);
                	}
            	}
            }*/
            Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("CommentOfApp.getCommentsCount", map);
            int commentCount = (returnCount!=null)?returnCount.intValue():0;
            if (returnCount == null || returnCount.intValue() < 1) {
                PagedComments pagedComments = new PagedComments(true, new PosterComment[0], commentCount);
                return new ServiceResult<PagedComments>(CommonCode.SUCCESS, pagedComments);
            }

            // 取得使用者評論內容
            int commentOfAppCount = returnCount.intValue();
            @SuppressWarnings("unchecked")
            List<PosterComment> commentOfAppList = sqlMapClientTemplate.queryForList("CommentOfApp.getComments", map);
            boolean isEnd = page * commentPageSize >= commentOfAppCount;
            PosterComment[] comments = commentOfAppList.toArray(new PosterComment[commentOfAppList.size()]);
            PagedComments pagedComments = new PagedComments(isEnd, comments, commentCount);

            return new ServiceResult<PagedComments>(CommonCode.SUCCESS, pagedComments);
        } catch (Throwable e) {
            return new ServiceResult<PagedComments>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * reportApp : 09. 檢舉Application service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Void> reportApplication(ApplicationService self, String token,
        String iccid, String imei, String mac, String pkgId, int lappv, int reportId, BigDecimal storeId) {

        try {
            // 檢查此APK是否存在
            if (!self.hasApplicationByPkgIdAndVersion(pkgId, lappv))
                return new ServiceResult<Void>(ReportAppCode.NO_APP);

            Prosumer users=null;
            if(!StringUtils.isBlank(token)){
	            // 取得使用者資訊
	            users = authenticationService.getProsumerByAccount(token);
	            if(users!=null){
		            String userUid = users.getUserUid();
		            String userId = users.getUserId();
		
		            // 取出ReportApp，條件為USER_UID/APP_ID
		            boolean update = true;
		            Map<String, Object> map = new HashMap<String, Object>();
		            map.put("pkgId", pkgId);
		            map.put("userUid", userUid);
		            map.put("storeId", storeId);
		            ReportApp reportApp = (ReportApp) sqlMapClientTemplate.queryForObject("ReportApp.getReportApp", map);
		            // 若reportApp != null 即可return, reportApp == null 則insert
		            if (reportApp == null) {
		                update = false;
		                reportApp = new ReportApp();
		            }
		            reportApp.setReportId(reportId);
		            reportApp.setUserId(userId);
		            reportApp.setUserUid(userUid);
		            reportApp.setAppId(pkgId);
		            reportApp.setIccid(iccid);
		            reportApp.setImei(imei);
		            reportApp.setMac(mac);
		            reportApp.setVersionId(lappv);
		            reportApp.setStoreId(storeId);
		            // insert ReportApp
		            try {
		                self.saveOrUpdateReportApp(self, reportApp, update);
		            } catch (Throwable e) {
		                return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
		            }
	            }
            }
            if(users==null){
	            boolean update = true;
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("pkgId", pkgId);
	            map.put("storeId", storeId);
            	if(!"0".equals(imei)){
    	            map.put("imei", imei);
            	}else if(!"0".equals(mac)){
    	            map.put("mac", mac);
            	}
            	Integer oldreport = (Integer) sqlMapClientTemplate.queryForObject("ReportApp.getReportAppByImeiMac", map);
	            if (oldreport == null) {
	                update = false;
	                oldreport=0;
	            }
	            ReportApp reportApp = new ReportApp();
	            reportApp.setReportId(reportId);
	            reportApp.setAppId(pkgId);
	            reportApp.setIccid(iccid);
	            reportApp.setImei(imei);
	            reportApp.setMac(mac);
	            reportApp.setVersionId(lappv);
	            reportApp.setStoreId(storeId);
                self.saveReportApp(self, reportApp, update);
            }
            return new ServiceResult<Void>(CommonCode.SUCCESS);
        } catch (Throwable e) {
            return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * register : register service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Credential> registerAccount(BigDecimal storeId,String userId,String pwd,String iccid, String imei,String nickName,String signature,String snum,String IP) {
        try {          
            // 取得使用者資訊
            Prosumer users = authenticationService.getProsumerByUserId(userId);
            Prosumer user = new Prosumer();
            if(users != null){
            	// 代表已有此使用者 ,所以不能註冊
            	return new ServiceResult<Credential>(RegisterCode.USER_ALREADY_EXISTED, null);
            }else{
            	users = authenticationService.getProsumerByNickname(nickName);
            	if(users != null){
            		// 代表暱稱重複 ,所以不能註冊
            		return new ServiceResult<Credential>(RegisterCode.NICKNAME_DUPLICATE, null);
            	} else {
	            	try {
	            		// insert Prosumer	   
	            		user.setStoreId(storeId);
	            		user.setUserId(userId.toUpperCase());
	            		user.setPassword(StringUtil.AES128CBCEncrypt(pwd, "aseyb129clet9vle"));
	            		user.setAccount(new cn.vstore.appserver.model.Account(user.getNewToken(), iccid, imei));
	            		user.setUserName(nickName);
	            		user.setLoginTime(new Date());
	            		user.setNickName(nickName);
	            		user.setSignature(signature);
	            		user.setSnum(snum);
	            		user.setIP(IP);
	            		sqlMapClientTemplate.insert("Auth.insertProsumerForRegister", user);
	            		
	            		//取得ID然後再ID放任USER_UID裡,再更新
	            		user.setUserUid(user.getId()+"");
	            		sqlMapClientTemplate.update("Auth.updateProsumer",user);
	            		
	            		AccountExample ae=new AccountExample();
	            		ae.createCriteria().andLoginIdEqualTo(userId.toUpperCase());
	            		Account acc=(Account)sqlMapClientTemplate.queryForObject("account.ibatorgenerated_selectByExample", ae);
	            		if(acc!=null){
	            			acc.setProsumerId(user.getId());
	            			sqlMapClientTemplate.update("account.ibatorgenerated_updateByPrimaryKeySelective",acc);
	            		}else{
	            			AccountWithBLOBs accb=new AccountWithBLOBs();
	            			accb.setLoginId(userId.toUpperCase());
	            			accb.setNickname(nickName);
	            			accb.setName(nickName);
	            			accb.setPasswd(StringUtil.AES128CBCEncrypt(pwd, "aseyb129clet9vle"));
	            			accb.setProsumerId(user.getId());
	            			sqlMapClientTemplate.update("account.ibatorgenerated_insert",accb);
	            		}
	            	} catch (Throwable e) {
	            		return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
	            	}
            	}
            }     
            return new ServiceResult<Credential>(CommonCode.SUCCESS, user.getCredential());
        } catch (Throwable e) {
        	System.out.println("error ..." + e);
            return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * register : register service for web
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Credential> registerAccountForWeb(BigDecimal storeId,String userId,String pwd,String iccid, String imei,String nickName,String signature) {
        try {          
            // 取得使用者資訊
            Prosumer users = authenticationService.getProsumerByUserId(userId);
            Prosumer user = new Prosumer();
            if(users != null){
            	// 代表已有此使用者 ,所以不能註冊
            	return new ServiceResult<Credential>(Web_RegisterCode.USER_ALREADY_EXISTED, null);
            }else{
            	users = authenticationService.getProsumerByNickname(nickName);
            	if(users != null){
            		// 代表暱稱重複 ,所以不能註冊
            		return new ServiceResult<Credential>(Web_RegisterCode.NICKNAME_DUPLICATE, null);
            	} else {
	            	try {
	            		// insert Prosumer	   
	            		user.setStoreId(storeId);
	            		user.setUserId(userId.toUpperCase());
	            		user.setPassword(StringUtil.AES128CBCEncrypt(pwd, "aseyb129clet9vle"));
	            		user.setAccount(new cn.vstore.appserver.model.Account(user.getNewToken(), iccid, imei));
	            		user.setUserName(nickName);
	            		user.setLoginTime(new Date());
	            		user.setNickName(nickName);
	            		user.setSignature(signature);
	            		sqlMapClientTemplate.insert("Auth.insertProsumerForRegister", user);      
	            		
	            		//取得ID然後再ID放任USER_UID裡,再更新
	            		user.setUserUid(user.getId()+"");
	            		sqlMapClientTemplate.update("Auth.updateProsumer",user);
	            	} catch (Throwable e) {
	            		return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
	            	}
            	}
            }     
            return new ServiceResult<Credential>(CommonCode.SUCCESS, user.getCredential());
        } catch (Throwable e) {
        	System.out.println("error ..." + e);
            return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
        }
    }
    
    /**
     * 存取 ReportApp
     * 
     * @param update : true則update， false則insert
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdateReportApp(ApplicationService self, ReportApp reportApp, boolean update) {
        if (update) {
            // 如果在USER_APK_BEHAVIOR有資料, 則update使用者APK使用紀錄
            sqlMapClientTemplate.update("ReportApp.updateReportApp", reportApp);
        } else {
            // 如果在USER_APK_BEHAVIOR沒有資料, 則新增使用者APK使用紀錄
            sqlMapClientTemplate.insert("ReportApp.insertReportApp", reportApp);
        }

        // 新增USER_REPORT_LOG
        sqlMapClientTemplate.insert("ReportApp.insertReportAppLog", reportApp);
    }
    /**
     * 存取 User report log
     * 
     * @param update : true則update， false則insert
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveReportApp(ApplicationService self, ReportApp reportApp, boolean update) {
        // 新增USER_REPORT_LOG
        sqlMapClientTemplate.insert("ReportApp.insertReportAppLog", reportApp);
    }

    /**
     * rateApplication : 10. 評論應用程式 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Void> rateApplication(ApplicationService self, String token, String iccid,
        String imei, String mac, String pkgId, int appVersion, int rating, String comment, StoreInfo storeInfo, String snum, String IP) {

    	BigDecimal storeId = storeInfo.getId();
    	int commentStatus = 0;
    	if(storeInfo.getIsApproved()==0)	commentStatus = 1;//如果评论的当下，此商城并不需审核评论，则将此评论自动设为审核通过
        try {
            // 檢查是否有APK
            if (!self.hasApplicationByPkgIdAndVersion(pkgId, appVersion))
                return new ServiceResult<Void>(RateAppCode.NO_APP);
            
            Prosumer users=null;
            String userUid = "";
            String userId = "";
            
            if(token!=null){
            	// 取得使用者資訊
            	users = authenticationService.getProsumerByAccount(token);
            	if(users!=null){
            		userUid = users.getUserUid();
    	            userId = users.getUserId();
            	}
            }
            
            if(users==null && storeInfo.getIsLogin()==1){
        		return new ServiceResult<Void>(RateAppCode.NEED_LOGIN);
        	}

            /*if(storeInfo.getIsDownload()==1){
            	Integer returnCount = 0;
            	Map<String, Object> map = new HashMap<String, Object>();
	            map.put("pkgId", pkgId);
	            map.put("storeId", storeId);
	            if(users!=null){
	            	// 取出安裝App總比數，以pkg/userId為條件
	            	map.put("userId", userId);
	            	returnCount = (Integer) sqlMapClientTemplate.queryForObject("UserInstallApp.getInstallAppCount", map);
	            }else{
	            	// 取出安裝App總比數，以pkg/imei/mac為條件
	            	if(!"0".equals(imei)){
	    	            map.put("imei", imei);
	            	}else if(!"0".equals(mac)){
	    	            map.put("mac", mac);
	            	}
	            	returnCount = (Integer) sqlMapClientTemplate.queryForObject("UserInstallApp.getInstallAppCountByImeiMac", map);
	            }
	            
	            int installAppCount = returnCount != null ? returnCount.intValue() : 0;
	            if (installAppCount <= 0) {
	            	return new ServiceResult<Void>(RateAppCode.NEED_DOWNLOAD);
	            }
            }*/
            
            if(users!=null){

	            // 取出RateApp，條件為USER_UID/APP_ID
	            boolean update = true;
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("pkgId", pkgId);
	            map.put("userUid", userUid);
	            map.put("storeId", storeId);
	            RateApp rateApp = (RateApp) sqlMapClientTemplate.queryForObject("RateApp.getRateApp", map);
	            if (rateApp == null) {
	                update = false;
	                rateApp = new RateApp();
	            }
	            rateApp.setAppId(pkgId);
	            rateApp.setUserUid(userUid);
	            rateApp.setUserId(userId);
	            rateApp.setLastAppComment(comment);
	            rateApp.setIccid(iccid);
	            rateApp.setImei(imei);
	            rateApp.setMac(mac);
	            rateApp.setVersionId(appVersion);
	            rateApp.setStoreId(storeId);
	            rateApp.setSnum(snum);
	            rateApp.setIP(IP);
	            rateApp.setStatus(commentStatus);
	            // 存取 RateApp
	            try {
	                self.saveOrUpdateRateApp(rateApp, update, rating);
	            } catch (Throwable e) {
	                return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
	            }
            }else{
	            boolean update = true;
	            /*  Map<String, Object> map = new HashMap<String, Object>();
	            map.put("pkgId", pkgId);
	            map.put("storeId", storeId);
            	if(!"0".equals(imei)){
    	            map.put("imei", imei);
            	}else if(!"0".equals(mac)){
    	            map.put("mac", mac);
            	}
            	Integer oldrating = (Integer) sqlMapClientTemplate.queryForObject("RateApp.getRateAppByImeiMac", map);
	            if (oldrating == null) {
	                update = false;
	                oldrating=rating;
	            }*/
	            RateApp rateApp = new RateApp();
            	rateApp.setUserRank(rating);
	            rateApp.setAppId(pkgId);
	            rateApp.setLastAppComment(comment);
	            rateApp.setIccid(iccid);
	            rateApp.setImei(imei);
	            rateApp.setMac(mac);
	            rateApp.setVersionId(appVersion);
	            rateApp.setStoreId(storeId);
	            rateApp.setSnum(snum);
	            rateApp.setIP(IP);
	            rateApp.setStatus(commentStatus);
            	self.saveAppRankLog(rateApp, update, rating);
            }
            return new ServiceResult<Void>(CommonCode.SUCCESS);
        } catch (Throwable e) {
            return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * 存取 RateApp
     * 
     * @param update : true則update， false則insert
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdateRateApp(RateApp rateApp, boolean update, int rating) {
        // 若使用者有評分此APK, 則update USER_APK_BEHAVIOR
        boolean updateRank = false;
        // update = true: USER_APK_BEHAVIOR 有此比紀錄，則update。反之則insert
        if (update) {
            // 只能評分一次, 評論可以多次
            if (rateApp.getUserRank() == 0) {
                rateApp.setUserRank(rating);
                // 更新評分和評論
                sqlMapClientTemplate.update("RateApp.updateRateAppByRatingAndComment", rateApp);
                updateRank = true;
            } else {
            	 sqlMapClientTemplate.update("RateApp.updateRateAppByRatingAndComment", rateApp);
                 updateRank = true;
                // 只更新評論
                //sqlMapClientTemplate.update("RateApp.updateRateAppByComment", rateApp);
            }
        } else { // insert
            rateApp.setUserRank(rating);
            // 新增RateApp
            sqlMapClientTemplate.insert("RateApp.insertRateApp", rateApp);
            updateRank = true;
        }
    	//删除旧的评分评论
    	//sqlMapClientTemplate.update("RateApp.deleteRateAppLog", rateApp);
        // 新增 USER_RANK_LOG
        sqlMapClientTemplate.insert("RateApp.insertRateAppLog", rateApp);

        if (updateRank) {
        	rateApp.setUserRank(rating);
            // 更新APPLICATION，累加APPLICATION內評分數(TOTAL_RANK)，以及評分次數(TOTAL_RANK_TIMES)
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", rateApp.getAppId());
            map.put("userRank", new Integer(rateApp.getUserRank()));
            map.put("storeId", rateApp.getStoreId());
            sqlMapClientTemplate.update("Application.addAppTotalRank", map);

            // 更新APPLICATION_VERSIONS，累加APPLICATION_VERSIONS內評分數(VERSION_RANK)，以及評分次數(RANK_TIMES)
            map = new HashMap<String, Object>();
            map.put("appId", rateApp.getAppId());
            map.put("versionId", new Integer(rateApp.getVersionId()));
            map.put("userRank", new Integer(rateApp.getUserRank()));
            map.put("storeId", rateApp.getStoreId());
            sqlMapClientTemplate.update("Application.addAppVersionTotalRank", map);
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAppRankLog(RateApp rateApp, boolean update, int rating) {
    	//删除旧的评分评论
    	//sqlMapClientTemplate.update("RateApp.deleteRateAppLog", rateApp);
        // 新增 USER_RANK_LOG
        sqlMapClientTemplate.insert("RateApp.insertRateAppLog", rateApp);

        if (!update) {
        	rateApp.setUserRank(rating);
            // 更新APPLICATION，累加APPLICATION內評分數(TOTAL_RANK)，以及評分次數(TOTAL_RANK_TIMES)
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", rateApp.getAppId());
            map.put("userRank", new Integer(rateApp.getUserRank()));
            map.put("storeId", rateApp.getStoreId());
            sqlMapClientTemplate.update("Application.addAppTotalRank", map);

            // 更新APPLICATION_VERSIONS，累加APPLICATION_VERSIONS內評分數(VERSION_RANK)，以及評分次數(RANK_TIMES)
            map = new HashMap<String, Object>();
            map.put("appId", rateApp.getAppId());
            map.put("versionId", new Integer(rateApp.getVersionId()));
            map.put("userRank", new Integer(rateApp.getUserRank()));
            map.put("storeId", rateApp.getStoreId());
            sqlMapClientTemplate.update("Application.addAppVersionTotalRank", map);
        }
    }

    /**
     * searchApps : 11. 搜尋某一類別的應用程式 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PagedApplications> searchApplications(ApplicationService self,
        String token, String imei, int page, String keyword, AppType appType,
        boolean appfilter, int aver, int wpx, int hpx, BigDecimal storeId, int psize) {
        try {
            // 當appfilter == 1表示要做Filter
            String supportVersion = null;
            String supportDPI = null;
            if (appfilter) {
                // 依照aver取得支援的版本
                supportVersion = constantService.getAndroidSdkVersionByLevel(aver);
                if (supportVersion == null) {
                    PagedApplications pagedApplication = new PagedApplications(true, new Application[0], 0);
                    return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
                }
                // 組解析度，(寬*高)，較小者擺在前面
                supportDPI = getSupportDPI(wpx, hpx);
            }

            // 取出Application總筆數
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(appType.value, appType.value);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("keyword", keyword);
            map.put("storeId", storeId);
	        Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getApplicationsByKeyWordCount", map);
	        int appsCount = returnCount != null ? returnCount.intValue() : 0;

            if (appsCount == 0) {
            	//实例memcache
                MemcachedClient mcc = new MemcachedClient();
            	//如果搜索不到結果，返回總排行數據
                returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getAllApplicationCount", map);
            	 // 取出Application列表，以categoryId為條件
                int pageSize = 0; 
                if(psize!=0){
                	pageSize = psize; // 每頁app的最大筆數
                }else{
                	pageSize = constantService.getApplicationPageSize(); // 每頁app的最大筆數
                }
                int startNo = (page - 1) * pageSize;
                int endNo = page * pageSize;
                map = new HashMap<String, Object>();
                map.put(appType.value, appType.value);
                map.put("supportVersion", supportVersion);
                map.put("supportDPI", supportDPI);
                map.put("startNo", new Integer(startNo));
                map.put("endNo", new Integer(pageSize));
                map.put("storeId", storeId);
                @SuppressWarnings("unchecked")
                List<Application> appsOfCategory =null;
                @SuppressWarnings("unchecked")
                List<Application> appsOfCategorys =new ArrayList<Application>();
                if(mcc.get(startNo+pageSize+"allappsranking"+storeId)==null||"".equals(mcc.get(startNo+pageSize+"allappsranking"+storeId))){
                	
                	appsOfCategory = (List<Application>) sqlMapClientTemplate.queryForList("Application.getAllRankingForSearch", map);
                	mcc.set(startNo+pageSize+"allappsranking"+storeId, appsOfCategory,new Date(8*60*60*1000));
                }else{
                	
                	appsOfCategory=(List<Application>)mcc.get(startNo+pageSize+"allappsranking"+storeId);
            	}
                
                String hostPath = constantService.getHostPath();
                List<String> appPkgNames = new ArrayList<String>();
                for (Application application : appsOfCategory) {
                    // icon url前加上hostPath
                    if (StringUtils.isNotBlank(application.getIcon()) && application.getIcon().charAt(0) == '/')
                        application.setIcon(hostPath + application.getIcon());
                    appPkgNames.add(application.getPkg());
                    //去掉versionName的空格
                    if(application.getVersionName()!=null&&"".equals(application.getVersionName())){
                    	 application.setVersionName(application.getVersionName().replace(" ",""));
                    }
                   
                    if(application.getTitle()!=null&&"".equals(application.getTitle())){
                    	 application.setTitle(application.getTitle().replace(" ",""));
                    }
                   
                    appsOfCategorys.add(application);
                }
                boolean pageEnd = page * pageSize >= appsCount || appsOfCategorys.size() == 0;
                Application[] apps = appsOfCategorys.toArray(new Application[appsOfCategorys.size()]);
                PagedApplications pagedApplication = new PagedApplications(pageEnd, apps, appsCount);
                return new ServiceResult<PagedApplications>(CommonCode.NO_APP_SEARCH, pagedApplication);
            }

            // 取出Application列表，以搜尋相同APP_TITLE/APP_DESC/APP_KEYWORD/CP_TITLE的KEYWORD為條件
            int pageSize = 0;
            if(psize!=0){
            	pageSize = psize;
            }else{
            	pageSize = constantService.getApplicationPageSize();
            }
            int startNo = (page - 1) * pageSize;
            int endNo = page * pageSize;
            map = new HashMap<String, Object>();
            map.put(appType.value, appType.value);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("keyword", keyword);
            map.put("startNo", new Integer(startNo));
            map.put("endNo", new Integer(pageSize));
            map.put("storeId", storeId);
            @SuppressWarnings("unchecked")
            List<Application> appsList = (List<Application>) sqlMapClientTemplate.queryForList("Application.getApplicationsByKeyWord", map);
            // 取得hostpath
            String hostPath = constantService.getHostPath();
            List<String> appPkgNames = new ArrayList<String>();
            for (Application app : appsList) {
                // icon url前加上hostPath
                if (StringUtils.isNotBlank(app.getIcon()) && app.getIcon().charAt(0) == '/')
                	app.setIcon(hostPath + app.getIcon());
                appPkgNames.add(app.getPkg());
                //去掉空格
                if(app.getVersionName()!=null&&!"".equals(app.getVersionName())){
                	 app.setVersionName(app.getVersionName().replace(" ", ""));
                }
               
            }

            /*// 取得使用者資訊
            if (!StringUtils.isBlank(token)&&imei!=null) {
	            Prosumer user = authenticationService.getProsumerByAccount(token);
	            String userUid = "";
	            String userId = "";
	            if (user != null){
	            	userUid = user.getUserUid();
	                userId = user.getUserId();
	                // 取得付費資訊
	                List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
	                for (Application application : appsList) {
	                    // 存入PaymentInformationList使取得PayStatus
	                	if (paymentInformationList != null && paymentInformationList.size() > 0){
	                		application.setPaymentInformationList(paymentInformationList);
	                	}
	                }
	                // 當使用者搜尋第一頁時，則insert UserSearchLog
	                if (page == 1) {
	                    try {
	                        // 存取 UserSearchLog
	                        self.insertUserSearchLog(self, keyword, imei, userId, userUid);
	                    } catch (Throwable e) {
	                        return new ServiceResult<PagedApplications>(CommonCode.SERVICE_FAIL, null, e);
	                    }
	                }
	            }
            }*/

            boolean isEnd = page * pageSize >= appsCount || appsList.size() == 0;

            Application[] apps = appsList.toArray(new Application[appsList.size()]);
            PagedApplications pagedApplication = new PagedApplications(isEnd, apps, appsCount);
            return new ServiceResult<PagedApplications>(CommonCode.SUCCESS, pagedApplication);
        } catch (Throwable e) {
            return new ServiceResult<PagedApplications>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * cpApps : 12. 開發者某一類別的應用程式 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PagedApplicationsOfContentProvider> cpApplications(
        ApplicationService self, String token, String imei, String cpId, int page,
        AppType appType, boolean appfilter, int aver, int wpx, int hpx, BigDecimal storeId, int psize) {
        try {
            // 取得開發商資訊，並驗證是否有此位開發商
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cpId", cpId);
            String provider = (String) sqlMapClientTemplate.queryForObject("ContentProvider.getProviderTitle", map);
            if (StringUtils.isBlank(provider))
                return new ServiceResult<PagedApplicationsOfContentProvider>(CpAppsCode.NO_CP, null);

            // 當appfilter == 1表示要做Filter
            String supportVersion = null;
            String supportDPI = null;
            if (appfilter) {
                // 依照aver取得支援的版本
                supportVersion = constantService.getAndroidSdkVersionByLevel(aver);
                if (supportVersion == null) {
                    PagedApplicationsOfContentProvider pagedApplication = new PagedApplicationsOfContentProvider(provider, true, new Application[0], 0);
                    return new ServiceResult<PagedApplicationsOfContentProvider>(CommonCode.SUCCESS, pagedApplication);
                }
                // 組解析度，(寬*高)，較小者擺在前面
                supportDPI = getSupportDPI(wpx, hpx);
            }

            // 取出Application總筆數
            map = new HashMap<String, Object>();
            map.put(appType.value, appType.value);
            map.put("cpId", cpId);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("storeId", storeId);
            Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.getApplicationsByCpIdCount", map);
            int applicationCategoryCount = returnCount != null ? returnCount.intValue() : 0;
            if (applicationCategoryCount == 0) {
                PagedApplicationsOfContentProvider pagedApplication = new PagedApplicationsOfContentProvider(provider, true, new Application[0], applicationCategoryCount);
                return new ServiceResult<PagedApplicationsOfContentProvider>(CommonCode.SUCCESS, pagedApplication);
            }

            // 取出Application列表
            int pageSize = 0;
            if(psize!=0){
            	pageSize = psize;
            }else{
            	pageSize = constantService.getApplicationPageSize();            	
            }
            int startNo = (page - 1) * pageSize;
            int endNo = page * pageSize;
            map = new HashMap<String, Object>();
            map.put(appType.value, appType.value);
            map.put("cpId", cpId);
            map.put("supportVersion", supportVersion);
            map.put("supportDPI", supportDPI);
            map.put("startNo", new Integer(startNo));
            map.put("endNo", new Integer(pageSize));
            map.put("storeId", storeId);
            @SuppressWarnings("unchecked")
            List<Application> appsList = (List<Application>) sqlMapClientTemplate.queryForList("Application.getApplicationsByCpId", map);

            // 取得hostpath
            String hostPath = constantService.getHostPath();
            List<String> appPkgList = new ArrayList<String>();
            for (Application app : appsList) {
                // icon url前加上hostPath
                if (StringUtils.isNotBlank(app.getIcon()) && app.getIcon().charAt(0) == '/')
                	app.setIcon(hostPath + app.getIcon());
                appPkgList.add(app.getPkg());
                //去掉应用的空格
                if(app.getVersionName()!=null&&"".equals(app.getVersionName())){
                	 app.setVersionName(app.getVersionName().replace(" ", ""));
                }
               
            }
            // 取得使用者資訊
            if (!StringUtils.isBlank(token)&&imei!=null) {
	            Prosumer users = authenticationService.getProsumerByAccount(token);
	            String userUid = "";
	            String userId = "";
	            if(users != null){
	            	userUid = users.getUserUid();
	                userId = users.getUserId();
	                // 取得付費資訊
	                List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgList, storeId);
	                for (Application application : appsList) {
	                    // 存入PaymentInformationList使取得PayStatus
	                	if(paymentInformationList != null && paymentInformationList.size() >0){
	                		application.setPaymentInformationList(paymentInformationList);
	                	}
	                }
                }
            }
            boolean isEnd = page * pageSize >= applicationCategoryCount || appsList.size() == 0;

            Application[] apps = appsList.toArray(new Application[appsList.size()]);
            PagedApplicationsOfContentProvider pagedApplication = new PagedApplicationsOfContentProvider(provider, isEnd, apps, applicationCategoryCount);
            return new ServiceResult<PagedApplicationsOfContentProvider>(CommonCode.SUCCESS, pagedApplication);
        } catch (Throwable e) {
            return new ServiceResult<PagedApplicationsOfContentProvider>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * myDownloadApplications : 20. 我的下載App清單 service
     */
    @SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NEVER)
    public ServiceResult<PagedMyDownloadApplications> myDownloadApplications(
        ApplicationService self, String token, String iccid, String imei, String mac, AppType appType, BigDecimal storeId) {
        try {
        	List<PaymentInformation> paymentInformationList=null;
        	List<MyDownloadApplication> applicationList = null;
        	Prosumer users = null;
        	if (!StringUtils.isBlank(token)) {
	        // 取得使用者資訊
	        users = authenticationService.getProsumerByAccount(token);
	        if(users!=null){
	            String userUid = users.getUserUid();
	            String userId = users.getUserId();
	            // 取出安裝MyDownload中Application總筆數，以userUid/imei為條件，(非月租)
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("userUid", userUid);
	            map.put("imei", imei);
	            map.put(appType.value, appType.value);
	            map.put("storeId", storeId);
	            applicationList = (List<MyDownloadApplication>) sqlMapClientTemplate.queryForList("MyDownLoadApp.getMyDownloadApps", map);
            
	            if (!AppType.FREE_APPS.equals(appType)) {
	                // 取出安裝MyDownload中Application總筆數，以userUid/imei為條件，(月租)
	                map = new HashMap<String, Object>();
	                map.put("userUid", userUid);
	                map.put("storeId", storeId);
	                @SuppressWarnings("unchecked")
	                List<MyDownloadApplication> applicationSubList = (List<MyDownloadApplication>) sqlMapClientTemplate.queryForList("MyDownLoadApp.getMyBuiedApps", map);
	                // 組合月租和非月租的App
	                //applicationList.addAll(applicationSubList);
	                for(int i=0;i<applicationSubList.size();i++){
	                	MyDownloadApplication m1=applicationSubList.get(i);
	                	boolean find=false;
	                	for(int j=0;j<applicationList.size();j++){
	                		MyDownloadApplication m2=applicationList.get(j);
	                		if(m1.getPkg().equals(m2.getPkg())){
	                			find=true;
	                			break;
	                		}
	                	}
	                	if(!find) applicationList.add(m1);
	                }
	            }
	
	            List<String> appPkgNames = new ArrayList<String>();
	            for (MyDownloadApplication application : applicationList) {
	                appPkgNames.add(application.getPkg());
	            }
	            // 取得付費資訊
	            paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgNames, storeId);
	        }
        	}
        	
        	if(users==null){
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("storeId", storeId);
            	if(!"0".equals(imei)){
    	            map.put("imei", imei);
            	}else if(!"0".equals(mac)){
    	            map.put("mac", mac);
            	}
	            applicationList = (List<MyDownloadApplication>) sqlMapClientTemplate.queryForList("MyDownLoadApp.getMyDownloadAppsByImeiMac", map);        		
        	}

            // 取得hostpath
            String hostPath = constantService.getHostPath();
            for (MyDownloadApplication applicationMyDownload : applicationList) {
                // 存入PaymentInformationList使取得PayStatus
                if(paymentInformationList!=null) applicationMyDownload.setPaymentInformationList(paymentInformationList);
                // icon url前加上hostPath
                if (StringUtils.isNotBlank(applicationMyDownload.getIcon()) && applicationMyDownload.getIcon().charAt(0) == '/')
                    applicationMyDownload.setIcon(hostPath + applicationMyDownload.getIcon());
                //去掉版本号的空格
                if(applicationMyDownload.getVersionName()!=null&&"".equals(applicationMyDownload.getVersionName())){
                	applicationMyDownload.setVersionName(applicationMyDownload.getVersionName().replace(" ",""));
               }
              
               if(applicationMyDownload.getTitle()!=null&&"".equals(applicationMyDownload.getTitle())){
            	   applicationMyDownload.setTitle(applicationMyDownload.getTitle().replace(" ",""));
               }
            }

            // 依sortTime排序，日期較近者排前面
            Collections.sort(applicationList, new MyDownloadComparator());

            boolean isEnd = true;
            MyDownloadApplication[] apps = applicationList.toArray(new MyDownloadApplication[applicationList.size()]);
            PagedMyDownloadApplications pagedApplication = new PagedMyDownloadApplications(isEnd, apps);
            return new ServiceResult<PagedMyDownloadApplications>(CommonCode.SUCCESS, pagedApplication);
        } catch (Throwable e) {
            return new ServiceResult<PagedMyDownloadApplications>(CommonCode.SERVICE_FAIL, null, e);
        }
    }

    /**
     * downloadAppData : 21. 下載App - 1. 取得APK，並取得前更新下載紀錄
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<DownloadAppInfo> downloadAppData(ApplicationService self, String appId,
        String token, String iccid, String imei, String mac, String categoryId, String vlogId, String dvc,
        String lappv, BigDecimal storeId, String snum, String IP) {
        try {
            // 取出 AppVersion，檢查是否有Apk, 條件為APP_ID
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("appId", appId);
            map.put("storeId", storeId);
            Integer appVersion = (Integer) sqlMapClientTemplate.queryForObject("Application.getAppVersion", map);
            if (appVersion == null)
                return new ServiceResult<DownloadAppInfo>(DownloadAppCode.NO_APP);

            // 取得下載APK資訊, 取出ApplictionDetail的下載用的檔案資訊，條件為APP_ID/VERSION_ID
            map = new HashMap<String, Object>();
            map.put("appId", appId);
            map.put("versionId", appVersion);
            map.put("storeId", storeId);
            ApkInfo apkInfo = (ApkInfo) sqlMapClientTemplate.queryForObject("Application.getApkInfo", map);
            if (apkInfo == null)
                return new ServiceResult<DownloadAppInfo>(DownloadAppCode.NO_APP);
            String userUid = null;
            String userId = null;
            if(token!=null){
            	//嘗試取得使用者資訊
                Prosumer users = authenticationService.getProsumerByAccount(token);
                if (users != null){
                	userUid = users.getUserUid();
                    userId = users.getUserId();
                }
            }
            
            long downloadId = 0;
            try {
            	userUid = "defaultUser@vtion.com.cn";
                userId = "defaultUser@vtion.com.cn";
            	if(userUid!=null && userId!=null){
            		// 當使用者已登入並下載APP時
            		// 把userUid/imei使用者的App下載紀錄刪除(IS_DELETE=1)，條件為AppId/iccid/imei
                    self.updateIsDeleteInstallApp(appId, userUid, imei, mac, storeId);
            	
	                // 取得UserInstallAppId，即downloadId
	                downloadId = self.getInstallApkId();
	                // 紀錄下載資訊
	                UserInstallApp userInstallApp = new UserInstallApp(downloadId,appId, appVersion, token, iccid, imei, mac, "0", dvc, lappv, userId, userUid, storeId, snum, IP);
	                
	                //----------------------請注意！--------------------------------
	                //此次改版會影響user_install_apk跟user_install_log兩個表格(token、userId與userUid有可能為null)
	                //因此務必等排程相關統計部分測試正常後才可上版！
	                // 紀錄UserInstallApp
	                System.out.println("return downloadId=======>"+downloadId);
	                self.insertUserInstallApp(userInstallApp);
	                
	                // 紀錄UserInstallAppLog
	                if(downloadId > 0){
	                	//当不使用敦阳之前的逻辑时，需要加上
//	                	userInstallApp.setDownloadId(downloadId); 
	                	self.insertUserInstallAppLog(userInstallApp);
	                }
	                if(!"".equals(userId) && userId != null){
	                	long memId = this.getUserByLoginId(userId).getId();
		                System.out.println("userId======>"+memId);
		                //插入到会员下载信息表
		                VMemDownloadInfo info = new VMemDownloadInfo();
		                info.setMemId(memId);
		                info.setAppId(appId);
		                info.setDownloadTime(new Date());
		                this.insertUserDownloadInfo(info);
	                }
	            	//----------------------須注意部分結束！2012.01.12---------------
            	}
                // 更新USER_GET_APPVERSION_LOG的downloadId，條件vlogId
                if (vlogId != null) {
                    self.updateGetAppvLog(downloadId, vlogId);
                }
                
            } catch (Throwable e) {
                return new ServiceResult<DownloadAppInfo>(CommonCode.SERVICE_FAIL, null, e);
            }

            // 回傳頁面資料
            DownloadAppInfo downloadAppInfo = new DownloadAppInfo();
       	 //测试添加hostPath
          //  String hostPath = constantService.getHostPath();
         //   apkInfo.setFilepath("http://www.51vapp.com/image"+apkInfo.getFilepath().substring(16, apkInfo.getFilepath().length()));
         //-----------------------------------   
            downloadAppInfo.apkInfo = apkInfo;
            downloadAppInfo.downloadId = downloadId;
            downloadAppInfo.appId = appId;
            downloadAppInfo.appVersion = appVersion;
            downloadAppInfo.userUid = userUid;
            downloadAppInfo.userId = userId;
            downloadAppInfo.imei = imei;

            return new ServiceResult<DownloadAppInfo>(CommonCode.SUCCESS, downloadAppInfo);
        } catch (Throwable e) {
            return new ServiceResult<DownloadAppInfo>(CommonCode.SERVICE_FAIL, null, e);
        }

    }

    /**
     * downloadAppData : 21. 下載App - 2. 取得APK後更新下載紀錄
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ServiceResult<Void> updateDownloadAppInfo(String appId, int appVersion, long downloadId,
        String userUid, String userId, String imei, String priceType, BigDecimal storeId) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try {
            // 累加APPLICATION內總下載次數TOTAL_DOWNLOAD_TIMES
            updateAppTotalDownload(appId, storeId);

            // 累加APPLICATION_VERSIONS內總下載次數TOTAL_DOWNLOAD_TIMES
            updateAppVersionTotalDownload(appId, appVersion, storeId);

            // 更新USER_INSTALL_APK的PRICE_TYPE，條件為DOWNLOAD_ID
            updateInstallAppOfPriceType(priceType, downloadId);

            if(userId!=null && userUid!=null){
            	// 取得UserApkBehavior總筆數，以USER_UID和APP_ID為條件
                int userApkBehaviorCount = getUserApkBehaviorCount(userUid, appId, storeId);
                if (userApkBehaviorCount == 0) {
                    insertUserApkBehavior(downloadId, appId, userId, userUid, storeId);
                } else {
                    updateUserApkBehaviorWithDownLoadIdAndUserId(downloadId, appId, userId, userUid, storeId);
                }
                
                // user已經付款的訂單的Price Type大於正在下載的Price Type比較，更新LAST_VERSION_ID
                boolean isTestBlankType = blankListService.isMockPayment(blankListService, userId);
                if (isTestBlankType) {// true:測試使用, false:實際使用)
                    // 假付費
                    paymentService.updateLastVersionIdByTest(paymentService, appId, userUid, priceType, appVersion, storeId);
                } else {
                    // 真付費
                    paymentService.updateLastVersionId(paymentService, appId, userUid, priceType, appVersion, storeId);
                }
            }

//            // 如果是月租型
//            if (Application.PRICE_TYPE_MONTH.equals(priceType)) {
//
//                // 更新最後一次使用的版本
//                subscribeService.updateLastVersionId(subscribeService, appVersion, appId, userUid, storeId);
//
//                // 取得UserSubscribe資訊，條件appId/userUid
//                UserSubscribe userSubscribe = subscribeService.getIsCanceledAndEndTime(appId, userUid, storeId);
//
//                // USER_SUBSCRIBE內有相關資訊，則更新USER_SUBSCRIBE
//                if (userSubscribe != null) {
//
//                    int is_canceled = userSubscribe.getIsCanceled();
//
//                    // 判斷 USER_SUBSCRIBE 的 IS_CANCELED若存在: 
//                    // 0:不做事,
//                    // 1:(過期)IS_CANCELED==>2,END_TIME==>null, 
//                    // 2:(無過期)不做事
//                    if (is_canceled == 1) {
//                        Date endDateQuery = userSubscribe.getEndTime();
//                        Date endDate = null;
//                        if (endDateQuery != null) {
//                            try {
//                                endDate = sdf.parse(sdf.format(endDateQuery).toString());
//                            } catch (Exception e) {
//                            }
//                        }
//                        Date nowDate = sdf.parse(sdf.format(new Date()).toString());
//                        if (endDate != null && (nowDate.after(endDate))) {// 過期
//                            // 更新USER_SUBSCRIBE中的IS_CANCELED=USER_SUBSCRIBE_DOWN_NOT_PAY=2;(下載但未付款)/END_TIME=null
//                            subscribeService.updateIsCanceledWithUserUidAndAppId(subscribeService, BusinessCommonCode.USER_SUBSCRIBE_DOWN_NOT_PAY, userUid, appId, storeId);
//                        } else if (endDate != null && (endDate.after(nowDate))) {// 無過期,則不動作
//                        }
//                    } else if (is_canceled == 0) { // is_canceled = 0,則不動作
//                    } else if (is_canceled == 2) { // is_canceled = 2,則不動作
//                    }
//
//                } else {
//                    // USER_SUBSCRIBE內沒相關資訊，則 insert USER_SUBSCRIBE
//                    // IS_CANCELED==>2,END_TIME==>null
//                    subscribeService.insertUserSubcribeForDownloadApk(subscribeService, userId, appId, null, BusinessCommonCode.USER_SUBSCRIBE_DOWN_NOT_PAY, appVersion, appVersion, userUid, storeId);
//                }
//            }
        } catch (Throwable e) {
            // 手動RollBack
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ServiceResult<Void>(CommonCode.SERVICE_FAIL, null, e);
        }

        return new ServiceResult<Void>(CommonCode.SUCCESS, null);
    }

    /**
     * 取出InstallApkId
     * 
     * @return long : downloadId
     */
    long getInstallApkId() {
        Long downloadId = (Long) sqlMapClientTemplate.queryForObject("UserInstallApp.getInstallApkId");
        return downloadId != null ? downloadId : 0;
    }

    /**
     * 新增userSearchLog
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserSearchLog(ApplicationService self, String keyword, String imei,
        String userId, String userUid) {
        UserSearchLog userSearchLog = new UserSearchLog();
//        userSearchLog.setIccid(iccid);
        userSearchLog.setImei(imei);
        userSearchLog.setKeyword(keyword);
        userSearchLog.setUserId(userId);
        userSearchLog.setUserUid(userUid);
        sqlMapClientTemplate.insert("Application.insertUserSearchLog", userSearchLog);
    }

    /**
     * 紀錄UserInstallApp
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserInstallApp(UserInstallApp userInstallApp) {
        sqlMapClientTemplate.insert("UserInstallApp.insertUserInstallApp", userInstallApp);
    }

    /**
     * 紀錄UserInstallAppLog
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserInstallAppLog(UserInstallApp userInstallApp) {
        sqlMapClientTemplate.insert("UserInstallApp.insertUserInstallAppLog", userInstallApp);
    }

    /**
     * 儲存使用者使用APK資訊
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserApkBehavior(long downloadId, String appId, String userId, String userUid, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downloadId", Long.valueOf(downloadId));
        map.put("appId", appId);
        map.put("userId", userId);
        map.put("userUid", userUid);
        map.put("storeId", storeId);
        sqlMapClientTemplate.insert("UserApkBehavior.insertUserApkBehavior", map);
    }
    /**
     * 儲存用户下载信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Long insertUserDownloadInfo(VMemDownloadInfo info) {
    	 return (Long)sqlMapClientTemplate.insert("v_mem_download_info.ibatorgenerated_insert",info);
    }
    /**
     * 更新使用者使用APK資訊的LAST_DOWNLOAD_ID/USER_ID資訊
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserApkBehaviorWithDownLoadIdAndUserId(long downloadId, String appId, String userId,
        String userUid, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downloadId", Long.valueOf(downloadId));
        map.put("appId", appId);
        map.put("userId", userId);
        map.put("userUid", userUid);
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("UserApkBehavior.updateUserApkBehaviorWithDownLoadIdAndUserId", map);
    }

    /**
     * 把userUid/imei使用者的App下載紀錄刪除，條件為AppId/iccid/imei
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIsDeleteInstallApp(String appId, String userUid, String imei, String mac, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("userUid", userUid);
        map.put("imei", imei);
        map.put("mac", mac);
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("UserInstallApp.updateIsDeleteInstallApp", map);
    }

    /**
     * 更新USER_GET_APPVERSION_LOG的downloadId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateGetAppvLog(long downloadId, String vlogId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("downloadId", downloadId);
        map.put("vlogId", vlogId);
        sqlMapClientTemplate.update("GetAppversionLog.updateGetAppvLog", map);
    }

    /**
     * 累加APPLICATION內總下載次數TOTAL_DOWNLOAD_TIMES
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAppTotalDownload(String appId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("Application.updateAppTotalDownload", map);
    }

    /**
     * 累加APPLICATION_VERSIONS內總下載次數TOTAL_DOWNLOAD_TIMES
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAppVersionTotalDownload(String appId, int versionId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appId", appId);
        map.put("versionId", Integer.valueOf(versionId));
        map.put("storeId", storeId);
        sqlMapClientTemplate.update("Application.updateAppVersionTotalDownload", map);
    }

    /**
     * 更新USER_INSTALL_APK的PRICE_TYPE，條件為DOWNLOAD_ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateInstallAppOfPriceType(String priceType, long downloadId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("priceType", priceType);
        map.put("downloadId", Long.valueOf(downloadId));
        
        sqlMapClientTemplate.update("UserInstallApp.updateInstallAppOfPriceType", map);
    }

    /**
     * 檢查是否有Apk，以pkg/appVersion為條件
     */
    boolean hasApplicationByPkgIdAndVersion(String pkgId, int appVersion) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pkgId", pkgId);
        if (appVersion != 0) {
            map.put("versionId", new Integer(appVersion));
        }
        Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.hasApplication", map);
        return returnCount != null && returnCount.intValue() > 0;
    }

    /**
     * 檢查是否有Apk，以pkg為條件，不判斷最新版本和是否上下架
     */
    boolean hasApplicationByPkgId(String pkgId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pkgId", pkgId);
        Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("Application.hasApplication", map);
        return returnCount != null && returnCount.intValue() > 0;
    }

    /**
     * 取得UserApkBehavior總筆數，以USER_UID和APP_ID為條件
     * 
     * @return int : UserApkBehavior數量
     */
    int getUserApkBehaviorCount(String userUid, String appId, BigDecimal storeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userUid", userUid);
        map.put("appId", appId);
        map.put("storeId", storeId);
        Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("UserApkBehavior.getUserApkBehaviorCount", map);
        return returnCount != null ? returnCount.intValue() : 0;
    }

    /**
     * 取得SERVICE_ID，以APP_ID/VERSION_ID為條件
     */
    String getServiceId(String pkgId, Integer versionId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pkgId", pkgId);
        map.put("versionId", versionId == null ? new Integer(0) : versionId);
        return (String) sqlMapClientTemplate.queryForObject("Application.getServiceId", map);
    }

    /**
     * 取得Apk金額，以app_id為條件
     */
    Amount getAppAmount(String pkgId, String store) {
    	StoreInfo si = getStoreInfoByPKG(store);
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("pkgId", pkgId);
        map.put("storeId", si.getId());
        return (Amount) sqlMapClientTemplate.queryForObject("Application.getAppAmount", map);
    }

    private String getSupportDPI(int wpx, int hpx){
    	// 組解析度，(寬*高)，較小者擺在前面
    	String supportDPI;
        if (wpx <= hpx)
        	supportDPI = new StringBuilder().append(wpx).append("*").append(hpx).toString();
        else
        	supportDPI = new StringBuilder().append(hpx).append("*").append(wpx).toString();
        return supportDPI;
    }
    
    /**
     * 取得application，以APP_ID為條件
     */
    public Application getApplication(String appId, Long storeId){
    	HashMap<String ,Object> hm = new HashMap<String, Object>();
    	hm.put("appId", appId);
    	hm.put("storeId", storeId);
    	return (Application) sqlMapClientTemplate.queryForObject("Application.getApplication",hm);
    }
    
    /**
     * 取得USER_INSTALL_APK，以DOWNLOAD_ID為條件
     */
    public UserInstallApp getUserInstallAppByDownloadId(Long downloadId){
    	HashMap<String,Object> hm = new HashMap<String, Object>();
    	hm.put("downloadId", downloadId);
    	return (UserInstallApp) sqlMapClientTemplate.queryForObject("UserInstallApp.getUserInstallAppByDownloadId",hm);
    }
    
    /**
     * 取得Download_Id
     */
    public Long getUserInstallAppByDownloadId(String pkgId, String userId, String imei, Long storeId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("pkgId", pkgId);
    	hm.put("userId", userId);
    	hm.put("imei", imei);
    	hm.put("storeId", storeId);
    	return (Long) sqlMapClientTemplate.queryForObject("UserInstallApp.getDownloadId",hm);
    }
    /**
     * 取得使用者資訊，條件為userId
     */
    public VMemberInfo getUserByLoginId(String userId) {
    	VMemberInfo map =new VMemberInfo();
    	map.setLoginId(userId);
        return (VMemberInfo) sqlMapClientTemplate.queryForObject("v_member_info.ibatorgenerated_selectByLoginid", map);
    }
    public Integer updateInstallAppOfStatusByDownloadId(Integer status, Long downloadId, Long storeId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("status", status);
    	hm.put("downloadId", downloadId);
    	hm.put("storeId", storeId);
    	return sqlMapClientTemplate.update("UserInstallApp.updateInstallAppOfStatusByDownloadId",hm);
    }
    
    public Integer updateIsCanceledWithUserIdAndAppId(String userId, String appId, Date endTime, Long storeId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("userId", userId);
    	hm.put("appId", appId);
    	hm.put("endTime", endTime);
    	hm.put("storeId", storeId);
    	return sqlMapClientTemplate.update("UserSubscribe.updateIsCanceledWithUserIdAndAppId",hm);
    }
    
    public Integer updateStatus(Integer status,String appId, String userId, String imei, Long downloadId, Long StoreId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("appId", appId);
    	hm.put("userId", userId);
    	hm.put("imei", imei);
    	hm.put("downloadId",downloadId);
    	hm.put("StoreId",StoreId);
    	hm.put("status",status);
    	return sqlMapClientTemplate.update("UserInstallApp.updateStatus",hm);
    }
    
    public Integer updateStatusBy(Integer status,String appId, String userId, String imei, Long downloadId, Long storeId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("appId", appId);
    	hm.put("userId", userId);
    	hm.put("imei", imei);
    	hm.put("downloadId",downloadId);
    	hm.put("storeId",storeId);
    	hm.put("status",status);
    	return sqlMapClientTemplate.update("UserInstallApp.updateStatusByDownloadEquals",hm);
    }
    
    public Integer updateReasonIdByDownloadId(int reasonId, Long downloadId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("reasonId", reasonId);
    	hm.put("downloadId",downloadId);
    	return sqlMapClientTemplate.update("UserInstallApp.updateReasonIdByDownloadId",hm);
    }
    
    public Integer updateStatusAndDelete(Integer status, String appId, String userId, String imei, Long downloadId, Long storeId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("status", status);
    	hm.put("appId",appId);
    	hm.put("userId", userId);
    	hm.put("imei",imei);
    	hm.put("downloadId",downloadId);
    	hm.put("storeId",storeId);
    	return sqlMapClientTemplate.update("UserInstallApp.updateStatusAndDelete",hm);
    }
    
    public Integer updateIsCanceledBy(String userId, String appId, Long storeId){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("userId", userId);
    	hm.put("appId", appId);
    	hm.put("storeId", storeId);
    	return sqlMapClientTemplate.update("UserSubscribe.updateIsCanceledBy",hm);
    }
    
    public StoreInfo getStoreInfoByPKG(String store) {
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("pkg", store);
    	return (StoreInfo)sqlMapClientTemplate.queryForObject("StoreInfo.getStoreInfo",hm);
    }
}