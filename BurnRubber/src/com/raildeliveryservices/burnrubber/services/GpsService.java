package com.raildeliveryservices.burnrubber.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.raildeliveryservices.burnrubber.tasks.GpsLocationAsyncTask;
import com.raildeliveryservices.burnrubber.utils.RuntimeSetting;

public class GpsService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = GpsService.class.getSimpleName();
    private LocationRequest _locationRequest;
    private LocationClient _locationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            _locationRequest = LocationRequest.create();
            _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            _locationRequest.setInterval(RuntimeSetting.locationUpdateInterval);
            _locationRequest.setFastestInterval(RuntimeSetting.fastestLocationUpdateInterval);

            _locationClient = new LocationClient(this, this, this);
            _locationClient.connect();
        } catch (Exception e) {
            Log.d(TAG, "Create Location Request exception", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GpsService onConnection Failed.");
    }

    @Override
    public void onConnected(Bundle bundle) {
        _locationClient.requestLocationUpdates(_locationRequest, this);
        Log.d(TAG, "GpsService is connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "GpsService is disconnected.");
    }

    @Override
    public void onLocationChanged(Location location) {
        GpsLocationAsyncTask task = new GpsLocationAsyncTask(this);
        task.execute(new Location[]{location});
        Log.d(TAG, String.format("onLocationChanged, interval: " + RuntimeSetting.locationUpdateInterval +  ", Lat: %s, Lgn: %s", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
    }

    @Override
    public void onDestroy() {
        _locationClient.removeLocationUpdates(this);
        _locationClient.disconnect();
        Log.d(TAG, "GpsService is destroyed.");
        super.onDestroy();
    }
}