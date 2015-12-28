package cn.vstore.appserver.service;

import cn.vstore.appserver.model.MyDownloadApplication;

/**
 * @version $Id: PagedMyDownloadApplications.java 6758 2010-12-20 06:38:39Z
 *          yhwang $
 */
public class PagedMyDownloadApplications extends PagedDatas<MyDownloadApplication> {

    public PagedMyDownloadApplications(boolean pageEnd, MyDownloadApplication[] applications) {
        super(pageEnd, applications);
    }

}
