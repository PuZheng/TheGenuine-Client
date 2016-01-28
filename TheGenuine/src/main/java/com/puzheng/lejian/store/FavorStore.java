package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.deferred.DoneHandler;
import com.puzheng.lejian.model.Favor;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.model.User;
import com.puzheng.lejian.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static okhttp3.MediaType.*;

public class FavorStore {

    private static volatile FavorStore instance;

    private FavorStore() {

    }

    public static synchronized FavorStore getInstance() {
        if (instance == null) {
            instance = new FavorStore();
        }
        return instance;
    }


    private Deferrable<Void, Void> request(User user, SPU spu, String method) {
        final Deferrable<Void, Void> deferrable = new Deferred<Void, Void>();
        Uri uri = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                .appendPath("favor")
                .appendPath(String.valueOf(spu.getId())).build();
        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), "");
        final Request request = new Request.Builder().url(uri.toString()).header("Authorization",
                "Bearer " + user.getToken()).method(method,
                requestBody).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                deferrable.reject(null);
            }

            @Override
            public void onResponse(final okhttp3.Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            deferrable.resolve(null);
                        } else {
                            onFailure(request, null);
                        }
                    }
                });

            }
        });

        return deferrable;

    }

    public Deferrable<Void, Void> favor(User user, SPU spu) {
        return request(user, spu, "POST");
    }


    public Deferrable<Void, Void> unfavor(User user, SPU spu) {
        return request(user, spu, "DELETE");
    }

    public Deferrable<List<Favor>, Void> fetchList(User user) {
        final Deferrable<List<Favor>, Void> deferrable = new Deferred<List<Favor>, Void>();

        final Uri.Builder builder = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                .path("favor/list");
        LocationStore.getInstance().getLocation().done(new DoneHandler<Pair<Double, Double>>() {
            @Override
            public void done(Pair<Double, Double> lnglat) {
                builder.appendQueryParameter("lnglat", lnglat.first + "," + lnglat.second);
            }
        });

        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(builder.build().toString()).header("Authorization",
                "Bearer " + user.getToken()).build();

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
                                deferrable.resolve(new Gson().<List<Favor>>fromJson(new JSONObject(data).getString("data"),
                                        new TypeToken<List<Favor>>() {}.getType()));
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
