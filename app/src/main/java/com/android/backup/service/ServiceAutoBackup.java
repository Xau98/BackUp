package com.android.backup.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.backup.R;

import static com.android.backup.activity.HomePage.CHANNEL_ID;

public class ServiceAutoBackup extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Tiennvh", "onCreate: service");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        senNotification();
        return START_NOT_STICKY;
    }

    private  void  senNotification(){
        RemoteViews notificationLayout =
                new RemoteViews(getPackageName(), R.layout.notification_backup);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Title notification service")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentText("Thong bao bat service auto backup")
                .setCustomContentView(notificationLayout)
                . build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
