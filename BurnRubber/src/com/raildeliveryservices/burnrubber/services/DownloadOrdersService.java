package com.raildeliveryservices.burnrubber.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.raildeliveryservices.burnrubber.tasks.DownloadOrdersServiceAsyncTask;

public class DownloadOrdersService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DownloadOrdersServiceAsyncTask task = new DownloadOrdersServiceAsyncTask(this);
		task.execute();
		return Service.START_NOT_STICKY;
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
