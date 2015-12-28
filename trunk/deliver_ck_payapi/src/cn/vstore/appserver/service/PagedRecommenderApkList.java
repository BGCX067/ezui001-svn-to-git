package cn.vstore.appserver.service;

import cn.vstore.appserver.model.RecommenderApkList;

/**
 * @version $Id: PagedRecommenderApkList.java
 */
public class PagedRecommenderApkList extends PagedDatas<RecommenderApkList> {
	public Integer total;
	
	public PagedRecommenderApkList(boolean pageEnd, RecommenderApkList[] recommenderApkLists, Integer total) {
        super(pageEnd, recommenderApkLists);
        this.total = total;
    }
	
}
