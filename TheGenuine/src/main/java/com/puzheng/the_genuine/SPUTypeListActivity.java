package com.puzheng.the_genuine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.the_genuine.model.SPUType;
import com.puzheng.the_genuine.image_utils.ImageFetcher;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.store.SPUTypeStore;
import com.puzheng.the_genuine.util.Misc;

import java.util.List;

public class SPUTypeListActivity extends ActionBarActivity implements BackPressedInterface, RefreshInterface {
    private GridView gridView;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private MaskableManager maskableManager;
    private ImageFetcher imageFetcher;
    private MySPUTypesAdapter mAdapter;

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
        // Inflate the menu; this adds items to the action bar if it is present.
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
        new GetCategoriesTask(gridView).execute();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.categories_grid_item_width);

        imageFetcher = ImageFetcher.getImageFetcher(this, imageThumbSize, 0.25f);
        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SPUType category = (SPUType) gridView.getAdapter().getItem(position);
                Intent intent = new Intent(SPUTypeListActivity.this, SPUListActivity.class);
                intent.putExtra("category_id", category.getId());
                intent.putExtra("categoryName", category.getName());
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
                        imageFetcher.setPauseWork(true);
                    }
                } else {
                    imageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        maskableManager = new MaskableManager(gridView, this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.categories);
        maskableManager.mask();
        SPUTypeStore.getInstance().fetchList().done(new DoneHandler<List<SPUType>>() {
            @Override
            public void done(List<SPUType> spuTypes) {
                mAdapter = new MySPUTypesAdapter(spuTypes);
                gridView.setAdapter(mAdapter);
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
                maskableManager.unmask(null);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageFetcher.closeCache();
    }

    private class GetCategoriesTask extends AsyncTask<Void, Void, List<SPUType>> {
        private final GridView gridView;
        private Exception exception;

        public GetCategoriesTask(GridView gridView) {
            this.gridView = gridView;
        }

        @Override
        protected List<SPUType> doInBackground(Void... params) {
            try {
                return WebService.getInstance(gridView.getContext()).getCategories();
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<SPUType> list) {
            if (maskableManager.unmask(exception)) {
                mAdapter = new MySPUTypesAdapter(list);
                gridView.setAdapter(mAdapter);
            }
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }
    }

    private class MySPUTypesAdapter extends BaseAdapter {

        private final List<SPUType> spuTypes;
        private final LayoutInflater inflater;

        public MySPUTypesAdapter(List<SPUType> spuTypes) {
            this.spuTypes = spuTypes;
            inflater = (LayoutInflater) SPUTypeListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return spuTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return spuTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return spuTypes.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spu_type_grid_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final SPUType spuType = (SPUType) getItem(position);

            imageFetcher.loadImage(spuType.getPic().getURL(), viewHolder.imageView);
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;

            ViewHolder(ImageView imageView) {
                this.imageView = imageView;
            }

        }
    }
}
