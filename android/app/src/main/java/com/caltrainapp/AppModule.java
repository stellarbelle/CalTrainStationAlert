package com.caltrainapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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

        Log.i(TAG, "Creating an intent filter");
        IntentFilter filter = new IntentFilter(MonitoringService.MY_FIRST_INTENT);

        Log.i(TAG, "Creating a broadcast receiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //do something based on the intent's action
                Log.i(TAG, String.valueOf(intent.getData()));
                Log.i(TAG, "Recieved intent");
            }
        };

        try {
            Log.i(TAG, "Getting a broadcast manager");
            ReactApplicationContext reactApplicationContext = getReactApplicationContext();
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(reactApplicationContext);

            Log.i(TAG, "Registering receiver");
            instance.registerReceiver(receiver, filter);
//            registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "failed to register reciever", e);
        }

        Log.i(TAG, "Done");
    }
    @Override
    public String getName() {
        return "AppAndroid";
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong, Callback callback) {
        String data = "caltrain://" + stationLat + "/" + stationLong;
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.setData(Uri.parse(data));

        Log.i(TAG, "Starting intent service");
        activity.startService(mServiceIntent);
        Log.i(TAG, "Intent service started");

        Log.i("TestingModule", data);
        activity.setCallback(callback);
    }



//    protected void createLocationRequest() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest
//                .setInterval(10000)
//                .setFastestInterval(5000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
}
