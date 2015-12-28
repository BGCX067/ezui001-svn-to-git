package cn.vstore.appserver.service;

import cn.vstore.appserver.model.Application;

/**
 * @version $Id: PagedApplications.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class PagedApplications extends PagedDatas<Application> {
	public int total; 
	
//    public PagedApplications(boolean pageEnd, Application[] applications) {
//        super(pageEnd, applications);
//    }
    
    public PagedApplications(boolean pageEnd, Application[] applications, int total) {
        super(pageEnd, applications);
        this.total = total;
    }
}
