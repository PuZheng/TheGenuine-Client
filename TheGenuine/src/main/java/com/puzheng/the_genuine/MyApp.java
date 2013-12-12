package com.puzheng.the_genuine;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.puzheng.the_genuine.data_structure.User;
import com.puzheng.the_genuine.netutils.WebService;
import com.puzheng.the_genuine.utils.Misc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class MyApp extends Application {
    public static final int LOGIN_ACTION = 1;
    private static User user;
    private static Context context;
    private WebService webServieHandler;
    private static LocationData mLocationData;
    private static LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        webServieHandler = WebService.getInstance(MyApp.context);
        Misc.assertDirExists(Misc.getStorageDir());
        initMap();
    }

    @Override
    public void onTerminate() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onTerminate();
    }

    private void initMap() {
        if (mLocationClient != null) {
            return;
        }
        mLocationClient = new LocationClient(MyApp.context);
        mLocationClient.setAK(Constants.BAIDU_MAP_KEY);
        mLocationData = new LocationData();
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.disableCache(true);//禁止启用缓存定位
        option.setPoiDistance(1000); //poi查询距离
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLocationData.latitude = bdLocation.getLatitude();
                mLocationData.longitude = bdLocation.getLongitude();
                mLocationData.accuracy = bdLocation.getRadius();
                mLocationData.direction = bdLocation.getDerect();
            }

            @Override
            public void onReceivePoi(BDLocation bdLocation) {

            }
        });
        mLocationClient.start();

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

    public static Pair<Double, Double> getLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        }
        return new Pair<Double, Double>(mLocationData.longitude, mLocationData.latitude);
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
