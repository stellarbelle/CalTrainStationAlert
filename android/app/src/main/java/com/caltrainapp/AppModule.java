package com.caltrainapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.View;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class AppModule extends ReactContextBaseJavaModule {
    private static final String TAG = "AppModule";
    private BroadcastReceiver receiver;

    public AppModule(ReactApplicationContext reactContext) {
        super(reactContext);

//        Log.i(TAG, "Creating an intent filter");
        IntentFilter filter = new IntentFilter(MonitoringService.MY_FIRST_INTENT);

//        Log.i(TAG, "Creating a broadcast receiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive!!!");
                String latitude = intent.getStringExtra("stationLat");
                String longitude = intent.getStringExtra("stationLong");
                Log.i(TAG, "Latitude: " + latitude);
                Log.i(TAG, "Longitude: " + longitude);
            }
        };

        try {
//            Log.i(TAG, "Getting a broadcast manager");
            ReactApplicationContext reactApplicationContext = getReactApplicationContext();
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(reactApplicationContext);

            Log.i(TAG, "Registering receiver");
            instance.registerReceiver(receiver, filter);
//            registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "failed to register reciever", e);
        }

//        Log.i(TAG, "Done");
    }
    @Override
    public String getName() {
        return "AppAndroid";
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong, Callback callback) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("stationLat", stationLat);
        mServiceIntent.putExtra("stationLong", stationLong);

        activity.startService(mServiceIntent);

        activity.setCallback(callback);
    }
    public void stopService(View view) {
        //is this ok?
    }



//    protected void createLocationRequest() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest
//                .setInterval(10000)
//                .setFastestInterval(5000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
}
