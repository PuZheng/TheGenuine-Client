package com.puzheng.the_genuine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;

import com.puzheng.the_genuine.data_structure.Category;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.views.NavBar;

import java.util.List;

public class CategoriesActivity extends ActionBarActivity implements Maskable, BackPressedInterface {

    private View mask;
    private GridView gridView;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();

    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void mask() {
        mask.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
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
    public void unmask(Boolean b) {
        mask.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
        mask = findViewById(R.id.mask);
        gridView = (GridView) findViewById(R.id.gridView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("分类列表");
        actionBar.setTitle("360真品");
        new GetCategoriesTask(gridView, this).execute();
    }

    private class GetCategoriesTask extends AsyncTask<Void, Void, List<Category>> {
        private final GridView gridView;
        private final Maskable maskable;

        public GetCategoriesTask(GridView gridView, Maskable maskable) {
            this.gridView = gridView;
            this.maskable = maskable;
        }

        @Override
        protected List<Category> doInBackground(Void... params) {
            try {
                return WebService.getInstance(gridView.getContext()).getCategories();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> list) {
            boolean b = list != null;
            if (b) {
                gridView.setAdapter(new MyCategoriesAdapter(list));
            }
            this.maskable.unmask(b);
        }

        @Override
        protected void onPreExecute() {
            maskable.mask();
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
                viewHolder = new ViewHolder((ImageButton) convertView.findViewById(R.id.imageButton));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Category category = (Category) getItem(position);
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoriesActivity.this, SPUListActivity.class);
                    intent.putExtra("category_id", category.getId());
                    intent.putExtra("categoryName", category.getName());
                    startActivity(intent);
                }
            });
            new GetImageTask(viewHolder.imageButton, category.getPicUrl()).execute();
            return convertView;
        }

        private class ViewHolder {
            ImageButton imageButton;

            ViewHolder(ImageButton imageButton) {
                this.imageButton = imageButton;
            }

        }
    }
}
