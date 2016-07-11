package com.caltrainapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
    private static final String TAG = "DatabaseClass";
    private static DbHelper mDbHelper;
    private static SQLiteDatabase db;
    private static ContentValues values;

    public static void updateDb(Context context,String key_title, String title, String table_name, String [] selectionArgs) {
        db = mDbHelper.getReadableDatabase();
        values.put(key_title, title);
        String selection = "id" + " LIKE ?";
        db.update(
                table_name,
                values,
                selection,
                selectionArgs);
        db.close();
    }

    public static void createRow(Context context, String column_id, int id, String column_title, String title, String table_name) {
        db = mDbHelper.getWritableDatabase();
        values.put(column_id, id);
        values.put(column_title, title);
        db.insert(
                table_name,
                null,
                values);
        db.close();
    }

    public static Cursor readDb(Context context, String _id, String column_title, String table_name, String [] selectionArgs) {
        db = mDbHelper.getReadableDatabase();
        String[] projection = {
                _id,
                column_title,

        };
        Cursor c = db.query(
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