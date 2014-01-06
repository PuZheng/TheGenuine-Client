package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Pair;
import com.baidu.mapapi.map.LocationData;
import com.google.gson.Gson;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.LocateErrorException;
import com.puzheng.the_genuine.utils.Misc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class MyApp extends Application {
    public static final int LOGIN_ACTION = 1;
    private static User user;
    private static Context context;
    private static LocationData mLocationData;
    private BaiduMapBroadcastReceiver receiver;
    private WebService webServieHandler;

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

    public static Pair<Double, Double> getLocation() throws LocateErrorException {
        if (mLocationData != null) {
            return new Pair<Double, Double>(mLocationData.longitude, mLocationData.latitude);
        }
        throw new LocateErrorException(context.getString(R.string.locate_error));
    }

    public static Pair<String, Integer> getServerAddress() {

        return Misc.getServerAddress(context);
    }

    public static void unsetCurrentUser() {
        MyApp.user = null;
        Misc.clearUserPrefs(MyApp.context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        webServieHandler = WebService.getInstance(MyApp.context);
        Misc.assertDirExists(Misc.getStorageDir());

        startService(new Intent(getApplicationContext(), BaiduMapService.class));

        receiver = new BaiduMapBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BaiduMapService.MY_LOCATION_ACTION);
        registerReceiver(receiver, filter);
    }

    private class BaiduMapBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(Constants.TAG_LOCATION_DATA);
            Gson gson = new Gson();
            mLocationData = gson.fromJson(data, LocationData.class);
        }
    }

}
