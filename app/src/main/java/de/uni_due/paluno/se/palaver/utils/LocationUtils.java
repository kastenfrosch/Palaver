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

    public static double latitude;
    public static double longitude;
    private Context context;

    public LocationUtils(Context context) {
        this.context = context;
    }

    public void getLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        // TODO: request permissions
        //String Provider = locationManager.getBestProvider(criteria, true);
        //LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
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
