package com.android.backup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity implements Dialog.onConfirmBackup {
TextView mTextViewEmail;
Button mListBackup, mSaveNow;
Switch mAutoBackup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
       /*  Bundle extras = getIntent().getExtras();
        if(extras == null){
            return;
        }*/
       mTextViewEmail= findViewById(R.id.email_account);
        SharedPreferences sharedPref = getSharedPreferences( MainActivity.SHAREPREFENCE, Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "email");
        mTextViewEmail.setText(email);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mListBackup = findViewById(R.id.list_backup);
        mSaveNow = findViewById(R.id.bt_backup_now);
        mAutoBackup = findViewById(R.id.switch_auto_backup);
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


            }
        });
        Dialog dialog = new Dialog();
        dialog.setConfirmListener(this);
        mAutoBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                String title ="";
                if(mAutoBackup.isChecked())
                    title ="Hãy xác nhận bạn muốn bắt đầu quá trình sao lưu dữ liệu tự động";
                else
                    title ="Hãy xác nhận bạn tắt quá trình sao lưu dữ liệu tự động";
                dialog.showDialog(HomePage.this,inflater , title);
            }
        });

    }

    @Override
    public void onConfirm() {

    }

    // Bkav TienNVh :init

}
