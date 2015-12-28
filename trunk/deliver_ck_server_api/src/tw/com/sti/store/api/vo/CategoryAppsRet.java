package tw.com.sti.store.api.vo;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.store.api.ApiDataInvalidException;

public class CategoryAppsRet extends AppsRet {

    private String categoryId;
    private String categoryTitle;

    public CategoryAppsRet(JSONObject json) {
        super(json);
        try {
            JSONObject categoryJo = json.optJSONObject("category");
            if (categoryJo == null)
                return;
            categoryId = categoryJo.getString("id");
            categoryTitle = categoryJo.getString("title");
        } catch (JSONException e) {
            throw new ApiDataInvalidException(json, CategoryAppsRet.class,
                    "CategoryAppsRet JSON invalid.", e);
        }
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }
}
