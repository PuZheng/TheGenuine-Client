package com.puzheng.lejian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.model.Retailer;
import com.puzheng.lejian.store.LocationStore;
import com.puzheng.lejian.store.RetailerStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyActivity extends AppCompatActivity implements BackPressedInterface, RefreshInterface {
    public static final int NEARBY_LIST = 1;
    private ViewPager viewPager;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private int spuId = Const.INVALID_ARGUMENT;
    private ListFragment nearbyListFragment;
    private AMapFragment amapFragment;
    private BroadcastReceiver receiver;
    private Pair<Double, Double> lnglat;


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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        nearbyListFragment = new ListFragment();
        nearbyListFragment.setListAdapter(new NearbyListAdapter());
        amapFragment = AMapFragment.newInstance();
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                return (new Fragment[]{amapFragment, nearbyListFragment}[position]);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString((new int[]{
                        R.string.map,
                        R.string.list
                })[position]);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchRetailerList();
            }
        };
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.map));
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.list));
//        viewPager = (ViewPager) findViewById(R.id.pager);
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//        ActionBar.TabListener listener = new ActionBar.TabListener() {
//            @Override
//            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//            }
//
//            @Override
//            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//            }
//        };
//
//        actionBar.addTab(actionBar.newTab().setText(R.string.map).setTabListener(listener));
//
//        actionBar.addTab(actionBar.newTab().setText(R.string.list).setTabListener(listener));


//
//        List<Fragment> fragmentList = new ArrayList<Fragment>();
//        fragmentList.add(nearbyListFragment);
//        fragmentList.add(amapFragment);
//
//        viewPager.setAdapter(new NearbyPagerAdapter(getSupportFragmentManager(), fragmentList));
//        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                getActionBar().setSelectedNavigationItem(position);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(String.valueOf(R.id.BROADCAST_LOCATION_ACTION));
        registerReceiver(receiver, filter);
        fetchRetailerList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    private void fetchRetailerList() {
        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
            @Override
            public void done(Pair<Double, Double> lnglat) {
                NearbyActivity.this.lnglat = lnglat;
                amapFragment.centerAt(lnglat);
                Map<String, String> args = new HashMap<String, String>();
                if (spuId != -1) {
                    args.put("spu_id", String.valueOf(spuId));
                }
                if (lnglat != null) {
                    args.put("lnglat", lnglat.first + "," + lnglat.second);
                }

                Logger.i(String.format("request retailer list - spu id: %d, lnglat: %f,%f",
                        spuId, lnglat.first, lnglat.second));
                RetailerStore.getInstance().fetchList(args).done(new DoneHandler<List<Retailer>>() {
                    @Override
                    public void done(List<Retailer> retailers) {
                        Logger.i("retailers fetched");
                        Logger.json(new Gson().toJson(retailers));
                        if (retailers.isEmpty()) {
                            nearbyListFragment.setEmptyText(getString(R.string.no_nearby_retailers));
                            nearbyListFragment.getView().setBackgroundColor(0xff0000);
                        } else {
                            ((NearbyListAdapter) nearbyListFragment.getListAdapter()).setRetailers(retailers);
                        }
                        amapFragment.setRetailers(retailers);
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

    public Pair<Double, Double> getLnglat() {
        return lnglat;
    }

    public static class NearbyListAdapter extends BaseAdapter {

        private List<Retailer> retailers;
        private final LayoutInflater inflater;

        public NearbyListAdapter() {
            inflater = LayoutInflater.from(MyApp.getContext());
        }

        @Override
        public int getCount() {
            return retailers == null ? 0 : retailers.size();
        }

        @Override
        public Object getItem(int position) {
            return retailers == null ? null : retailers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return retailers == null ? null : retailers.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_nearby_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                        (TextView) convertView.findViewById(R.id.textViewName),
                        (TextView) convertView.findViewById(R.id.textViewDistance),
                        (TextView) convertView.findViewById(R.id.textViewAddr),
                        (RatingBar) convertView.findViewById(R.id.ratingBar),
                        (TextView) convertView.findViewById(R.id.textViewMark));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Retailer retailer = (Retailer) getItem(position);
            Glide.with(MyApp.getContext()).load(retailer.getPic().getURL()).into(viewHolder.imageView);

            viewHolder.textViewName.setText(retailer.getName());
            viewHolder.textViewDistance.setText(Humanize.with(MyApp.getContext()).distance(
                    retailer.getPOI().getDistance()));
            viewHolder.textViewAddr.setText(retailer.getPOI().getAddr());
            viewHolder.ratingBar.setRating(retailer.getRating());
            viewHolder.textViewMark.setText(String.valueOf(position + 1));
            return convertView;
        }

        public void setRetailers(List<Retailer> retailers) {
            this.retailers = retailers;
            notifyDataSetChanged();
        }

        class ViewHolder {
            ImageView imageView;
            TextView textViewName;
            TextView textViewDistance;
            TextView textViewAddr;
            RatingBar ratingBar;
            TextView textViewMark;

            ViewHolder(ImageView imageView, TextView textViewName, TextView textViewDistance,
                       TextView textViewAddr, RatingBar ratingBar, TextView textViewMark) {
                this.imageView = imageView;
                this.textViewName = textViewName;
                this.textViewDistance = textViewDistance;
                this.textViewAddr = textViewAddr;
                this.ratingBar = ratingBar;
                this.textViewMark = textViewMark;
            }
        }
    }


    class NearbyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public NearbyPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
            super(fragmentManager);
//            if (MyApp.isGooglePlayServiceAvailable()) {
//                fragmentList.add(new GoogleMapFragment());
//            } else {
//                fragmentList.add(new BaiduMapFragment());
//            }
            this.fragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        public void setRetailers(List<Retailer> retailers) {
//            ((NearbyListAdapter) ((NearbyListFragment) fragmentList.get(0)).getListAdapter()).setRetailers(retailers);
//            ((BaiduMapFragment) fragmentList.get(1)).setRetailers(retailers);
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
