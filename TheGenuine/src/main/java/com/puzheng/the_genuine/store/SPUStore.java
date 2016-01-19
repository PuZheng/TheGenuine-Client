package com.puzheng.the_genuine.store;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.the_genuine.model.SPU;
import com.puzheng.the_genuine.model.SPUType;
import com.puzheng.the_genuine.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

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
        final Deferred<List<SPU>, Pair<String, String>> ret = new Deferred<List<SPU>, Pair<String, String>>();
        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        Uri.Builder builder = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon().path("spu/list");
        for (Map.Entry<String, String> entry: query.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(builder.build().toString()).build();
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
                            ret.resolve((List<SPU>) gson.fromJson(object.getString("data"), type));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        return ret;
    }
}
