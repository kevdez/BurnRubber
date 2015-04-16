package com.raildeliveryservices.burnrubber.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.raildeliveryservices.burnrubber.Constants;
import com.raildeliveryservices.burnrubber.WebServiceConstants;
import com.raildeliveryservices.burnrubber.data.MessageAlert;
import com.raildeliveryservices.burnrubber.tasks.UploadQueueAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    public static void saveRuntimeSetting(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        settings.edit().putInt(Constants.SETTINGS_DOWNLOAD_MESSAGE_SERVICE_INTERVAL, RuntimeSetting.downloadMessageInterval).commit();
        settings.edit().putInt(Constants.SETTINGS_DOWNLOAD_ORDERS_SERVICE_INTERVAL, RuntimeSetting.downloadOrderInterval).commit();
        settings.edit().putInt(Constants.SETTINGS_UPLOAD_SERVICE_INTERVAL, RuntimeSetting.uploadServiceInterval).commit();
        settings.edit().putInt(Constants.SETTINGS_LOCATION_SERVICE_INTERVAL, RuntimeSetting.locationServiceInterval).commit();
        settings.edit().putInt(Constants.SETTINGS_REQUEST_NEW_LOCATION_INTERVAL, RuntimeSetting.locationUpdateInterval).commit();
        settings.edit().putInt(Constants.SETTINGS_FASTEST_LOCATION_UPDATE_INTERVAL, RuntimeSetting.fastestLocationUpdateInterval).commit();
        settings.edit().putBoolean(Constants.SETTINGS_SEND_GPS_WHEN_OFFLINE, RuntimeSetting.sendGpsWhenOffline).commit();
    }

    public static void loadRuntimeSetting(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        RuntimeSetting.downloadMessageInterval = settings.getInt(Constants.SETTINGS_DOWNLOAD_MESSAGE_SERVICE_INTERVAL, Constants.DEFAULT_DOWNLOAD_MESSAGES_SERVICE_INTERVAL);
        RuntimeSetting.downloadOrderInterval = settings.getInt(Constants.SETTINGS_DOWNLOAD_ORDERS_SERVICE_INTERVAL, Constants.DEFAULT_DOWNLOAD_ORDERS_SERVICE_INTERVAL);
        RuntimeSetting.uploadServiceInterval = settings.getInt(Constants.SETTINGS_UPLOAD_SERVICE_INTERVAL, Constants.DEFAULT_UPLOAD_SERVICE_INTERVAL);
        RuntimeSetting.locationServiceInterval = settings.getInt(Constants.SETTINGS_LOCATION_SERVICE_INTERVAL, Constants.DEFAULT_LOCATION_SERVICE_INTERVAL);
        RuntimeSetting.locationUpdateInterval = settings.getInt(Constants.SETTINGS_REQUEST_NEW_LOCATION_INTERVAL, Constants.DEFAULT_REQUEST_NEW_LOCATION_INTERVAL);
        RuntimeSetting.fastestLocationUpdateInterval = settings.getInt(Constants.SETTINGS_FASTEST_LOCATION_UPDATE_INTERVAL, Constants.DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL);
        RuntimeSetting.sendGpsWhenOffline = settings.getBoolean(Constants.SETTINGS_SEND_GPS_WHEN_OFFLINE, Constants.DEFAULT_SEND_GPS_WHEN_OFFLINE);
    }



    public static void setUserOnline(Context context, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        settings.edit().putBoolean(Constants.SETTINGS_DRIVER_ONLINE + "-" + Utils.getDriverNo(context), value).commit();
    }

    public static boolean isUserOnline(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.SETTINGS_DRIVER_ONLINE + "-" + getDriverNo(context), false);
    }

    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.SETTINGS_DRIVER_LOGGED_IN, false);
    }

    public static void setUserLoggedIn(Context context, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        settings.edit().putBoolean(Constants.SETTINGS_DRIVER_LOGGED_IN, value).commit();
    }

    public static String getDriverOnlineSystem(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        return settings.getString(Constants.SETTINGS_DRIVER_ONLINE_SYSTEM + "-" + getDriverNo(context), "");
    }

    public static void setDriverOnlineSystem(Context context, String system) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        settings.edit().putString(Constants.SETTINGS_DRIVER_ONLINE_SYSTEM + "-" + Utils.getDriverNo(context), system).commit();
    }

    public static String getDriverNo(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        return settings.getString(Constants.SETTINGS_DRIVER_NO, "");
    }

    public static void setDriverNo(Context context, String driverNo) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        settings.edit().putString(Constants.SETTINGS_DRIVER_NO, driverNo).commit();
    }

    public static int getSequenceNumber(Context context, String sequenceName) {
        int sequenceNumber = 0;
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        sequenceNumber = settings.getInt(sequenceName, 1);
        settings.edit().putInt(sequenceName, sequenceNumber + 1).commit();

        return sequenceNumber;
    }

    public static void sendUserOnlineToServer(Context context, boolean onlineFlag, String system) {
        try {
            JSONObject jsonObject = getOnlineOfflineJson(context, onlineFlag, system);
            sendMessageToServer(context, WebServiceConstants.URL_CREATE_MESSAGE, jsonObject);
        } catch (JSONException e) {
        }
    }

    private static JSONObject getOnlineOfflineJson(Context context, boolean onlineFlag, String system) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(context));
        jsonObject.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
        jsonObject.accumulate(WebServiceConstants.FIELD_LABEL, onlineFlag ? TextUtils.equals(system, "Crossdock") ? "XDOCK" : "ON-LINE" : "OFF-LINE");
        jsonObject.accumulate(WebServiceConstants.FIELD_FORM_NAME, "CANNED");
        jsonObject.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, getCurrentDateTime(Constants.ServerDateFormat));

        return jsonObject;
    }

    public static void sendMessageToServer(Context context, String url, JSONObject jsonObject) {
        UploadQueueAsyncTask uploadAsyncTask = new UploadQueueAsyncTask(context);
        uploadAsyncTask.execute(new String[]{url, jsonObject.toString()});
    }

    public static void sendDebugMessageToServer(Context context, String tag, String message) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate(WebServiceConstants.FIELD_DRIVER_NO, Utils.getDriverNo(context));
//            jsonObject.accumulate(WebServiceConstants.FIELD_IN_OUT_FLAG, "I");
//            jsonObject.accumulate(WebServiceConstants.FIELD_LABEL, "DEBUG");
//            jsonObject.accumulate(WebServiceConstants.FIELD_FORM_NAME, "DEBUG");
//
//            jsonObject.accumulate(WebServiceConstants.FIELD_MESSAGE_TEXT, tag + ":" + message);
//            jsonObject.accumulate(WebServiceConstants.FIELD_CLIENT_DATETIME, Utils.getCurrentDateTime(Constants.ServerDateFormat));
//
//            Utils.sendMessageToServer(context, WebServiceConstants.URL_CREATE_MESSAGE, jsonObject);
//        } catch (JSONException e) {
//            Log.d(tag, "Failed to send debug message to server: " + message);
//        }

        Log.d(tag, message);
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTabletDateTime(String date) {

        try {
            DateFormat inDfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            inDfm.setTimeZone(TimeZone.getDefault());
            Date inDate = inDfm.parse(date);

            DateFormat outDfm = Constants.ClientDateFormat;
            outDfm.setTimeZone(TimeZone.getDefault());
            return outDfm.format(inDate);
        } catch (Exception e) {
            Log.e("convertLocalDateTime", e.getMessage());
            return null;
        }
    }

    public static String getCurrentDateTime(SimpleDateFormat dateFormat) {

        final SimpleDateFormat sdf = dateFormat;
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    public static void sendNotification(Context context, int id, String title, String message, Class<?> cls) {

        Intent intent = new Intent(context, cls);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
        nb.setSmallIcon(android.R.drawable.btn_star_big_on);
        nb.setContentTitle(title);
        nb.setContentText(message);
        nb.setContentIntent(pIntent);
        nb.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, nb.build());
    }

    private static boolean messageAlertExists(Context context, String driverNo) {

        String[] projection = {MessageAlert.Columns._ID,
                MessageAlert.Columns.DRIVER_NO,
                MessageAlert.Columns.MESSAGE_FLAG};
        String selection = MessageAlert.Columns.DRIVER_NO + " = " + driverNo;

        final Cursor cursor = context.getContentResolver().query(MessageAlert.CONTENT_URI, projection, selection, null, null);
        return cursor.moveToFirst();

    }

    public static void setMessageAlertFlag(Context context, String driverNo, boolean value) {

        ContentValues values = new ContentValues();
        values.put(MessageAlert.Columns.MESSAGE_FLAG, value ? 1 : 0);

        if (Utils.messageAlertExists(context, driverNo)) {
            context.getContentResolver().update(MessageAlert.CONTENT_URI, values, MessageAlert.Columns.DRIVER_NO + " = " + driverNo, null);
        } else {
            values.put(MessageAlert.Columns.DRIVER_NO, driverNo);
            context.getContentResolver().insert(MessageAlert.CONTENT_URI, values);
        }
    }

    public static void playMessageNotificationSound(Context context, int soundResId) {

        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(soundResId);
            MediaPlayer mp = null;
            mp.reset();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mp.prepare();
            mp.start();
            afd.close();
        } catch (Exception e) {

        }
    }
}
