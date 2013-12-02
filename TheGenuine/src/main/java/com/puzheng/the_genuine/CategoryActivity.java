package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.GetImageTask;
import com.puzheng.the_genuine.utils.Misc;
import com.puzheng.the_genuine.views.NavBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CategoryActivity extends ActionBarActivity implements Maskable {
    private SpinnerAdapter mSpinnerAdapter;
    private ActionBar.OnNavigationListener mOnNavigationListener;
    private List<Recommendation> mRecommendationList;
    private View mask;

    private String[] sortStrings;

    @Override
    public void mask() {
        mask.setVisibility(View.VISIBLE);
    }

    @Override
    public void unmask(Boolean b) {
        mask.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mask = findViewById(R.id.mask);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_LIST);
        mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.short_list, android.R.layout.simple_spinner_dropdown_item);
        sortStrings = getResources().getStringArray(R.array.short_list);
        mOnNavigationListener = new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                ListContentFragment currentFragment = (ListContentFragment) getFragmentManager().findFragmentById(R.id.container);
                if (currentFragment == null) {
                    currentFragment = new ListContentFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    currentFragment.setListAdapter(new RecommendationListAdapter());
                    // Replace whatever is in the fragment container with this fragment
                    //  and give the fragment a tag name equal to the string at the position selected
                    ft.replace(R.id.container, currentFragment);
                    ft.commit();
                }
                currentFragment.sort(sortStrings[position]);
                ((BaseAdapter) currentFragment.getListAdapter()).notifyDataSetChanged();
                return true;
            }
        };
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);

        new GetRecommendationListByCategoryIdTask().execute();

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);

        setTitle("360真品-"+ getIntent().getStringExtra("categoryName"));
    }


    class GetRecommendationListByCategoryIdTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            mask();
            try {
                int category_id = getIntent().getIntExtra("category_id", 0);
                mRecommendationList = WebService.getInstance(CategoryActivity.this).getRecommendationsByCategoryId(
                        category_id);
                return true;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                unmask(true);
            }
        }
    }

    public class ListContentFragment extends ListFragment {

        public void sort(String string) {
            if (string.equals(sortStrings[0])) {
                Collections.sort(mRecommendationList, new Comparator<Recommendation>() {
                    @Override
                    public int compare(Recommendation lhs, Recommendation rhs) {
                        return lhs.getPriceInYuan() - rhs.getPriceInYuan();
                    }
                });
            } else if (string.equals(sortStrings[1])) {
                Collections.sort(mRecommendationList, new Comparator<Recommendation>() {
                    @Override
                    public int compare(Recommendation lhs, Recommendation rhs) {
                        return lhs.getDistance() - rhs.getDistance();
                    }
                });
            } else if (string.equals(sortStrings[2])) {
                Collections.sort(mRecommendationList, new Comparator<Recommendation>() {
                    @Override
                    public int compare(Recommendation lhs, Recommendation rhs) {
                        return lhs.getProductName().compareTo(rhs.getProductName());
                    }
                });
            }
        }
    }

    class ViewHolder {
        ImageView imageView;
        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewDistance;
        RatingBar ratingBar;

        ViewHolder(ImageView imageView, TextView textViewProductName, TextView textViewDistance, TextView textViewPrice, RatingBar ratingBar) {
            this.imageView = imageView;
            this.textViewProductName = textViewProductName;
            this.textViewDistance = textViewDistance;
            this.textViewPrice = textViewPrice;
            this.ratingBar = ratingBar;
        }
    }

    class RecommendationListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public RecommendationListAdapter() {
            inflater = (LayoutInflater) CategoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mRecommendationList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecommendationList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mRecommendationList.get(position).getProductId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.recommendation_category_list_item, null);
            }
            ViewHolder viewHolder;
            if (convertView.getTag() == null) {
                viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.imageView),
                        (TextView) convertView.findViewById(R.id.textViewProductName),
                        (TextView) convertView.findViewById(R.id.textViewDistance),
                        (TextView) convertView.findViewById(R.id.textViewPrice),
                        (RatingBar) convertView.findViewById(R.id.ratingBar));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Recommendation recommendation = (Recommendation) getItem(position);
            new GetImageTask(viewHolder.imageView, recommendation.getPicUrl()).execute();

            viewHolder.textViewProductName.setText(recommendation.getProductName());
            viewHolder.textViewDistance.setText(Misc.humanizeDistance(recommendation.getDistance()));
            viewHolder.textViewPrice.setText("￥" + recommendation.getPriceInYuan());
            viewHolder.ratingBar.setRating(recommendation.getRating());
            return convertView;
        }
    }
}
