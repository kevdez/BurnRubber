package com.raildeliveryservices.burnrubber.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.raildeliveryservices.burnrubber.services.DownloadFormsService;
import com.raildeliveryservices.burnrubber.services.DownloadMessagesService;
import com.raildeliveryservices.burnrubber.services.DownloadOrdersService;
import com.raildeliveryservices.burnrubber.services.GpsService;
import com.raildeliveryservices.burnrubber.services.LocationService;
import com.raildeliveryservices.burnrubber.services.UploadService;

public class Services {
    private static final String TAG = Services.class.getSimpleName();

    public static void startAll(Context context) {
        Log.d(TAG, "All Services start...");
        Services.startGpsService(context);
        Services.startMessagesDownloadService(context);
        Services.startOrdersDownloadService(context);
        Services.startFormDownloadService(context);
        Services.startLocationService(context);
        Services.startUploadService(context);
    }

    public static void stopAll(Context context) {
        Services.stopGpsService(context);
        Services.stopLocationService(context);
        Services.stopMessagesDownloadService(context);
        Services.stopOrdersDownloadService(context);
        Services.stopFormDownloadService(context);
        Services.stopUploadService(context);
        Log.d(TAG, "All Services stop...");
    }

    public static void startMessagesDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadMessagesService.class);
        PendingIntent downloadMessageServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.downloadMessageInterval, downloadMessageServicePendingIntent);
        Log.d(TAG, "Message Download is started");
    }

    public static void stopMessagesDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadMessagesService.class);
        PendingIntent downloadMessageServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(downloadMessageServicePendingIntent);
        Log.d(TAG, "Download Message Service is stopped.");
    }

    public static void startOrdersDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadOrdersService.class);
        PendingIntent downloadOrderServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.downloadOrderInterval, downloadOrderServicePendingIntent);
        Log.d(TAG, "Order Download Service is started");
    }

    public static void stopOrdersDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadOrdersService.class);
        PendingIntent downloadOrderServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(downloadOrderServicePendingIntent);
        Log.d("Services", "Order Download Service is stopped");
    }

    public static void startLocationService(Context context) {
        //TODO: For testing
        if (!Utils.isUserOnline(context) && !RuntimeSetting.sendGpsWhenOffline) {
            return;
        }
        context = context.getApplicationContext();
        Intent locationService = new Intent(context, LocationService.class);
        PendingIntent locationServicePendingIntent = PendingIntent.getService(context, 0, locationService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.locationServiceInterval, locationServicePendingIntent);
        Log.d(TAG, "Location Service is started, locationInterval =  " + RuntimeSetting.locationServiceInterval);
    }

    public static void startUploadService(Context context) {
        context = context.getApplicationContext();
        Intent uploadService = new Intent(context, UploadService.class);
        PendingIntent uploadServicePendingIntent = PendingIntent.getService(context, 0, uploadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.uploadServiceInterval, uploadServicePendingIntent);
        Log.d(TAG, "Upload Service is started, UploadServiceInterval = " + RuntimeSetting.uploadServiceInterval);
    }

    public static void stopUploadService(Context context){
        context = context.getApplicationContext();
        Intent uploadService = new Intent(context, UploadService.class);
        PendingIntent uploadServicePendingIntent = PendingIntent.getService(context, 0, uploadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(uploadServicePendingIntent);
        Log.d(TAG, "Upload Service is stopped.");
    }

    public static void stopLocationService(Context context) {
        context = context.getApplicationContext();
        Intent locationService = new Intent(context, LocationService.class);
        PendingIntent locationServicePendingIntent = PendingIntent.getService(context, 0, locationService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(locationServicePendingIntent);
        Log.d(TAG, "Location Service is stopped.");
    }

    public static void startGpsService(Context context) {
        //TODO: For testing
        if (!Utils.isUserOnline(context) && !RuntimeSetting.sendGpsWhenOffline) {
            return;
        }
        Intent gpsService = new Intent(context, GpsService.class);
        context.getApplicationContext().startService(gpsService);
        RuntimeSetting.isGpsServiceRunning = true;
        Log.d(TAG, "Gps Service is started");
    }

    public static void stopGpsService(Context context) {
        Intent gpsService = new Intent(context, GpsService.class);
        context.getApplicationContext().stopService(gpsService);
        RuntimeSetting.isGpsServiceRunning = false;
        Log.d(TAG, "Gps Service is stopped");
    }

    public static void startFormDownloadService(Context context) {
        Intent downloadFormsServiceIntent = new Intent(context, DownloadFormsService.class);
        context.startService(downloadFormsServiceIntent);
        Log.d(TAG, "Form Download Service is start");
    }

    public static void stopFormDownloadService(Context context) {
        Intent downloadFormsService = new Intent(context, DownloadFormsService.class);
        context.getApplicationContext().stopService(downloadFormsService);
        Log.d(TAG, "Form Download Service is stopped");
    }

}
