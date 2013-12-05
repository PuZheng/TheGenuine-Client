package com.puzheng.the_genuine;

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
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.search.SearchSuggestionsProvider;
import com.puzheng.the_genuine.views.NavBar;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class ProductListActivity extends FragmentActivity implements ActionBar.TabListener {
    private int mCategoryId;
    private String[] sortableString;
    private SearchView mSearchView;
    private ProductListFragmentPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    //TODO 删除搜索记录放在设置里
    private Button mClearButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        if (mCategoryId == Constants.INVALID_ARGUMENT) {
            menuItem.expandActionView();
        }
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
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryId = ProductListActivity.this.getIntent().getIntExtra("category_id", Constants.INVALID_ARGUMENT);

        setContentView(R.layout.activity_search);
        setTitle(R.string.search_product);
        sortableString = getResources().getStringArray(R.array.short_list);
        mClearButton = (Button) findViewById(R.id.clear_recent_search);
        setClearButton(mClearButton);
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mCategoryId == Constants.INVALID_ARGUMENT) {
            handleIntent(getIntent());
        } else {
            drawFragments();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void addTabs() {
        final ActionBar actionBar = getActionBar();
        mPageAdapter = new ProductListFragmentPageAdapter(getSupportFragmentManager());
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
        drawFragments();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            ProductListFragment productListFragment = (ProductListFragment) fragment;
            GetProductListByName queryClass = new GetProductListByName(ProductListActivity.this, query);
            new GetProductListTask(productListFragment, queryClass).execute();
        }
        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
            mSearchView.clearFocus();
        }
    }

    private void drawFragments() {
        if (getActionBar().getTabCount() == 0) {
            addTabs();
        }
        if (mClearButton != null) {
            mClearButton.setVisibility(View.GONE);
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Intent productIntent = new Intent(ProductListActivity.this, ProductActivity.class);
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
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(ProductListActivity.this, SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
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



    public class ProductListFragmentPageAdapter extends FragmentPagerAdapter {
        public ProductListFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return sortableString.length;
        }

        @Override
        public Fragment getItem(int i) {
            return ProductListFragment.newInstance(i, mCategoryId);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sortableString[position];
        }
    }
}

class ProductListFragment extends ListFragment {
    public static String ARG_SECTION_NUMBER = "section_number";
    private int mCategoryId;

    private ProductListFragment(int mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    private ProductListFragment() {
    }

    public static ProductListFragment newInstance(int sortIdx, int category_id) {
        ProductListFragment result;
        if (category_id != Constants.INVALID_ARGUMENT) {
            result = new ProductListFragment();
        } else {
            result = new ProductListFragment(category_id);
        }
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sortIdx);
        result.setArguments(args);
        return result;
    }

    public int getSortIdx() {
        return this.getArguments().getInt(ARG_SECTION_NUMBER, Constants.INVALID_ARGUMENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCategoryId != Constants.INVALID_ARGUMENT) {

            GetProductListByCategory queryClass = new GetProductListByCategory(this.getActivity(), mCategoryId);
            new GetProductListTask(this, queryClass).execute();
        }
    }
}

interface GetProductListInterface {
        List<Recommendation> getProductList(int sortIdx);
    }

    class GetProductListByName implements GetProductListInterface {
        private Context context;
        private String query;

        GetProductListByName(Context context, String param) {
            this.context = context;
            this.query = param;
        }

        @Override
        public List<Recommendation> getProductList(int sortIdx) {
            return WebService.getInstance(this.context).getProductListByName(this.query, sortIdx);
        }
    }

    class GetProductListByCategory implements GetProductListInterface {
        private Context context;
        private int category_id;

        GetProductListByCategory(Context context, int category_id) {
            this.context = context;
            this.category_id = category_id;
        }

        @Override
        public List<Recommendation> getProductList(int sortIdx) {
            return WebService.getInstance(this.context).getProductListByCategory(this.category_id, sortIdx);
        }
    }

 class GetProductListTask extends AsyncTask<Void, Void, List<Recommendation>> {
        private ProductListFragment mFragment;
        private GetProductListInterface mGetProductListClass;

        public GetProductListTask(ProductListFragment fragment, GetProductListInterface queryClass) {
            this.mFragment = fragment;
            this.mGetProductListClass = queryClass;
        }

        @Override
        protected List<Recommendation> doInBackground(Void... params) {
            int sortIdx = mFragment.getSortIdx();
            if (sortIdx != Constants.INVALID_ARGUMENT) {
                return this.mGetProductListClass.getProductList(sortIdx);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Recommendation> list) {
            if (list != null) {
                ProductListAdapter listAdapter = new ProductListAdapter(list, mFragment.getActivity());
                mFragment.setListAdapter(listAdapter);
            } else {
                mFragment.setEmptyText(mFragment.getString(R.string.search_no_result_found));
            }
        }
    }