package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.puzheng.the_genuine.data_structure.VerificationInfo;
import com.puzheng.the_genuine.utils.Misc;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TabPageIndicator;


import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private static final String TAG = "ProductActivity";
    private ViewPager viewPager;
    private VerificationInfo verificationInfo;
    private ViewPager viewPagerCover;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        verificationInfo = getIntent().getParcelableExtra(MainActivity.TAG_VERIFICATION_INFO);

        TextView tv = (TextView) findViewById(R.id.textViewRating);
        tv.setText(verificationInfo.getRating() + "分");
        Button button = (Button) findViewById(R.id.buttonComment);
        button.setText("评论\n(" + Misc.humanizeFavorCnt(verificationInfo.getCommentsCnt()) + ")");

        viewPagerCover = (ViewPager) findViewById(R.id.viewPagerCover);
        viewPagerCover.setAdapter(new MyCoverAdapter(getSupportFragmentManager(), verificationInfo.getPicUrlList()));
        CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(viewPagerCover);


        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1").setIndicator("验证信息");
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);

        String s = "周边推荐(" + verificationInfo.getNearbyRecommendationsCnt() + ")";
        tabSpec = tabHost.newTabSpec("tab2").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);

        s = "同厂推荐(" + verificationInfo.getSameVendorRecommendationsCnt() + ")";
        tabSpec = tabHost.newTabSpec("tab3").setIndicator(s);
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(this);

        for (int i=0; i < tabHost.getTabWidget().getChildCount(); ++i) {
            View v = tabHost.getTabWidget().getChildTabViewAt(i);
            v.setBackground(getResources().getDrawable(R.drawable.tab_indicator_holo));
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == 0) {
                color = getResources().getColor(R.color.highlighted_tab);
            }
            title.setTextColor(color);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPagerBottom);
        viewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {
        int pos = viewPager.getCurrentItem();
        tabHost.setCurrentTab(pos);
    }

    @Override
    public void onTabChanged(String s) {
        int pos = tabHost.getCurrentTab();
        for (int i=0; i < tabHost.getTabWidget().getChildCount(); ++i) {
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == pos) {
                color = getResources().getColor(R.color.highlighted_tab);
            }
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            title.setTextColor(color);
        }
        viewPager.setCurrentItem(pos);
    }


    class MyTabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        public MyTabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            fragments.add(VerificationInfoFragment.getInstance(ProductActivity.this, verificationInfo));
            fragments.add(RecommendationsFragment.createNearByProductsFragment(ProductActivity.this));
            fragments.add(RecommendationsFragment.createSameVendorProductsFragment(ProductActivity.this,
                    verificationInfo.getVendorId(), verificationInfo.getId()));
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    class MyCoverAdapter extends  FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyCoverAdapter(FragmentManager fm, List<String> picUrlList) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            for (String url: picUrlList) {
                fragments.add(new CoverFragment(ProductActivity.this, url));
                Log.d(TAG, url);
            }
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product, menu);
        return true;
    }

}
