package com.raildeliveryservices.burnrubber;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public static final String SETTINGS_NAME = "appSettings";
    public static final String SETTINGS_DRIVER_NO = "driverNo";
    public static final String SETTINGS_DRIVER_ONLINE = "driverOnline";
    public static final String SETTINGS_DRIVER_ONLINE_SYSTEM = "driverOnlineSystem";
    public static final String SETTINGS_LAST_DOWNLOADED_MESSAGE_ID = "lastDownloadedMessageId";
    public static final String SETTINGS_DVIR_TOWED_SEQUENCE = "dvirTowedSequence";
    public static final String SETTINGS_DVIR_POWERED_SEQUENCE = "dvirPoweredSequence";
    public static final String SETTINGS_DRIVER_LOGGED_IN = "driverLoggedIn";
    public static final String SETTINGS_LAST_UPDATE_DATE_TIME_ORDERS = "lastUpdateDateTimeOrders";
    public static final String SETTINGS_DOWNLOAD_ORDERS_SERVICE_INTERVAL = "downloadOrderServiceInterval";
    public static final String SETTINGS_DOWNLOAD_MESSAGE_SERVICE_INTERVAL = "downloadMessageServiceInterval";
    public static final String SETTINGS_UPLOAD_SERVICE_INTERVAL = "uploadServiceInterval";
    public static final String SETTINGS_LOCATION_SERVICE_INTERVAL = "locationServiceInterval";
    public static final String SETTINGS_REQUEST_NEW_LOCATION_INTERVAL = "requestNewLocationInterval";
    public static final String SETTINGS_FASTEST_LOCATION_UPDATE_INTERVAL = "fastestLocationUpdateInterval";
    public static final String SETTINGS_SEND_GPS_WHEN_OFFLINE = "sendGpsWhenOffline";



    public static final int DEFAULT_DOWNLOAD_ORDERS_SERVICE_INTERVAL = 60000;  //1 minute
    public static final int DEFAULT_DOWNLOAD_MESSAGES_SERVICE_INTERVAL = 60000; //1 minute
    public static final int DEFAULT_UPLOAD_SERVICE_INTERVAL = 60000; //1 minute
    public static final int DEFAULT_LOCATION_SERVICE_INTERVAL = 300000; //5 minutes
    public static final int DEFAULT_REQUEST_NEW_LOCATION_INTERVAL = 270000;//every 4.5 minutes request a new location from device.
    public static final int DEFAULT_FASTEST_LOCATION_UPDATE_INTERVAL = 60000;//every 1 minute get location update from GPS providers.
    public static final boolean DEFAULT_SEND_GPS_WHEN_OFFLINE = true;
    public static final int DEFAULT_SYNC_TIME_IN_SECONDS = 180;


    public static final String BUNDLE_PARAM_ORDER_ID = "orderId";
    public static final String BUNDLE_PARAM_LEG_ID = "legId";
    public static final String BUNDLE_PARAM_FILE_NO = "fileNo";
    public static final String BUNDLE_PARAM_READ_ONLY = "readOnly";
    public static final String BUNDLE_PARAM_TRIP_HISTORY = "tripHistory";
    public static final String BUNDLE_PARAM_FORM_NAME = "formName";

    public static final int NOTIFICATION_MESSAGES = 0;
    public static final String SETTING_SYNC_TIME_IN_SECONDS = "syncTimeInSeconds";

    public static SimpleDateFormat ServerDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.s", Locale.US);

    /**
     * Update to BurnRubber 2.0:
     * <p/>
     * Changed from 'MM/dd/yy HH:mm:ss', Locale.US' to the current. Dropped seconds and milliseconds also.
     * <p/>
     * -Kevin H. (Jan, 5 2015)
     */
    public static SimpleDateFormat ClientDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.US);
}
