package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public class Message {

    public static final String TABLE_NAME = "messages";
    public static final String DATABASE_CREATE_SQL =
            "create table " + TABLE_NAME +
                    "(" +
                    Columns._ID + " integer primary key autoincrement, " +
                    Columns.DRIVER_NO + " integer, " +
                    Columns.ORDER_ID + " integer, " +
                    Columns.FILE_NO + " integer, " +
                    Columns.MESSAGE_TYPE + " text, " +
                    Columns.MESSAGE_TEXT + " text, " +
                    Columns.CREATED_DATE_TIME + " text " +
                    ");";
    public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/messages");
    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
    static {
        PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
        PROJECTION_MAP.put(Columns.DRIVER_NO, TABLE_NAME + "." + Columns.DRIVER_NO);
        PROJECTION_MAP.put(Columns.ORDER_ID, TABLE_NAME + "." + Columns.ORDER_ID);
        PROJECTION_MAP.put(Columns.FILE_NO, TABLE_NAME + "." + Columns.FILE_NO);
        PROJECTION_MAP.put(Columns.MESSAGE_TYPE, TABLE_NAME + "." + Columns.MESSAGE_TYPE);
        PROJECTION_MAP.put(Columns.MESSAGE_TEXT, TABLE_NAME + "." + Columns.MESSAGE_TEXT);
        PROJECTION_MAP.put(Columns.CREATED_DATE_TIME, TABLE_NAME + "." + Columns.CREATED_DATE_TIME);
    }

    public static final class Columns implements BaseColumns {
        public static final String DRIVER_NO = "driver_no";
        public static final String ORDER_ID = "order_id";
        public static final String FILE_NO = "file_no";
        public static final String MESSAGE_TYPE = "message_type";
        public static final String MESSAGE_TEXT = "message_text";
        public static final String CREATED_DATE_TIME = "created_date_time";
    }
}
