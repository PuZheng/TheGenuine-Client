package com.puzheng.the_genuine.search;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class ProductSearchSuggestionsProvider extends ContentProvider {
    public final static String AUTHORITY = "com.puzheng.the_genuine.search.RecentSearchSuggestionProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/product");
    public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/vnd.puzheng.the_genuine.search.searchproduct";
    public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/vnd.puzheng.the_genuine.search.searchproduct";
    private static final int SEARCH_PRODUCT = 0;
    private static final int GET_PRODUCT = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "product", SEARCH_PRODUCT);
        matcher.addURI(AUTHORITY, "product/#", GET_PRODUCT);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_PRODUCT:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_PRODUCT:
                return getProduct(uri);
            case REFRESH_SHORTCUT:
                return refreshShortcut(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    private Cursor getProduct(Uri uri) {
        return null;
    }

    private Cursor getSuggestions(String query) {
        return null;
    }

    private Cursor refreshShortcut(Uri uri) {
        return null;
    }

    private Cursor search(String query) {
        return null;
    }
}
