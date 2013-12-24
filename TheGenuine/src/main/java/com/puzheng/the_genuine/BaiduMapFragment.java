package com.puzheng.the_genuine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.*;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.puzheng.the_genuine.data_structure.MyLocationData;
import com.puzheng.the_genuine.data_structure.StoreResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class BaiduMapFragment extends Fragment {
    boolean isFirstLoc = true;//是否首次定位
    private List<StoreResponse> mStoreList;
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
    private BaiduMapBroadcastReceiver receiver;
    private ArrayList<Integer> marks;

    public BaiduMapFragment(List<StoreResponse> storeList) {
        this.mStoreList = storeList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewCache = inflater.inflate(R.layout.map_text_view, null);
        popupInfo = viewCache.findViewById(R.id.popinfo);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);

        mBMapManager = new BMapManager(this.getActivity());
        mBMapManager.init(Constants.BAIDU_MAP_KEY, null);
        mLocationData = new LocationData();

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(18);

        receiver = new BaiduMapBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BaiduMapService.MY_LOCATION_ACTION);
        getActivity().registerReceiver(receiver, filter);

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

    private ArrayList<Integer> getMarks() {
        if (marks == null) {
            marks = new ArrayList<Integer>();
            marks.add(R.drawable.icon_marka);
            marks.add(R.drawable.icon_markb);
            marks.add(R.drawable.icon_markc);
            marks.add(R.drawable.icon_markd);
            marks.add(R.drawable.icon_marke);
            marks.add(R.drawable.icon_markf);
            marks.add(R.drawable.icon_markg);
            marks.add(R.drawable.icon_markh);
            marks.add(R.drawable.icon_marki);
            marks.add(R.drawable.icon_markj);
        }
        return marks;
    }

    private void addItemOverlay(MapView mapView) {
        if (myLocationOverlay != null) {
            Drawable mark = getResources().getDrawable(R.drawable.red_mark);

            //创建IteminizedOverlay
            mOverlay = new MyOverlay(mark, mapView);
            //将IteminizedOverlay添加到MapView中
            mapView.getOverlays().add(mOverlay);
            ArrayList<Integer> markIcons = getMarks();

            for (int i = 0, size = mStoreList.size(); i < size; i++) {
                StoreResponse storeResponse = mStoreList.get(i);
                GeoPoint p = new GeoPoint((int) (storeResponse.getStore().getLatitude() * 1E6), ((int) (storeResponse.getStore().getLongitude() * 1E6)));
                OverlayItem item = new OverlayItem(p, storeResponse.getStore().getName(), storeResponse.getStore().getDesc());
                if (i < markIcons.size()) {
                    item.setMarker(getResources().getDrawable(markIcons.get(i)));
                }
                mOverlay.addItem(item);
            }

            pop = new PopupOverlay(mapView, new PopupClickListener() {
                @Override
                public void onClickedPopup(int index) {
                }
            });
        }
        mapView.refresh();
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

    private class BaiduMapBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLocationData data = intent.getParcelableExtra(Constants.TAG_LOCATION_DATA);
            mLocationData = data.toBaiduLocationData();
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
    }
}
