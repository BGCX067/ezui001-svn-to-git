package cn.vstore.appserver.service;

/**
 * @version $Id: PagedDatas.java 6892 2010-12-27 10:49:50Z yellow $
 */
abstract class PagedDatas<T> {

    public final boolean pageEnd;
    public final T[] datas;

    PagedDatas(boolean pageEnd, T[] datas) {
        super();
        this.pageEnd = pageEnd;
        this.datas = datas;
    }
}