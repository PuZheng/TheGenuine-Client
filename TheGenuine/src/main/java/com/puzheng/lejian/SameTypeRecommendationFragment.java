package com.puzheng.lejian;

import android.util.Pair;

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
    public Deferrable<List<SPU>, Pair<String, String>> fetchRecommendations() {
        return RecommendationStore.getInstance().fetchList(RecommendationStore.SAME_TYPE);
    }
}
