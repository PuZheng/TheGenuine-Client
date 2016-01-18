package com.puzheng.the_genuine.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Pair;

import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.the_genuine.MyApp;
import com.puzheng.the_genuine.R;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.util.Misc;
import com.puzheng.the_genuine.util.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by xc on 16-1-14.
 */
public class AuthStore {
    public static final String INVALID_PASSWORD_OR_EMAIL = "INVALID_PASSWORD_OR_EMAIL";
    public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
    private static volatile AuthStore instance;
    private User user;


    private AuthStore() {
    }

    public static synchronized AuthStore getInstance() {
        if (instance == null) {
            instance = new AuthStore();
        }
        return instance;
    }


    public Deferrable<User, Pair<String, String>> register(String email, String password) {
        final Deferrable<User, Pair<String, String>> ret = new Deferred<User, Pair<String, String>>();
        AuthService service = ServiceGenerator.createService(AuthService.class);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        service.register(params).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.isSuccess()) {
                    user = response.body();
                    storeUser(user);
                    ret.resolve(user);
                } else {
                    Pair err = response.code() == 403 ? new Pair(EMAIL_EXISTS,
                            MyApp.getContext().getString(R.string.email_exists)) : null;
                    ret.reject(err);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO
            }
        });
        return ret;
    }

    private void storeUser(User user) {

        SharedPreferences pref = MyApp.getContext().getSharedPreferences(
                "user", Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("id", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("token", user.getToken());
        editor.commit();
    }

    public Deferrable<User, Pair<String, String>> login(String email, String password) {
        final Deferrable<User, Pair<String, String>> ret = new Deferred<User, Pair<String, String>>();
        AuthService service = ServiceGenerator.createService(AuthService.class);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        service.login(params).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.isSuccess()) {
                    user = response.body();
                    storeUser(user);
                    ret.resolve(user);
                } else {
                    Pair err = response.code() == 403 ? new Pair(
                            INVALID_PASSWORD_OR_EMAIL, "") : null;
                    ret.reject(err);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO
            }
        });
        return ret;
    }

    public User getUser() {
        if (user == null) {
            SharedPreferences preferences = MyApp.getContext().getSharedPreferences("user",
                    Context.MODE_PRIVATE);
            int id = preferences.getInt("id", 0);
            String email = preferences.getString("email", null);
            String token = preferences.getString("token", null);
            if (id == -1 || email == null || token == null) {
                return null;
            }
            user = new User(id, email, token);
        }
        return user;
    }

    public boolean isAnonymous() {
        return user == null;
    }

    public void logout() {
        user = null;
        SharedPreferences preferences = MyApp.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    interface AuthService {
        @POST("auth/login")
        Call<User> login(@Body Map<String, String> params);

        @POST("auth/register")
        Call<User> register(@Body Map<String, String> params);
    }
}
