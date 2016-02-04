package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.lejian.model.Retailer;
import com.puzheng.lejian.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RetailerStore {

    private static volatile RetailerStore instance;

    private RetailerStore() {

    }

    public static synchronized RetailerStore getInstance() {
        if (instance == null) {
            instance = new RetailerStore();
        }

        return instance;
    }

    public Deferrable<List<Retailer>, Void> fetchList(Map<String, String> params) {
        final Deferrable<List<Retailer>, Void> deferrable = new Deferred<List<Retailer>, Void>();
        Uri.Builder builder = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                .path("retailer/list");
        builder.appendQueryParameter("sortBy", "distance.asc");
        for (Map.Entry<String, String> entry: params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(builder.build().toString()).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                deferrable.reject(null);
            }

            @Override
            public void onResponse(final okhttp3.Response response) throws IOException {
                final String data = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                deferrable.resolve(new Gson().<List<Retailer>>fromJson(
                                        jsonObject.getString("data"),
                                        new TypeToken<List<Retailer>>() {
                                        }.getType()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            onFailure(request, null);
                        }
                    }
                });

            }
        });
        return deferrable;
    }
}
