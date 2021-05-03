package com.android.backup.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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

public class HomePage extends AppCompatActivity implements Dialog.onConfirmBackup {
TextView mTextViewEmail, mTextViewBackuplast;
Button mListBackup, mSaveNow;
Switch mAutoBackup;
ConstraintLayout  mInfoAccount;
Callback mCallback;
Handler mHandler;
String mJsonData;
SharedPreferences mSharedPref;
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
        Dialog dialog = new Dialog();
        dialog.setConfirmListener(this);
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
        Log.d("Tiennvh", "onCreate: "+mSharedPref.getBoolean("modeautobackup", false));
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
                dialog.showDialog(HomePage.this,inflater , title, true,0);
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


    }


    private void createChannelNotification(){
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "Channel service ", NotificationManager.IMPORTANCE_DEFAULT);
         NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        }
    }
    // Bkav TienNVh :init

}
