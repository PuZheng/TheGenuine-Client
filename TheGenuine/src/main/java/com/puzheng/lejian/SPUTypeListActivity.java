package com.puzheng.lejian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.adapter.SPUTypeListAdapter;
import com.puzheng.lejian.model.SPUType;
import com.puzheng.lejian.search.SearchActivity;
import com.puzheng.lejian.store.SPUTypeStore;
import com.puzheng.lejian.util.Misc;

import java.util.List;

public class SPUTypeListActivity extends ActionBarActivity implements BackPressedInterface, RefreshInterface {
    static final String SPU_TYPE = "SPU_TYPE_ID";
    private GridView gridView;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private MaskableManager maskableManager;
    private SPUTypeListAdapter adapter;

    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onBackPressed() {
        backPressedHandle.doBackPressed(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spu_types, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public void refresh() {

        //new GetCategoriesTask(gridView).execute();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spu_type_list);


        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SPUType spuType = (SPUType) gridView.getAdapter().getItem(position);
                Intent intent = new Intent(SPUTypeListActivity.this, SPUListActivity.class);
                intent.putExtra(SPU_TYPE, spuType);
                startActivity(intent);
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Misc.hasHoneycomb()) {
                    }
                } else {
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

//        maskableManager = new MaskableManager(gridView, this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.spu_types);
//        maskableManager.mask();
        SPUTypeStore.getInstance().fetchList().done(new DoneHandler<List<SPUType>>() {
            @Override
            public void done(List<SPUType> spuTypes) {
                Logger.i("spu types fetched");
                Logger.json(new Gson().toJson(spuTypes));
                adapter = new SPUTypeListAdapter(spuTypes);
                gridView.setAdapter(adapter);
            }
        }).fail(new FailHandler<Pair<String, String>>() {
            @Override
            public void fail(Pair<String, String> err) {
                Toast.makeText(SPUTypeListActivity.this, R.string.data_load_failed,
                        Toast.LENGTH_SHORT).show();
            }
        }).always(new AlwaysHandler() {
            @Override
            public void always() {
//                maskableManager.unmask(null);
            }
        });
    }


}
