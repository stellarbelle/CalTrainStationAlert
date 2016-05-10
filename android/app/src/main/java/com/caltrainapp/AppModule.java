package com.caltrainapp;


import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
//import com.google.android.gms.location.LocationRequest;

public class AppModule extends ReactContextBaseJavaModule {

    public AppModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }
    @Override
    public String getName() {
        return "AppAndroid";
    }

    @ReactMethod
    public void setStation(String stationLat, String stationLong, Callback callback) {
        String data = "caltrain://" + stationLat + "/" + stationLong;
        Intent mServiceIntent = new Intent(this.getCurrentActivity(), MonitoringService.class);
        mServiceIntent.setData(Uri.parse(data));
        getCurrentActivity().startService(mServiceIntent);
        Log.i("TestingModule", data);
        callback.invoke(3);
    }

//    protected void createLocationRequest() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest
//                .setInterval(10000)
//                .setFastestInterval(5000)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
}
