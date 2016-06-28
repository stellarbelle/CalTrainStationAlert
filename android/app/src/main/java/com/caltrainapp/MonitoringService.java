package com.caltrainapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import android.os.Vibrator;


public class MonitoringService extends Service {

    private static final String TAG = "TestingService";
    public static final String MY_FIRST_INTENT = "com.caltrainapp.MY_FIRST_INTENT";
    private LocationManager mLocationManager = null;
    private static final int MINIMUM_TIME_BETWEEN_UPDATES = 1;
    private static final float MINIMUM_DISTANCE_BETWEEN_UPDATES = 1;
    private String stationLat;
    private String stationLong;
    public static MediaPlayer mp = new MediaPlayer();
    private static boolean audioValue = true;
    private static boolean vibrateValue = true;
    private static boolean tone;
    private static int minuteAlert = 1;
    private static String toneUri;
    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    int mNotificationId;

    public static final String ACTION_1 = "action_1";

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        public LocationListener(String provider) {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            Log.i(TAG, "Last Location: " + mLastLocation.toString());
        }

        @Override
        public void onLocationChanged(Location location) {
            mBuilder.setSound(null);
            mBuilder.mActions.clear();
            if (stationLat != null && stationLong != null) {
                float destLat = Float.parseFloat(stationLat);
                float destLong = Float.parseFloat(stationLong);
                Location destLocation = new Location("destLocation");
                destLocation.setLatitude(destLat);
                destLocation.setLongitude(destLong);
                double distanceMeters = location.distanceTo(destLocation);
                double distance = distanceMeters/1609.344;
                Intent myBroadcastIntent = new Intent(MY_FIRST_INTENT);

                PendingIntent pIntent = PendingIntent.getActivity(MonitoringService.this, (int) System.currentTimeMillis(), myBroadcastIntent, 0);
//                float speed = location.getSpeed();
//                double minutesAway = distanceMeters/speed;
                Log.i(TAG, "Miles: " + distance);
                Log.i(TAG, "Meters: " + distanceMeters);
//                Log.i(TAG, "Speed: " + speed);
                Log.i(TAG, "Minutes Away: " + minuteAlert);

                if(distance <= minuteAlert){
//                    mp = MediaPlayer.create(MonitoringService.this, R.raw.elegant_ringtone);
//                    mp.setLooping(true);
//                    myBroadcastIntent.putExtra("audioValue", true);
//                    myBroadcastIntent.putExtra("vibrateValue", true);
                    String currentText = "Get ready! Your stop is in " + String.format("%.1f", distance) + " miles!";
                    mBuilder.setContentText(currentText);
                    mBuilder.setContentTitle("Alert! Your stop is next!");
                    mBuilder.setContentIntent(pIntent);
                    //mBuilder.addAction(0,"End", pIntent);
                    //mp.start();

                    //End intent
                    NotificationUtils.displayNotification(mBuilder.mContext, mBuilder);

                    // We display an alert


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
                        .setSmallIcon(R.drawable.icon)
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
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand");
        Log.i(TAG, "intent: " + intent);
        Log.i(TAG, "next string lat & long");
        if (intent.hasExtra("stationLat") && intent.hasExtra("stationLong")) {
            Log.i(TAG, "has lat & long");
            stationLat = intent.getStringExtra("stationLat");
            Log.i(TAG, "lat: " + stationLat);
            stationLong = intent.getStringExtra("stationLong");
            Log.i(TAG, "long: " + stationLong);
        }
        Log.i(TAG, "next string minute alert");
        if (intent.hasExtra("minuteAlert")) {;
            minuteAlert = intent.getIntExtra("minuteAlert", 1);
        }
        Log.i(TAG, "next string audio val");
        if(intent.hasExtra("audioValue")) {
            audioValue = intent.getBooleanExtra("audioValue", true);
        }
        Log.i(TAG, "next string vib val");
        if (intent.hasExtra("vibrateValue")) {
            vibrateValue = intent.getBooleanExtra("vibrateValue", true);
        }
        Log.i(TAG, "next string Uri");
        toneUri = intent.getStringExtra("toneUri");
        Log.i(TAG, "setting Uri value: " + toneUri);
//        tone = intent.getBooleanExtra("tone", false);
//        if (tone) {
//            Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
//            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
//            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
//            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
//            startActivityForResult(ringtoneIntent, 999);
//        }
        String action = intent.getAction();
        if (ACTION_1.equals(action)) {
            // TODO: handle action 1.
            // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            NotificationManagerCompat.from(this).cancel(1);
            //mp.stop();
            Log.w(TAG, "new notif clicked");

            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1);
        }

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

    /*NEW*/

    public static class NotificationUtils {
        public static final int NOTIFICATION_ID = 1;

        public static final String ACTION_1 = "action_1";

        public static void displayNotification(Context context, NotificationCompat.Builder builder) {

            Intent action1Intent = new Intent(context, MonitoringService.class)
                    .setAction(ACTION_1);

            PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                    action1Intent, PendingIntent.FLAG_ONE_SHOT);

            builder.addAction(new NotificationCompat.Action(0, "Close", action1PendingIntent));

            Log.e(TAG,"Audio Value = " + String.valueOf(audioValue));

            //Sound check if audioValue is true
            if (audioValue) {
                Log.i(TAG, "audio value tone Uri: " + toneUri);
                builder.setSound(Uri.parse(toneUri));
            }
            //                    mp = MediaPlayer.create(MonitoringService.this, R.raw.elegant_ringtone);
            //                    mp.setLooping(true);
            //Vibration check if vibrateValue is true
            if (vibrateValue) {
                builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            }
            //LED
            builder.setLights(Color.CYAN, 3000, 3000);

            builder.mNotification.flags |= Notification.FLAG_INSISTENT;
                    //.setContentTitle("Sample Notification")
                    //.setContentText("Notification text goes here")


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            //Log.w(TAG, "context of notif : " + builder.mContext.toString());
            //Log.w(TAG, "context : " + context.toString());
            //Log.w(TAG, "notif intent class name : " + MonitoringService.class.getSimpleName());
        }

        public static class NotificationActionService extends IntentService {
            public NotificationActionService() {
                super(MonitoringService.class.getSimpleName());
            }

            @Override
            protected void onHandleIntent(Intent intent) {
//                Log.w(TAG, "heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeYYYYYYYYYYYYYYYYYYY");
//                String action = intent.getAction();
//                if (ACTION_1.equals(action)) {
//                    // TODO: handle action 1.
//                    // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
//                    //NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
//                    mp.stop();
//                    Log.w(TAG, "new notif clicked");
//                }
//
//                if (intent != null) {
//                    String str = intent.getStringExtra("Key");
//                    // Do whatever you need to do here.
//                    Log.w(TAG, "tezst");
//                }
            }
        }
    }
/*NED NEW*/
}
