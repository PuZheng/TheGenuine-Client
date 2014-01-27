package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Pair;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.LocateErrorException;
import com.puzheng.the_genuine.utils.Misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyApp extends Application {
    public static final int LOGIN_ACTION = 1;
    public static ServiceConnection connection;
    private static User user;
    private static Context context;
    private static LocationService mLocationService;
    public static String SHAREURL;
    private WebService webServieHandler;
    public static boolean isNetworkSettingDialogShowed = false;
    public static boolean isGPSSettingDialogShowed = false;

    public static String SHARETEMPLATE = null;
    public static boolean SHAREMEDIA = false;

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
        new GetShareTemplateClass().execute();
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

    private class GetShareTemplateClass extends AsyncTask<Void, Void, HashMap<String, Object>>{

        @Override
        protected HashMap<String, Object> doInBackground(Void... params) {
            try {
                ArrayList<String> configs = new ArrayList<String>();
                configs.add("share_content");
                configs.add("spu_share_media");
                configs.add("spu_share_url");
                return WebService.getInstance(context).getConfigs(configs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> map) {
            if (map != null) {
                MyApp.SHARETEMPLATE = (String) map.get("share_content");
                MyApp.SHAREMEDIA = (Boolean) map.get("spu_share_media");
                MyApp.SHAREURL = (String) map.get("spu_share_url");
            }
        }
    }

}
