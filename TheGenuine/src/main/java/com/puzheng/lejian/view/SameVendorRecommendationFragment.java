package com.puzheng.lejian.view;

import android.util.Pair;

import com.puzheng.deferred.Deferrable;
import com.puzheng.lejian.RecommendationFragment;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.store.RecommendationStore;

import java.util.List;

public class SameVendorRecommendationFragment extends RecommendationFragment {
    @Override
    public Deferrable<List<SPU>, Pair<String, String>> fetchRecommendations(SPU spu) {
        return RecommendationStore.getInstance().fetchList(spu, RecommendationStore.SAME_VENDOR);
    }
}
