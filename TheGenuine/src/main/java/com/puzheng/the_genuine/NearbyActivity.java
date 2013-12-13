package com.puzheng.the_genuine;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.StoreResponse;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.views.NavBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-26.
 */
public class NearbyActivity extends FragmentActivity implements BackPressedInterface {
    public static final int NEARBY_LIST = 1;
    private TabHost mTabHost;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nearby);
        mSpuId = getIntent().getIntExtra(Constants.TAG_SPU_ID, Constants.INVALID_ARGUMENT);

        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        TabHost.TabSpec tabSpec1 = mTabHost.newTabSpec("tabList").setIndicator("地图");
        tabSpec1.setContent(new TabFactory(this));
        mTabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = mTabHost.newTabSpec("tabList").setIndicator("列表");
        tabSpec2.setContent(new TabFactory(this));
        mTabHost.addTab(tabSpec2);

        initTabHostBackgroud();
        setTextColor();
        mViewPager = (ViewPager) findViewById(R.id.viewPagerBottom);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTextColor();
                mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                mTabHost.setCurrentTab(mViewPager.getCurrentItem());
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }
        });
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);

        new GetNearbyListTask().execute();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initTabHostBackgroud() {
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); ++i) {
            View v = mTabHost.getTabWidget().getChildTabViewAt(i);
            v.setBackground(getResources().getDrawable(R.drawable.tab_indicator_holo));
        }
    }

    private void setTextColor() {
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); ++i) {
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == mTabHost.getCurrentTab()) {
                color = getResources().getColor(R.color.highlighted_tab);
            }
            TextView title = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            title.setTextColor(color);
        }
    }

    class TabFactory implements TabHost.TabContentFactory {
        private Context mContext;

        TabFactory(Context content) {
            this.mContext = content;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
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
            mTabHost.setCurrentTab(current);
            mViewPager.setCurrentItem(current);
        }

    }
}
