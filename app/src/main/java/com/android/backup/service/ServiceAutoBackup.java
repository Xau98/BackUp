package com.android.backup.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityDiagnosticsManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
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
import java.text.SimpleDateFormat;
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
        mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT+"/Album");
        onUploadAll(mListAllFile);
    }

    public class BackupBinder extends Binder {
        public ServiceAutoBackup getMusicBinder() {
            return ServiceAutoBackup.this;
        }
    }
    AsyncTaskUpload myAsyncTaskCode;
    long mTotalSize = 0;
    long[] totalDetail  = new long[1];;
    int j =0;

    public void onUploadAll(ArrayList<FileItem> listAllFile){
        senNotification();
        mTotalSize = handleFile.totalCapacity(listAllFile);
        String namePathBackup ="False";

        Callback  callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                totalDetail[0] += listAllFile.get(j).getSize();
                float percen = 100f *totalDetail[0]/mTotalSize;
                notificationLayout.setProgressBar(R.id.progress_bar_notification , 100, (int) percen, false);
                notificationLayout.setTextViewText(R.id.status_load_notification,"đang backuping "+(int) percen+"%");
                startForeground(1, builder.build());
                j++;
            }
        };

        for(int i=0;i<listAllFile.size();i++) {
            if(i==0)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                LocalDateTime now = LocalDateTime.now();
                namePathBackup = "Data"+dtf.format(now);
            }
            myAsyncTaskCode = new AsyncTaskUpload(this, listAllFile.get(i), namePathBackup, callback, totalDetail, null);
            myAsyncTaskCode.execute();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Tiennvh"+mTotalSize, "run123: "+ totalDetail[0]);
                if(mTotalSize <= totalDetail[0])
                    handler.removeCallbacksAndMessages(null);
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
