package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MyApp extends Application {
    public static final int LOGIN_ACTION = 1;
    private static User user;
    private static Context context;
    private WebService webServieHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        webServieHandler = WebService.getInstance(MyApp.context);
        Misc.assertDirExists(Misc.getStorageDir());
    }

    public static User getCurrentUser() {
        if (user == null) {
            user = Misc.readUserPrefs(context);
        }
        return user;
    }

    public static void setCurrentUser(User user) {
        MyApp.user = user;
        Misc.storeUserPrefs(user, context);
    }

    public static void unsetCurrentUser() {
        MyApp.user = null;
        Misc.clearUserPrefs(MyApp.context);
    }

    public static Pair<String, Integer> getServerAddress() {

        return Misc.getServerAddress(context);
    }

    public static Pair<Float, Float> getLocation() {
        //TODO fake location
        return new Pair<Float, Float>(0.0F, 0.0F);
    }

    public static void doLoginIn(Activity activity) {
        doLoginIn(activity, null);
    }

    public static void doLoginIn(Activity activity, HashMap<String, Serializable> params) {
        Intent intent = new Intent(activity, LoginActivity.class);
        if (params != null) {
            for (Map.Entry<String, Serializable> entry : params.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        activity.startActivityForResult(intent, LOGIN_ACTION);
    }
}
