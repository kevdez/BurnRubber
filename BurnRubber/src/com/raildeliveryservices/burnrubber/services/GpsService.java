package com.raildeliveryservices.burnrubber.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.raildeliveryservices.burnrubber.tasks.GpsLocationAsyncTask;
import com.raildeliveryservices.burnrubber.utils.Utils;

public class GpsService extends Service implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener {

	private LocationRequest _locationRequest;
	private LocationClient _locationClient;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			_locationRequest = LocationRequest.create();
			_locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			_locationRequest.setInterval(1000);
			_locationRequest.setFastestInterval(1000);
			
			_locationClient = new LocationClient(this, this, this);
			_locationClient.connect();
			
		} catch (Exception e) {
			Utils.sendDebugMessageToServer(this, "GpsService.onCreate", e.getMessage());
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
		Utils.sendDebugMessageToServer(this, "GpsService.onConnectionFailed", "Connection Failed");
	}

	@Override
	public void onConnected(Bundle bundle) {
		_locationClient.requestLocationUpdates(_locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		
	}

	@Override
	public void onLocationChanged(Location location) {
		GpsLocationAsyncTask task = new GpsLocationAsyncTask(this);
		task.execute(new Location[] { location });
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		_locationClient.removeLocationUpdates(this);
		_locationClient.disconnect();
	}
}