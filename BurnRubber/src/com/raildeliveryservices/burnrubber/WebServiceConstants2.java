package com.raildeliveryservices.burnrubber;

/**
 * Created by Kevin7 on 03/20/2015.
 */
@SuppressWarnings("all")
public class WebServiceConstants2 {
    public static final String WEB_SERVICE_LOGIN = "[REDACTED]";
    public static final String WEB_SERVICE_PASSWORD = "[REDACTED]";

    public static final String URL_BASE = "[REDACTED]";
    public static final String URL_LOGIN = URL_BASE + "driver/authenticate";
    public static final String URL_CREATE_MESSAGE = URL_BASE + "message/create";
    public static final String URL_CREATE_DVIR_MESSAGE = URL_BASE + "dvirmessage/create";
    public static final String URL_GET_MESSAGES = URL_BASE + "message/select";
    public static final String URL_GET_FORMS = URL_BASE + "form/select";
    public static final String URL_GET_ORDERS = URL_BASE + "order/select";

    public static final String OBJECT_RESPONSE_MESSAGE = "ResponseMessage";
    public static final String OBJECT_FORMS = "Forms";
    public static final String OBJECT_MESSAGES = "Messages";

    public static final String FIELD_DRIVER_NO = "DriverNo";
    public static final String FIELD_PASSWORD = "Password";
    public static final String FIELD_FILE_NO = "FileNo";
    public static final String FIELD_LEG_NO = "LegNo";
    public static final String FIELD_AUTHENTIC = "Authentic";
    public static final String FIELD_IN_OUT_FLAG = "InOutFlag";
    public static final String FIELD_MESSAGE_ID = "MessageId";
    public static final String FIELD_MESSAGE_START_ID = "MessageStartId";
    public static final String FIELD_MESSAGE_TEXT = "MessageText";
    public static final String FIELD_CLIENT_DATETIME = "ClientDateTime";
    public static final String FIELD_SERVER_DATETIME = "ServerDateTime";
    public static final String FIELD_CLIENT_DATETIME_STRING = "ClientDateTimeString";
    public static final String FIELD_SERVER_DATETIME_STRING = "ServerDateTimeString";
    public static final String FIELD_FORM_NAME = "FormName";
    public static final String FIELD_LABEL = "Label";
    public static final String FIELD_FILL_IN = "FillIn";
    public static final String FIELD_FORM_TYPE = "FormType";
    public static final String FIELD_MUST_FILL_FLAG = "MustFillFlag";
    public static final String FIELD_DRIVER_FLAG = "DriverFlag";
    public static final String FIELD_GPS = "GPS";
    public static final String FIELD_LAST_UPDATE_DATE_TIME_ORDERS = "LastUpdateDateTime";
}
