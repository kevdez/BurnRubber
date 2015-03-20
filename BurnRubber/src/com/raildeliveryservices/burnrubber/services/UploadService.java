package com.raildeliveryservices.burnrubber.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.raildeliveryservices.burnrubber.tasks.UploadServiceAsyncTask;

public class UploadService extends Service {
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		UploadServiceAsyncTask uploadServiceTask = new UploadServiceAsyncTask(this);
		uploadServiceTask.execute();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
