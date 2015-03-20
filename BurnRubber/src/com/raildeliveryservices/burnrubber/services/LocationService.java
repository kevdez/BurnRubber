package com.raildeliveryservices.burnrubber.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.raildeliveryservices.burnrubber.tasks.LocationAsyncTask;

public class LocationService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LocationAsyncTask locationTask = new LocationAsyncTask(this);
		locationTask.execute();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}