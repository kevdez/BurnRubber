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
	
	public static final int DOWNLOAD_ORDERS_SERVICE_INTERVAL = 60000;  //1 minute
	public static final int DOWNLOAD_MESSAGES_SERVICE_INTERVAL = 60000; //1 minute
	public static final int UPLOAD_SERVICE_INTERVAL = 60000; //1 minute
	public static final int LOCATION_SERVICE_INTERVAL = 300000; //5 minutes
    public static final int REQUEST_NEW_LOCATION_INTERVAL = 120000;//every 2 minutes request a new location from device.
    public static final int FASTEST_REQUEST_LOCATION_INTERVAL = 60000;//every 1 minute get location update from GPS providers.
	
	public static final String BUNDLE_PARAM_ORDER_ID = "orderId";
	public static final String BUNDLE_PARAM_LEG_ID = "legId";
    public static final String BUNDLE_PARAM_FILE_NO = "fileNo";
	public static final String BUNDLE_PARAM_READ_ONLY = "readOnly";
	public static final String BUNDLE_PARAM_TRIP_HISTORY = "tripHistory";
	public static final String BUNDLE_PARAM_FORM_NAME = "formName";
	
	public static final int NOTIFICATION_MESSAGES = 0;


    public static SimpleDateFormat ServerDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.US);

    /**
     * Update to BurnRubber 2.0:
     *
     * Changed from 'MM/dd/yy HH:mm:ss', Locale.US' to the current. Dropped seconds and milliseconds also.
     *
     * -Kevin H. (Jan, 5 2015)
     */
	public static SimpleDateFormat ClientDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.US);
}
