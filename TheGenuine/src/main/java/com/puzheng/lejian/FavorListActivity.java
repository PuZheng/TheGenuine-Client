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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.adapter.FavorListAdapter;
import com.puzheng.lejian.model.Favor;
import com.puzheng.lejian.image_utils.ImageFetcher;
import com.puzheng.lejian.model.SPUType;
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

public class FavorListActivity extends ActionBarActivity implements LoginRequired.ILoginHandler, BackPressedInterface, RefreshInterface {
    private static final int LOGIN_ACTION = 1;
    private DrawerLayout drawerLayout;
    private ListView spuTypeListView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mTitle = "";
    private List<String> mPlanetTitles;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private SparseArray<List<Favor>> mData;
    private MaskableManager maskableManager;
    private LoginRequired loginRequired;
    private LoginRequired.LoginHandler loginHandler;
    private ListView.OnItemClickListener drawerListItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            // update selected item and title, then close the drawer
            spuTypeListView.setItemChecked(position, true);


            favorListFragment.setListAdapter(new FavorListAdapter((List<Favor>) view.getTag()));
        }

    };
    private FavorListFragment favorListFragment;
    private List<Favor> favors;

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
        boolean drawerOpen = drawerLayout.isDrawerOpen(spuTypeListView);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void refresh() {
//        new GetFavorsTask().execute();
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
        setContentView(R.layout.activity_favor_list);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        spuTypeListView = (ListView) findViewById(R.id.left_drawer);
        spuTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SPUType spuType = (SPUType) parent.getAdapter().getItem(position);
                setActionBarTitle(spuType == null ? getString(R.string.any_spu_type) : spuType.getName());
                drawerLayout.closeDrawer(spuTypeListView);
                favorListFragment.setListAdapter(new FavorListAdapter((List<Favor>) view.getTag()));
            }
        });

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
        loginRequired.wraps(new LoginRequired.Runnable() {
            @Override
            public void run(User user) {
                FavorStore.getInstance().fetchList(user).done(new DoneHandler<List<Favor>>() {
                    @Override
                    public void done(List<Favor> favors) {
                        FavorListActivity.this.favors = favors;
                        spuTypeListView.setAdapter(new SPUTypeAdapter(favors));
                        if (spuTypeListView.getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                            spuTypeListView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    spuTypeListView.performItemClick(spuTypeListView.getChildAt(0), 0, 0);
                                }
                            }, 100);
                        }
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
        favorListFragment = (FavorListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_favor_list);
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
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        spuTypeListView.setItemChecked(position, true);
        setActionBarTitle(mPlanetTitles.get(position));
        drawerLayout.closeDrawer(spuTypeListView);
    }

    private void setNavBar() {
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.enableTab(NavBar.FAVOR, true);
    }

    @Override
    public void onLoginDone(LoginRequired.Runnable runnable) {
        loginHandler.onLoginDone(runnable);
    }

    public static class FavorListFragment extends android.support.v4.app.ListFragment {
        private Activity mActivity;

        public FavorListFragment() {

        }

        void setActivity(Activity activity) {
            this.mActivity = activity;
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Favor favor = (Favor) getListAdapter().getItem(position);
            Intent intent = new Intent(getActivity(), SPUActivity.class);
            intent.putExtra(Const.TAG_SPU_ID, favor.getSPU().getId());
            startActivity(intent);
        }
    }




    private static class SPUTypeAdapter extends BaseAdapter {

        private static final int VIEW_HOLDER = 1;
        private static final int SPU_LIST = 2;
        private List<SPUType> spuTypes = new ArrayList<SPUType>();
        private LayoutInflater inflater;
        private HashMap<Integer, List<Favor>> spuTypeMap = new HashMap<Integer, List<Favor>>();

        SPUTypeAdapter(List<Favor> favors) {
            // 不限类型, 对应所有收藏
            spuTypes.add(null);
            spuTypeMap.put(null, favors);
            for (Favor favor : favors) {
                List<Favor> subFavors;
                SPUType spuType = favor.getSPU().getSpuType();
                if (!spuTypeMap.containsKey(spuType.getId())) {
                    subFavors = new ArrayList<Favor>();
                    spuTypes.add(spuType);
                    spuTypeMap.put(spuType.getId(), subFavors);
                } else {
                    subFavors = spuTypeMap.get(spuType.getId());
                }
                subFavors.add(favor);
            }
            inflater = LayoutInflater.from(MyApp.getContext());
        }


        @Override
        public int getCount() {
            return this.spuTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return this.spuTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            SPUType spuType = this.spuTypes.get(position);
            return spuType == null? 0: spuType.getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.drawer_list_item, null);
            }
            SPUType spuType = this.spuTypes.get(position);
            String spuTypeName = spuType == null ?
                    MyApp.getContext().getString(R.string.any_spu_type) : spuType.getName();
            Integer spuTypeId = spuType == null? null: spuType.getId();
            convertView.setTag(spuTypeMap.get(spuTypeId));
            ((TextView) convertView).setText(spuTypeName + "(" + spuTypeMap.get(spuTypeId).size() + ")");
            return convertView;
        }


    }

}

