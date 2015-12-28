package cn.vstore.appserver.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.SupportVersion;

/**
 * 
 * @version $Id: ConstantService.java 6892 2010-12-27 10:49:50Z yellow $
 */
@Service("ConstantService")
public class ConstantService {

    private static final String GET_CONSTANT_MAPPING_VERSION = "ConstanMapping.getConstantMappingVersion";

    private static final long EXPIRED_TIME = 60 * 30 * 1000;//30分鐘
    // Map<Level, Version>
    private static final Map<String, String> ANDROID_SDK_LEVEL_VERSION_MAPPING = Collections.synchronizedMap(new HashMap<String, String>());
    private static long cacheTime;
    private int applicationPageSize;
    private String hostPath;
    private int featureAppsShowCount;
    private int commentPageSize;
    private int recommenderApkListPageSize;
    private int newsPageSize;

    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    @Qualifier("configurationMessageSource")
    private MessageSource messageSource;

    /**
     * 取得支援版本，條件為手機傳入的aver
     * 
     * @param aver Android API Level
     * @return
     */
    public String getAndroidSdkVersionByLevel(int aver) {
        long currentTime = System.currentTimeMillis();
        if (currentTime > (cacheTime + EXPIRED_TIME)) {
            synchronized (ANDROID_SDK_LEVEL_VERSION_MAPPING) {
                if (!(currentTime > (cacheTime + EXPIRED_TIME)))
                    return ANDROID_SDK_LEVEL_VERSION_MAPPING.get(Integer.toString(aver));
                ANDROID_SDK_LEVEL_VERSION_MAPPING.clear();
                @SuppressWarnings("unchecked")
                List<SupportVersion> SupportVersionList = sqlMapClientTemplate.queryForList(GET_CONSTANT_MAPPING_VERSION);
                for (SupportVersion supportVersion : SupportVersionList) {
                    ANDROID_SDK_LEVEL_VERSION_MAPPING.put(supportVersion.getParam(), supportVersion.getValue());
                }
                cacheTime = currentTime;
            }
        }
        return ANDROID_SDK_LEVEL_VERSION_MAPPING.get(Integer.toString(aver));
    }

    int getApplicationPageSize() {
        if (applicationPageSize == 0) {
            applicationPageSize = NumberUtils.toInt(messageSource.getMessage("config.apps.page.show.count", null, Locale.ENGLISH), 10);
        }
        return applicationPageSize;
    }

    int getCommentPageSize() {
        if (commentPageSize == 0) {
            commentPageSize = NumberUtils.toInt(messageSource.getMessage("config.apps.page.show.count", null, Locale.ENGLISH), 10);
        }
        return commentPageSize;
    }

    String getHostPath() {
        if (StringUtils.isBlank(hostPath)) {
            hostPath = messageSource.getMessage("config.hostpath", null, Locale.ENGLISH);
        }
        return hostPath;
    }

    int getFeatureAppsShowCount() {
        if (featureAppsShowCount == 0) {
            featureAppsShowCount = NumberUtils.toInt(messageSource.getMessage("config.feature.show.count", null, Locale.ENGLISH), 30);
        }
        return featureAppsShowCount;
    }

	int getRecommenderApkListPageSize() {
		if(recommenderApkListPageSize == 0){
			recommenderApkListPageSize = NumberUtils.toInt(messageSource.getMessage("config.recommender.page.show.count", null, Locale.ENGLISH), 10);
		}
		return recommenderApkListPageSize;
	}

	int getNewsPageSize() {
		if(newsPageSize == 0){
			newsPageSize = NumberUtils.toInt(messageSource.getMessage("config.news.page.show.count", null, Locale.ENGLISH), 10);
		}
		return newsPageSize;
	}
}
