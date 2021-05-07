package com.android.backup.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityDiagnosticsManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.backup.FileItem;
import com.android.backup.R;
import com.android.backup.code.AsyncTaskUpload;
import com.android.backup.handleFile;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.android.backup.activity.HomePage.CHANNEL_ID;

public class ServiceAutoBackup extends Service {
    private IBinder binder = new BackupBinder();
    private ArrayList<FileItem> mListAllFile ;
    @Override
    public void onCreate() {
        super.onCreate();
        senNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }
    RemoteViews notificationLayout;
    NotificationCompat.Builder builder ;
    NotificationManagerCompat notificationManager;
    public void senNotification(){
        notificationManager = NotificationManagerCompat.from(this);
        notificationLayout =
                new RemoteViews(getPackageName(), R.layout.notification_backup);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Picture Download")
                .setContentTitle("Title notification service")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentText("Thong bao bat service auto backup")
                .setCustomContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100,R.id.progress_bar_notification,true);
        startForeground(1, builder.build());

    }

    public void updateUI(){
   /*  new Thread(new Runnable() {
            @Override
            public void run() {
                  for (int i=0;i<100;i++){
                    notificationLayout.setProgressBar(R.id.progress_bar_notification , 100,i, false);
                    notificationLayout.setTextViewText(R.id.status_load_notification,"đang backuping "+i+"%");
                    startForeground(1, builder.build());
                myAsyncTaskCode = new AsyncTaskUpload(this, mListFileChecked.get(i), namePathBackup, callback, mProgressBar, mStatusLoad);
                    myAsyncTaskCode.execute();
                    builder.setProgress(0,0,false);
                    notificationManager.notify(1, builder.build());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("Tiennvh", "run: "+i);
                }
            }
        }).start();*/
        onUploadAll();
    }

    public class BackupBinder extends Binder {
        public ServiceAutoBackup getMusicBinder() {
            return ServiceAutoBackup.this;
        }
    }
    AsyncTaskUpload myAsyncTaskCode;
    long mTotalSize = 0;
    long totalDetail = 0;
    int j =0;
    public void onUploadAll(){


        mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT+"/Album");
        mTotalSize = handleFile.totalCapacity(mListAllFile);
        Log.d("Tiennvh", "onUploadAll1: "+mTotalSize);
        String namePathBackup ="False";

        Callback  callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: "+e);


            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("Tiennvh", "onResponse: "+ response.isSuccessful());
                totalDetail += mListAllFile.get(j).getSize();
                float percen = 100f *totalDetail/mTotalSize;
                Log.d("Tiennvh"+percen, mTotalSize+"onUploadAll: "+ totalDetail);
                notificationLayout.setProgressBar(R.id.progress_bar_notification , 100, (int) percen, false);
                notificationLayout.setTextViewText(R.id.status_load_notification,"đang backuping "+(int) percen+"%");
                startForeground(1, builder.build());
                j++;
            }
        };

        for(int i=0;i<mListAllFile.size();i++) {
            if(i==0)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                LocalDateTime now = LocalDateTime.now();
                namePathBackup = "Data"+dtf.format(now);
            }
            myAsyncTaskCode = new AsyncTaskUpload(this, mListAllFile.get(i), namePathBackup, callback, null, null);
            myAsyncTaskCode.execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
