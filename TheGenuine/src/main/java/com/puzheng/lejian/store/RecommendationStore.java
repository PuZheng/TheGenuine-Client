package com.puzheng.lejian.store;

import android.os.Handler;
import android.util.Pair;

import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.util.FakeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xc on 16-1-24.
 */
public class RecommendationStore {
    private static volatile RecommendationStore instance;
    public final static String SAME_TYPE = "SAME_TYPE";

    private RecommendationStore() {

    }

    public static synchronized RecommendationStore getInstance() {
        if (instance == null) {
            instance = new RecommendationStore();
        }
        return instance;
    }

    public Deferrable<List<SPU>, Pair<String, String>> fetchList(String type) {
        final Deferrable<List<SPU>, Pair<String, String>> deferred = new Deferred<List<SPU>, Pair<String, String>>();
        final Handler handler = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<SPU> spus = new ArrayList<SPU>();
                        for (int i = 0; i < 6; ++i) {
                            spus.add(FakeUtil.getInstance().spu());
                        }
                        deferred.resolve(spus);
                    }
                });
            }
        }, 2000);
        return deferred;
    }
}
