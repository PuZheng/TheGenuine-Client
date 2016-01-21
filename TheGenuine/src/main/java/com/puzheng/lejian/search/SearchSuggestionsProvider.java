package com.puzheng.lejian.search;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
    //TODO 从网络获取suggestions
    public final static String AUTHORITY = "com.puzheng.the_genuine.search.RecentSearchSuggestionProvider";

    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    private static final SearchSuggestionsProvider instance = new SearchSuggestionsProvider();

    public static SearchSuggestionsProvider getInstance() {
        return instance;
    }

}
