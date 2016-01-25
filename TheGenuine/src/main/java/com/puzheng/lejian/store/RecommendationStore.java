package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xc on 16-1-24.
 */
public class RecommendationStore {
    public static final String SAME_VENDOR = "SAME_VENDOR";
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

    public Deferrable<List<SPU>, Pair<String, String>> fetchList(SPU spu, String type, Map<String, String> params) {
        final Deferrable<List<SPU>, Pair<String, String>> deferred = new Deferred<List<SPU>, Pair<String, String>>();
        final Handler handler = new Handler();
        OkHttpClient okHttpClient = new OkHttpClient();
        Uri.Builder builder = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                .path("/recommendation/" + spu.getId())
                .appendQueryParameter("type", type);
        if (params != null) {
            for (Map.Entry<String, String> entry: params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Uri uri = builder.build();

        Request request = new Request.Builder().url(uri.toString()).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    final String data = new JSONObject(response.body().string()).getString("data");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            deferred.resolve(
                                    new Gson().<List<SPU>>fromJson(
                                            data,
                                            new TypeToken<List<SPU>>() {
                                            }.getType()
                                    ));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        return deferred;
    }

    public Deferrable<List<SPU>, Pair<String, String>> fetchList(SPU spu, String type) {
        return fetchList(spu, type, null);
    }
}
