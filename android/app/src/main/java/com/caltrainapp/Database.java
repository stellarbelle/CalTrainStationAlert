package com.caltrainapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        dbUpdate.update(
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
        dbWrite.insert(
                table_name,
                null,
                values);
        dbWrite.close();

    }

    public static Cursor readDb(Context context, String _id, String column_title, String table_name, String [] selectionArgs) {
        mDbHelper = new DbHelper(context);
        SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
        String[] projection = {
                _id,
                column_title,

        };
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
        return c;
    }

}