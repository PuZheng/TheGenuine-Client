package com.puzheng.the_genuine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 11-27.
 */
public class BaiduMapFragment extends Fragment {
    private BMapManager mBMapManager;
    private MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBMapManager = new BMapManager(this.getActivity());
        mBMapManager.init("UTW8RC2pRPD9oGrGn8jXgcnO", null);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.bmapsView);
        MapController mapController = mMapView.getController();
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));

        mapController.setCenter(point);
        mapController.setZoom(12);
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
}
