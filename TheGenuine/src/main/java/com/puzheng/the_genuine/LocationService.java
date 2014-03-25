package com.puzheng.the_genuine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;
import com.puzheng.the_genuine.utils.Misc;

/**
 * Created by abc549825@163.com(https://github.com/abc549825) at 01-13.
 */
public class LocationService extends Service implements LocationListener {
    public static final String LOCATION_ACTION = "location_action";
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1000;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private LocationManager locManager;
    private Location mLocation;

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocationBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locManager != null) {
            locManager.removeUpdates(this);
        }
        if (mLocation != null) {
            Misc.storeLastLocation(mLocation, getApplicationContext());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, mLocation)) {
            mLocation = location;
            Intent intent = new Intent(LOCATION_ACTION);
            sendBroadcast(intent);
        }
    }



    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Location location = locManager.getLastKnownLocation(provider);
        if (isBetterLocation(location, mLocation)) {
            mLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private Criteria getCriteria() {
        Criteria result = new Criteria();
        result.setAccuracy(Criteria.ACCURACY_FINE);
        //设置不需要获取海拔方向数据
        result.setAltitudeRequired(false);
        result.setBearingRequired(false);
        //设置允许产生资费
        result.setCostAllowed(true);
        //要求低耗电
        result.setPowerRequirement(Criteria.POWER_LOW);
        return result;
    }

    private void initLocation() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String[] providers = {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};
        for (String provider : providers) {
            try {
                Location location = locManager.getLastKnownLocation(provider);
                if (isBetterLocation(location, mLocation)) {
                    mLocation = location;
                }
                locManager.requestLocationUpdates(provider, FASTEST_INTERVAL_IN_SECONDS, 8, this);
            } catch (IllegalArgumentException e) {
                // provider is null or doesn't exists
                e.printStackTrace();
            }
        }
    }

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        if (location == null) {
            return false;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void setGPSEnabled() {
        Toast.makeText(getApplicationContext(), getString(R.string.EnableGPS), Toast.LENGTH_SHORT).show();
        Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingIntent);
    }

    public class LocationBind extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }

    }
}
