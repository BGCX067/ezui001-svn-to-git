package tw.com.sti.store.api.vo;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

final public class CommentsRet extends BaseRet {

	private boolean pageEnd;
	private Comment[] comments;

	public CommentsRet(JSONObject json) {
		super(json);
		pageEnd = json.optBoolean("pageEnd");
		comments = Comment.parseComments(json.optJSONArray("comments"));
	}

	public boolean isPageEnd() {
		return pageEnd;
	}

	public Comment[] getComments() {
		return comments;
	}

	public static final class Comment implements Serializable {

		private static final long serialVersionUID = -1848184197120373292L;
		
		private String id;
		private String poster;
		private String date;
		private float rating;
		private String content;

		final static Comment[] parseComments(JSONArray commentsData) {
			if (commentsData == null || commentsData.length() == 0) {
				return new Comment[0];
			}
			Comment[] comments;
			int count = commentsData.length();
			ArrayList<Comment> commentsList = new ArrayList<Comment>(count);
			for (int i = 0; i < count; i++) {
				JSONObject commentData = commentsData.optJSONObject(i);
				Comment comment = parseComment(commentData);
				if (comment != null)
					commentsList.add(comment);
			}
			comments = commentsList.toArray(new Comment[commentsList.size()]);
			return comments;
		}

		final static Comment parseComment(JSONObject json) {
			Comment ret = new Comment();
			try {
				ret.id = json.optString("id");
				ret.poster = json.getString("poster");
				ret.date = json.getString("date");
				ret.rating = (float) json.getDouble("rating");
				ret.content = json.getString("content");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(json, Comment.class,
						"Comment JSON invalid.", e);
			}
			return ret;
		}

		private Comment() {

		}

		public String getId() {
			return id;
		}

		public String getPoster() {
			return poster;
		}

		public String getDate() {
			return date;
		}

		public float getRating() {
			return rating;
		}

		public String getContent() {
			return content;
		}
	}
}