package com.puzheng.the_genuine.store;

import android.util.Pair;

import com.google.gson.Gson;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.util.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by xc on 16-1-14.
 */
public class AuthStore extends Deferred<User, Pair<String, String>> {
    public static final String INVALID_PASSWORD_OR_EMAIL = "INVALID_PASSWORD_OR_EMAIL";
    private static volatile AuthStore instance;


    private AuthStore() {
    }

    public static synchronized AuthStore getInstance() {
        if (instance == null) {
            instance = new AuthStore();
        }
        return instance;
    }

    interface AuthService {
        @POST("auth/login")
        Call<User> login(@Body Map<String, String> params);
    }

    public AuthStore login(String email, String password) {
        AuthService service = ServiceGenerator.createService(AuthService.class);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        service.login(params).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.isSuccess()) {
                    getDoneHandler().done(response.body());
                } else {
                    Pair err = response.code() == 403 ? new Pair(INVALID_PASSWORD_OR_EMAIL, "") : null;
                    getFailHandler().fail(err);
                }
                getAlwaysHandler().always();
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO
            }
        });
        return this;

//                (new AsyncTask<Void, Void, Boolean>() {
//
//                    public User user;
//
//                    @Override
//                    protected Boolean doInBackground(Void... params) {
//
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(final Boolean success) {
//                        if (success) {
//                            getDoneHandler().done(user);
//                        } else {
////                    getFailHandler().fail();
//                        }
//                    }
//                }).execute((Void) null);
//        return this;
    }
}
