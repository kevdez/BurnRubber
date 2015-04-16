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

    public static void startMessagesDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadMessagesService.class);
        PendingIntent downloadMessageServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.downloadMessageInterval, downloadMessageServicePendingIntent);
    }


    public static void stopAll(Context context)
    {
        Services.stopGpsService(context);
        Services.stopLocationService(context);
        Services.stopMessagesDownloadService(context);
        Services.stopOrdersDownloadService(context);
        Services.stopFormDownloadService(context);
    }

    public static void startAll(Context context){
        Services.startGpsService(context);
        Services.startMessagesDownloadService(context);
        Services.startOrdersDownloadService(context);
        Services.startFormDownloadService(context);
        Services.startLocationService(context);
        Services.startUploadService(context);
    }

    public static void stopMessagesDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadMessagesService.class);
        PendingIntent downloadMessageServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(downloadMessageServicePendingIntent);
    }

    public static void startOrdersDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadOrdersService.class);
        PendingIntent downloadOrderServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.downloadOrderInterval, downloadOrderServicePendingIntent);
    }

    public static void stopOrdersDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadOrdersService.class);
        PendingIntent downloadOrderServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(downloadOrderServicePendingIntent);
    }

    public static void startLocationService(Context context) {
        //TODO: For testing
        if(!Utils.isUserOnline(context) && !RuntimeSetting.sendGpsWhenOffline){
            return;
        }
        context = context.getApplicationContext();
        Intent locationService = new Intent(context, LocationService.class);
        PendingIntent locationServicePendingIntent = PendingIntent.getService(context, 0, locationService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d("GpsService", "LocationServiceInterval = " + RuntimeSetting.locationServiceInterval);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.locationServiceInterval, locationServicePendingIntent);
    }

    public static void stopLocationService(Context context) {
        context = context.getApplicationContext();
        Intent locationService = new Intent(context, LocationService.class);
        PendingIntent locationServicePendingIntent = PendingIntent.getService(context, 0, locationService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(locationServicePendingIntent);
    }

    public static void startUploadService(Context context) {
        context = context.getApplicationContext();
        Intent uploadService = new Intent(context, UploadService.class);
        PendingIntent uploadServicePendingIntent = PendingIntent.getService(context, 0, uploadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d("GpsService", "UploadServiceInterval = " + RuntimeSetting.uploadServiceInterval);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, RuntimeSetting.uploadServiceInterval, uploadServicePendingIntent);
    }

    public static void startGpsService(Context context) {
        //TODO: For testing
        if(!Utils.isUserOnline(context) && !RuntimeSetting.sendGpsWhenOffline){
            return;
        }
        Intent gpsService = new Intent(context, GpsService.class);
        context.getApplicationContext().startService(gpsService);
        RuntimeSetting.isGpsServiceRunning = true;
        Log.d("Service", "Gps Service is started");
    }

    public static void stopGpsService(Context context) {
        Intent gpsService = new Intent(context, GpsService.class);
        context.getApplicationContext().stopService(gpsService);
        RuntimeSetting.isGpsServiceRunning = false;
        Log.d("Service", "Gps Service is stopped");
    }

    public static void startFormDownloadService(Context context) {
        Intent downloadFormsServiceIntent = new Intent(context, DownloadFormsService.class);
        context.startService(downloadFormsServiceIntent);
    }

    public static void stopFormDownloadService(Context context) {
        Intent downloadFormsService = new Intent(context, DownloadFormsService.class);
        context.getApplicationContext().stopService(downloadFormsService);
        Log.d("Service", "Download Form Service is stopped");
    }

}
