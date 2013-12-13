package com.puzheng.the_genuine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.puzheng.the_genuine.data_structure.MyLocationData;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 12-13.
 */
public class BaiduMapService extends Service {
    public static final String MY_LOCATION_ACTION = "com.puzheng.the_genuine.location";
    private MyLocationData mLocationData;
    private LocationClient mLocationClient;

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
        mLocationData = new MyLocationData();
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
                mLocationData.setLatitude( bdLocation.getLatitude());
                mLocationData.setLongitude(bdLocation.getLongitude());
                mLocationData.setAccuracy(bdLocation.getRadius());
                mLocationData.setDirection(bdLocation.getDerect());
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
    }
}
