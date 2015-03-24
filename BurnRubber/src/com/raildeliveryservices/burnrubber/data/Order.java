package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class Order {
	
	public static final String TABLE_NAME = "orders";
	public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/orders");
	
	public static final String DATABASE_CREATE_SQL =
			"create table " + TABLE_NAME +
			"(" +
			Columns._ID + " integer primary key autoincrement, " +
			Columns.FILE_NO + " integer, " +
			Columns.DRIVER_NO + " integer, " +
			Columns.PARENT_FILE_NO + " integer, " +
			Columns.VOYAGE_NO + " text, " +
			Columns.TRIP_NO + " text, " +
			Columns.HAZMAT_FLAG + " integer, " +
			Columns.APPT_DATE_TIME + " text, " +
            Columns.APPT_TIME + " text, " +
			Columns.MOVE_TYPE + " text, " +
			Columns.CONTAINER_NO + " text, " +
			Columns.CHASSIS_NO + " text, " +
			Columns.LUMPER_FLAG + " integer, " +
			Columns.SCALE_FLAG + " integer, " +
			Columns.WEIGHT_FLAG + " integer, " +
			Columns.CONFIRMED_FLAG + " integer, " +
			Columns.STARTED_FLAG + " integer, " +
			Columns.COMPLETED_FLAG + " integer " +
			");";
			
	public static final class Columns implements BaseColumns {
		public static final String FILE_NO = "file_no";
		public static final String DRIVER_NO = "driver_no";
		public static final String PARENT_FILE_NO = "parent_file_no";
		public static final String VOYAGE_NO = "voyage_no";
		public static final String TRIP_NO = "trip_no";
		public static final String HAZMAT_FLAG = "hazmat_flag";
		public static final String APPT_DATE_TIME = "appt_date_time";
        public static final String APPT_TIME = "appt_time";
		public static final String MOVE_TYPE = "move_type";
		public static final String CONFIRMED_FLAG = "confirmed_flag";
		public static final String CONTAINER_NO = "container_no";
		public static final String CHASSIS_NO = "chassis_no";
		public static final String LUMPER_FLAG = "lumper_flag";
		public static final String SCALE_FLAG = "scale_flag";
		public static final String WEIGHT_FLAG = "weight_flag";
		public static final String STARTED_FLAG = "started_flag";
		public static final String COMPLETED_FLAG = "completed_flag";
	}
	
	public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
	static {
		PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
		PROJECTION_MAP.put(Columns.FILE_NO, TABLE_NAME + "." + Columns.FILE_NO);
		PROJECTION_MAP.put(Columns.DRIVER_NO, TABLE_NAME + "." + Columns.DRIVER_NO);
		PROJECTION_MAP.put(Columns.PARENT_FILE_NO, TABLE_NAME + "." + Columns.PARENT_FILE_NO);
		PROJECTION_MAP.put(Columns.VOYAGE_NO, TABLE_NAME + "." + Columns.VOYAGE_NO);
		PROJECTION_MAP.put(Columns.TRIP_NO, TABLE_NAME + "." + Columns.TRIP_NO);
		PROJECTION_MAP.put(Columns.HAZMAT_FLAG, TABLE_NAME + "." + Columns.HAZMAT_FLAG);
        PROJECTION_MAP.put(Columns.APPT_DATE_TIME, TABLE_NAME + "." + Columns.APPT_DATE_TIME);
        PROJECTION_MAP.put(Columns.APPT_TIME, TABLE_NAME + "." + Columns.APPT_TIME);
		PROJECTION_MAP.put(Columns.MOVE_TYPE, TABLE_NAME + "." + Columns.MOVE_TYPE);
		PROJECTION_MAP.put(Columns.CONTAINER_NO, TABLE_NAME + "." + Columns.CONTAINER_NO);
		PROJECTION_MAP.put(Columns.CHASSIS_NO, TABLE_NAME + "." + Columns.CHASSIS_NO);
		PROJECTION_MAP.put(Columns.LUMPER_FLAG, TABLE_NAME + "." + Columns.LUMPER_FLAG);
		PROJECTION_MAP.put(Columns.SCALE_FLAG, TABLE_NAME + "." + Columns.SCALE_FLAG);
		PROJECTION_MAP.put(Columns.WEIGHT_FLAG, TABLE_NAME + "." + Columns.WEIGHT_FLAG);
		PROJECTION_MAP.put(Columns.CONFIRMED_FLAG, TABLE_NAME + "." + Columns.CONFIRMED_FLAG);
		PROJECTION_MAP.put(Columns.STARTED_FLAG, TABLE_NAME + "." + Columns.STARTED_FLAG);
		PROJECTION_MAP.put(Columns.COMPLETED_FLAG, TABLE_NAME + "." + Columns.COMPLETED_FLAG);
	}
}
