package com.caltrainapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.shell.MainReactPackage;
import com.zmxv.RNSound.RNSoundPackage;
import com.facebook.react.bridge.Callback;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ReactActivity {
    private static final String TAG = "TestingActivity";
    private BroadcastReceiver receiver;
    private static boolean tone;
    public static final String MY_FIRST_INTENT = "com.caltrainapp.MY_FIRST_INTENT";
    private static Uri uri;
    final IntentFilter filter = new IntentFilter(MainActivity.MY_FIRST_INTENT);
    Intent ringtoneIntent;
    StationAlertToneDbHelper mDbHelper = new StationAlertToneDbHelper(this);
    private static Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate Bundle");
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context,Intent intent){
                ringtoneIntent = new Intent(context, MainActivity.class);
                Log.i(TAG,"activity onReceive!!!");
                SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
                String[] projection = {
                        "id",
                        "tone_uri",

                };
                Log.i(TAG, "Projection: " + projection);
                c = dbRead.query(
                        "station_alert_tone",
                        projection,
                        "id = ?",
                        new String[] {"1"},
                        null,
                        null,
                        null
                );
                c.moveToFirst();

                if (c.getCount() >= 1) {
                    Log.i(TAG, "URI: " + c.getString(c.getColumnIndex("tone_uri")));
                    uri = Uri.parse(c.getString(c.getColumnIndex("tone_uri")));
                    ringtoneIntent.putExtra("toneUri",uri.toString());
                    Log.i(TAG, "ringtone intent: " + ringtoneIntent);
                    LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
                    instance.sendBroadcast(ringtoneIntent);
                    Log.i(TAG, "Service passed ringtone intent!!!");
                } else {
                    Log.i(TAG, "Default URI: " + Settings.System.DEFAULT_NOTIFICATION_URI.getPath());
                    uri = Uri.parse(Settings.System.DEFAULT_NOTIFICATION_URI.getPath());
                    ringtoneIntent.putExtra("toneUri",uri.toString());
                    LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
                    instance.sendBroadcast(ringtoneIntent);                }
                double distanceMiles=intent.getDoubleExtra("distance",0);
                if(distanceMiles <= 0.5){
                    Log.i(TAG, "calling alert!");
                    alert();
                }
            }
        };
        try {
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
            instance.registerReceiver(receiver, filter);
            Log.i(TAG, "Registering receiver");
        } catch (Exception e) {
            Log.e(TAG, "failed to register receiver", e);
        }
    }

    public void alert() {
        Intent intent = new Intent(this, this.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        this.startActivity(intent);
//        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle("Alert");
//        alertDialog.setMessage("Alert message to be shown");
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase();
        SQLiteDatabase dbUpdate = mDbHelper.getReadableDatabase();
        if (resultCode == RESULT_OK) {
            uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                String ringTonePath = uri.toString();
                Log.i(TAG, "Tone: " + ringTonePath);
                RingtoneManager.setActualDefaultRingtoneUri(
                        this,
                        RingtoneManager.TYPE_RINGTONE,
                        uri);
                Log.i(TAG, "Set Default: " + ringTonePath);
//                Log.i(TAG, "URI Count: " + c.getCount());
//                if (Settings.System.canWrite(context)) {
                    if (c.getCount() >= 1) {
                        Log.i(TAG, "Updating URI");
                        Log.i(TAG, "Current Index: " + c.getColumnIndex("tone_uri"));
                        Log.i(TAG, "Current URI: " + c.getString(c.getColumnIndex("tone_uri")));
                        ContentValues values = new ContentValues();
                        values.put("tone_uri", ringTonePath);
                        String selection = "id" + " LIKE ?";
                        String[] selectionArgs = {"1"};
                        int count = dbUpdate.update(
                                "station_alert_tone",
                                values,
                                selection,
                                selectionArgs);
                        Log.i(TAG, "New URI: " + uri);
                        ringtoneIntent.putExtra("toneUri", ringTonePath);
                        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
                        instance.sendBroadcast(ringtoneIntent);
                        Log.i(TAG, "new URI Count: " + c.getCount());

                    } else {
                        Log.i(TAG, "Creating URI");
                        ContentValues values = new ContentValues();
                        values.put("id", 1);
                        values.put("tone_uri", ringTonePath);
                        long newRowId;
                        newRowId = dbWrite.insert(
                                "station_alert_tone",
                                null,
                                values);
                        ringtoneIntent.putExtra("toneUri", ringTonePath);
                        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
                        instance.sendBroadcast(ringtoneIntent);
                        Log.i(TAG, "update URI Count: " + c.getCount());
                    }
//                } else {
//                    Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                    intent.setData(Uri.parse("package:" + this.getPackageName()));
//                    startActivity(permissionIntent);
//                }
            }
        }
    }

    public void tone(Intent intent) {
        tone = intent.getBooleanExtra("tone", false);
        if (tone) {
//            final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_ALARM);
            Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
           if(uri != null) {
               ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
           }
            startActivityForResult(ringtoneIntent, 999);
        }
    }
    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "CalTrainApp";
    }

    /**
     * Returns whether dev mode should be enabled.
     * This enables e.g. the dev menu.
     */
    @Override
    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }


    /**
     * A list of packages used by the app. If the app uses additional views
     * or modules besides the default ones, add more packages here.
     */
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new RNSoundPackage(),
            new AppReactPackage()
        );
    }
}
