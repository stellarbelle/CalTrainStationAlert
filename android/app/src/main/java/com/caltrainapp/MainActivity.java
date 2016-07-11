package com.caltrainapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.zmxv.RNSound.RNSoundPackage;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ReactActivity {
    private static final String TAG = "TestingActivity";
    public static final String MY_FIRST_INTENT = "com.caltrainapp.MY_FIRST_INTENT";
    private static Uri uri;
    final IntentFilter filter = new IntentFilter(MainActivity.MY_FIRST_INTENT);
    private static int minuteAlert = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BroadcastReceiver receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context,Intent intent){
                double distanceMin=intent.getDoubleExtra("distance",0);
                if (intent.hasExtra("minuteAlert")) {;
                    minuteAlert = intent.getIntExtra("minuteAlert", 1);
                }
                if(distanceMin <= minuteAlert){
                    alert();
                }
            }
        };
        try {
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
            instance.registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "failed to register receiver", e);
        }
    }

    public void alert() {
        Intent intent = new Intent(this, this.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        this.startActivity(intent);
    }

    Database db = new Database();
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                String[] selectionArgs = {"1"};
                Cursor c = db.readDb(this, "id", "tone_uri", "station_alert_tone", selectionArgs);
                String ringTonePath = uri.toString();
                RingtoneManager.setActualDefaultRingtoneUri(
                        this,
                        RingtoneManager.TYPE_RINGTONE,
                        uri);
                if (c.getCount() >= 1) {
                    Database.updateDb(this, "tone_uri", ringTonePath, "station_alert_tone", selectionArgs);
                } else {
                    Database.createRow(this, "id", 1, "tone_uri", ringTonePath, "station_alert_tone");
                }

            }
        }
    }

    public void tone() {
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

    @Override
    protected String getMainComponentName() {
        return "CalTrainApp";
    }

    @Override
    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new RNSoundPackage(),
            new AppReactPackage()
        );
    }
}
