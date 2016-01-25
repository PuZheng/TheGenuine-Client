package com.puzheng.lejian.store;

import android.content.Context;
import android.os.Handler;
import android.util.Pair;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;
import com.puzheng.deferred.Deferrable;
import com.puzheng.deferred.Deferred;
import com.puzheng.deferred.LazyDeferred;

public class LocationStore {

    private static volatile LocationStore instance;
    private AMapLocationClient locationClient;
    private double lng;
    private double lat;
    private String errInfo;
    private int errCode;

    private LocationStore() {

    }

    public static synchronized LocationStore getInstance() {
        if (instance == null) {
            instance = new LocationStore();
        }
        return instance;
    }

    public void setup(Context context) {
        locationClient = new AMapLocationClient(context);
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (location != null) {
                    if (location.getErrorCode() == 0) {
                        lng = location.getLongitude();
                        lat = location.getLatitude();
                        Logger.i("located at " + String.format("%f,%f", lng, lat));
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        errCode = location.getErrorCode();
                        errInfo = location.getErrorInfo();
                        Logger.e("AmapError" + "location Error, ErrCode:"
                                + location.getErrorCode() + ", errInfo:"
                                + location.getErrorInfo());
                    }
                }
            }
        });
        locationClient.startLocation();
    }

    public Deferrable<Pair<Double, Double>, Pair<Integer, String>> getLocation() {
        return new LazyDeferred<Pair<Double, Double>, Pair<Integer, String>>() {
            @Override
            public void onStart() {
                if (lng != 0 && lat != 0) {
                    resolve(Pair.create(lng, lat));
                } else {
                    reject(Pair.create(errCode, errInfo));
                }
            }
        };
    }

}
