package com.puzheng.the_genuine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;

import com.puzheng.the_genuine.data_structure.Category;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.views.NavBar;

import java.util.List;

public class CategoriesActivity extends ActionBarActivity implements Maskable, BackPressedInterface {

    private View mask;
    private GridView gridView;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();

    @Override
    public void onBackPressed() {
        backPressedHandle.doBackPressed(this, this);
    }

    @Override
    public void doBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);

        ActionBar actionBar = getSupportActionBar();
        if (getIntent().getBooleanExtra("Favor", false)) {
            actionBar.setTitle("您的收藏");
            navBar.enableTab(NavBar.FAVOR, true);
        } else {
            actionBar.setTitle("360真品");
        }
        actionBar.setSubtitle("分类列表");
        mask = findViewById(R.id.mask);
        gridView = (GridView) findViewById(R.id.gridView);
        new GetCategoriesTask(gridView, this).execute();

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
                Intent intent = new Intent(this, ProductListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void mask() {
        mask.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
    }

    @Override
    public void unmask(Boolean b) {
        mask.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
    }

    private class GetCategoriesTask extends AsyncTask<Void, Void, Boolean> {
        private final GridView gridView;
        private final Maskable maskable;
        private List<Category> categories;

        public GetCategoriesTask(GridView gridView, Maskable maskable) {
            this.gridView = gridView;
            this.maskable = maskable;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                categories = WebService.getInstance(gridView.getContext()).getCategories();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            maskable.mask();
        }


        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                this.maskable.unmask(b);
                gridView.setAdapter(new MyCategoriesAdapter(categories));
            }
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

        private class ViewHolder {
            ImageButton imageButton;

            ViewHolder(ImageButton imageButton) {
                this.imageButton = imageButton;
            }

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
            final Category comment = (Category) getItem(position);
            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoriesActivity.this, ProductListActivity.class);
                    intent.putExtra("category_id", comment.getId());
                    intent.putExtra("categoryName", comment.getName());
                    startActivity(intent);
                }
            });
            new GetImageTask(viewHolder.imageButton, comment.getPicUrl()).execute();
            return convertView;
        }
    }
}
