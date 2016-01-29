package com.puzheng.lejian;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.model.Retailer;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.store.LocationStore;
import com.puzheng.lejian.store.RetailerStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-26.
 */
public class NearbyActivity extends ActionBarActivity implements BackPressedInterface, RefreshInterface {
    public static final int NEARBY_LIST = 1;
    private ViewPager viewPager;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private int spuId = Const.INVALID_ARGUMENT;


    @Override
    public void doBackPressed() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (spuId == Const.INVALID_ARGUMENT) {
            backPressedHandle.doBackPressed(this, this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        spuId = getIntent().getIntExtra(Const.TAG_SPU_ID, -1);
        viewPager = (ViewPager) findViewById(R.id.pager);
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
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(actionBar.newTab().setText(R.string.map).setTabListener(listener));

        actionBar.addTab(actionBar.newTab().setText(R.string.list).setTabListener(listener));

//        viewPager.setAdapter(new NearbyPagerAdapter(getSupportFragmentManager()));
//        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                getActionBar().setSelectedNavigationItem(position);
//            }
//        });
        IntentFilter filter = new IntentFilter(String.valueOf(R.id.BROADCAST_LOCATION_ACTION));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchRetailerList();
            }
        }, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
            @Override
            public void done(Pair<Double, Double> lnglat) {
                fetchRetailerList();
            }
        });
    }

    private void fetchRetailerList() {
        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
            @Override
            public void done(Pair<Double, Double> lnglat) {
                Map<String, String> args = new HashMap<String, String>();
                if (spuId != -1) {
                    args.put("spu_id", String.valueOf(spuId));
                }
                if (lnglat != null) {
                    args.put("lnglat", lnglat.first + "," + lnglat.second);
                }

                Logger.i(String.format("request fetailer list - spu id: %d, lnglat: %f,%f",
                        spuId, lnglat.first, lnglat.second));
                RetailerStore.getInstance().fetchList(args).done(new DoneHandler<List<Retailer>>() {
                    @Override
                    public void done(List<Retailer> retailers) {
                        Logger.i("retailers fetched");
                        Logger.json(new Gson().toJson(retailers));
                        // TODO: 16-1-29 setup list view
                        // TODO: 16-1-29 setup map view
                    }
                }).fail(new FailHandler<Void>() {
                    @Override
                    public void fail(Void aVoid) {

                    }
                }).always(new AlwaysHandler() {
                    @Override
                    public void always() {

                    }
                });
            }
        });
    }


    class NearbyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public NearbyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragmentList = new ArrayList<Fragment>();
//            if (MyApp.isGooglePlayServiceAvailable()) {
//                fragmentList.add(new GoogleMapFragment());
//            } else {
//                fragmentList.add(new BaiduMapFragment());
//            }
            NearbyFragment nearbyFragment = new NearbyFragment();
            fragmentList.add(new NearbyFragment());
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

//    class GetNearbyListTask extends AsyncTask<Void, Void, List<StoreResponse>> {
//
//        private Exception exception;
//
//        @Override
//        protected List<StoreResponse> doInBackground(Void... params) {
//            try {
//                return WebService.getInstance(NearbyActivity.this).getNearbyStoreList(spuId);
//            } catch (Exception e) {
//                exception = e;
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<StoreResponse> storeList) {
//            task = null;
//            mStoreList = storeList;
//            if (maskableManager.unmask(exception)) {
//                int current = getIntent().getIntExtra("current", 0);
//                getActionBar().setSelectedNavigationItem(current);
//                viewPager.setCurrentItem(current);
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            maskableManager.mask();
//        }
//
//    }
}
