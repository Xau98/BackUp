package com.android.backup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends Activity {
TextView mTextView;
Button mListBackup, mSaveNow;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
       /*  Bundle extras = getIntent().getExtras();
        if(extras == null){
            return;
        }
       textView= findViewById(R.id.email_account);
        String token = extras.getString("token");
        if(token!=null){
          textView.setText("KQ :"+ token);
        }*/
        mListBackup = findViewById(R.id.list_backup);
        mSaveNow = findViewById(R.id.bt_backup_now);
        mSaveNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                

            }
        });
        mListBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), BackupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
            }
        });


    }

    // Bkav TienNVh :init

}
