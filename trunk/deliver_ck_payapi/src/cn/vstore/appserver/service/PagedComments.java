package cn.vstore.appserver.service;

import cn.vstore.appserver.model.PosterComment;

/**
 * @version $Id: PagedComments.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class PagedComments extends PagedDatas<PosterComment> {

	public int total;
    public PagedComments(boolean pageEnd, PosterComment[] posterComments, int total) {
        super(pageEnd, posterComments);
        this.total = total;
    }

}
