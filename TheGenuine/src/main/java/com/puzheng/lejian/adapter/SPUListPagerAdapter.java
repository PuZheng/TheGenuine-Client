package com.puzheng.lejian.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Pair;

import com.puzheng.lejian.R;
import com.puzheng.lejian.SPUListActivity;
import com.puzheng.lejian.SPUListFragment;
import com.puzheng.lejian.model.SPUType;
import com.puzheng.lejian.store.SPUStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPUListPagerAdapter extends FragmentPagerAdapter {

    private final List<Pair<String, String>> orderBys;
    private final String kw;
    private SPUListActivity spuListActivity;
    private final Pair<Double, Double> lnglat;
    private final SPUType spuType;
    private Map<Integer, Fragment> fragments;

    public SPUListPagerAdapter(SPUListActivity spuListActivity, FragmentManager fm,
                               Pair<Double, Double> lnglat, SPUType spuType,
                               List<Pair<String, String>> orderBys,
                               String kw) {
        super(fm);
        this.spuListActivity = spuListActivity;
        fragments = new HashMap<Integer, Fragment>();
        this.lnglat = lnglat;
        this.spuType = spuType;
        this.orderBys = orderBys;
        this.kw = kw;
    }

    @Override
    public int getCount() {
        return orderBys.size();
    }

    @Override
    public Fragment getItem(int i) {
        if (fragments.get(i) == null) {
            fragments.put(i, new SPUListFragment.Builder().deferred(
                    SPUStore.getInstance().fetchList(setupQuery(orderBys.get(i).second))).build());
        }
        return fragments.get(i);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return orderBys.get(position).first;
    }

    private Map<String, String> setupQuery(String sortBy) {
        Map<String, String> query = new HashMap<String, String>();
        if (spuType != null) {
            query.put("spu_type_id", String.valueOf(spuType.getId()));
        }
        if (!TextUtils.isEmpty(kw)) {
            query.put("kw", kw);
        }
        query.put("lnglat", String.format("%f,%f", lnglat.first, lnglat.second));
        query.put("sort_by", sortBy);
        return query;
    }
}
