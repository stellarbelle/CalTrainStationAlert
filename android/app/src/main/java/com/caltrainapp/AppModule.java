package com.caltrainapp;

import android.content.BroadcastReceiver;
import android.content.Context;
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

public class AppModule extends ReactContextBaseJavaModule {
    private static final String TAG = "AppModule";
    private Intent mServiceIntent;
    public int minAlert;

    public AppModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        Log.i(TAG, "intent: " + mServiceIntent);

        final IntentFilter filter = new IntentFilter(MonitoringService.MY_FIRST_INTENT);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double distanceMin = intent.getDoubleExtra("distance", 0);
                WritableMap params = Arguments.createMap();
                String distance = String.format("%.2f", distanceMin);
                params.putString("distance", distance);
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
        mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("audioValue", value);
        activity.startService(mServiceIntent);
    }

    @ReactMethod
    public void setVibrate(boolean value) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("vibrateValue", value);
        activity.startService(mServiceIntent);
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        mServiceIntent = new Intent(activity, MonitoringService.class);
        mServiceIntent.putExtra("stationLat", stationLat);
        mServiceIntent.putExtra("stationLong", stationLong);
        activity.startService(mServiceIntent);

    }

    @ReactMethod
    public void setMinuteAlert(int minuteAlert) {
        MainActivity activity = (MainActivity)getCurrentActivity();
        mServiceIntent = new Intent(activity, MonitoringService.class);
        minAlert = minuteAlert;
        mServiceIntent.putExtra("minuteAlert", minuteAlert);
        activity.startService(mServiceIntent);

    }

    @ReactMethod
    public void setTone() {
        MainActivity activity = (MainActivity)getCurrentActivity();
        activity.tone();
    }
}
