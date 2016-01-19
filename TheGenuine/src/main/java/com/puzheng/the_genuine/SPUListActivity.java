package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.puzheng.the_genuine.image_utils.ImageFetcher;
import com.puzheng.the_genuine.model.SPUType;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.store.SPUStore;
import com.puzheng.the_genuine.store.SPUTypeStore;

import java.util.HashMap;
import java.util.Map;

import static com.puzheng.the_genuine.SPUListFragment.*;

public class SPUListActivity extends ActionBarActivity implements ActionBar.TabListener, ImageFetcherInteface {
    private SPUType spuType;
    private String[] orderByDescs;
    private String[] orderByStrs;
    private SPUListFragmentPageAdapter pageAdapter;
    private ViewPager viewPager;
    private boolean inSearchMode;
    private String query;
    private ImageFetcher imageFetcher;

    public ImageFetcher getImageFetcher() {
        return imageFetcher;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spu_list, menu);
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
            case android.R.id.home:
                SPUListActivity.this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        imageFetcher = ImageFetcher.getImageFetcher(this, this.getResources().getDimensionPixelSize(R.dimen
                .image_view_list_item_width), 0.25f);

        spuType = getIntent().getParcelableExtra(SPUTypeListActivity.SPU_TYPE);
        query = getIntent().getStringExtra(SearchManager.QUERY);
        inSearchMode = spuType == null;
        setContentView(R.layout.activity_spu_list);
        orderByDescs = getResources().getStringArray(R.array.order_by_list);
        orderByStrs = getResources().getStringArray(R.array.order_by_str_list);
        viewPager = (ViewPager) findViewById(R.id.pager);
        addTabs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageFetcher.closeCache();
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
    }

    private void addTabs() {
        final ActionBar actionBar = getActionBar();
        pageAdapter = new SPUListFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(pageAdapter.getCount());
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < pageAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(pageAdapter.getPageTitle(i)).setTabListener(this));
        }
        if (inSearchMode) {
            actionBar.setTitle(query);
            actionBar.setSubtitle(R.string.search_result);
        } else {
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(spuType.getName());
        }
    }

    public class SPUListFragmentPageAdapter extends FragmentPagerAdapter {
        public SPUListFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return orderByDescs.length;
        }

        @Override
        public Fragment getItem(int i) {
            if (inSearchMode) {
                return SPUListFragmentByName.newInstance(orderByStrs[i], query);
            } else {
                Map<String, String> query = new HashMap<String, String>();
                query.put("spu_type_id", String.valueOf(spuType.getId()));
                query.put("sort_by", orderByStrs[i]);
                return new SPUListFragment.Builder().src(SPUStore.getInstance().fetchList(query)).build();
//                return SPUListFragmentByCategory.newInstance(orderByStrs[i], spuType.getId());
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return orderByDescs[position];
        }
    }
}


