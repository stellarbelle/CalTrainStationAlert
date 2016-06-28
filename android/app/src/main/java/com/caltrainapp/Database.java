package com.caltrainapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;

import com.caltrainapp.DbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Database {
    private static final String TAG = "DatabaseClass";
    private static DbHelper mDbHelper;
    private static ContentValues values;
    private static Cursor c;

    public static void updateDb(Context context,String key_title, String title, String table_name, String [] selectionArgs) {
        mDbHelper = new DbHelper(context);
        SQLiteDatabase dbUpdate = mDbHelper.getReadableDatabase();
        values = new ContentValues();
        values.put(key_title, title);
        String selection = "id" + " LIKE ?";
        int count = dbUpdate.update(
                table_name,
                values,
                selection,
                selectionArgs);
        dbUpdate.close();
    }

    public static void createRow(Context context, String column_id, int id, String column_title, String title, String table_name) {
        mDbHelper = new DbHelper(context);
        SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase();
        values = new ContentValues();
        values.put(column_id, id);
        values.put(column_title, title);
        long newRowId;
        newRowId = dbWrite.insert(
                table_name,
                null,
                values);
        dbWrite.close();

    }

    public static Cursor readDb(Context context, String _id, String column_title, String table_name, String [] selectionArgs) {
        mDbHelper = new DbHelper(context);
        Log.i(TAG, "inside of readDb");
        SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
        Log.i(TAG, "readDb: " + dbRead);
        String[] projection = {
                _id,
                column_title,

        };
        Log.i(TAG, "querying");
        c = dbRead.query(
                table_name,
                projection,
                "id = ?",
                selectionArgs,
                null,
                null,
                null
        );
        c.moveToFirst();
        Log.i(TAG, "Cursor: " + c);
//        c.close();
//        dbRead.close();
        return c;
    }

}