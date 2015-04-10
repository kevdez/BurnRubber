package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class Form {

    public static final String TABLE_NAME = "forms";
    public static final String DATABASE_CREATE_SQL =
            "create table " + TABLE_NAME +
                    "(" +
                    Columns._ID + " integer primary key autoincrement, " +
                    Columns.FORM_NAME + " text, " +
                    Columns.LABEL + " text, " +
                    Columns.FILL_IN + " integer, " +
                    Columns.FORM_TYPE + " text, " +
                    Columns.MUST_FILL_FLAG + " integer, " +
                    Columns.DRIVER_FLAG + " integer " +
                    ");";
    public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/forms");
    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
    static {
        PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
        PROJECTION_MAP.put(Columns.FORM_NAME, TABLE_NAME + "." + Columns.FORM_NAME);
        PROJECTION_MAP.put(Columns.LABEL, TABLE_NAME + "." + Columns.LABEL);
        PROJECTION_MAP.put(Columns.FILL_IN, TABLE_NAME + "." + Columns.FILL_IN);
        PROJECTION_MAP.put(Columns.FORM_TYPE, TABLE_NAME + "." + Columns.FORM_TYPE);
        PROJECTION_MAP.put(Columns.MUST_FILL_FLAG, TABLE_NAME + "." + Columns.MUST_FILL_FLAG);
        PROJECTION_MAP.put(Columns.DRIVER_FLAG, TABLE_NAME + "." + Columns.DRIVER_FLAG);
    }

    public static final class Columns implements BaseColumns {
        public static final String FORM_NAME = "form_name";
        public static final String LABEL = "label";
        public static final String FILL_IN = "fill_in";
        public static final String FORM_TYPE = "form_type";
        public static final String MUST_FILL_FLAG = "must_fill";
        public static final String DRIVER_FLAG = "driver";
    }
}
