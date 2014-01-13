package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import com.puzheng.the_genuine.data_structure.StoreResponse;
import com.puzheng.the_genuine.netutils.WebService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-26.
 */
public class NearbyActivity extends ActionBarActivity implements BackPressedInterface, RefreshInterface {
    public static final int NEARBY_LIST = 1;
    private ViewPager mViewPager;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private int mSpuId = Constants.INVALID_ARGUMENT;
    private MaskableManager maskableManager;
    private GetNearbyListTask task;
    private List<StoreResponse> mStoreList;


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
    public void refresh() {
        if (task == null) {
            task = new GetNearbyListTask();
            task.execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        mSpuId = getIntent().getIntExtra(Constants.TAG_SPU_ID, Constants.INVALID_ARGUMENT);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        maskableManager = new MaskableManager(mViewPager, NearbyActivity.this);
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

        mViewPager.setAdapter(new NearbyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task == null) {
            task = new GetNearbyListTask();
            task.execute();
        }
    }

    public List<StoreResponse> getStoreResponses() {
        return this.mStoreList;
    }

    class NearbyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        public NearbyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            mFragmentList = new ArrayList<Fragment>();
            if (MyApp.isGooglePlayServiceAvailable()) {
                mFragmentList.add(new GoogleMapFragment());
            } else {
                mFragmentList.add(new BaiduMapFragment());
            }
            mFragmentList.add(new NearbyFragment(NearbyActivity.this));
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

        private Exception exception;

        @Override
        protected List<StoreResponse> doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                return WebService.getInstance(NearbyActivity.this).getNearbyStoreList(mSpuId);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StoreResponse> storeList) {
            task = null;
            mStoreList = storeList;
            if (maskableManager.unmask(exception)) {
                int current = getIntent().getIntExtra("current", 0);
                getActionBar().setSelectedNavigationItem(current);
                mViewPager.setCurrentItem(current);
            }
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }

    }
}
