package com.raildeliveryservices.burnrubber.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

public final class MessageAlert {

	public static final String TABLE_NAME = "message_alert";
	public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY + "/messagealerts");
	
	public static final String DATABASE_CREATE_SQL =
			"create table " + TABLE_NAME +
			"(" +
			Columns._ID + " integer primary key autoincrement, " +
			Columns.DRIVER_NO + " integer, " +
			Columns.MESSAGE_FLAG + " integer " +
			");";
	
	public static final class Columns implements BaseColumns {
		public static final String DRIVER_NO = "driver_no";
		public static final String MESSAGE_FLAG = "message_flag";
	}
	
	public static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();
	static {
		PROJECTION_MAP.put(Columns._ID, TABLE_NAME + "." + Columns._ID);
		PROJECTION_MAP.put(Columns.DRIVER_NO, TABLE_NAME + "." + Columns.DRIVER_NO);
		PROJECTION_MAP.put(Columns.MESSAGE_FLAG, TABLE_NAME + "." + Columns.MESSAGE_FLAG);
	}
}
