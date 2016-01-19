package com.puzheng.the_genuine.store;

import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.the_genuine.model.SPUType;
import com.puzheng.the_genuine.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;


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
        OkHttpClient client = new OkHttpClient();
        try {
            URL url = new URL(new URL(ConfigUtil.getInstance().getBackend()),
                    "spu-type/list");
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(final okhttp3.Response response) throws IOException {
                    final String data = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject object = new JSONObject(data);
                                final Type type = new TypeToken<List<SPUType>>() {
                                }.getType();
                                final Gson gson = new Gson();
                                ret.resolve((List<SPUType>) gson.fromJson(object.getString("data"), type));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
