package com.puzheng.lejian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.puzheng.lejian.util.LocateErrorException;
import com.puzheng.lejian.util.Misc;

import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class BaiduMapFragment extends Fragment {
    boolean isFirstLoc = true;//是否首次定位
    private BMapManager mBMapManager;
    private MapView mMapView;
    private LocationData mLocationData;
    private MapController mMapController;
    private MyLocationOverlay myLocationOverlay = null;
    private PopupOverlay pop = null;
    private View viewCache = null;
    private View popupInfo = null;
    private TextView popupText = null;
    private MyOverlay mOverlay = null;
    private BroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewCache = inflater.inflate(R.layout.map_text_view, null);
        popupInfo = viewCache.findViewById(R.id.popinfo);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);

        mBMapManager = new BMapManager(this.getActivity());
        mBMapManager.init(Const.BAIDU_MAP_KEY, null);

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(18);

        receiver = new LocationBroadcastReceiver();
        IntentFilter filter = new IntentFilter(LocationService.LOCATION_ACTION);
        getActivity().registerReceiver(receiver, filter);

        myLocationOverlay = new MyLocationOverlay(mMapView);
        //添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        locateToMyLocation();
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
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
        receiver = null;
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        this.setStores(((NearbyActivity) getActivity()).getStoreResponses());
    }

//    public void setStores(List<StoreResponse> storeList) {
//        if (storeList == null) {
//            return;
//        }
//        if (mMapView != null) {
//            Drawable mark = getResources().getDrawable(R.drawable.red_mark);
//
//            //创建IteminizedOverlay
//            if (mOverlay == null) {
//                mOverlay = new MyOverlay(mark, mMapView);
//                //将IteminizedOverlay添加到MapView中
//                mMapView.getOverlays().add(mOverlay);
//            }
//            mOverlay.getAllItem().clear();
//
//            for (int i = 0, size = storeList.size(); i < size; i++) {
//                StoreResponse storeResponse = storeList.get(i);
//                GeoPoint p = new GeoPoint((int) (storeResponse.getRetailer().getLatitude() * 1E6), ((int) (storeResponse.getRetailer().getLongitude() * 1E6)));
//                OverlayItem item = new OverlayItem(p, storeResponse.getRetailer().getName(), storeResponse.getRetailer().getDesc());
//                if (i < Const.MARKS.size()) {
//                    item.setMarker(getResources().getDrawable(Const.MARKS.get(i)));
//                }
//                mOverlay.addItem(item);
//            }
//
//            pop = new PopupOverlay(mMapView, new PopupClickListener() {
//                @Override
//                public void onClickedPopup(int index) {
//                }
//            });
//            mMapView.refresh();
//        }
//    }

    private void animateToLastLocation() {
        //移动地图到定位点
        mMapController.animateTo(new GeoPoint((int) (mLocationData.latitude * 1e6), (int) (mLocationData.longitude * 1e6)));
        myLocationOverlay.setLocationMode(MyLocationOverlay.LocationMode.NORMAL);
    }

    private void locateToMyLocation() {
        Location data;
        try {
            data = MyApp.getLocation();
        } catch (LocateErrorException e) {
            data = Misc.getLastLocation(getActivity());
        }
        if (data != null) {
            mLocationData = parseData(data);
            //设置定位数据
            animateToLastLocation();
        }else {
            mLocationData = new LocationData();
        }
        myLocationOverlay.setData(mLocationData);
    }

    private LocationData parseData(Location location) {
        LocationData data = new LocationData();
        data.longitude = location.getLongitude();
        data.latitude = location.getLatitude();
        data.speed = location.getSpeed();
        data.accuracy = location.getAccuracy();
        return data;
    }

    Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache(true);
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

        public boolean onTap(GeoPoint pt, MapView mapView) {
            //在此处理MapView的点击事件，当返回 true时
            pop.hidePop();
            return false;
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
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {
        private Toast toast;

        @Override
        public void onReceive(Context context, Intent intent) {
            Location data = intent.getParcelableExtra(Const.TAG_LOCATION_DATA);
            if (data != null) {
                mLocationData = parseData(data);
                //更新定位数据
                myLocationOverlay.setData(mLocationData);
                //更新图层数据执行刷新后生效

                mMapView.refresh();
                //是手动触发请求或首次定位时，移动到定位点
                if (isFirstLoc) {
                    animateToLastLocation();
                }
                //首次定位完成
                isFirstLoc = false;
            } else {
                if (toast == null) {
                    toast = Toast.makeText(context, getString(R.string.locate_error), Toast.LENGTH_SHORT);
                    toast.show();
                    toast = null;
                }
            }
        }
    }
}
