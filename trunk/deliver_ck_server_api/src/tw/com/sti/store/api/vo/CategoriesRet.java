package tw.com.sti.store.api.vo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;
import tw.com.sti.store.api.util.LangUtils;

final public class CategoriesRet extends BaseRet {

	private Category[] categories;

	public CategoriesRet(JSONObject json) {
		super(json);
		JSONArray categoriesData = json.optJSONArray("categories");
		categories = Category.parseCategories(categoriesData);
	}

	public Category[] getCategories() {
		return categories;
	}

	final static public class Category {

		private String id;
		private String title;
		private String icon;
		private boolean hasNewApp;
		private int newAppCount;

		final static Category[] parseCategories(JSONArray categoriesData) {
			if (categoriesData == null || categoriesData.length() == 0) {
				return new Category[0];
			}

			Category[] categories = null;
			int count = categoriesData.length();
			ArrayList<Category> categoriesList = new ArrayList<CategoriesRet.Category>(
					count);
			for (int i = 0; i < count; i++) {
				JSONObject categoryData = categoriesData.optJSONObject(i);
				Category category = Category.parseCategory(categoryData);
				if (category != null)
					categoriesList.add(category);
			}
			categories = categoriesList.toArray(new Category[categoriesList
					.size()]);
			return categories;
		}

		final static Category parseCategory(JSONObject categoryData) {
			if (categoryData == null)
				return null;
			Category ret = new Category();
			try {
				ret.id = categoryData.getString("id");
				ret.title = categoryData.getString("title");
				ret.icon = categoryData.getString("icon");
				if (LangUtils.isBlank(ret.icon)) {
					ret.icon = null;
				}
				ret.hasNewApp = categoryData.optBoolean("hasNewApp");
				ret.newAppCount = categoryData.optInt("newAppCount");
			} catch (JSONException e) {
				throw new ApiDataInvalidException(categoryData, Category.class,
						"Category JSON invalid.", e);
			}
			return ret;
		}

		private Category() {
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public String getIcon() {
			return icon;
		}

		public boolean isHasNewApp() {
			return hasNewApp;
		}

		public int getNewAppCount() {
			return newAppCount;
		}

	};

}
