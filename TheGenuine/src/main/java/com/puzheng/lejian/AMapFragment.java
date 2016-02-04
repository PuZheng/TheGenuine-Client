package com.puzheng.lejian;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.puzheng.lejian.model.Retailer;
import com.puzheng.lejian.store.LocationStore;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AMapFragment extends Fragment {

    private AMap aMap;
    private LocationSource.OnLocationChangedListener onLocationChangedListener;

    public AMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AMapFragment newInstance() {
        AMapFragment fragment = new AMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_amap, container, false);
        MapView mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();
        aMap.setLocationSource(new LocationSource() {

            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                AMapFragment.this.onLocationChangedListener = onLocationChangedListener;
                centerAt(((NearbyActivity) getActivity()).getLnglat());
            }

            @Override
            public void deactivate() {
                AMapFragment.this.onLocationChangedListener = null;
            }
        });
        aMap.setMyLocationEnabled(true);

        return view;
    }

    public void setRetailers(List<Retailer> retailers) {
        Logger.i("show retailers");
        int[] markers = new int[]{
                R.drawable.icon_mark1,
                R.drawable.icon_mark2,
                R.drawable.icon_mark3,
                R.drawable.icon_mark4,
                R.drawable.icon_mark5,
                R.drawable.icon_mark6,
                R.drawable.icon_mark7,
                R.drawable.icon_mark8,
                R.drawable.icon_mark9,
                R.drawable.icon_mark10,
        };
        for (int i = 0; i < Math.min(retailers.size(), 10); ++i) {
            Retailer retailer = retailers.get(i);
            MarkerOptions markerOptions = new MarkerOptions().anchor(0.5f, 0.5f)
                    .position(new LatLng(retailer.getPOI().getLat(), retailer.getPOI().getLng()))
                    .title(retailer.getName())
                    .icon(BitmapDescriptorFactory.fromResource(markers[i]));
            aMap.addMarker(markerOptions);
        }
    }

    public void centerAt(Pair<Double, Double> lnglat) {
        if (onLocationChangedListener != null && lnglat != null) {
            Location location = new Location("");
            location.setLongitude(lnglat.first);
            location.setLatitude(lnglat.second);
            Logger.i("centered at: " + lnglat.first + "," + lnglat.second);
            onLocationChangedListener.onLocationChanged(location);
        }
    }

}
