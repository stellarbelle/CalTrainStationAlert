package com.caltrainapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.text.DecimalFormat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class AppModule extends ReactContextBaseJavaModule {
    private static final String TAG = "AppModule";
    private BroadcastReceiver receiver;

    public AppModule(final ReactApplicationContext reactContext) {
        super(reactContext);

        final IntentFilter filter = new IntentFilter(MonitoringService.MY_FIRST_INTENT);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive!!!");
                double distanceMiles = intent.getDoubleExtra("distance", 0);
                boolean audioValue = intent.getBooleanExtra("audioValue", false);
                boolean vibrateValue = intent.getBooleanExtra("vibrateValue", false);
                WritableMap params = Arguments.createMap();
                String distance = String.format("%.1f", distanceMiles);
                params.putString("distance", distance);
                params.putBoolean("audioValue", audioValue);
                params.putBoolean("vibrateValue", vibrateValue);
                if (distanceMiles <= 0.5) {
                    params.putBoolean("alert", true);
                }
                sendEvent(reactContext, "updatedDistance", params);
            }
        };

        try {
            ReactApplicationContext reactApplicationContext = getReactApplicationContext();
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(reactApplicationContext);

            Log.i(TAG, "Registering receiver");
            instance.registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "failed to register receiver", e);
        }

    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public String getName() {
        return "AppAndroid";
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("stationLat", stationLat);
        mServiceIntent.putExtra("stationLong", stationLong);

        activity.startService(mServiceIntent);

    }
    public void stopService(View view) {
        //is this ok?
    }

    @ReactMethod
    public void hasClickedAlert() {
        MainActivity activity = (MainActivity)getCurrentActivity();
        activity.getIntent().putExtra("hasClickedAlert",true);
    }

//    public void alert() {
//        Activity alertActivity = getCurrentActivity();
//        AlertDialog alertDialog = new AlertDialog.Builder(alertActivity).create();
//        alertDialog.setTitle("Alert");
//        alertDialog.setMessage("Alert message to be shown");
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();
//    }

}
