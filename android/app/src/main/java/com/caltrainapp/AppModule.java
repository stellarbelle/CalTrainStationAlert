package com.caltrainapp;


import android.util.Log;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by Stella on 5/8/2016.
 */
public class AppModule extends ReactContextBaseJavaModule {

    public AppModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }
    @Override
    public String getName() {
        return "AppAndroid";
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong) {
        Log.i("TestingModule", "Lat: " + stationLat +" Long: " + stationLong);
    }
}
