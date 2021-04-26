package com.android.backup.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.backup.code.AsyncTaskDownload;
import com.android.backup.code.Code;
import com.android.backup.ItemListRestore;
import com.android.backup.RequestToServer;
import com.android.backup.adapter.AdapterItemFile;
import com.android.backup.Dialog;
import com.android.backup.FileItem;
import com.android.backup.fragment.FragmentBackuping;
import com.android.backup.fragment.FragmentRestoring;
import com.android.backup.fragment.FragmentStatusBackUp;
import com.android.backup.R;
import com.android.backup.handleFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;

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
    ItemListRestore itemListRestore;
    Handler mHandler;
    String mJsonData;
    AdapterItemFile adapterListFile;
    SharedPreferences mSharedPref;
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
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 9:
                        if (!mJsonData.equals("False")) {
                            try {
                                JSONObject Jobject = new JSONObject(mJsonData);
                                JSONArray listData = Jobject.getJSONArray("list");
                                for(int i=0;i<listData.length();i++){
                                    String name = (String) listData.get(i);
                                    String name1= name.substring(0, name.length()-4);
                                    FileItem fileItem = new FileItem(name1,itemListRestore.getPath()+"/"+name);
                                    mListAllFile.add(fileItem);
                                    adapterListFile.notifyDataSetChanged();
                                }
                                Log.d("Tiennvh", "handleMessage: "+ mListAllFile.size());
                            } catch (JSONException e) {
                                Log.d("Tiennvh", "handleMessage: " + e);
                            }
                            break;
                        }else {

                            Toast.makeText(getApplicationContext(), "Đăng nhập thất bại "  , LENGTH_SHORT).show();
                        }
                }
            }
        };
        mCallback= new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Tiennvh", "isSuccessful: ");
                    mJsonData = response.body().string();
                    if(!mJsonData.equals("True"))
                        mHandler.sendEmptyMessage(9);
                    Log.d("Tiennvh", "isSuccessful: "+ mJsonData);
                }
            }
        };
        mSharedPref = getSharedPreferences(MainActivity.SHAREPREFENCE, MODE_PRIVATE);
        String id = mSharedPref.getString("id", null);
        String token = mSharedPref.getString("token", null) ;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRestore = bundle.getBoolean("restore");
        }
        if(isRestore){
            itemListRestore = (ItemListRestore) bundle.getSerializable("ItemListRestore");
            mNameBackup.setText(itemListRestore.getName());
            mShowDateBackup.setText(itemListRestore.getDateBackup());
            restoreFile();
            mNameTab.setText(bundle.getString("NameTab"));
            mBTUpdateName.setVisibility(View.VISIBLE);
            mFragmentRestoring = new FragmentRestoring(dialog,0);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FagmentBackup, mFragmentRestoring).commit();
            adapterListFile=new AdapterItemFile(this, mListAllFile, true ,true);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("token", token);
                jsonObject.put("path",itemListRestore.getPath());
                String path = "getlistdata";
                RequestToServer.post(path, jsonObject, mCallback);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tiennvh", "onCallbackBackup: "+e);
            }


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
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("token", token);
                    jsonObject.put("newname", mNameBackup.getText().toString());
                    jsonObject.put("id_history", itemListRestore.getID()+"");
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


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initFile(){
         mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void restoreFile(){
        //mListAllFile = handleFile.loadFile(handleFile.PATH_ROOT);
    }
    FileOutputStream fos;
    String mPathfile;
    @Override
    public void onConfirm(int type) {
        if(type == 0) {
            if (isRestore) {

                fragmentBackuping = new FragmentBackuping(mListFileChecked, dialog, true);
                fragmentBackuping.setCallbackBackup(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FagmentBackup, fragmentBackuping).commit();
                AdapterItemFile adapterListFile = new AdapterItemFile(this, mListFileChecked, false, true);
                mRecyclerView.setAdapter(adapterListFile);
               /* Callback mCallback1= new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("Tiennvh", "onFailure: "+e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d("Tiennvh", "onResponse: ");
                            fos.write(response.body().bytes());
                            try {
                                Log.d("Tiennvh", "onResponse: "+mPathfile);
                                Code.decrypt(getApplicationContext(),mPathfile,handleFile.PATH_ROOT+"/CompressionFile");
                                Code.decrypt(getApplicationContext(),handleFile.PATH_ROOT+"/CompressionFile/Pictures.txt",handleFile.PATH_ROOT+"/CompressionFile/"+mPathfile+"2.zip");
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                                Log.d("Tiennvh", "onResponse: "+e);
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                                Log.d("Tiennvh", "onResponse: "+e);
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                                Log.d("Tiennvh", "onResponse: "+e);
                            }
                            fos.close();
                        }
                    }
                };
                String id = mSharedPref.getString("id", null);
                String token = mSharedPref.getString("token", null) ;
                AsyncTaskDownload asyncTaskDownload;
                for(int i=0;i<mListAllFile.size();i++){
                    asyncTaskDownload = new AsyncTaskDownload(this,mListAllFile.get(i) );
                    asyncTaskDownload.execute();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", id);
                        jsonObject.put("token", token);
                        jsonObject.put("path", mListAllFile.get(i).getPath()+"/"+ mListAllFile.get(i).getName()+".txt" );
                        mPathfile = handleFile.PATH_ROOT+"/CompressionFile/"+ mListAllFile.get(i).getName()+".zip";
                        fos = new FileOutputStream(handleFile.PATH_ROOT+"/CompressionFile/"+ mListAllFile.get(i).getName()+".txt" );
                        RequestToServer.post("download", jsonObject,  mCallback1);
                    } catch (FileNotFoundException | JSONException e) {
                    e.printStackTrace();
                }
                }*/
            } else {
               fragmentBackuping = new FragmentBackuping(mListFileChecked, dialog, false);
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
