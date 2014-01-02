package com.puzheng.the_genuine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.google.gson.Gson;
import com.puzheng.the_genuine.utils.Misc;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-13.
 */
public class BaiduMapService extends Service {
    public static final String MY_LOCATION_ACTION = "com.puzheng.the_genuine.location";
    private LocationClient mLocationClient;
    private String mLocationData;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initMap();
    }

    private void initMap() {
        if (mLocationClient != null) {
            return;
        }
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setAK(Constants.BAIDU_MAP_KEY);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.disableCache(false);//禁止启用缓存定位
        option.setPoiDistance(1000); //poi查询距离
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                switch (bdLocation.getLocType()) {
                    case BDLocation.TypeGpsLocation:
                    case BDLocation.TypeCacheLocation:
                    case BDLocation.TypeNetWorkLocation:
                        LocationData data = new LocationData();
                        data.latitude = bdLocation.getLatitude();
                        data.longitude = bdLocation.getLongitude();
                        data.accuracy = bdLocation.getRadius();
                        data.direction = bdLocation.getDerect();
                        Gson gson = new Gson();
                        mLocationData = gson.toJson(data);
                        break;
                    default:
                        mLocationData = null;
                }
                Intent intent = new Intent();
                intent.setAction(MY_LOCATION_ACTION);
                intent.putExtra(Constants.TAG_LOCATION_DATA, mLocationData);
                sendBroadcast(intent);
            }

            @Override
            public void onReceivePoi(BDLocation bdLocation) {

            }
        });
        mLocationClient.start();
    }

    @Override
    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient = null;
        }
        if (mLocationData != null) {
            Misc.storeLastLocation(mLocationData, getApplicationContext());
        }
    }
}

