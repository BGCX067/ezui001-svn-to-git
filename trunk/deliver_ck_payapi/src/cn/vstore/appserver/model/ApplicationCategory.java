package cn.vstore.appserver.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ApplicationCategory implements Serializable{
	
	

    /**
	 * 
	 */
	private static final long serialVersionUID = 4628079149090216278L;
	
	
	private long id;
    private String title;
    private String icon;
    private int newAppCount;
    private String top1;
    private String top2;
    private String top3;
    
    

    public String getTop1() {
		return top1;
	}

	public void setTop1(String top1) {
		this.top1 = top1;
	}

	public String getTop2() {
		return top2;
	}

	public void setTop2(String top2) {
		this.top2 = top2;
	}

	public String getTop3() {
		return top3;
	}

	public void setTop3(String top3) {
		this.top3 = top3;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isHasNewApp() {
        return newAppCount > 0;
    }

    public int getNewAppCount() {
        return newAppCount;
    }

    public void setNewAppCount(int newAppCount) {
        this.newAppCount = newAppCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

}
