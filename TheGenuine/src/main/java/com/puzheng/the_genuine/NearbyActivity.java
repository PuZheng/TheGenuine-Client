package com.puzheng.the_genuine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-26.
 */
public class NearbyActivity extends FragmentActivity {
    public static final int NEARBY_LIST = 1;
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        TabHost.TabSpec tabSpec1 = mTabHost.newTabSpec("tabList").setIndicator("地图");
        tabSpec1.setContent(new TabFactory(this));
        mTabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = mTabHost.newTabSpec("tabList").setIndicator("列表");
        tabSpec2.setContent(new TabFactory(this));
        mTabHost.addTab(tabSpec2);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTextColor();
            }
        });
        mTabHost.setCurrentTab(getIntent().getIntExtra("current", 0));
        initTabHostBackgroud();
        setTextColor();
    }

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

}
