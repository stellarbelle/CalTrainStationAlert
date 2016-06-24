package com.caltrainapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate Bundle");
        final IntentFilter filter = new IntentFilter(MainActivity.MY_FIRST_INTENT);

        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context,Intent intent){
                Log.i(TAG,"activity onReceive!!!");
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
    public void tone(Intent intent) {
        tone = intent.getBooleanExtra("tone", false);
        if (tone) {
//            final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_ALARM);
            Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
            if(uri != null) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
            }
            startActivityForResult(ringtoneIntent, 999);
//            int resultCode = new setResult(int);
            onActivityResult(999, resultCode, ringtoneIntent);
            if (resultCode == RESULT_OK) {
                uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    String ringTonePath = uri.toString();
                    Log.i(TAG, "Tone: " + ringTonePath);
//                    RingtoneManager.setActualDefaultRingtoneUri(
//                            MainActivity,
//                            RingtoneManager.TYPE_RINGTONE,
//                            uri);
                }
            }
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
