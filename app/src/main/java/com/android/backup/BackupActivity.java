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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BackupActivity extends AppCompatActivity implements Dialog.onConfirmBackup , AdapterItemFile.isChooseFolder {
    FragmentStatusBackUp fragmentStatusBackUp;
    FragmentBackuping fragmentBackuping;
    RecyclerView mRecyclerView;
    ArrayList<FileItem> mListAllFile;
    Button mBTBackHome;
    Dialog dialog;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        dialog= new Dialog();
        dialog.setConfirmListener(this);
        fragmentBackuping = new FragmentBackuping();
        fragmentStatusBackUp = new FragmentStatusBackUp(dialog, 0);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FagmentBackup, fragmentStatusBackUp).commit();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mBTBackHome =findViewById(R.id.bt_backhome);
        mBTBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerview_backup);
        mListAllFile = new ArrayList<>();
        for(int i=0;i<10;i++)
            initFile();
        AdapterItemFile adapterListFile=new AdapterItemFile(this, mListAllFile, true);
        mRecyclerView.setAdapter(adapterListFile);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterListFile.setChooseFolder((AdapterItemFile.isChooseFolder) this);
        mListFileChecked = new ArrayList<>();

        handleFile.createFolderCompression();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initFile(){
         mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);

    }

    @Override
    public void onConfirm() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FagmentBackup, fragmentBackuping).commit();
        AdapterItemFile adapterListFile=new AdapterItemFile(this, mListAllFile, false);
        mRecyclerView.setAdapter(adapterListFile);
    }

    ArrayList <FileItem> mListFileChecked;
    @Override
    public void getTotalCapacity(FileItem fileItem , boolean ischecked) {
        if(handleFile.duplicateFileItem(mListFileChecked,fileItem) && ischecked)
            mListFileChecked.add(fileItem);
        if(!ischecked)
            mListFileChecked = handleFile.removeFileItem(mListFileChecked, fileItem);
        fragmentStatusBackUp = new FragmentStatusBackUp(dialog, fileItem.getSize());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FagmentBackup, fragmentStatusBackUp).commit();
    }


}
