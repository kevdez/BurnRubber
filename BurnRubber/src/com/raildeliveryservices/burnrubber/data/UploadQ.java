package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public class UploadQ {

	public static final String TABLE_NAME = "upload_q";
	public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/uploadq");
	
	public static final String DATABASE_CREATE_SQL =
			"create table " + TABLE_NAME +
			"(" +
			Columns._ID + " integer primary key autoincrement, " +
			Columns.SERVICE_URL + " text, " +
			Columns.JSON_DATA + " text, " +
			Columns.DELETE_FLAG + " integer " +
			")";
	
	public static final class Columns implements BaseColumns {
		public static final String SERVICE_URL = "service_url";
		public static final String JSON_DATA = "json_data";
		public static final String DELETE_FLAG = "delete_flag";
	}
	
	public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
	static {
		PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
		PROJECTION_MAP.put(Columns.SERVICE_URL, TABLE_NAME + "." + Columns.SERVICE_URL);
		PROJECTION_MAP.put(Columns.JSON_DATA, TABLE_NAME + "." + Columns.JSON_DATA);
		PROJECTION_MAP.put(Columns.DELETE_FLAG, TABLE_NAME + "." + Columns.DELETE_FLAG);
	}
}