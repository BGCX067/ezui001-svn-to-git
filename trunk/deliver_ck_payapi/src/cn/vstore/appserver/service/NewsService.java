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

import cn.vstore.appserver.model.News;
import cn.vstore.appserver.service.ResultCode.CommonCode;

@Service("NewsService")
public class NewsService {
	
	private static final Logger logger = LoggerFactory.getLogger(RecommenderService.class);
	
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Autowired
	private ConstantService constantService;
	
	public ServiceResult<PagedNews> getNews(BigDecimal storeId, int pageNo, int psize){
		logger.info("storeId = " + storeId + "pageNo = " + pageNo + "psize = " + psize);
		try {
			int pageSize = 0;
			if(psize!=0){
				pageSize = psize;
			}else{
				pageSize = constantService.getNewsPageSize();
			}
			int startNo = (pageNo - 1) * pageSize;
			int endNo = pageNo * pageSize;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("storeId", storeId);
			map.put("startNo", new Integer(startNo));
			map.put("endNo", new Integer(endNo));
//		最新消息 總筆數
			Integer returnCount = (Integer)sqlMapClientTemplate.queryForObject("News.getNewsCount", map);
			int newsCount = returnCount!=null?returnCount.intValue():0;
			if(newsCount==0){
				PagedNews pagedNews = new PagedNews(true, new News[0], newsCount);
				return new ServiceResult<PagedNews>(CommonCode.SUCCESS, pagedNews);
			}
//		最新消息
			List<News> newsList = sqlMapClientTemplate.queryForList("News.getNews", map);
			
			boolean pageEnd = pageNo * pageSize >= newsCount || newsList.size()==0; 
			News[] newsArray = newsList.toArray(new News[newsList.size()]);
			PagedNews pagedNews = new PagedNews(pageEnd, newsArray, newsCount);
			return new ServiceResult<PagedNews>(CommonCode.SUCCESS, pagedNews);
		} catch (Throwable e) {
			return new ServiceResult<PagedNews>(CommonCode.SERVICE_FAIL, null, e);
		}
	}
}
