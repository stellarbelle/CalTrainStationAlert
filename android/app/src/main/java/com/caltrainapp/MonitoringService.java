package com.caltrainapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MonitoringService extends IntentService{

    public MonitoringService() {
        super("MonitoringService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TestingService", "I am in the Intent");
    }
}
