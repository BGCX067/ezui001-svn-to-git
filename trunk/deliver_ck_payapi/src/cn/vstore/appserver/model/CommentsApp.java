package cn.vstore.appserver.model;

public class CommentsApp extends Application{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6002817729505220754L;

	private String comments;
	private int userrank;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getUserrank() {
		return userrank;
	}

	public void setUserrank(int userrank) {
		this.userrank = userrank;
	}
}
