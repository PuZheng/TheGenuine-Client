package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ListView;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.views.CustomActionBar;
import com.puzheng.the_genuine.views.NavBar;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-03.
 */
public class SPUListActivity extends ActionBarActivity implements ActionBar.TabListener {
    private int mCategoryId;
    private String[] orderByDescs;
    private String[] orderByStrs;
    private ProductListFragmentPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private boolean inSearchMode;
    private String mQuery;
    private CustomActionBar customActionBar;
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
                SPUListActivity.this.finish();
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
        customActionBar = CustomActionBar.setCustomerActionBar(getActionBar(),
                SPUListActivity.this);
        customActionBar.setUpButtonEnable(true);
        mCategoryId = getIntent().getIntExtra("category_id", Constants.INVALID_ARGUMENT);
        mQuery = getIntent().getStringExtra(SearchManager.QUERY);
        inSearchMode = mCategoryId == Constants.INVALID_ARGUMENT;
        setContentView(R.layout.activity_product_list);
        orderByDescs = getResources().getStringArray(R.array.order_by_list);
        orderByStrs = getResources().getStringArray(R.array.order_by_str_list);
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
            customActionBar.setTitle(mQuery);
            customActionBar.setSubtitle(getString(R.string.search_result));
        } else {
            customActionBar.setTitle("360真品");
            customActionBar.setSubtitle(getIntent().getStringExtra("categoryName"));
        }
    }

    public class ProductListFragmentPageAdapter extends FragmentPagerAdapter {
        public ProductListFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return orderByDescs.length;
        }

        @Override
        public Fragment getItem(int i) {
            if (inSearchMode) {
                return ProductListFragmentByName.newInstance(orderByStrs[i], mQuery);
            } else {
                return ProductListFragmentByCategory.newInstance(orderByStrs[i], mCategoryId);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return orderByDescs[position];
        }
    }
}

abstract class ProductListFragment extends ListFragment {
    private String orderBy;

    ProductListFragment(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Recommendation recommendation = (Recommendation) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), SPUActivity.class);
        intent.putExtra(Constants.TAG_SPU_ID, recommendation.getSPUId());
        getActivity().startActivity(intent);
    }
}

class ProductListFragmentByName extends ProductListFragment {
    private String mQuery;

    ProductListFragmentByName(String orderBy, String query) {
        super(orderBy);
        this.mQuery = query;
    }

    public static ProductListFragment newInstance(String orderBy, String query) {
        return new ProductListFragmentByName(orderBy, query);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSPUListInterface queryClass = new GetSPUListByName(this.getActivity(), mQuery);
        new GetSPUListTask(this, queryClass).execute();
    }
}

class ProductListFragmentByCategory extends ProductListFragment {
    private int mCategoryId;

    private ProductListFragmentByCategory(String orderBy, int categoryId) {
        super(orderBy);
        this.mCategoryId = categoryId;
    }

    public static ProductListFragment newInstance(String orderBy, int categoryId) {
        return new ProductListFragmentByCategory(orderBy, categoryId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetSPUListInterface queryClass = new GetSPUListByCategory(this.getActivity(), mCategoryId);
        new GetSPUListTask(this, queryClass).execute();
    }
}
