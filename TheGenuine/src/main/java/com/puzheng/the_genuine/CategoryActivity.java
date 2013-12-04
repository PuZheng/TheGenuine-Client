package com.puzheng.the_genuine;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.*;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.views.NavBar;

import java.util.List;


public class CategoryActivity extends ActionBarActivity implements Maskable {
    private SpinnerAdapter mSpinnerAdapter;
    private ActionBar.OnNavigationListener mOnNavigationListener;
    private View mask;

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
        mOnNavigationListener = new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                ListFragment currentFragment = (ListFragment) getFragmentManager().findFragmentById(R.id.container);
                if (currentFragment == null) {
                    currentFragment = new ListFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    new GetRecommendationListByCategoryIdTask(currentFragment, CategoryActivity.this).execute(
                            CategoryActivity.this.getIntent().getIntExtra("category_id", 0));
                    // Replace whatever is in the fragment container with this fragment
                    //  and give the fragment a tag name equal to the string at the position selected
                    ft.replace(R.id.container, currentFragment);
                    ft.commit();
                } else {
                    RecommendationListAdapter adapter = (RecommendationListAdapter) currentFragment.getListAdapter();
                    adapter.sort(position);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        };
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);

        setTitle("360真品-" + getIntent().getStringExtra("categoryName"));
    }

}

class GetRecommendationListByCategoryIdTask extends AsyncTask<Integer, Void, List<Recommendation>> {
    private ListFragment mFragment;
    private Maskable mMaskable;

    public GetRecommendationListByCategoryIdTask(ListFragment fragment, Maskable maskable) {
        super();
        this.mFragment = fragment;
        this.mMaskable = maskable;
    }

    @Override
    protected List<Recommendation> doInBackground(Integer... params) {
        mMaskable.mask();
        try {
            int category_id = params[0];
            return WebService.getInstance(mFragment.getActivity()).getRecommendationsByCategoryId(category_id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Recommendation> list) {
        if (list != null) {
            RecommendationListAdapter adapter = new RecommendationListAdapter(list, mFragment.getActivity());
            mFragment.setListAdapter(adapter);
            adapter.sort();
            adapter.notifyDataSetChanged();
        } else {
            mFragment.setListAdapter(null);
        }
        mMaskable.unmask(true);
    }
}