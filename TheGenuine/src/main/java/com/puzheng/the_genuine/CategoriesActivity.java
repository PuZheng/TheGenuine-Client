package com.puzheng.the_genuine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.Category;
import com.puzheng.the_genuine.image_utils.ImageFetcher;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.views.CustomActionBar;
import com.puzheng.the_genuine.views.NavBar;

import java.util.List;

public class CategoriesActivity extends ActionBarActivity implements BackPressedInterface, RefreshInterface {
    private GridView gridView;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private MaskableManager maskableManager;

    private ImageFetcher mImageFetcher;

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
        getMenuInflater().inflate(R.menu.categories, menu);
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

        mImageFetcher = ImageFetcher.getImageFetcher(this, imageThumbSize, 0.25f);

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
        gridView = (GridView) findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) gridView.getAdapter().getItem(position);
                Intent intent = new Intent(CategoriesActivity.this, SPUListActivity.class);
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
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });

        maskableManager = new MaskableManager(gridView, this);
        setActionBar();
        new GetCategoriesTask(gridView).execute();
    }

    private void setActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        CustomActionBar customActionBar = CustomActionBar.setCustomerActionBar(actionBar, CategoriesActivity.this);
        customActionBar.setTitle("360真品");
        customActionBar.setSubtitle("分类列表");
    }


    private class GetCategoriesTask extends AsyncTask<Void, Void, List<Category>> {
        private final GridView gridView;
        private Exception exception;

        public GetCategoriesTask(GridView gridView) {
            this.gridView = gridView;
        }

        @Override
        protected List<Category> doInBackground(Void... params) {
            try {
                return WebService.getInstance(gridView.getContext()).getCategories();
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> list) {
            if (maskableManager.unmask(exception)) {
                gridView.setAdapter( new MyCategoriesAdapter(list));
            }
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }
    }

    private class MyCategoriesAdapter extends BaseAdapter {

        private final List<Category> categories;
        private final LayoutInflater inflater;

        public MyCategoriesAdapter(List<Category> categories) {
            this.categories = categories;
            inflater = (LayoutInflater) CategoriesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return categories.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.categories_grid_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Category category = (Category) getItem(position);

            mImageFetcher.loadImage(category.getPicUrl(), viewHolder.imageView);
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
