package com.android.backup.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.android.backup.R;

public class RegisterAcivity extends Activity {

EditText mEmail, mPassword,mREPassword;
Button mBTRegister;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mREPassword = findViewById(R.id.register_prepassword);
        mBTRegister = findViewById(R.id.bt_confirm_regidter);
        mBTRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
