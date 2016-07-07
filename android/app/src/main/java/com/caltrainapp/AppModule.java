package com.caltrainapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.View;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.text.DecimalFormat;

public class AppModule extends ReactContextBaseJavaModule {
    private static final String TAG = "AppModule";
    private BroadcastReceiver receiver;
    private Intent myIntent;
    public static int minAlert;

    public AppModule(final ReactApplicationContext reactContext) {
        super(reactContext);

        final IntentFilter filter = new IntentFilter(MonitoringService.MY_FIRST_INTENT);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double distanceMin = intent.getDoubleExtra("distance", 0);
                WritableMap params = Arguments.createMap();
                String distance = String.format("%.2f", distanceMin);
                // String distance = Double.toString(distanceMin);
                Log.i(TAG, "distanceMin: " + distanceMin);
                Log.i(TAG, "distance string: " + distance);
                params.putString("distance", distance);
                Log.i(TAG, "params: " + params);
                myIntent = intent;
                if (distanceMin <= minAlert) {
                    params.putBoolean("alert", true);
                }
                sendEvent(reactContext, "updatedDistance", params);
            }
        };

        try {
            ReactApplicationContext reactApplicationContext = getReactApplicationContext();
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(reactApplicationContext);
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
    public void setAudio(boolean value) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        Log.e(TAG, "set audio value = " + String.valueOf(value));
        mServiceIntent.putExtra("audioValue", value);
        activity.startService(mServiceIntent);
    }

    @ReactMethod
    public void setVibrate(boolean value) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        Log.e(TAG, "set vibrate value = " + String.valueOf(value));
        mServiceIntent.putExtra("vibrateValue", value);
        activity.startService(mServiceIntent);
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("stationLat", stationLat);
        mServiceIntent.putExtra("stationLong", stationLong);
        Log.i(TAG, "set lat intent: " + stationLat);
        Log.i(TAG, "set long intent: " + stationLong);
        activity.startService(mServiceIntent);
        Log.i(TAG, "intent sent!!!");

    }

    @ReactMethod
    public void setMinuteAlert(int minuteAlert) {
        minAlert = minuteAlert;
        Log.i(TAG,"min Alert: " + minuteAlert);
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("minuteAlert", minuteAlert);
        activity.startService(mServiceIntent);

    }

    @ReactMethod
    public void setTone(boolean tone) {
        Log.i(TAG,"Tone: " + tone);
        MainActivity activity = (MainActivity)getCurrentActivity();
        Intent mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("tone", tone);
        activity.tone(mServiceIntent);
    }
}
