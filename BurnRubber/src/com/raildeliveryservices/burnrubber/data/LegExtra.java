package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class LegExtra {

	public static final String TABLE_NAME = "leg_extras";
	public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/legextras");
	
	public static final String DATABASE_CREATE_SQL =
			"create table " + TABLE_NAME +
			"(" +
			Columns._ID + " integer primary key autoincrement, " +
			Columns.LEG_ID + " integer, " +
			Columns.FILE_NO + " integer, " +
			Columns.LEG_NO + " integer, " +
			Columns.LEG_PART + " text, " +
			Columns.EXTRA + " text " +
			");";
	
	public static final class Columns implements BaseColumns {
		public static final String LEG_ID = "leg_id";
		public static final String FILE_NO = "file_no";
		public static final String LEG_NO = "leg_no";
		public static final String LEG_PART = "leg_part";
		public static final String EXTRA = "extra";
	}
	
	public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
	static {
		PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
		PROJECTION_MAP.put(Columns.LEG_ID, TABLE_NAME + "." + Columns.LEG_ID);
		PROJECTION_MAP.put(Columns.FILE_NO, TABLE_NAME + "." + Columns.FILE_NO);
		PROJECTION_MAP.put(Columns.LEG_NO, TABLE_NAME + "." + Columns.LEG_NO);
		PROJECTION_MAP.put(Columns.LEG_PART, TABLE_NAME + "." + Columns.LEG_PART);
		PROJECTION_MAP.put(Columns.EXTRA, TABLE_NAME + "." + Columns.EXTRA);
	}
}