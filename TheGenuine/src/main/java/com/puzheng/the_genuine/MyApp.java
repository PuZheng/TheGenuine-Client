package com.puzheng.the_genuine;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;


public class MyApp extends Application {
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
}
