package com.puzheng.the_genuine;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import com.puzheng.the_genuine.data_structure.Recommendation;
import com.puzheng.the_genuine.netutils.WebService;

import java.util.List;

/**
 * Created by xc on 13-11-21.
 */
public class RecommendationsFragment extends ListFragment implements RefreshInterface {
    public static final int SAME_CATEGORY = 3;
    private static final String NEARYBY = "nearby";
    private static final String SAME_VENDOR = "same_vendor";
    private String queryType;
    private int mSpuId;
    private MaskableManager maskableManager;

    public RecommendationsFragment(String queryType, int spu_id) {
        this.queryType = queryType;
        this.mSpuId = spu_id;
    }

    public static RecommendationsFragment createNearByProductsFragment(int productId) {
        return new RecommendationsFragment(NEARYBY, productId);
    }

    public static RecommendationsFragment createSameVendorProductsFragment( int productId) {
        return new RecommendationsFragment(SAME_VENDOR, productId);
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
        maskableManager = new MaskableManager(getListView(), this);
        new GetRecommendationsTask(this, queryType, mSpuId).execute();
    }

    @Override
    public void refresh() {
        new GetRecommendationsTask(this, queryType, mSpuId).execute();
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
                }else {
                    listFragment.setListAdapter(null);
                    listFragment.setEmptyText(listFragment.getString(R.string.search_no_result_found));
                }
            }
        }

        @Override
        protected void onPreExecute() {
            maskableManager.mask();
        }
    }

}
