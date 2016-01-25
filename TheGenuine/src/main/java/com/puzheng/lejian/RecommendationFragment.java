package com.puzheng.lejian;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.AlwaysHandler;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.adapter.RecommendationListAdapter;
import com.puzheng.lejian.model.Recommendation;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.netutils.WebService;

import java.util.List;

/**
 * Created by xc on 13-11-21.
 */
public abstract class RecommendationFragment extends ListFragment implements RefreshInterface {

    private MaskableManager maskableManager;
    private com.puzheng.lejian.model.SPU spu;
    private List<SPU> spus;

    public RecommendationFragment() {

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
        SPU spu = getArguments().getParcelable(Const.TAG_SPU);

        if (spus == null) {
            this.spu = spu;
            fetchRecommendations(spu).done(new DoneHandler<List<SPU>>() {
                @Override
                public void done(List<SPU> spus) {
                    Logger.i("recommendations fetched");
                    Logger.json(new Gson().toJson(spus));
                    RecommendationFragment.this.spus = spus;
                    if (spus.size() != 0) {
                        setListAdapter(new RecommendationListAdapter(spus));
                    } else {
                        setEmptyText(getActivity().getString(R.string.no_recommendations));
                    }

                }
            }).fail(new FailHandler<Pair<String, String>>() {
                @Override
                public void fail(Pair<String, String> stringStringPair) {

                }
            }).always(new AlwaysHandler() {
                @Override
                public void always() {

                }
            });
        }
    }

    abstract public Deferrable<List<SPU>, Pair<String, String>> fetchRecommendations(SPU spu);

    @Override
    public void refresh() {

//        new GetRecommendationsTask(this, queryType, spuId).execute();
    }

}
