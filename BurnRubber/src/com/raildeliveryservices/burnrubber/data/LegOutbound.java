package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class LegOutbound {
    public static final String TABLE_NAME = "leg_outbound";
    public static final String DATABASE_CREATE_SQL =
            "create table " + TABLE_NAME +
                    "(" +
                    Columns._ID + " integer primary key autoincrement, " +
                    Columns.LEG_ID + " integer, " +
                    Columns.PIECES + " integer, " +
                    Columns.WEIGHT + " real, " +
                    Columns.SEAL + " text, " +
                    Columns.DESTINATION + " text, " +
                    Columns.BOL + " text, " +
                    Columns.BOL_PAGES + " integer, " +
                    Columns.PALLETS + " integer, " +
                    Columns.COMMODITY + " text " +
                    ");";
    public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/legoutbound");
    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
    static {
        PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
        PROJECTION_MAP.put(Columns.LEG_ID, TABLE_NAME + "." + Columns.LEG_ID);
        PROJECTION_MAP.put(Columns.PIECES, TABLE_NAME + "." + Columns.PIECES);
        PROJECTION_MAP.put(Columns.WEIGHT, TABLE_NAME + "." + Columns.WEIGHT);
        PROJECTION_MAP.put(Columns.SEAL, TABLE_NAME + "." + Columns.SEAL);
        PROJECTION_MAP.put(Columns.DESTINATION, TABLE_NAME + "." + Columns.DESTINATION);
        PROJECTION_MAP.put(Columns.BOL, TABLE_NAME + "." + Columns.BOL);
        PROJECTION_MAP.put(Columns.BOL_PAGES, TABLE_NAME + "." + Columns.BOL_PAGES);
        PROJECTION_MAP.put(Columns.PALLETS, TABLE_NAME + "." + Columns.PALLETS);
        PROJECTION_MAP.put(Columns.COMMODITY, TABLE_NAME + "." + Columns.COMMODITY);
    }

    public static final class Columns implements BaseColumns {
        public static final String LEG_ID = "leg_id";
        public static final String PIECES = "pieces";
        public static final String WEIGHT = "weight";
        public static final String SEAL = "seal";
        public static final String DESTINATION = "destination";
        public static final String BOL = "bol";
        public static final String BOL_PAGES = "bol_pages";
        public static final String PALLETS = "pallets";
        public static final String COMMODITY = "commodity";
    }
}
