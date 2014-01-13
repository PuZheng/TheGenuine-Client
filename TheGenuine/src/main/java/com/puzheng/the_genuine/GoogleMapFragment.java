package com.puzheng.the_genuine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.MapView;


/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 01-10.
 */
public class GoogleMapFragment extends Fragment {
    private MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);

    // create an overlay that shows our current location
        return rootView;
    }
}
