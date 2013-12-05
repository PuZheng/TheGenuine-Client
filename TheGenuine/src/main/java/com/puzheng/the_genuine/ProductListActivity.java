package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.views.NavBar;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class ProductListActivity extends ActionBarActivity implements ActionBar.TabListener {
    private int mCategoryId;
    private String[] sortableString;
    private ProductListFragmentPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private boolean inSearchMode;
    private String mQuery;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                ProductListActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
        mCategoryId = getIntent().getIntExtra("category_id", Constants.INVALID_ARGUMENT);
        mQuery = getIntent().getStringExtra(SearchManager.QUERY);
        inSearchMode = mCategoryId == Constants.INVALID_ARGUMENT;
        setContentView(R.layout.activity_product_list);
        sortableString = getResources().getStringArray(R.array.short_list);
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        addTabs();
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
        if (inSearchMode) {
            actionBar.setTitle(mQuery);
            actionBar.setSubtitle(R.string.search_result);
        } else {
            actionBar.setTitle("360真品");
            actionBar.setSubtitle(getIntent().getStringExtra("categoryName"));
        }
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
            if (inSearchMode) {
                return ProductListFragmentByName.newInstance(i, mQuery);
            } else {
                return ProductListFragmentByCategory.newInstance(i, mCategoryId);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sortableString[position];
        }
    }
}

abstract class ProductListFragment extends ListFragment {
    private int sortIdx;

    ProductListFragment(int sortIdx) {
        this.sortIdx = sortIdx;
    }

    public int getSortIdx() {
        return sortIdx;
    }

}

class ProductListFragmentByName extends ProductListFragment {
    private String mQuery;

    ProductListFragmentByName(int sortIdx, String query) {
        super(sortIdx);
        this.mQuery = query;
    }

    public static ProductListFragment newInstance(int sortIdx, String query) {
        return new ProductListFragmentByName(sortIdx, query);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetProductListInterface queryClass = new GetProductListByName(this.getActivity(), mQuery);
        new GetProductListTask(this, queryClass).execute();
    }
}

class ProductListFragmentByCategory extends ProductListFragment {
    private int mCategoryId;

    private ProductListFragmentByCategory(int sortIdx, int categoryId) {
        super(sortIdx);
        this.mCategoryId = categoryId;
    }

    public static ProductListFragment newInstance(int sortIdx, int categoryId) {
        return new ProductListFragmentByCategory(sortIdx, categoryId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetProductListInterface queryClass = new GetProductListByCategory(this.getActivity(), mCategoryId);
        new GetProductListTask(this, queryClass).execute();
    }
}
