package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.lejian.model.SKU;
import com.puzheng.lejian.model.SPU;
import com.puzheng.lejian.util.ConfigUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by xc on 16-2-16.
 */
public class SKUStore {
    private static volatile SKUStore instance = new SKUStore();

    private SKUStore() {

    }

    public static SKUStore getInstance() {
        return instance;
    }

    public Deferrable<SKU, Void> verify(String token, Pair<Double, Double> lnglat) {

        final Deferred<SKU, Void> deferred = new Deferred<>();
        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        Uri uri = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon().path("/sku/verify")
                .appendPath(token).appendQueryParameter("lnglat", lnglat.first + "," + lnglat.second).build();
        Request.Builder builder = new Request.Builder();
        if (!AuthStore.getInstance().isAnonymous()) {
            builder.header("Authorization", "Bearer " + AuthStore.getInstance().getUser().getToken());
        }
        final Request request = builder.url(uri.toString()).build();
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
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                SKU sku = new SKU.Builder().id(jsonObject.getInt("id"))
                                        .token(jsonObject.getString("token"))
                                        .checksum(jsonObject.getString("checksum"))
                                        .verifyCount(jsonObject.getInt("verifyCount"))
                                        .lastVerifiedAt(
                                                new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").parse(jsonObject.getString("lastVerifiedAt")))
                                        .productionDate(
                                                new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("productionDate")))
                                        .expireDate(
                                                new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString("expireDate")))
                                        .spu(new Gson().fromJson(jsonObject.getString("spu"), SPU.class)).build();
                                deferred.resolve(sku);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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
