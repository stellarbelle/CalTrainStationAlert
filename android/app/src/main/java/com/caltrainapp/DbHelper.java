package com.caltrainapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "StationAlertTone.db";
    public static final String DB_QUERY = "CREATE TABLE station_alert_tone ( id INTEGER PRIMARY KEY, tone_uri VARCHAR NOT NULL)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS station_alert_tone";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_QUERY);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}