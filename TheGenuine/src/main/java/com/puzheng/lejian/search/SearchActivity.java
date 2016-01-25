package com.puzheng.lejian.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;

import com.puzheng.lejian.R;
import com.puzheng.lejian.SPUActivity;
import com.puzheng.lejian.SPUListActivity;


/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-05.
 */
public class SearchActivity extends Activity {
    //TODO 删除搜索记录放在设置里
    private Button mClearButton;
    private SearchView mSearchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.expandActionView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            setSearchView(menuItem);
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    SearchActivity.this.finish();
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mClearButton = (Button) findViewById(R.id.clear_recent_search);
        setClearButton(mClearButton);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void doSearch(String query) {
        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
            mSearchView.clearFocus();
        }
        Intent searchIntent = new Intent(SearchActivity.this, SPUListActivity.class);
        searchIntent.putExtra(SearchManager.QUERY, query);
        startActivity(searchIntent);
        this.finish();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Intent productIntent = new Intent(SearchActivity.this, SPUActivity.class);
            productIntent.setData(intent.getData());
            startActivity(productIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
            storeRecentSearch(query);
        }
    }

    private void setClearButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchActivity.this, SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
                suggestions.clearHistory();
            }
        });
    }

    private void setSearchView(MenuItem menuItem) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setInputType(InputType.TYPE_CLASS_TEXT);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryRefinementEnabled(true);
    }

    private void storeRecentSearch(String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(query, null);
    }
}