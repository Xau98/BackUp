package com.android.backup.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.backup.service.ConditionAutoBackup;
import com.android.backup.Dialog;
import com.android.backup.R;
import com.android.backup.RequestToServer;
import com.android.backup.service.ServiceAutoBackup;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomePage extends AppCompatActivity {
TextView mTextViewEmail, mTextViewBackuplast;
Button mListBackup, mSaveNow;
Switch mAutoBackup;
ConstraintLayout  mInfoAccount;
Callback mCallback;
Handler mHandler;
String mJsonData;
SharedPreferences mSharedPref;
long mScheduleTime = 0;
public  static  final String CHANNEL_ID= "channel_service";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        mTextViewEmail= findViewById(R.id.email_account);
        mSharedPref= getSharedPreferences( MainActivity.SHAREPREFENCE, Context.MODE_PRIVATE);
        String email = mSharedPref.getString("email", "email");
        mTextViewEmail.setText(email);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mListBackup = findViewById(R.id.list_backup);
        mSaveNow = findViewById(R.id.bt_backup_now);
        mAutoBackup = findViewById(R.id.switch_auto_backup);
        mInfoAccount = findViewById(R.id.account_backup);
        mTextViewBackuplast = findViewById(R.id.textview_backuplast);
        mInfoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ProfileAccount.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
            }
        });
        mSaveNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), BackupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);

            }
        });
        mListBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RestoreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
            }
        });
        mAutoBackup.setChecked(mSharedPref.getBoolean("modeautobackup", false));

        mAutoBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                String title ="";
                if(mAutoBackup.isChecked())
                    title ="Hãy xác nhận bạn muốn bắt đầu quá trình sao lưu dữ liệu tự động";
                else
                    title ="Hãy xác nhận bạn tắt quá trình sao lưu dữ liệu tự động";
                showDialog(HomePage.this,inflater , title , mAutoBackup.isChecked());
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 5:
                        if (!mJsonData.equals("False")) {
                            Log.d("Tiennvh", "handleMessage: "+mJsonData);
                            mTextViewBackuplast.setText(mJsonData);
                            break;
                        }else {
                            Log.d("Tiennvh", "handleMessage: ");
                        }
                }
            }
        };
        mCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    mJsonData= response.body().string();
                    mHandler.sendEmptyMessage(5);
                }
            }
        };
        createChannelNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JSONObject jsonObject = new JSONObject();
        SharedPreferences sharedPref =  getSharedPreferences(MainActivity.SHAREPREFENCE,  MODE_PRIVATE);
        try {
            jsonObject.put("id", sharedPref.getString("id", null));
            jsonObject.put("token", sharedPref.getString("token", null));
            String path = "getbackuplast";
            RequestToServer.post(path, jsonObject, mCallback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    ServiceAutoBackup mServiceBackup;
    boolean mBound = false;
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceAutoBackup.BackupBinder binder = (ServiceAutoBackup.BackupBinder) iBinder;
            mServiceBackup = binder.getMusicBinder();
            Log.d("Tiennvh", "onServiceConnected: "+mServiceBackup);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            Log.d("Tiennvh", "onServiceDisconnected: "+ componentName);
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.putInt("play", mPosition);
//            editor.putString("nameSong",mNameSong.getText()+"");
//            editor.apply();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }
    private void startForgoundService() {
        // Bind to LocalService
        Intent intent = new Intent(this, ServiceAutoBackup.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startForegroundService(intent);
    }

    public void showDialog(Context context, LayoutInflater inflater, String title , boolean statusSwtich ){
        View alertLayout = inflater.inflate(R.layout.dialog_confirm, null);
        TextView tile = alertLayout.findViewById(R.id.title_dialog);
        LinearLayout linearLayout = alertLayout.findViewById(R.id.schedule_auto_backup);
        RadioButton radioBT_7Day = alertLayout.findViewById(R.id.radio_7day);
        RadioButton radioBT_Everyday = alertLayout.findViewById(R.id.radio_everyday);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        tile.setText(title);

        if(radioBT_7Day.isChecked()){
            mScheduleTime = 7*24*60*1000;
            // mScheduleTime = 12*1000;
        }

        if(radioBT_Everyday.isChecked()){
           mScheduleTime = 1*24*60*1000;
          //  mScheduleTime = 6*1000;
        }

        if(!statusSwtich){
            linearLayout.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.VISIBLE);
        }
        alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
                if(statusSwtich)
                      mAutoBackup.setChecked(false);
                else
                      mAutoBackup.setChecked(true);
            }
        });
            alert.setPositiveButton("Xác Nhận", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // code for matching password
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putBoolean("modeautobackup",mAutoBackup.isChecked());
                    editor.commit();
                    if(statusSwtich){
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                        ConditionAutoBackup myReceiver = new ConditionAutoBackup(mScheduleTime);
                        startForgoundService();
                        myReceiver.setCallbackConditionBackup(new ConditionAutoBackup.callbackConditionBackup() {
                            @Override
                            public void onCallback() {
                                if(mBound)
                                    mServiceBackup.updateUI();
                            }
                        });
                        registerReceiver(myReceiver,intentFilter);
                    }else {
                        //stopService(intent);
                    }
                }
            });
        alert.show();
    }
/*
    @Override
    public void onConfirm(int type) {
        Log.d("Tiennvh", mAutoBackup.isChecked()+"onConfirm: "+type);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean("modeautobackup",mAutoBackup.isChecked());
        editor.commit();
        Intent intent = new Intent(this, ServiceAutoBackup.class);
        if(mAutoBackup.isChecked()) {
            startService(intent);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            ConditionAutoBackup myReceiver = new ConditionAutoBackup();
            registerReceiver(myReceiver,intentFilter);

        }else {
            stopService(intent);
        }


    }*/


    private void createChannelNotification(){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel service ", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mBound) {
//            unbindService(mServiceConnection);
//            mBound = false;
//        }
//    }

    // Bkav TienNVh :init

}
