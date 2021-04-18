package com.android.backup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BackupActivity extends AppCompatActivity implements Dialog.onConfirmBackup , AdapterItemFile.isChooseFolder, FragmentBackuping.callbackBackup {
    FragmentStatusBackUp fragmentStatusBackUp;
    FragmentRestoring mFragmentRestoring;
    FragmentBackuping fragmentBackuping;
    RecyclerView mRecyclerView;
    ArrayList<FileItem> mListAllFile;
    Button mBTBackHome;
    Dialog dialog;
    boolean isRestore = false;
    long mTotalCapacityChecked = 0;
    ArrayList <FileItem> mListFileChecked;
    TextView mNameTab;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        dialog= new Dialog();
        dialog.setConfirmListener(this);
        mNameTab = findViewById(R.id.name_tab_backup_activity);
        mListFileChecked = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerview_backup);
        mListAllFile = new ArrayList<>();
        AdapterItemFile adapterListFile;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRestore = bundle.getBoolean("restore");
        }
        if(isRestore){
            restoreFile();
            mNameTab.setText(bundle.getString("NameTab"));
            int ID = bundle.getInt("position");
            int position=0;

            mFragmentRestoring = new FragmentRestoring(dialog,ID);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, mFragmentRestoring).commit();
            adapterListFile=new AdapterItemFile(this, mListAllFile, true ,true);



        }else {
            initFile();
            fragmentStatusBackUp = new FragmentStatusBackUp(dialog, 0);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, fragmentStatusBackUp).commit();
            adapterListFile=new AdapterItemFile(this, mListAllFile, true ,false);
        }



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


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initFile(){
         mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void restoreFile(){
        mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);
    }

    @Override
    public void onConfirm(int type) {
        if(type ==0) {
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
    public void onCallbackBackup(boolean isSuccessful) {

    }
}
