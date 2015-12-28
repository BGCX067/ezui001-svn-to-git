/**
 *
 */
package cn.vstore.appserver.model;

import java.util.Comparator;

public class MyDownloadComparator implements Comparator<MyDownloadApplication> {

    @Override
    public int compare(MyDownloadApplication o1, MyDownloadApplication o2) {
        int cop = 0;
        if (o1.getSortTime().getTime() > o2.getSortTime().getTime()) {
            cop = -1;
        } else if (o1.getSortTime().getTime() < o2.getSortTime().getTime()) {
            cop = 1;
        }
        return cop;
    }

}
