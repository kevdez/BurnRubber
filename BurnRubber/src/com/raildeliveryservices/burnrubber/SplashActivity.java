package com.raildeliveryservices.burnrubber;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.raildeliveryservices.burnrubber.tasks.DownloadSettingsAsyncTask;
import com.raildeliveryservices.burnrubber.utils.Services;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nghia on 04/15/2015.
 */
public class SplashActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash);
        Services.stopAll(this.getApplicationContext());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DownloadSettingsAsyncTask downloadSettingsAsyncTask = new DownloadSettingsAsyncTask(SplashActivity.this);
                downloadSettingsAsyncTask.execute();
            }
        }, 750);

    }
}
