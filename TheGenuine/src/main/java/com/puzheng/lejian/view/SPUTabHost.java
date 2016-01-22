package com.puzheng.lejian.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.puzheng.lejian.R;

/**
 * Created by xc on 16-1-22.
 */
public class SPUTabHost extends TabHost {
    public SPUTabHost(Context context) {
        super(context);
    }

    public SPUTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setup() {
        super.setup();
        TabHost.TabSpec tabSpec = newTabSpec("tab1").setIndicator("基本信息");
//        tabSpec.setContent(new MyTabFactory(this));
        addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tab2").setIndicator(getString(R.string.sameType));
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tab3").setIndicator(getString(R.string.sameVendor));
        tabSpec.setContent(new MyTabFactory(this));
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int pos = tabHost.getCurrentTab();
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); ++i) {
                    int color = getResources().getColor(android.R.color.darker_gray);
                    if (i == pos) {
                        color = getResources().getColor(R.color.base_color1);
                    }
                    TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    title.setTextColor(color);
                }
                //        viewPager.setCurrentItem(pos);

            }
        });
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); ++i) {
            View v = tabHost.getTabWidget().getChildTabViewAt(i);
            v.setBackground(getResources().getDrawable(R.drawable.tab_indicator_holo));
            TextView title = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == 0) {
                color = getResources().getColor(R.color.base_color1);
            }
            title.setTextColor(color);
        }

    }


}
