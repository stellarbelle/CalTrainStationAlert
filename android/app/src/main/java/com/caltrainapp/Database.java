package com.caltrainapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

public class Database {
    private static final String TAG = "DatabaseClass";
    private DbHelper mDbHelper;

    public Database(Context context) {
        mDbHelper = new DbHelper(context);
    }

    public void updateDb(Context context, String key_title, String title, String table_name, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(key_title, title);
        String selection = "id" + " LIKE ?";
        db.update(
                table_name,
                values,
                selection,
                selectionArgs);
        db.close();
    }

    public void createRow(Context context, String column_id, int id, String column_title, String title, String table_name) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column_id, id);
        values.put(column_title, title);
        db.insert(
                table_name,
                null,
                values);
        db.close();
    }

    public Cursor readDb(Context context, String _id, String column_title, String table_name, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
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
        Log.i(TAG,"Cursor: " + c);
        return c;
    }
}