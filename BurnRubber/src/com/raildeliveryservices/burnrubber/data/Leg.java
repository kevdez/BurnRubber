package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class Leg {
	
	public static final String TABLE_NAME = "legs";
	public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/legs");
	
	public static final String DATABASE_CREATE_SQL =
			"create table " + TABLE_NAME +
			"(" +
			Columns._ID + " integer primary key autoincrement, " +
			Columns.ORDER_ID + " integer, " +
			Columns.FILE_NO + " integer, " +
			Columns.LEG_NO + " integer, " +
			Columns.PARENT_LEG_NO + " integer, " +
			Columns.COMPANY_NAME_FROM + " text, " +
			Columns.ADDRESS_FROM + " text, " +
			Columns.CITY_FROM + " text, " +
			Columns.STATE_FROM + " text, " +
			Columns.ZIP_CODE_FROM + " text, " +
			Columns.ARRIVE_FROM_DATE_TIME + " text, " +
			Columns.DEPART_FROM_DATE_TIME + " text, " +
			Columns.COMPANY_NAME_TO + " text, " +
			Columns.ADDRESS_TO + " text, " +
			Columns.CITY_TO + " text, " +
			Columns.STATE_TO + " text, " +
			Columns.ZIP_CODE_TO + " text, " +
			Columns.ARRIVE_TO_DATE_TIME + " text, " +
			Columns.DEPART_TO_DATE_TIME + " text, " +
			Columns.COUNT_FLAG + " integer, " +
			Columns.WEIGHT_FLAG + " integer, " +
			Columns.OUTBOUND_FLAG + " integer, " +
			Columns.COMPLETED_FLAG + " integer " +
			");";
	
	public static final class Columns implements BaseColumns {
		public static final String ORDER_ID = "order_id";
		public static final String FILE_NO = "file_no";
		public static final String LEG_NO = "leg_no";
		public static final String PARENT_LEG_NO = "parent_leg_no";
		public static final String COMPANY_NAME_FROM = "company_name_from";
		public static final String ADDRESS_FROM = "address_from";
		public static final String CITY_FROM = "city_from";
		public static final String STATE_FROM = "state_from";
		public static final String ZIP_CODE_FROM = "zip_code_from";
		public static final String ARRIVE_FROM_DATE_TIME = "arrive_from_date_time";
		public static final String DEPART_FROM_DATE_TIME = "depart_from_date_time";
		public static final String COMPANY_NAME_TO = "company_name_to";
		public static final String ADDRESS_TO = "address_to";
		public static final String CITY_TO = "city_to";
		public static final String STATE_TO = "state_to";
		public static final String ZIP_CODE_TO = "zip_code_to";
		public static final String ARRIVE_TO_DATE_TIME = "arrive_to_date_time";
		public static final String DEPART_TO_DATE_TIME = "depart_to_date_time";
		public static final String COUNT_FLAG = "count_flag";
		public static final String WEIGHT_FLAG = "weight_flag";
		public static final String OUTBOUND_FLAG = "outbound_flag";
		public static final String COMPLETED_FLAG = "completed_flag";
	}
	
	public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
	static {
		PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
		PROJECTION_MAP.put(Columns.ORDER_ID, TABLE_NAME + "." + Columns.ORDER_ID);
		PROJECTION_MAP.put(Columns.FILE_NO, TABLE_NAME + "." + Columns.FILE_NO);
		PROJECTION_MAP.put(Columns.LEG_NO, TABLE_NAME + "." + Columns.LEG_NO);
		PROJECTION_MAP.put(Columns.PARENT_LEG_NO, TABLE_NAME + "." + Columns.PARENT_LEG_NO);
		PROJECTION_MAP.put(Columns.COMPANY_NAME_FROM, TABLE_NAME + "." + Columns.COMPANY_NAME_FROM);
		PROJECTION_MAP.put(Columns.ADDRESS_FROM, TABLE_NAME + "." + Columns.ADDRESS_FROM);
		PROJECTION_MAP.put(Columns.CITY_FROM, TABLE_NAME + "." + Columns.CITY_FROM);
		PROJECTION_MAP.put(Columns.STATE_FROM, TABLE_NAME + "." + Columns.STATE_FROM);
		PROJECTION_MAP.put(Columns.ZIP_CODE_FROM, TABLE_NAME + "." + Columns.ZIP_CODE_FROM);
		PROJECTION_MAP.put(Columns.ARRIVE_FROM_DATE_TIME, TABLE_NAME + "." + Columns.ARRIVE_FROM_DATE_TIME);
		PROJECTION_MAP.put(Columns.DEPART_FROM_DATE_TIME, TABLE_NAME + "." + Columns.DEPART_FROM_DATE_TIME);
		PROJECTION_MAP.put(Columns.COMPANY_NAME_TO, TABLE_NAME + "." + Columns.COMPANY_NAME_TO);
		PROJECTION_MAP.put(Columns.ADDRESS_TO, TABLE_NAME + "." + Columns.ADDRESS_TO);
		PROJECTION_MAP.put(Columns.CITY_TO, TABLE_NAME + "." + Columns.CITY_TO);
		PROJECTION_MAP.put(Columns.STATE_TO, TABLE_NAME + "." + Columns.STATE_TO);
		PROJECTION_MAP.put(Columns.ZIP_CODE_TO, TABLE_NAME + "." + Columns.ZIP_CODE_TO);
		PROJECTION_MAP.put(Columns.ARRIVE_TO_DATE_TIME, TABLE_NAME + "." + Columns.ARRIVE_TO_DATE_TIME);
		PROJECTION_MAP.put(Columns.DEPART_TO_DATE_TIME, TABLE_NAME + "." + Columns.DEPART_TO_DATE_TIME);
		PROJECTION_MAP.put(Columns.COUNT_FLAG, TABLE_NAME + "." + Columns.COUNT_FLAG);
		PROJECTION_MAP.put(Columns.WEIGHT_FLAG, TABLE_NAME + "." + Columns.WEIGHT_FLAG);
		PROJECTION_MAP.put(Columns.OUTBOUND_FLAG, TABLE_NAME + "." + Columns.OUTBOUND_FLAG);
		PROJECTION_MAP.put(Columns.COMPLETED_FLAG, TABLE_NAME + "." + Columns.COMPLETED_FLAG);
	}
}
