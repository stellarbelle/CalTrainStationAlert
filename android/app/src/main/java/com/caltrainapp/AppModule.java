package com.caltrainapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.View;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
                Float distance = intent.getFloatExtra("distance", 0);
                WritableMap params = Arguments.createMap();
                params.putDouble("distance", distance);
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


}
