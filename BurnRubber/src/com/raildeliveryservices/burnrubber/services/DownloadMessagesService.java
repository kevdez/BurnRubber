package com.raildeliveryservices.burnrubber.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.raildeliveryservices.burnrubber.tasks.DownloadMessagesServiceAsyncTask;

public class DownloadMessagesService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownloadMessagesServiceAsyncTask task = new DownloadMessagesServiceAsyncTask(this);
        task.execute();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
