package com.puzheng.lejian;

import android.util.Pair;

import com.orhanobut.logger.Logger;
import com.puzheng.deferred.Deferrable;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.store.RecommendationStore;

import java.util.List;

/**
 * Created by xc on 16-1-24.
 */
public class SameTypeRecommendationFragment extends RecommendationFragment {

    public SameTypeRecommendationFragment() {
    }

    @Override
    public Deferrable<List<SPU>, Pair<String, String>> fetchRecommendations(SPU spu) {
        Logger.i("fetch recommendations for %d", spu.getId());
        return RecommendationStore.getInstance().fetchList(spu, RecommendationStore.SAME_TYPE);
    }
}
