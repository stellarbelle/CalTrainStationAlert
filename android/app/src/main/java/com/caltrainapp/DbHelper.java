package com.caltrainapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "StationAlertTone.db";
    public static final String DB_QUERY = "CREATE TABLE station_alert_tone ( id INTEGER PRIMARY KEY, tone_uri VARCHAR NOT NULL)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS station_alert_tone";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG, "inside of DbHelper");
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "inside of DbHelper onCreate");
        db.execSQL(DB_QUERY);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}