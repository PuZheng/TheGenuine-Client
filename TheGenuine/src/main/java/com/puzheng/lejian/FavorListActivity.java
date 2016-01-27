package com.puzheng.lejian;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.adapter.FavorListAdapter;
import com.puzheng.lejian.model.Favor;
import com.puzheng.lejian.image_utils.ImageFetcher;
import com.puzheng.lejian.model.User;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.search.SearchActivity;
import com.puzheng.lejian.store.FavorStore;
import com.puzheng.lejian.util.LoginRequired;
import com.puzheng.lejian.view.NavBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavorListActivity extends ActionBarActivity implements BackPressedInterface, RefreshInterface {
    private static final int LOGIN_ACTION = 1;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mTitle = "";
    private List<String> mPlanetTitles;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private SparseArray<List<Favor>> mData;
    private MaskableManager maskableManager;
    private ImageFetcher mImageFetcher;
    private LoginRequired loginRequired;
    private LoginRequired.LoginHandler loginHandler;

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
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.spu_types, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
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
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void refresh() {
        new GetFavorsTask().execute();
    }

    public void setActionBarTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(R.string.title_activity_favor);
        getActionBar().setSubtitle(mTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loginHandler.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED && requestCode == LOGIN_ACTION) {
            // 不登录， 就意味着无法看到收藏， 直接返回上一个Activity
            this.doBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_categories);
        mImageFetcher = ImageFetcher.getImageFetcher(this, getResources().getDimensionPixelSize(R.dimen.image_view_list_item_width), 0.25f);

        maskableManager = new MaskableManager(findViewById(R.id.content_frame), FavorListActivity.this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(R.string.title_activity_favor);

        setNavBar();

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_action_storage,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                setActionBarTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.taxonomy));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        loginRequired = LoginRequired.with(this).requestCode(LOGIN_ACTION);
        loginHandler = loginRequired.createLoginHandler();
        maskableManager.mask();
        loginRequired.wraps(new LoginRequired.Runnable() {
            @Override
            public void run(User user) {
                FavorStore.getInstance().fetchList(user).done(new DoneHandler<List<Favor>>() {
                    @Override
                    public void done(List<Favor> favors) {

                    }
                }).fail(new FailHandler<Void>() {
                    @Override
                    public void fail(Void aVoid) {

                    }
                }).always(new AlwaysHandler() {
                    @Override
                    public void always() {
                        maskableManager.unmask(null);
                    }
                });
            }
        });

//        if (AuthStore.getInstance().getUser() == null) {
//            HashMap<String, Serializable> map = new HashMap<String, Serializable>();
//            map.put("ISTOPACTIVITY", true);
//            MyApp.doLoginIn(FavorListActivity.this, map);
//        } else {
//            new GetFavorsTask().execute();
//        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
        FavorListFragment fragment = new FavorListFragment();
        fragment.setActivity(FavorListActivity.this);
        fragment.setListAdapter(new FavorListAdapter(mData.get(position)));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);
        setActionBarTitle(mPlanetTitles.get(position));
        drawerLayout.closeDrawer(drawerList);
    }

    private void setNavBar() {
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.enableTab(NavBar.FAVOR, true);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public static class FavorListFragment extends ListFragment {
        private Activity mActivity;

        public FavorListFragment() {

        }

        void setActivity(Activity activity) {
            this.mActivity = activity;
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Favor favor = (Favor) getListAdapter().getItem(position);
            Intent intent = new Intent(mActivity, SPUActivity.class);
            intent.putExtra(Const.TAG_SPU_ID, favor.getSPU().getId());
            startActivity(intent);
        }
    }

    class GetFavorsTask extends AsyncTask<Void, Void, HashMap<String, List<Favor>>> {

        private Exception exception;

        @Override
        protected HashMap<String, List<Favor>> doInBackground(Void... params) {
            try {
                return WebService.getInstance(FavorListActivity.this).getFavorCategories();
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, List<Favor>> map) {
            if (maskableManager.unmask(exception)) {
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
                mPlanetTitles.add(0, String.format(format, getString(R.string.any_spu_type), totalCnt));
                drawerList.setAdapter(new ArrayAdapter<String>(FavorListActivity.this,
                        R.layout.drawer_list_item, mPlanetTitles));
                if (drawerList.getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                    selectItem(0);
                }
            } else {
                getActionBar().setTitle(getString(R.string.error_message));
                getActionBar().setSubtitle(null);
                drawerLayout.setDrawerListener(null);
            }
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }
    }

}

