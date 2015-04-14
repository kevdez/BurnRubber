package com.raildeliveryservices.burnrubber.services;

import android.app.IntentService;
import android.content.Intent;

import com.raildeliveryservices.burnrubber.tasks.DownloadFormServiceAsyncTask;

public class DownloadFormsService extends IntentService {

    public DownloadFormsService() {
        super("DownloadFormsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DownloadFormServiceAsyncTask task = new DownloadFormServiceAsyncTask(this);
        task.execute();
    }
}