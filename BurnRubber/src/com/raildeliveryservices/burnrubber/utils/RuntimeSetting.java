package com.raildeliveryservices.burnrubber.utils;

import com.raildeliveryservices.burnrubber.Constants;

/**
 * Created by nghia on 04/13/2015.
 */
public class RuntimeSetting {
    public static int downloadOrderInterval = Constants.DEFAULT_DOWNLOAD_ORDERS_SERVICE_INTERVAL;
    public static int downloadMessageInterval = Constants.DEFAULT_DOWNLOAD_MESSAGES_SERVICE_INTERVAL;
    public static int uploadServiceInterval = Constants.DEFAULT_UPLOAD_SERVICE_INTERVAL;
    public static int locationServiceInterval = Constants.DEFAULT_LOCATION_SERVICE_INTERVAL;
    public static int locationUpdateInterval = Constants.DEFAULT_REQUEST_NEW_LOCATION_INTERVAL;
    public static int fastestLocationUpdateInterval = Constants.DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL;
    public static boolean sendGpsWhenOffline = Constants.DEFAULT_SEND_GPS_WHEN_OFFLINE;
    public static boolean isGpsServiceRunning = false;
}
