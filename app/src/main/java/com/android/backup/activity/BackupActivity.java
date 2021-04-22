package com.android.backup.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.backup.ItemListRestore;
import com.android.backup.RequestToServer;
import com.android.backup.ServiceBackup;
import com.android.backup.adapter.AdapterItemFile;
import com.android.backup.Dialog;
import com.android.backup.FileItem;
import com.android.backup.fragment.FragmentBackuping;
import com.android.backup.fragment.FragmentRestoring;
import com.android.backup.fragment.FragmentStatusBackUp;
import com.android.backup.R;
import com.android.backup.handleFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BackupActivity extends AppCompatActivity implements Dialog.onConfirmBackup, AdapterItemFile.isChooseFolder, FragmentBackuping.callbackBackup {
    FragmentStatusBackUp fragmentStatusBackUp;
    FragmentRestoring mFragmentRestoring;
    FragmentBackuping fragmentBackuping;
    RecyclerView mRecyclerView;
    ArrayList<FileItem> mListAllFile;
    Button mBTBackHome;
    ImageButton mBTUpdateName;
    EditText mNameBackup;
    Dialog dialog;
    Callback mCallback;
    boolean isRestore = false;
    long mTotalCapacityChecked = 0;
    ArrayList <FileItem> mListFileChecked;
    TextView mNameTab, mShowDateBackup;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        dialog= new Dialog();
        dialog.setConfirmListener(this);
        mNameBackup = findViewById(R.id.edittext_name_backup);
        mNameTab = findViewById(R.id.name_tab_backup_activity);
        mBTUpdateName = findViewById(R.id.bt_change_name);
        mShowDateBackup = findViewById(R.id.show_date_backup);
        mListFileChecked = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerview_backup);
        mListAllFile = new ArrayList<>();
        AdapterItemFile adapterListFile;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRestore = bundle.getBoolean("restore");
        }
        if(isRestore){
            ItemListRestore itemListRestore = (ItemListRestore) bundle.getSerializable("ItemListRestore");
            mNameBackup.setText(itemListRestore.getName());
            mShowDateBackup.setText(itemListRestore.getDateBackup());
            restoreFile();
            mNameTab.setText(bundle.getString("NameTab"));
            mBTUpdateName.setVisibility(View.VISIBLE);
            mFragmentRestoring = new FragmentRestoring(dialog,0);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, mFragmentRestoring).commit();
            adapterListFile=new AdapterItemFile(this, mListAllFile, true ,true);

        }else {
            initFile();
            mBTUpdateName.setVisibility(View.GONE);
            fragmentStatusBackUp = new FragmentStatusBackUp(dialog, 0);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, fragmentStatusBackUp).commit();
            adapterListFile=new AdapterItemFile(this, mListAllFile, true ,false);
        }
        mBTUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHAREPREFENCE, MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", sharedPref.getString("id", null));
                    jsonObject.put("token", sharedPref.getString("token", null));
                    jsonObject.put("newname", mNameBackup.getText().toString());
                    int ID_history = bundle.getInt("position");
                    Log.d("Tiennvh", "onClick: "+ ID_history);
                    jsonObject.put("id_history", ID_history+"");
                    String path = "renamebackup";
                    RequestToServer.post(path, jsonObject, mCallback);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Tiennvh", "onCallbackBackup: "+e);
                }
            }
        });


        mRecyclerView.setAdapter(adapterListFile);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterListFile.setChooseFolder((AdapterItemFile.isChooseFolder) this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mBTBackHome =findViewById(R.id.bt_backhome);
        mBTBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mCallback= new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String mJsonData = response.body().string();
                    Log.d("Tiennvh", "isSuccessful: "+ mJsonData);
                }
            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initFile(){
         mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void restoreFile(){
        //mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);
    }

    @Override
    public void onConfirm(int type) {
        if(type == 0) {
            if (isRestore) {
                fragmentBackuping = new FragmentBackuping(mListFileChecked, dialog);
                fragmentBackuping.setCallbackBackup(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FagmentBackup, fragmentBackuping).commit();
                AdapterItemFile adapterListFile = new AdapterItemFile(this, mListFileChecked, false, true);
                mRecyclerView.setAdapter(adapterListFile);

            } else {
                fragmentBackuping = new FragmentBackuping(mListFileChecked, dialog);
                fragmentBackuping.setCallbackBackup(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FagmentBackup, fragmentBackuping).commit();
                AdapterItemFile adapterListFile = new AdapterItemFile(this, mListFileChecked, false, false);
                mRecyclerView.setAdapter(adapterListFile);
            }
        }else {
            Log.d("Tiennvh", "onConfirm: "+type);
        }

    }

    @Override
    public void getTotalCapacity(FileItem fileItem , boolean ischecked) {
        if (isRestore) {
            Log.d("Tiennvh", "getTotalCapacity: "+ fileItem.getName());
            if (handleFile.duplicateFileItem(mListFileChecked, fileItem) && ischecked) {
                mListFileChecked.add(fileItem);
                mTotalCapacityChecked += fileItem.getSize();
            }
            if (!ischecked) {
                mListFileChecked = handleFile.removeFileItem(mListFileChecked, fileItem);
                mTotalCapacityChecked -= fileItem.getSize();
            }
            mFragmentRestoring = new FragmentRestoring(dialog, mTotalCapacityChecked);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, mFragmentRestoring).commit();

        } else {
            if (handleFile.duplicateFileItem(mListFileChecked, fileItem) && ischecked) {
                mListFileChecked.add(fileItem);
                mTotalCapacityChecked += fileItem.getSize();
            }
            if (!ischecked) {
                mListFileChecked = handleFile.removeFileItem(mListFileChecked, fileItem);
                mTotalCapacityChecked -= fileItem.getSize();
            }
            fragmentStatusBackUp = new FragmentStatusBackUp(dialog, mTotalCapacityChecked);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, fragmentStatusBackUp).commit();
        }

    }


    @Override
    public void onCallbackBackup(String isSuccessful) {
        //Bkav Tiennvh: update DB
        if(!isSuccessful.equals("False")) {
             SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHAREPREFENCE, MODE_PRIVATE);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", sharedPref.getString("id", "0"));
                jsonObject.put("token", sharedPref.getString("token", "0"));
                jsonObject.put("namebackup", mNameBackup.getText().toString());
                jsonObject.put("namedevice", "Bphone 4");
                jsonObject.put("path", isSuccessful);
                String path = "insertbackup";
                RequestToServer.post(path, jsonObject, mCallback);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tiennvh", "onCallbackBackup: "+e);
            }
        }else{
            Log.d("Tiennvh", "onCallbackBackup: Not OKE");
        }
    }
}
