package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.lejian.model.Denounce;
import com.puzheng.lejian.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by xc on 16-2-18.
 */
public class DenounceStore {
    private static DenounceStore instance = new DenounceStore();

    private DenounceStore() {

    }

    public static DenounceStore getInstance() {
        return instance;
    }

    public Deferrable<Denounce, Void> denounce(String token, String reason, Pair<Double, Double> lnglat) {
        final Deferred<Denounce, Void> deferred = new Deferred<>();
        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        if (!AuthStore.getInstance().isAnonymous()) {
            builder.header("Authorization", "Bearer " + AuthStore.getInstance().getUser().getToken());
        }

        final Denounce denounce = new Denounce(reason, token, lnglat.first, lnglat.second);
        Uri uri = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon().path("/denounce/object").build();
        final Request request = builder.url(uri.toString()).method("POST",
                RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(denounce))).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                deferred.reject(null);
            }

            @Override
            public void onResponse(final okhttp3.Response response) throws IOException {
                final String data = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            deferred.resolve(denounce);
                        } else {
                            onFailure(request, null);
                        }

                    }
                });

            }
        });


        return deferred;
    }


}
