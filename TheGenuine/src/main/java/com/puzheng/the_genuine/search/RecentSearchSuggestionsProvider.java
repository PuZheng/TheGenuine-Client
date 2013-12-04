package com.puzheng.the_genuine.search;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class RecentSearchSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.puzheng.the_genuine.search.RecentSearchSuggestionProvider";

    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    private static final RecentSearchSuggestionsProvider instance = new RecentSearchSuggestionsProvider();

    public static RecentSearchSuggestionsProvider getInstance() {
        return instance;
    }

}
