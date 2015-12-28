package cn.vstore.appserver.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.model.UserDownloadTimes;
import cn.vstore.appserver.model.UserFeedApp;
import cn.vstore.appserver.model.UserFeedCategory;
import cn.vstore.appserver.model.UserFeedSpace;
import cn.vstore.appserver.model.UserFeedbackBehavior;
import cn.vstore.appserver.model.UserMenuFeed;
import cn.vstore.appserver.model.UserParameter;
import cn.vstore.appserver.model.UserSearchwords;


@Service("FeedbackBehaviorService")
public class FeedbackBehaviorService {
	
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	
	public Long insert(UserParameter parameter){
		 return (Long)sqlMapClientTemplate.insert("userParas.insertParaSelective",parameter);
	 }
	
	public Long insertBehavior(UserFeedbackBehavior behavior){
		 return (Long)sqlMapClientTemplate.insert("userParas.insertBehaviorSelective",behavior);
	 }
	
	public Long insertDownTimes(UserDownloadTimes downloadTimes){
		 return (Long)sqlMapClientTemplate.insert("userParas.insertDownTimesSelective",downloadTimes);
	 }
	
	public Integer getTimesByWords(String keywords){
		return (Integer)sqlMapClientTemplate.queryForObject("userParas.selectCountByWords",keywords);
	}
	
	public Integer updateTimesByKeyWords(String keywords, Integer times){
    	HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("keywords", keywords);
    	hm.put("times", times);
    	return sqlMapClientTemplate.update("userParas.updateTimesByWords",hm);
    }
	
	public UserSearchwords selectSearchWordsByWords(String keywords){
		UserSearchwords searchwords = (UserSearchwords) sqlMapClientTemplate.queryForObject("userParas.selectSearchWordsByWords",keywords);
		return searchwords;
	}
	
	public Long insertSearchWords(UserSearchwords userSearchwords){
		return (Long)sqlMapClientTemplate.insert("userParas.insertSearchWordsSelective",userSearchwords);
	}
	
	public Long insertMenufeed(UserMenuFeed menuFeed){
		return (Long)sqlMapClientTemplate.insert("userParas.insertMenufeedSelective",menuFeed);
	}
	
	public Long insertFeedApp(UserFeedApp feedApp){
		return (Long)sqlMapClientTemplate.insert("userParas.insertFeedappsSelective",feedApp);
	}
	
	public Long insertFeedCategory(UserFeedCategory feedCategory){
		return (Long)sqlMapClientTemplate.insert("userParas.insertCategorysSelective",feedCategory);
	}
	
	public Long insertFeedSpace(UserFeedSpace feedSpace){
		return (Long)sqlMapClientTemplate.insert("userParas.insertSpacesSelective",feedSpace);
	}
}
