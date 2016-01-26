package com.puzheng.lejian.store;

import android.net.Uri;
import android.os.Handler;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.lejian.model.Comment;
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

public class CommentStore {

    private static volatile CommentStore instance;

    private CommentStore() {

    }

    public static synchronized CommentStore getInstance() {
        if (instance == null) {
            instance = new CommentStore();
        }
        return instance;
    }

    public Deferrable<List<Comment>, Pair<String, String>> fetchList(int spuId) {
        final Deferrable<List<Comment>, Pair<String, String>> deferrable = new Deferred<List<Comment>,Pair<String,String>>();

        Uri uri = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                .path("/comment/list").appendQueryParameter("spu_id", String.valueOf(spuId)).build();

        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(uri.toString()).build();
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
                                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                List<Comment> comments = gson.fromJson(new JSONObject(data).getString("data"),
                                        new TypeToken<List<Comment>>() {
                                        }.getType());
                                deferrable.resolve(comments);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // TODO show real reason
                            deferrable.reject(null);
                        }

                    }
                });

            }
        });
        return deferrable;
    }

    public Deferrable<Comment, Pair<String, String>> create(Comment comment) {
        Uri uri = Uri.parse(ConfigUtil.getInstance().getBackend()).buildUpon()
                .path("/comment/object").build();


        final Deferrable<Comment, Pair<String, String>> deferrable = new Deferred<Comment, Pair<String, String>>();
        final Handler handler = new Handler();
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(uri.toString())
                .method("POST", RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        new Gson().toJson(comment)))
                .header("Authorization", "Bearer " + AuthStore.getInstance().getUser().getToken())
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                deferrable.reject(null);
            }

            @Override
            public void onResponse(final okhttp3.Response response) throws IOException {
                final String data = response.body().string();
                Logger.i(data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            Comment comment = gson.fromJson(data,
                                    Comment.class);
                            deferrable.resolve(comment);
                        } else {
                            // TODO show real reason
                            deferrable.reject(null);
                        }

                    }
                });

            }
        });
        return deferrable;
    }
}
