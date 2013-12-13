package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.*;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.Favor;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.views.NavBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavorCategoriesActivity extends ActionBarActivity implements BackPressedInterface {
    private final String mDrawerTitle = "选择分类";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle = "";
    private List<String> mPlanetTitles;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private SparseArray<List<Favor>> mData;

    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        backPressedHandle.doBackPressed(this, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public void setActionBarTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle("我的收藏");
        getActionBar().setSubtitle(mTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                new GetFavorsTask().execute();

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_categories);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("我的收藏");

        setNavBar();

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                setActionBarTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (MyApp.getCurrentUser() == null) {
            HashMap<String, Serializable> map = new HashMap<String, Serializable>();
            map.put("ISTOPACTIVITY", true);
            MyApp.doLoginIn(FavorCategoriesActivity.this, map);
        } else {
            new GetFavorsTask().execute();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        ListFragment fragment = new FavorListFragment(FavorCategoriesActivity.this);
        fragment.setListAdapter(new FavorListAdapter(mData.get(position), FavorCategoriesActivity.this));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setActionBarTitle(mPlanetTitles.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setNavBar() {
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.enableTab(NavBar.FAVOR, true);
        navBar.setContext(this);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    class FavorListFragment extends ListFragment {
        private Activity mActivity;

        FavorListFragment(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Favor favor = (Favor) getListAdapter().getItem(position);
            Intent intent = new Intent(mActivity, SPUActivity.class);
            intent.putExtra(Constants.TAG_SPU_ID, favor.getSPU().getId());
            startActivity(intent);
        }
    }

    class GetFavorsTask extends AsyncTask<Void, Void, HashMap<String, List<Favor>>> {

        @Override
        protected HashMap<String, List<Favor>> doInBackground(Void... params) {
            try {
                return WebService.getInstance(FavorCategoriesActivity.this).getFavorCategories();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, List<Favor>> map) {
            if (map != null) {
                String format = "%s(%d)";
                mPlanetTitles = new ArrayList<String>();
                mData = new SparseArray<List<Favor>>();
                mData.put(0, new ArrayList<Favor>());
                int idx = 1;
                int totalCnt = 0;
                for (Map.Entry<String, List<Favor>> entry : map.entrySet()) {
                    List<Favor> value = entry.getValue();
                    int size = value.size();
                    totalCnt += size;
                    mPlanetTitles.add(String.format(format, entry.getKey(), size));
                    mData.append(idx, value);
                    List<Favor> allData = mData.get(0);
                    allData.addAll(entry.getValue());
                    idx++;
                }
                mPlanetTitles.add(0, String.format(format, "所有", totalCnt));
                mDrawerList.setAdapter(new ArrayAdapter<String>(FavorCategoriesActivity.this,
                        R.layout.drawer_list_item, mPlanetTitles));
                if (mDrawerList.getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                    selectItem(0);
                }
            } else {
                getActionBar().setTitle(getString(R.string.error_message));
                getActionBar().setSubtitle(null);
                mDrawerLayout.setDrawerListener(null);
                Fragment fragment = new ErrorListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        }
    }
}

class FavorListAdapter extends BaseAdapter {
    private List<Favor> mFavorList;
    private LayoutInflater inflater;
    private Activity mActivity;

    public FavorListAdapter(List<Favor> list, Activity activity) {
        this.mActivity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mFavorList = list;
    }

    @Override
    public int getCount() {
        return mFavorList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFavorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFavorList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recommendation_list_item, null);
        }
        RecommendationListAdapter.ViewHolder viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = new RecommendationListAdapter.ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                    (TextView) convertView.findViewById(R.id.textViewProductName),
                    (TextView) convertView.findViewById(R.id.textViewFavorCnt),
                    (TextView) convertView.findViewById(R.id.textViewPrice),
                    (Button) convertView.findViewById(R.id.button),
                    (RatingBar) convertView.findViewById(R.id.ratingBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecommendationListAdapter.ViewHolder) convertView.getTag();
        }

        final Favor favor = (Favor) getItem(position);
        new GetImageTask(viewHolder.imageView, favor.getSPU().getIcon()).execute();

        viewHolder.textViewProductName.setText(favor.getSPU().getName());
        viewHolder.textViewPrice.setText("￥" + favor.getSPU().getMsrp());
        viewHolder.textViewFavorCnt.setText("人气" + Misc.humanizeNum(favor.getFavorCnt()));
        viewHolder.ratingBar.setRating(favor.getSPU().getRating());
        viewHolder.button.setText("最近" + Misc.humanizeDistance(favor.getDistance()));
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, NearbyActivity.class);
                intent.putExtra("current", NearbyActivity.NEARBY_LIST);
                intent.putExtra(Constants.TAG_SPU_ID, favor.getSPU().getId());
                mActivity.startActivity(intent);
            }
        });
        if (favor.getDistance() == -1) {
            viewHolder.button.setVisibility(View.INVISIBLE);
        }

        viewHolder.button.setText(Misc.humanizeDistance(favor.getDistance()));
        viewHolder.textViewPrice.setText(String.valueOf(favor.getSPU().getMsrp()));
        return convertView;
    }


}
