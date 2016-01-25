package com.puzheng.lejian;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocationClientOption;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.lejian.adapter.SPUListPagerAdapter;
import com.puzheng.lejian.model.SPUType;
import com.puzheng.lejian.search.SearchActivity;
import com.puzheng.lejian.store.LocationStore;

import java.util.ArrayList;

public class SPUListActivity extends ActionBarActivity implements ActionBar.TabListener {
    private SPUType spuType;
    private String[] orderByDescs;
    private SPUListPagerAdapter pageAdapter;
    private ViewPager viewPager;
    private String kw;
    private ArrayList<Pair<String, String>> orderBys;

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
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        //设置是否只定位一次,默认为false

        spuType = getIntent().getParcelableExtra(SPUTypeListActivity.SPU_TYPE);
        kw = getIntent().getStringExtra(SearchManager.QUERY);
        setContentView(R.layout.activity_spu_list);
        orderBys = new ArrayList<Pair<String, String>>();
        for (String s: getResources().getStringArray(R.array.order_by_list)) {
            String[] p = s.split(",");
            orderBys.add(Pair.create(p[0], p[1]));
        }

        viewPager = (ViewPager) findViewById(R.id.pager);

        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
            @Override
            public void done(Pair<Double, Double> lnglat) {
                addTabs(lnglat, spuType);
            }
        });
    }

    private void addTabs(Pair<Double, Double> lnglat, SPUType spuType) {
        final ActionBar actionBar = getActionBar();
        pageAdapter = new SPUListPagerAdapter(this, getSupportFragmentManager(),
                lnglat, spuType, orderBys, kw);
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
            actionBar.setSubtitle(this.spuType.getName());
        }
        // init the first page
        ((SPUListFragment) pageAdapter.getItem(0)).init();
    }


}


