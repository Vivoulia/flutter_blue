package com.pauldemarco.flutter_blue;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {
    private static String TAG = "FlutterForegroundService";
    public static int ONGOING_NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "CHANNEL_ID";

    public final static String START_FOREGROUND_ACTION = "com.pauldemarco.flutter_blue.action.startforeground";
    public final static String STOP_FOREGROUND_ACTION = "ccom.pauldemarco.flutter_blue.action.stopforeground";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PackageManager pm = getApplicationContext().getPackageManager();
        Intent notificationIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Bundle bundle = intent.getExtras();

        if(intent != null && STOP_FOREGROUND_ACTION.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "flutter_foreground_service_channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);
        }

        Intent stopSelf = new Intent(this, ForegroundService.class);
        stopSelf.setAction(STOP_FOREGROUND_ACTION);

        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf ,PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_bluetooth_24)
                //.setColor(bundle.getInt("color"))
                .setContentTitle(bundle.getString("title"))
                .setContentText(bundle.getString("content"))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_bluetooth_24,"Close", pStopSelf)
                .setOngoing(true);

        startForeground(ONGOING_NOTIFICATION_ID, builder.build());
        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private int getNotificationIcon(String iconName) {
        int resourceId = getApplicationContext().getResources().getIdentifier(iconName, "drawable", getApplicationContext().getPackageName());
        return resourceId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }
}