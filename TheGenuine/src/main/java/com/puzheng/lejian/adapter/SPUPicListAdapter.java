package com.puzheng.lejian.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.puzheng.lejian.CoverFragment;
import com.puzheng.lejian.SPUActivity;

import java.util.ArrayList;
import java.util.List;

public class SPUPicListAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public SPUPicListAdapter(FragmentManager fm, List<String> urls) {
        super(fm);
        fragments = new ArrayList<Fragment>();
        for (String url : urls) {
            CoverFragment coverFragment = new CoverFragment();
            coverFragment.setUrl(url);
            fragments.add(coverFragment);
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }
}
