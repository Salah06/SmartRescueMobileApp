package com.smartcity.smartrescue;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import timber.log.Timber;

public class LocationService implements LocationListener {
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    public Location location;

    private static final long MIN_TIME_BW_UPDATES = 3000;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    private static LocationService instance = null;
    private LocationManager lm;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference locRef;

    public static LocationService getLocationManager(Context context, String vehiculeId) {
        if (null == instance) {
            instance = new LocationService(context, vehiculeId);
        }
        return instance;
    }

    private LocationService(Context context, String vehiculeId) {
        locRef = dbRef.child(vehiculeId);
        initLocationService(context);
    }

    private void initLocationService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        this.isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Timber.d("Location service not available");
        } else {
            if (isNetworkEnabled) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (lm != null)   {
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            if (isGPSEnabled)  {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (lm != null)  {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        String str = location.getLatitude() +";"+ location.getLongitude();
        locRef.child("latitude").setValue(location.getLatitude());
        locRef.child("longitude").setValue(location.getLongitude());
        Timber.d("Location changed : %s", str);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
