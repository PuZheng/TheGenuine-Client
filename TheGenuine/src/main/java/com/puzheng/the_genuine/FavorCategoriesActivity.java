package com.puzheng.the_genuine;

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
import android.view.*;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.views.NavBar;

import java.io.Serializable;
import java.util.*;

public class FavorCategoriesActivity extends ActionBarActivity implements BackPressedInterface {
    private final String mDrawerTitle = "选择分类";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle = "分类";
    private List<String> mPlanetTitles;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private SparseArray<List<Recommendation>> mData;

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
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_categories);
        setTitle("您的收藏");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(mTitle);

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
                getActionBar().setTitle(mTitle);
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
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetCategoriesTask().execute();
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        ListFragment fragment = new ListFragment();
        if (mData != null) {
            fragment.setListAdapter(new ProductListAdapter(mData.get(position), FavorCategoriesActivity.this));
        }else{
            fragment.setEmptyText(getString(R.string.search_no_result_found));
        }

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

    private class GetCategoriesTask extends AsyncTask<Void, Void, HashMap<String, List<Recommendation>>> {

        public GetCategoriesTask() {
        }

        @Override
        protected HashMap<String, List<Recommendation>> doInBackground(Void... params) {
            try {
                return WebService.getInstance(FavorCategoriesActivity.this).getFavorCategories();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, List<Recommendation>> map) {
            boolean b = map != null;
            if (b) {
                String format = "%s(%d)";
                mPlanetTitles = new ArrayList<String>();
                mData = new SparseArray<List<Recommendation>>();
                mData.put(0, new ArrayList<Recommendation>());
                int idx = 1;
                int totalCnt = 0;
                for (Map.Entry<String, List<Recommendation>> entry : map.entrySet()) {
                    List<Recommendation> value = entry.getValue();
                    int size = value.size();
                    totalCnt += size;
                    mPlanetTitles.add(String.format(format, entry.getKey(), size));
                    mData.append(idx, value);
                    List<Recommendation> allData = mData.get(0);
                    allData.addAll(entry.getValue());
                    idx++;
                }
                mPlanetTitles.add(0, String.format(format, "所有", totalCnt));
                mDrawerList.setAdapter(new ArrayAdapter<String>(FavorCategoriesActivity.this,
                        R.layout.drawer_list_item, mPlanetTitles));
                if (mDrawerList.getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                    selectItem(0);
                }
            }
        }
    }
}
