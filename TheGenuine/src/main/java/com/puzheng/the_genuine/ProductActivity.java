package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;

import com.puzheng.the_genuine.data_structure.VerificationInfo;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private ViewPager viewPager;
    private TabHost tabHost;
    private VerificationInfo verificationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        verificationInfo = getIntent().getParcelableExtra(MainActivity.TAG_VERIFICATION_INFO);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("verification_info").setIndicator("验证信息");
        tabSpec1.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("near_by_recommendation").setIndicator("附近同类真品");
        tabSpec2.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec2);
        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec("same_vendor_recommendation").setIndicator("同厂家真品");
        tabSpec3.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec3);

        tabHost.setOnTabChangedListener(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
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
    public void onTabChanged(String tabId) {
        int pos = tabHost.getCurrentTab();
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
            fragments.add(ProductsFragment.createNearByProductsFragment(ProductActivity.this));
            fragments.add(ProductsFragment.createSameVendorProductsFragment(ProductActivity.this,
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product, menu);
        return true;
    }


}
