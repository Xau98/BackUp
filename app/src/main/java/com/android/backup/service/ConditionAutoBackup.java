package com.android.backup.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.backup.ConditionBackup;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ConditionAutoBackup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction()!=null)
        {
            if(ConditionBackup.isCharging(context)&& ConditionBackup.isNetworkConnected(context)&& ConditionBackup.isScreenLock(context)){
                ComponentName componentName = new ComponentName(getApplicationContext(), JobService.class);
                JobInfo info = new JobInfo.Builder(123, componentName)
                        .setRequiresCharging(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)

                        .setPersisted(true)
                        .setPeriodic(15*60*1000)
                        .build();
                JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
                int result =  scheduler.schedule(info);
                if(result == JobScheduler.RESULT_SUCCESS){
                    Log.d("Tiennvh", "onClick:OKE ");
                }else {
                    Log.d("Tiennvh", "onClick: not OKE ");
                }
        }
        }

//        if(intent.getExtras() != null)
//        {
//
//            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
//            if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED)
//            {
//                Log.i("app", "Network " + ni.getTypeName() + " connected");
//            }
//        }
//


    }
}
