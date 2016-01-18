package com.puzheng.the_genuine.store;

import android.os.Handler;
import android.util.Pair;

import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.the_genuine.model.SPUType;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SPUTypeStore {

    private static SPUTypeStore instance;

    private SPUTypeStore() {

    }

    public static synchronized SPUTypeStore getInstance() {
        if (instance == null) {
            instance = new SPUTypeStore();
        }
        return instance;
    }

    public Deferrable<List<SPUType>, Pair<String, String>> fetchList() {
        final Deferred<List<SPUType>, Pair<String, String>> ret = new Deferred<List<SPUType>, Pair<String, String>>();
        final Handler handler = new Handler();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ret.reject(new Pair("", ""));
                    }
                });
            }
        }, 1000);
        return ret;
    }
}
