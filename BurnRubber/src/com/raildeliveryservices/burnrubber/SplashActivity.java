package com.raildeliveryservices.burnrubber;

import android.os.Bundle;

import com.raildeliveryservices.burnrubber.data.Message;
import com.raildeliveryservices.burnrubber.tasks.DownloadSettingsAsyncTask;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nghia on 04/15/2015.
 */
public class SplashActivity extends BaseFragmentActivity {

    private int splashMilliseconds = 750;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.splash);
        //Services.stopAll(this.getApplicationContext());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DownloadSettingsAsyncTask downloadSettingsAsyncTask = new DownloadSettingsAsyncTask(SplashActivity.this);
                downloadSettingsAsyncTask.execute();

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -15);

                getContentResolver().delete(Message.CONTENT_URI, "CREATED_DATE_TIME < '" + Constants.ClientDateFormat.format(calendar.getTime()) + "'", null);
            }
        }, splashMilliseconds);
    }

}
