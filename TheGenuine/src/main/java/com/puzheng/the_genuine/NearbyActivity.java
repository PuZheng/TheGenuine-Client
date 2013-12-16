package com.puzheng.the_genuine;

import android.app.*;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.puzheng.the_genuine.data_structure.StoreResponse;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.views.NavBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-26.
 */
public class NearbyActivity extends ActionBarActivity implements BackPressedInterface {
    public static final int NEARBY_LIST = 1;
    private ViewPager mViewPager;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private int mSpuId = Constants.INVALID_ARGUMENT;

    @Override
    public void doBackPressed() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (mSpuId == Constants.INVALID_ARGUMENT) {
            backPressedHandle.doBackPressed(this, this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        mSpuId = getIntent().getIntExtra(Constants.TAG_SPU_ID, Constants.INVALID_ARGUMENT);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener listener = new ActionBar.TabListener() {
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
        };

        actionBar.addTab(actionBar.newTab().setText("地图").setTabListener(listener));

        actionBar.addTab(actionBar.newTab().setText("列表").setTabListener(listener));

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        });

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);

        new GetNearbyListTask().execute();
    }

    class NearbyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        public NearbyPagerAdapter(FragmentManager fragmentManager, List<StoreResponse> list) {
            super(fragmentManager);
            mFragmentList = new ArrayList<Fragment>();
            if (list == null) {
                mFragmentList.add(new ErrorSupportFragment());
                mFragmentList.add(new ErrorSupportFragment());
            } else {
                mFragmentList.add(new BaiduMapFragment(list));
                mFragmentList.add(new NearbyFragment(NearbyActivity.this, list));
            }
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
    }

    class GetNearbyListTask extends AsyncTask<Void, Void, List<StoreResponse>> {

        @Override
        protected List<StoreResponse> doInBackground(Void... params) {
            try {
                return WebService.getInstance(NearbyActivity.this).getNearbyStoreList(mSpuId);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StoreResponse> storeList) {
            mViewPager.setAdapter(new NearbyPagerAdapter(getSupportFragmentManager(), storeList));
            int current = getIntent().getIntExtra("current", 0);
            getActionBar().setSelectedNavigationItem(current);
            mViewPager.setCurrentItem(current);
        }

    }
}
