package com.caltrainapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;


public class MonitoringService extends Service {

    private static final String TAG = "TestingService";
    public static final String MY_FIRST_INTENT = "com.caltrainapp.MY_FIRST_INTENT";
    private LocationManager mLocationManager = null;
    private static final int MINIMUM_TIME_BETWEEN_UPDATES = 1;
    private static final float MINIMUM_DISTANCE_BETWEEN_UPDATES = 1;
    private String stationLat;
    private String stationLong;
//    public static MediaPlayer mp = new MediaPlayer();

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    int mNotificationId;


//    public static class switchButtonListener extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mp.stop();
//        }
//    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        public LocationListener(String provider) {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            Log.i(TAG, "Last Location: " + mLastLocation.toString());
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            Log.i(TAG, "Longitude: " + location.getLongitude());
            Log.i(TAG, "Latitude: " + location.getLatitude());
            if (stationLat != null && stationLong != null) {
                float destLat = Float.parseFloat(stationLat);
                float destLong = Float.parseFloat(stationLong);
                Location destLocation = new Location("destLocation");
                destLocation.setLatitude(destLat);
                destLocation.setLongitude(destLong);
                double distanceMeters = location.distanceTo(destLocation);
                double distance = distanceMeters/1609.344;
                Intent myBroadcastIntent = new Intent(MY_FIRST_INTENT);
//                Intent switchIntent = new Intent(MonitoringService.this, switchButtonListener.class);
//                RemoteViews notificationView = new RemoteViews(getPackageName(),
//                        R.layout.mynotification);
//                PendingIntent pendingSwitchIntent = PendingIntent.getActivity(MonitoringService.this, (int) System.currentTimeMillis(), switchIntent, 0);
//                PendingIntent pIntent = PendingIntent.getActivity(MonitoringService.this, (int) System.currentTimeMillis(), myBroadcastIntent, 0);
//                notificationView.setOnClickPendingIntent(R.id.closeOnFlash,
//                        pendingSwitchIntent);
                if(distance <= 0.5){
//                    mp = MediaPlayer.create(MonitoringService.this, R.raw.elegant_ringtone);
//                    mp.setLooping(true);
//                    myBroadcastIntent.putExtra("audioValue", true);
                    myBroadcastIntent.putExtra("vibrateValue", true);
                    String currentText = " Get ready! Your stop is in " + String.format("%.1f", distance) + " miles!";
                    mBuilder.setContentText(currentText);
                    mBuilder.setContentTitle("Alert! Your stop is next!");
//                    mBuilder.setContentIntent(pIntent);
//                    mBuilder.addAction(R.drawable.train,"end", pIntent);
//                    mp.start();
                } else {
                    String currentText = "You are " + String.format("%.1f", distance) + " away.";
                    Log.i(TAG, "setting current text " + currentText);
                    mBuilder.setContentText(currentText);
                    Log.i(TAG, "set text");
                }
                mNotifyMgr.notify(
                        mNotificationId,
                        mBuilder.build());
                myBroadcastIntent.putExtra("distance", distance);
                LocalBroadcastManager instance = LocalBroadcastManager.getInstance(MonitoringService.this);
                instance.sendBroadcast(myBroadcastIntent);
            }

//            try {
//                Log.i(TAG, "Distance: " + location.distanceTo(destLocation));
//            } catch (Exception e) {
//                Log.e(TAG, "Error!", e);
//            }
            mLastLocation.set(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
    }
    LocationListener mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_BETWEEN_UPDATES,
                    mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        Log.i(TAG, "assigning mBuilder");
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.train)
                        .setContentTitle("You are on your way!")
                        .setContentText("Getting distance...");
        mNotificationId = 1;
        // Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        stationLat = intent.getStringExtra("stationLat");
        stationLong = intent.getStringExtra("stationLong");

        return START_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.w(TAG, "fail to remove location listeners, ignore", ex);
            }
        }
    }
}