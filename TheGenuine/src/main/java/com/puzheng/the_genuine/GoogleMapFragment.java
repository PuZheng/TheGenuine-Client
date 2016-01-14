package com.puzheng.the_genuine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.puzheng.the_genuine.data_structure.Store;
import com.puzheng.the_genuine.data_structure.StoreResponse;
import com.puzheng.the_genuine.util.LocateErrorException;
import com.puzheng.the_genuine.util.Misc;

import java.util.List;


/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 01-10.
 */
public class GoogleMapFragment extends SupportMapFragment {
    private static final int zoom = 18;
    private BroadcastReceiver receiver;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleMap map = getMap();
        if (map != null) {
            map.setMyLocationEnabled(true);
            locateToMyLocation(map);
            setStores(((NearbyActivity) getActivity()).getStoreResponses(), map);

            receiver = new LocationBroadcastReceiver();
            IntentFilter filter = new IntentFilter(LocationService.LOCATION_ACTION);
            getActivity().registerReceiver(receiver, filter);
        }
    }

    public void setStores(List<StoreResponse> stores, GoogleMap map) {
        if (stores == null) {
            return;
        }
        if (map != null) {
            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i).getStore();
                MarkerOptions marker = new MarkerOptions().position(new LatLng(store.getLatitude(),
                        store.getLongitude())).title(store.getName());
                if (i < Constants.MARKS.size()) {
                    marker.icon(BitmapDescriptorFactory.fromResource(Constants.MARKS.get(i)));
                } else {
                    marker.icon(BitmapDescriptorFactory.defaultMarker());
                }
                map.addMarker(marker);
            }
        }
    }

    private void locateToMyLocation(GoogleMap map) {
        Location location;
        try {
            location = MyApp.getLocation();
        } catch (LocateErrorException e) {
            location = Misc.getLastLocation(getActivity());
        }

        if (location != null) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        } else {
            Toast.makeText(getActivity(), getString(R.string.locate_error), Toast.LENGTH_SHORT).show();
        }
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location data = intent.getParcelableExtra(Constants.TAG_LOCATION_DATA);
            if (data != null) {
                getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(),
                        data.getLongitude()), zoom));
            }
        }
    }
}
