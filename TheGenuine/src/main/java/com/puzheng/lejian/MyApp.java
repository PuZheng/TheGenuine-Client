package com.puzheng.lejian;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.orhanobut.logger.Logger;
import com.puzheng.lejian.netutils.WebService;
import com.puzheng.lejian.store.LocationStore;
import com.puzheng.lejian.util.LocateErrorException;
import com.puzheng.lejian.util.Misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyApp extends MultiDexApplication {
    public static final int LOGIN_ACTION = 1;
    public static ServiceConnection connection;
    private static Context context;
    private static LocationService mLocationService;
    public static String SHAREURL;
    private WebService webServieHandler;
    public static boolean isNetworkSettingDialogShowed = false;
    public static boolean isGPSSettingDialogShowed = false;

    public static String SHARETEMPLATE = null;
    public static boolean SHAREMEDIA = false;
    private Activity currentActivity;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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

    public static Context getContext() {
        return MyApp.context;
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

    public static boolean isGooglePlayServiceAvailable() {
        return ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
//        webServieHandler = WebService.getInstance(MyApp.context);
        Misc.assertDirExists(Misc.getStorageDir());
        // TODO: 16-1-29 remove this function
        //connectLocationService();

        new GetShareTemplateClass().execute();
        Logger.init(getString(R.string.app_name));
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        LocationStore.getInstance().setup(MyApp.context);

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

    public static Activity getCurrentActivity() {
        return ((MyApp)getContext()).currentActivity;
    }

    private class GetShareTemplateClass extends AsyncTask<Void, Void, HashMap<String, Object>>{

        @Override
        protected HashMap<String, Object> doInBackground(Void... params) {
            try {
                ArrayList<String> configs = new ArrayList<String>();
                configs.add("share_content");
                configs.add("spu_share_media");
                configs.add("spu_share_url");
//                return WebService.getInstance(context).getConfigs(configs);
                return null;
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
