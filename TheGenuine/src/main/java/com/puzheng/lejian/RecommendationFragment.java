package com.puzheng.lejian;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.puzheng.lejian.model.Recommendation;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.netutils.WebService;

import java.util.List;

/**
 * Created by xc on 13-11-21.
 */
public class RecommendationFragment extends ListFragment implements RefreshInterface {
    public static final int SAME_CATEGORY = 3;
    private static final String NEARYBY = "nearby";
    private static final String SAME_VENDOR = "same_vendor";
    private static final String SAME_TYPE = "same_type";
    private String queryType;
    private int spuId;
    private MaskableManager maskableManager;

    public RecommendationFragment() {

    }



    public RecommendationFragment setQueryType(String queryType) {
        this.queryType = queryType;
        return this;
    }

    public RecommendationFragment setSPUId(int spuId) {
        this.spuId = spuId;
        return this;
    }


    public static RecommendationFragment createNearByProductsFragment(int spuId) {
        return new RecommendationFragment().setQueryType(NEARYBY).setSPUId(spuId);
    }

    public static RecommendationFragment createSameTypeProductsFragment(int spuId) {
        return new RecommendationFragment().setQueryType(SAME_TYPE).setSPUId(spuId);
    }

    public static RecommendationFragment createSameVendorProductsFragment( int spuId) {
        return new RecommendationFragment().setQueryType(SAME_VENDOR).setSPUId(spuId);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Recommendation recommendation = (Recommendation) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), SPUActivity.class);
        intent.putExtra(Const.TAG_SPU_ID, recommendation.getSPUId());
        intent.putExtra(Const.TAG_SPU_NAME, recommendation.getProductName());
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        maskableManager = new MaskableManager(getListView(), this);
//        new GetRecommendationsTask(this, queryType, spuId).execute();
    }

    @Override
    public void refresh() {
        new GetRecommendationsTask(this, queryType, spuId).execute();
    }

    class GetRecommendationsTask extends AsyncTask<Void, Void, List<Recommendation>> {
        private Exception exception;
        private final ListFragment listFragment;
        private final String queryType;
        private final int spuId;

        GetRecommendationsTask(ListFragment listFragment, String queryType, int spuId) {
            this.listFragment = listFragment;
            this.queryType = queryType;
            this.spuId = spuId;
        }

        @Override
        protected List<Recommendation> doInBackground(Void... params) {
            try {
                return  WebService.getInstance(getActivity()).getRecommendations(queryType, spuId);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Recommendation> recommendations) {
            if (maskableManager.unmask(exception)) {
                if (recommendations != null && !recommendations.isEmpty()) {
                    listFragment.setListAdapter(new RecommendationListAdapter(recommendations, getActivity(), (ImageFetcherInteface) getActivity()));
                    return;
                }else {
                    listFragment.setEmptyText(listFragment.getString(R.string.search_no_result_found));
                }
            }
            listFragment.setListAdapter(null);
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }
    }

}
