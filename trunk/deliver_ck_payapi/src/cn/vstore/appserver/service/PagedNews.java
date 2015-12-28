package cn.vstore.appserver.service;

import cn.vstore.appserver.model.News;

public class PagedNews extends PagedDatas<News> {
	public int total;
	public PagedNews(boolean pageEnd, News[] news, int total){
		super(pageEnd, news);
		this.total = total;
	}
}
