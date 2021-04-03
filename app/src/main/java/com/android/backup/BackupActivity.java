package com.android.backup;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BackupActivity extends AppCompatActivity {
    FragmentStatusBackUp fragmentStatusBackUp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        fragmentStatusBackUp = new FragmentStatusBackUp();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FagmentBackup, fragmentStatusBackUp).commit();


    }
}
