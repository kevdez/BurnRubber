package com.raildeliveryservices.burnrubber.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "burnrubber.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Order.DATABASE_CREATE_SQL);
        db.execSQL(Leg.DATABASE_CREATE_SQL);
        db.execSQL(LegExtra.DATABASE_CREATE_SQL);
        db.execSQL(LegOutbound.DATABASE_CREATE_SQL);
        db.execSQL(Message.DATABASE_CREATE_SQL);
        db.execSQL(UploadQ.DATABASE_CREATE_SQL);
        db.execSQL(Form.DATABASE_CREATE_SQL);
        db.execSQL(MessageAlert.DATABASE_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2) {
            db.delete(Message.TABLE_NAME, null, null);
            db.execSQL(Message.DATABASE_CREATE_SQL);
        }
    }
}
