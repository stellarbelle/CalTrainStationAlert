package com.caltrainapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Stella on 5/8/2016.
 */
public class MonitoringService extends IntentService{

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MonitoringService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TestingService", "I am in the Intent");
    }
}
