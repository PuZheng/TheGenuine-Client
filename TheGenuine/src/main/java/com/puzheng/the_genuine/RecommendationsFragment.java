package com.puzheng.the_genuine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;

import java.util.List;

/**
 * Created by xc on 13-11-21.
 */
public class RecommendationsFragment extends ListFragment implements Maskable {
    public static final int SAME_CATEGORY = 3;
    private static final String NEARYBY = "nearby";
    private static final String SAME_VENDOR = "same_vendor";
    private Context context;
    private String queryType;
    private int mSpuId;
    private View mask;
    private View error;
    private View no_data;

    public RecommendationsFragment(Context context, String queryType, int spu_id) {
        this.context = context;
        this.queryType = queryType;
        this.mSpuId = spu_id;
    }

    public static RecommendationsFragment createNearByProductsFragment(Context context, int productId) {
        return new RecommendationsFragment(context, NEARYBY, productId);
    }

    public static RecommendationsFragment createSameVendorProductsFragment(Context context, int productId) {
        return new RecommendationsFragment(context, SAME_VENDOR, productId);
    }

    @Override
    public void mask() {
        mask.setVisibility(View.VISIBLE);
        getListView().setVisibility(View.GONE);
        error.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment_base, container, false);
        mask = rootView.findViewById(R.id.mask);
        error = rootView.findViewById(R.id.error);
        no_data = rootView.findViewById(R.id.no_data);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Recommendation recommendation = (Recommendation) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), SPUActivity.class);
        intent.putExtra(Constants.TAG_SPU_ID, recommendation.getSPUId());
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        new GetRecommendationsTask(this, queryType, mSpuId).execute();
    }

    @Override
    public void unmask(Boolean b) {
        if (b) {
            if (getListAdapter().getCount() == 0) {
                getListView().setVisibility(View.GONE);
                no_data.setVisibility(View.VISIBLE);
            } else {
                getListView().setVisibility(View.VISIBLE);
                no_data.setVisibility(View.GONE);
            }
            error.setVisibility(View.GONE);
        } else {
            getListView().setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        }
        mask.setVisibility(View.GONE);
    }

    class GetRecommendationsTask extends AsyncTask<Void, Void, Boolean> {

        private final Maskable maskable;
        private final ListFragment listFragment;
        private final String queryType;
        private final int spuId;
        private List<Recommendation> recommendations;

        GetRecommendationsTask(ListFragment listFragment, String queryType, int spuId) {
            this.maskable = (Maskable) listFragment;
            this.listFragment = listFragment;
            this.queryType = queryType;
            this.spuId = spuId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                recommendations = WebService.getInstance(getActivity()).getRecommendations(queryType, spuId);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b && recommendations != null) {
                listFragment.setListAdapter(new RecommendationListAdapter(recommendations, getActivity()));
            }
            this.maskable.unmask(b);
        }

        @Override
        protected void onPreExecute() {
            maskable.mask();
        }
    }

}
