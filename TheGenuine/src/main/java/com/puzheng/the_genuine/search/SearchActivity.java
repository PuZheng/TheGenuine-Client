package com.puzheng.the_genuine.search;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import com.puzheng.the_genuine.ProductActivity;
import com.puzheng.the_genuine.R;
import com.puzheng.the_genuine.RecommendationListAdapter;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.views.NavBar;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class SearchActivity extends FragmentActivity implements ActionBar.TabListener {
    private String[] sortableString;
    private SearchView mSearchView;
    private ResultFragmentPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    //TODO 删除搜索记录放在设置里
    private Button mClearButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.expandActionView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            setSearchView(menuItem);
        }
        return true;
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
        ListFragment fragment = (ListFragment) getSupportFragmentManager().getFragments().get(mViewPager.getCurrentItem());
        RecommendationListAdapter listAdapter = (RecommendationListAdapter) fragment.getListAdapter();
        if (listAdapter != null) {
            listAdapter.sort(fragment.getArguments().getInt(SearchResultListFragment.ARG_SECTION_NUMBER));
            listAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.search_product);
        sortableString = getResources().getStringArray(R.array.short_list);
        mClearButton = (Button) findViewById(R.id.clear_recent_search);
        setClearButton(mClearButton);
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void addTabs() {
        final ActionBar actionBar = getActionBar();
        mPageAdapter = new ResultFragmentPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOffscreenPageLimit(mPageAdapter.getCount());
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < mPageAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(mPageAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    private void doSearch(String query) {
        if (getActionBar().getTabCount() == 0) {
             addTabs();
        }
        if (mClearButton != null) {
            mClearButton.setVisibility(View.GONE);
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        new SearchTask(fragments).execute(query);
        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
            mSearchView.clearFocus();
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Intent productIntent = new Intent(SearchActivity.this, ProductActivity.class);
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
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryRefinementEnabled(true);
    }

    private void storeRecentSearch(String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(query, null);
    }

    class SearchTask extends AsyncTask<String, Void, List<Recommendation>> {
        private List<Fragment> mFragmentList;

        public SearchTask(List<Fragment> fragments) {
            this.mFragmentList = fragments;
        }

        @Override
        protected List<Recommendation> doInBackground(String... params) {
            try {
                String query = params[0];
                return WebService.getInstance(SearchActivity.this).getRecommendationsByName(query);
            } catch (Exception ignore) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Recommendation> list) {
            if (list != null) {
                boolean first = true;
                for (Fragment fragment : mFragmentList) {
                    RecommendationListAdapter listAdapter = new RecommendationListAdapter(list, SearchActivity.this);
                    ListFragment listFragment = (ListFragment) fragment;
                    listFragment.setListAdapter(listAdapter);
                    if (first) {
                        listAdapter.sort(fragment.getArguments().getInt(SearchResultListFragment.ARG_SECTION_NUMBER));
                        listAdapter.notifyDataSetChanged();
                    }
                    first = false;
                }
            } else {
                for (Fragment fragment : mFragmentList) {
                    ListFragment listFragment = (ListFragment) fragment;
                    listFragment.setEmptyText(getString(R.string.search_no_result_found));
                }
            }
        }
    }

    public class ResultFragmentPageAdapter extends FragmentPagerAdapter {
        public ResultFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return sortableString.length;
        }

        @Override
        public Fragment getItem(int i) {
            return SearchResultListFragment.newInstance(i);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sortableString[position];
        }
    }
}

class SearchResultListFragment extends ListFragment {
    public static String ARG_SECTION_NUMBER = "section_number";

    public static SearchResultListFragment newInstance(int sortIdx) {
        SearchResultListFragment result = new SearchResultListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sortIdx);
        result.setArguments(args);
        return result;
    }

}