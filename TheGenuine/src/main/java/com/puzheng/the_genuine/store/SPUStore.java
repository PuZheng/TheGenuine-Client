package com.puzheng.the_genuine.store;

import android.util.Pair;

import com.puzheng.deferred.Deferrable;
import com.puzheng.the_genuine.model.SPU;

import java.util.List;
import java.util.Map;

public class SPUStore {
    private static SPUStore instance;

    private SPUStore() {

    }

    public static synchronized SPUStore getInstance() {
        if (instance == null) {
            instance = new SPUStore();
        }
        return instance;
    }


    public Deferrable<List<SPU>, Pair<String, String>> fetchList(Map<String, String> query) {
        return null;
    }
}
