package com.puzheng.lejian.view;

import android.util.Pair;

import com.orhanobut.logger.Logger;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.deferred.FailHandler;
import com.puzheng.lejian.RecommendationFragment;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.store.LocationStore;
import com.puzheng.lejian.store.RecommendationStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SameVendorRecommendationFragment extends RecommendationFragment {
    @Override
    public Deferrable<List<SPU>, Pair<String, String>> fetchRecommendations(final SPU spu) {
        Logger.i("fetch recommendations for %d", spu.getId());
        final Deferrable<List<SPU>, Pair<String, String>> deferred = new Deferred<List<SPU>, Pair<String, String>>();
        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
            @Override
            public void done(Pair<Double, Double> lnglat) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lnglat", String.format("%f,%f", lnglat.first, lnglat.second));
                RecommendationStore.getInstance().fetchList(spu, RecommendationStore.SAME_VENDOR, params).done(new DoneHandler<List<SPU>>() {
                    @Override
                    public void done(List<SPU> spus) {
                        deferred.resolve(spus);
                    }
                });
            }
        }).fail(new FailHandler<Pair<Integer, String>>() {
            @Override
            public void fail(Pair<Integer, String> integerStringPair) {
                RecommendationStore.getInstance().fetchList(spu, RecommendationStore.SAME_VENDOR).done(new DoneHandler<List<SPU>>() {
                    @Override
                    public void done(List<SPU> spus) {
                        deferred.resolve(spus);
                    }
                });
            }
        });
        return deferred;
    }
}
