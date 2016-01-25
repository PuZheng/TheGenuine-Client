package com.puzheng.lejian.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.puzheng.lejian.Const;
import com.puzheng.lejian.R;
import com.puzheng.lejian.RecommendationFragment;
import com.puzheng.lejian.SPUFragment;
import com.puzheng.lejian.SameTypeRecommendationFragment;
import com.puzheng.lejian.model.SPU;

/**
 * Created by xc on 16-1-22.
 */
public class SPUTabHost extends FragmentTabHost {
    public SPUTabHost(Context context) {
        super(context);
    }

    public SPUTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setup(Context context, FragmentManager manager, int containerId) {
        super.setup(context, manager, containerId);
    }

    public void setSPU(SPU spu) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Const.TAG_SPU, spu);
        addTab(newTabSpec("basic").setIndicator("基本信息"), SPUFragment.class, bundle);
        addTab(newTabSpec("sameType").setIndicator(getContext().getString(R.string.sameType)),
                SameTypeRecommendationFragment.class, bundle);
        addTab(newTabSpec("sameVendor").setIndicator(getContext().getString(R.string.sameVendor)),
                SameVendorRecommendationFragment.class, bundle);

        setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int pos = getCurrentTab();
                for (int i = 0; i < getTabWidget().getChildCount(); ++i) {
                    int color = getResources().getColor(android.R.color.darker_gray);
                    if (i == pos) {
                        color = getResources().getColor(R.color.base_color1);
                    }
                    ((TextView) getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(color);
                }
            }
        });
        for (int i = 0; i < getTabWidget().getChildCount(); ++i) {
            View v = getTabWidget().getChildTabViewAt(i);
            v.setBackground(getResources().getDrawable(R.drawable.tab_indicator_holo));
            int color = getResources().getColor(android.R.color.darker_gray);
            if (i == 0) {
                color = getResources().getColor(R.color.base_color1);
            }
            ((TextView) getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(color);
        }
    }

}
