package cn.com.vapk.vstore.client.search;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.net.Uri;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	
	public static final String AUTHORITY = "cn.com.vapk.vstore.client.provider.SuggestionProvider";
	public static final int MODE = DATABASE_MODE_QUERIES;

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/"
			+ SearchManager.SUGGEST_URI_PATH_QUERY);

	public SearchSuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
}