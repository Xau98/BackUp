package com.android.backup.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.backup.R;
import com.android.backup.RequestToServer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterAcivity extends Activity {

EditText mEmail, mPassword,mREPassword, mName, mUsername;
Button mBTRegister;
Callback mCallback;
Handler mHandler;
String mJsonData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        mName = findViewById(R.id.register_name);
        mUsername = findViewById(R.id.register_username);
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mREPassword = findViewById(R.id.register_prepassword);
        mBTRegister = findViewById(R.id.bt_confirm_regidter);

        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 6:
                        if ( mJsonData.equals("True")) {
                                onBackPressed();
                            break;
                        }else {
                            if(mJsonData.equals("Duplicate"))
                                Toast.makeText(getBaseContext(), "Trùng  tài khoản ", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getBaseContext(), " Lỗi  ", Toast.LENGTH_SHORT).show();

                        }
                }
            }
        };
        mCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("Tiennvh", "isSuccessful: ");
                if (response.isSuccessful()) {
                   mJsonData = response.body().string();
                   mHandler.sendEmptyMessage(6);
                    Log.d("Tiennvh", "onResponse: "+mJsonData);

                }
            }
        };

        mBTRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getText().toString();
                String username = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                String repass = mREPassword.getText().toString();
                if(email.equals("")|pass.equals("")| repass.equals("")){

                    return;
                }
                if(pass.equals(repass)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email", email);
                        jsonObject.put("name",name);
                        jsonObject.put("username", username);
                        jsonObject.put("password", pass);
                        String path = "register";
                        RequestToServer.post(path , jsonObject,mCallback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getBaseContext(), " Mật khẩu không khớp ", Toast.LENGTH_SHORT);
                }
            }
        });

    }
}
