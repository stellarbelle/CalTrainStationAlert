package com.caltrainapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.zmxv.RNSound.RNSoundPackage;
import com.facebook.react.bridge.Callback;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends ReactActivity {
    private static final String TAG = "TestingActivity";

    private BroadcastReceiver receiver;

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "CalTrainApp";
    }

    /**
     * Returns whether dev mode should be enabled.
     * This enables e.g. the dev menu.
     */
    @Override
    protected boolean getUseDeveloperSupport() {
        return BuildConfig.DEBUG;
    }

    public void setCallback(Callback callback) {
        callback.invoke(4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Log.i(TAG, "Creating an intent filter");
//        IntentFilter filter = new IntentFilter(MonitoringService.MY_FIRST_INTENT);
//
//        Log.i(TAG, "Creating a broadcast receiver");
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                //do something based on the intent's action
//                Log.i(TAG, String.valueOf(intent.getData()));
//                Log.i(TAG, "Recieved intent");
//            }
//        };
//
//        try {
//            Log.i(TAG, "Getting a broadcast manager");
//            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
//
//            Log.i(TAG, "Registering receiver");
//            instance.registerReceiver(receiver, filter);
////            registerReceiver(receiver, filter);
//        } catch (Exception e) {
//            Log.e(TAG, "failed to register reciever", e);
//        }
//
//        Log.i(TAG, "Done");
    }


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (receiver != null) {
            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(this);
            instance.unregisterReceiver(receiver);
//            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    /**
     * A list of packages used by the app. If the app uses additional views
     * or modules besides the default ones, add more packages here.
     */
    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new RNSoundPackage(),
            new AppReactPackage()
        );
    }
}
