package com.puzheng.the_genuine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.*;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class BaiduMapFragment extends Fragment {
    private BMapManager mBMapManager;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private LocationData mLocationData;
    private MapController mMapController;
    private MyLocationOverlay myLocationOverlay = null;

    boolean isFirstLoc = true;//是否首次定位

    private final static String KEY = "UTW8RC2pRPD9oGrGn8jXgcnO";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBMapManager = new BMapManager(this.getActivity());
        mBMapManager.init(KEY, null);
        mLocationClient = new LocationClient(this.getActivity());
        mLocationClient.setAK(KEY);
        mLocationData = new LocationData();

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(12);

        mLocationClient.registerLocationListener(new MyLocationListenner());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        myLocationOverlay = new MyLocationOverlay(mMapView);
        //设置定位数据
        myLocationOverlay.setData(mLocationData);
        //添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();

        mMapView.refresh();
        return rootView;
    }

    @Override
    public void onDestroy() {
        mMapView.destroy();
        if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        if (mBMapManager != null) {
            mBMapManager.stop();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        if (mBMapManager != null) {
            mBMapManager.start();
        }
        super.onResume();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            mLocationData.latitude = location.getLatitude();
            mLocationData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            mLocationData.accuracy = location.getRadius();
            // 此处可以设置 mLocationData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            mLocationData.direction = location.getDerect();
            //更新定位数据
            myLocationOverlay.setData(mLocationData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();
            //是手动触发请求或首次定位时，移动到定位点
            if (isFirstLoc) {
                //移动地图到定位点
                mMapController.animateTo(new GeoPoint((int) (mLocationData.latitude * 1e6), (int) (mLocationData.longitude * 1e6)));
                myLocationOverlay.setLocationMode(MyLocationOverlay.LocationMode.FOLLOWING);
            }
            //首次定位完成
            isFirstLoc = false;
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

}
