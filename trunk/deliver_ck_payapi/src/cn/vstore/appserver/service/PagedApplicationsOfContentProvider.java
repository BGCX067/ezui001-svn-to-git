package cn.vstore.appserver.service;

import cn.vstore.appserver.model.Application;

/**
 * @version $Id: PagedApplicationsOfContentProvider.java 6758 2010-12-20
 *          06:38:39Z yhwang $
 */
public class PagedApplicationsOfContentProvider extends PagedApplications {

    public final String provider;

    public PagedApplicationsOfContentProvider(String provider, boolean pageEnd,
            Application[] applications, int total) {
        super(pageEnd, applications, total);
        this.provider = provider;
    }

}
