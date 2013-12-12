package com.puzheng.the_genuine;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    private PopupOverlay pop = null;
    private View viewCache = null;
    private View popupInfo = null;
    private TextView popupText = null;
    private MyOverlay mOverlay = null;
    boolean isFirstLoc = true;//是否首次定位

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewCache = inflater.inflate(R.layout.map_text_view, null);
        popupInfo = (View) viewCache.findViewById(R.id.popinfo);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);

        mBMapManager = new BMapManager(this.getActivity());
        mBMapManager.init(Constants.BAIDU_MAP_KEY, null);
        mLocationClient = new LocationClient(this.getActivity());
        mLocationClient.setAK(Constants.BAIDU_MAP_KEY);
        mLocationData = new LocationData();

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(18);

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

    private void addItemOverlay(MapView mapView) {
        if (myLocationOverlay != null) {
            int currentLat = (int) (myLocationOverlay.getMyLocation().latitude * 1E6);
            int currentLon = (int) (myLocationOverlay.getMyLocation().longitude * 1E6);
            GeoPoint p1 = new GeoPoint(currentLat + 1000, currentLon + 1000);
            GeoPoint p2 = new GeoPoint(currentLat + 200, currentLon + 200);
            //准备overlay图像数据，根据实情情况修复
            Drawable mark = getResources().getDrawable(R.drawable.icon_marka);
            Drawable mark2 = getResources().getDrawable(R.drawable.icon_markb);
            //用OverlayItem准备Overlay数据
            OverlayItem item1 = new OverlayItem(p1, "肯德基", "item1");
            //使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置
            OverlayItem item2 = new OverlayItem(p2, "麦当劳", "item2");
            item2.setMarker(mark2);

            //创建IteminizedOverlay
            mOverlay = new MyOverlay(mark, mapView);
            //将IteminizedOverlay添加到MapView中

            mapView.getOverlays().add(mOverlay);

            //现在所有准备工作已准备好，使用以下方法管理overlay.
            //添加overlay, 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高
            mOverlay.addItem(item1);
            mOverlay.addItem(item2);

            pop = new PopupOverlay(mapView, new PopupClickListener() {
                @Override
                public void onClickedPopup(int index) {
                }
            });
        }
        mapView.refresh();
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

    /*
 * 要处理overlay点击事件时需要继承ItemizedOverlay
 * 不处理点击事件时可直接生成ItemizedOverlay.
 */
    public class MyOverlay extends ItemizedOverlay<OverlayItem> {
        //用MapView构造ItemizedOverlay
        public MyOverlay(Drawable mark, MapView mapView) {
            super(mark, mapView);
        }

        protected boolean onTap(int index) {
            //在此处理item点击事件
            OverlayItem item = getItem(index);
            popupText.setText(getItem(index).getTitle());
            Bitmap[] bitMaps = {
                    getBitmapFromView(popupInfo),
            };
            pop.showPopup(bitMaps, item.getPoint(), 48);
            mMapView.refresh();
            return true;
        }

        public boolean onTap(GeoPoint pt, MapView mapView) {
            //在此处理MapView的点击事件，当返回 true时
            pop.hidePop();
            return false;
        }
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
                addItemOverlay(mMapView);
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

    Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
    }
}
