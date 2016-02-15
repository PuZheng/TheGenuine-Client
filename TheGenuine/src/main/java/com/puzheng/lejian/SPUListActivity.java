package com.puzheng.lejian;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.humanize.Humanize;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.model.SPUType;
import com.puzheng.lejian.search.SearchActivity;
import com.puzheng.lejian.store.LocationStore;
import com.puzheng.lejian.store.SPUStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPUListActivity extends AppCompatActivity {
    private SPUType spuType;
    private ViewPager viewPager;
    private String kw;
    private ArrayList<Pair<String, String>> orderBys;
    private android.support.v7.app.ActionBar supportActionBar;
    private Pair<Double, Double> lnglat;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                SPUListActivity.this.finish();
                return true;
            case android.R.id.home:
                SPUListActivity.this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        spuType = getIntent().getParcelableExtra(SPUTypeListActivity.SPU_TYPE);
        kw = getIntent().getStringExtra(SearchManager.QUERY);
        setContentView(R.layout.activity_spu_list);
        orderBys = new ArrayList<>();
        for (String s : getResources().getStringArray(R.array.order_by_list)) {
            String[] p = s.split(",");
            orderBys.add(Pair.create(p[0], p[1]));
        }

        viewPager = (ViewPager) findViewById(R.id.pager);

        if (!TextUtils.isEmpty(kw)) {
            supportActionBar.setTitle(kw);
            supportActionBar.setSubtitle(R.string.search_result);
        } else {
            supportActionBar.setTitle(R.string.spu_list);
            supportActionBar.setSubtitle(this.spuType.getName());
        }
        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {

            @Override
            public void done(Pair<Double, Double> lnglat) {
                SPUListActivity.this.lnglat = lnglat;
                final MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), orderBys, lnglat, spuType, kw);
                viewPager.setAdapter(adapter);
                ((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(viewPager);
                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {
                        ((SPUListFragment) adapter.getItem(position)).init();
                    }

                });
                ((SPUListFragment) adapter.getItem(0)).init();
            }
        });


    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<Pair<String, String>> orderBys;
        private final String kw;
        private final Pair<Double, Double> lnglat;
        private final SPUType spuType;
        private Map<Integer, Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<Pair<String, String>> orderBys,
                              Pair<Double, Double> lnglat, SPUType spuType, String kw) {
            super(fm);
            fragments = new HashMap<>();
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

    public static class SPUListAdapter extends BaseAdapter {

        private final List<SPU> spus;
        private final LayoutInflater inflater;
        private final Activity activity;

        public SPUListAdapter(Activity activity, List<SPU> spus) {
            this.spus = spus;
            this.activity = activity;
            inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return spus.size();
        }

        @Override
        public Object getItem(int position) {
            return spus.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((SPU) getItem(position)).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spu_list_item, parent, false);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                        (TextView) convertView.findViewById(R.id.textViewName),
                        (TextView) convertView.findViewById(R.id.textViewFavorCnt),
                        (TextView) convertView.findViewById(R.id.textViewMSRP),
                        (Button) convertView.findViewById(R.id.btnNearby),
                        (RatingBar) convertView.findViewById(R.id.ratingBar));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final SPU spu = (SPU) getItem(position);
            Context context = MyApp.getContext();
            Glide.with(context).load(spu.getIcon().getURL())
                    .error(R.drawable.ic_broken_image_black_24dp).into(viewHolder.imageView);

            viewHolder.textViewName.setText(spu.getName());
            viewHolder.textViewFavorCnt.setText(context.getString(R.string.popularity, Humanize.with(context).num(1200)));
            viewHolder.textViewMSRP.setText(String.valueOf(spu.getMSRP()) + "元");
            viewHolder.ratingBar.setRating(spu.getRating());
            if (spu.getDistance() != 0) {
                viewHolder.button.setText(context.getString(R.string.nearest, Humanize.with(context).distance(spu.getDistance())));
                viewHolder.button.setVisibility(View.VISIBLE);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, NearbyActivity.class);
                        intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                        intent.putExtra(Const.TAG_SPU_ID, spu.getId());
                        activity.startActivity(intent);
                    }
                });
            } else {
                viewHolder.button.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }


        private static class ViewHolder {
            ImageView imageView;
            TextView textViewName;
            TextView textViewMSRP;
            TextView textViewFavorCnt;
            Button button;
            RatingBar ratingBar;

            ViewHolder(ImageView imageView, TextView textViewName,
                       TextView textViewFavorCnt, TextView textViewMSRP, Button button, RatingBar ratingBar) {
                this.imageView = imageView;
                this.textViewName = textViewName;
                this.textViewFavorCnt = textViewFavorCnt;
                this.textViewMSRP = textViewMSRP;
                this.button = button;
                this.ratingBar = ratingBar;
            }
        }
    }
}


