package com.raildeliveryservices.burnrubber.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.raildeliveryservices.burnrubber.Constants;
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
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, Constants.DOWNLOAD_MESSAGES_SERVICE_INTERVAL, downloadMessageServicePendingIntent);
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
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, Constants.DOWNLOAD_ORDERS_SERVICE_INTERVAL, downloadOrderServicePendingIntent);
    }

    public static void stopOrdersDownloadService(Context context) {
        context = context.getApplicationContext();
        Intent downloadService = new Intent(context, DownloadOrdersService.class);
        PendingIntent downloadOrderServicePendingIntent = PendingIntent.getService(context, 0, downloadService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(downloadOrderServicePendingIntent);
    }

    public static void startLocationService(Context context) {
        context = context.getApplicationContext();
        Intent locationService = new Intent(context, LocationService.class);
        PendingIntent locationServicePendingIntent = PendingIntent.getService(context, 0, locationService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, Constants.LOCATION_SERVICE_INTERVAL, locationServicePendingIntent);
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
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 60000, Constants.UPLOAD_SERVICE_INTERVAL, uploadServicePendingIntent);
    }

    public static void startGpsService(Context context) {
        Intent gpsService = new Intent(context, GpsService.class);
        context.getApplicationContext().startService(gpsService);
    }

    public static void stopGpsService(Context context) {
        Intent gpsService = new Intent(context, GpsService.class);
        context.getApplicationContext().stopService(gpsService);
    }

    public static void startFormDownloadService(Context context) {
        Intent downloadFormsServiceIntent = new Intent(context, DownloadFormsService.class);
        context.startService(downloadFormsServiceIntent);
    }
}
