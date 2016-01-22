package com.puzheng.lejian;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;
import com.puzheng.lejian.model.SPUType;
import com.puzheng.lejian.search.SearchActivity;
import com.puzheng.lejian.store.SPUStore;

import java.util.HashMap;
import java.util.Map;

public class SPUListActivity extends ActionBarActivity implements ActionBar.TabListener {
    private SPUType spuType;
    private String[] orderByDescs;
    private String[] orderByStrs;
    private MyPageAdapter pageAdapter;
    private ViewPager viewPager;
    private String kw;
    private AMapLocationClient locationClient;
    private double lat;
    private double lng;

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
        locationClient = new AMapLocationClient(this);
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (location != null) {
                    if (location.getErrorCode() == 0) {
                        lng = location.getLongitude();
                        lat = location.getLatitude();
                        Logger.i("located at " + String.format("%f,%f", lng, lat));
                        addTabs();
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Logger.e("AmapError", "location Error, ErrCode:"
                                + location.getErrorCode() + ", errInfo:"
                                + location.getErrorInfo());
                    }
                }
            }
        });
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        //设置是否只定位一次,默认为false
        aMapLocationClientOption.setOnceLocation(true);
        locationClient.setLocationOption(aMapLocationClientOption);
        locationClient.startLocation();

        spuType = getIntent().getParcelableExtra(SPUTypeListActivity.SPU_TYPE);
        kw = getIntent().getStringExtra(SearchManager.QUERY);
        setContentView(R.layout.activity_spu_list);
        orderByDescs = getResources().getStringArray(R.array.order_by_list);
        orderByStrs = getResources().getStringArray(R.array.order_by_str_list);
        viewPager = (ViewPager) findViewById(R.id.pager);

    }

    private void addTabs() {
        final ActionBar actionBar = getActionBar();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(pageAdapter.getCount());
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                ((SPUListFragment) pageAdapter.getItem(position)).init();
            }


        });

        for (int i = 0; i < pageAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(pageAdapter.getPageTitle(i)).setTabListener(this));
        }
        if (!TextUtils.isEmpty(kw)) {
            actionBar.setTitle(kw);
            actionBar.setSubtitle(R.string.search_result);
        } else {
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(spuType.getName());
        }
        // init the first page
        ((SPUListFragment) pageAdapter.getItem(0)).init();
    }

    public class MyPageAdapter extends FragmentPagerAdapter {

        private Map<Integer, Fragment> fragments;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new HashMap<Integer, Fragment>();
        }

        @Override
        public int getCount() {
            return orderByDescs.length;
        }

        @Override
        public Fragment getItem(int i) {
            if (fragments.get(i) == null) {

                fragments.put(i, new SPUListFragment.Builder().deferred(
                        SPUStore.getInstance().fetchList(setupQuery(orderByStrs[i]))).build());
            }
            return fragments.get(i);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return orderByDescs[position];
        }
    }

    private Map<String, String> setupQuery(String sortBy) {
        Map<String, String> query = new HashMap<String, String>();
        if (spuType != null) {
            query.put("spu_type_id", String.valueOf(spuType.getId()));
        }
        if (!TextUtils.isEmpty(kw)) {
            query.put("kw", kw);
        }
        query.put("lnglat", String.format("%f,%f", lng, lat));
        query.put("sort_by", sortBy);
        return query;
    }
}


