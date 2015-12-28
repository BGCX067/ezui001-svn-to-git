package cn.vstore.appserver.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @version $Id: PosterComment.java 6892 2010-12-27 10:49:50Z yellow $
 */
public class PosterComment extends Comment {

    private static final long serialVersionUID = 1748271156696203778L;
    private String poster;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
