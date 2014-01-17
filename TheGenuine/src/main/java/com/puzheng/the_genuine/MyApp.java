package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Pair;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.LocateErrorException;
import com.puzheng.the_genuine.utils.Misc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class MyApp extends Application {
    public static final int LOGIN_ACTION = 1;
    public static ServiceConnection connection;
    private static User user;
    private static Context context;
    private static LocationService mLocationService;
    private WebService webServieHandler;
    public static boolean isNetworkSettingDialogShowed = false;
    public static boolean isGPSSettingDialogShowed = false;

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

    public static Location getLocation() throws LocateErrorException {
        if (mLocationService != null) {
            Location location = mLocationService.getLocation();
            if (location != null) {
                return location;
            }
        }
        throw new LocateErrorException(context.getString(R.string.locate_error));
    }


    public static Pair<String, Integer> getServerAddress() {
        return Misc.getServerAddress(context);
    }

    public static boolean isGooglePlayServiceAvailable() {
        return ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        webServieHandler = WebService.getInstance(MyApp.context);
        Misc.assertDirExists(Misc.getStorageDir());
        connectLocationService();
    }

    public static void unsetCurrentUser() {
        MyApp.user = null;
        Misc.clearUserPrefs(MyApp.context);
    }

    private void connectLocationService() {
        Intent intent = new Intent(context, LocationService.class);
        if (connection == null) {
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mLocationService = ((LocationService.LocationBind) service).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mLocationService = null;
                }
            };
        }

        bindService(intent, connection, BIND_AUTO_CREATE);
    }
}
