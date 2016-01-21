package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.LazyDeferred;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
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


    public Deferrable<List<SPU>, Pair<String, String>> fetchList(final Map<String, String> query) {
        Uri.Builder builder = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon().path("spu/list");
        for (Map.Entry<String, String> entry: query.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        try {
            final URL url = new URL(builder.build().toString());
            return new LazyDeferred<List<SPU>, Pair<String, String>>() {
                @Override
                public void onStart() {
                    final Deferrable<List<SPU>, Pair<String, String>> deferrable = this;
                    final Handler handler = new Handler();
                    OkHttpClient client = new OkHttpClient();

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
                                        final Type type = new TypeToken<List<SPU>>() {
                                        }.getType();
                                        final Gson gson = new Gson();
                                        deferrable.resolve((List<SPU>) gson.fromJson(object.getString("data"), type));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    });
                }
            };
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
