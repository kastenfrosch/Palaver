package de.uni_due.paluno.se.palaver.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import de.uni_due.paluno.se.palaver.utils.api.MagicCallback;
import de.uni_due.paluno.se.palaver.utils.api.PalaverApi;
import de.uni_due.paluno.se.palaver.utils.api.request.SendMessageApiRequest;
import de.uni_due.paluno.se.palaver.utils.api.response.DateTimeContainer;
import de.uni_due.paluno.se.palaver.utils.storage.ChatMessage;
import de.uni_due.paluno.se.palaver.utils.storage.Storage;

public class LocationUtils extends ContextAware implements LocationListener {

    public Location location;
    public double latitude = 0;
    public double longitude = 0;

    public LocationUtils() {
        getLocation();
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getCtx().getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location net_loc = null, gps_loc = null;

        try {

            if (gps_enabled)
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gps_loc != null && net_loc != null) {

                //smaller the number more accurate result will
                if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                    location = net_loc;
                else
                    location = gps_loc;

            } else {

                if (gps_loc != null) {
                    location = gps_loc;
                } else if (net_loc != null) {
                    location = net_loc;
                }
            }

        String provider = LocationManager.GPS_PROVIDER;
        LocationProvider locationProvider = locationManager.getProvider(provider);

//        if (locationProvider == null) {
//            provider = LocationManager.NETWORK_PROVIDER;
//            locationProvider = locationManager.getProvider(provider);
//        }

            //location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 1000, 0, this);


            if (location != null) {
                Log.i("Location Info", "Location achieved!");
                Log.i("Alt:", String.valueOf(location.getLatitude()));
                Log.i("Lng:", String.valueOf(location.getLongitude()));
            } else {
                Log.i("Location Info", "No location :(");
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        this.location = location;
//        Log.i("Location info: Lat", String.valueOf(latitude));
//        Log.i("Location info: Lng", String.valueOf(longitude));
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
