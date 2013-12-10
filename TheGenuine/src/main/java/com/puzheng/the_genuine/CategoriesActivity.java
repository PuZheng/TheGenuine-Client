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
import com.puzheng.the_genuine.search.SearchActivity;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.views.NavBar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CategoriesActivity extends ActionBarActivity implements Maskable, BackPressedInterface {

    private View mask;
    private GridView gridView;
    private BackPressedHandle backPressedHandle = new BackPressedHandle();
    private boolean isFavorActivity = false;

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
        isFavorActivity = getIntent().getBooleanExtra("Favor", false);
        setContentView(R.layout.activity_categories);
        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
        mask = findViewById(R.id.mask);
        gridView = (GridView) findViewById(R.id.gridView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("分类列表");
        if (isFavorActivity) {
            actionBar.setTitle("您的收藏");
            navBar.enableTab(NavBar.FAVOR, true);
            if (MyApp.getCurrentUser() == null) {
                HashMap<String, Serializable> map = new HashMap<String, Serializable>();
                map.put("ISTOPACTIVITY", true);
                MyApp.doLoginIn(CategoriesActivity.this, map);
                return;
            }
        } else {
            actionBar.setTitle("360真品");
        }
        new GetCategoriesTask(gridView, this).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApp.LOGIN_ACTION) {
                new GetCategoriesTask(gridView, this).execute();
            }
        }
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
                if (isFavorActivity) {
                    categories = WebService.getInstance(gridView.getContext()).getFavorCategories();
                } else {
                    categories = WebService.getInstance(gridView.getContext()).getCategories();
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                gridView.setAdapter(new MyCategoriesAdapter(categories));
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

        private class ViewHolder {
            ImageButton imageButton;

            ViewHolder(ImageButton imageButton) {
                this.imageButton = imageButton;
            }

        }
    }
}
