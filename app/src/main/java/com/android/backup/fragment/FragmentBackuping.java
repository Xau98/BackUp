package com.android.backup.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.backup.activity.MainActivity;
import com.android.backup.code.AsyncTaskDownload;
import com.android.backup.code.AsyncTaskUpload;
import com.android.backup.Dialog;
import com.android.backup.FileItem;
import com.android.backup.R;
import com.android.backup.code.Code;
import com.android.backup.handleFile;
import com.android.backup.service.ServiceAutoBackup;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentBackuping extends Fragment {
    public static final int MSG_BACKUP = 3;
    ArrayList <FileItem> mListFileChecked;
    ProgressBar mProgressBar;
    TextView showTotalFileChecked , mStatusLoad;
    ImageButton mPauseBackup, mStopBackup;
    Dialog dialog;
    int mCountUpload = 0;
    long mTotalCapacity =0;
    boolean mIsRestore = false;
    String mNameBackup = null;
    ServiceAutoBackup mServiceAutoBackup;
    public FragmentBackuping(ArrayList<FileItem> listFileChecked,ServiceAutoBackup serviceAutoBackup, Dialog dialog, boolean isRestore, String namebackup) {
        mListFileChecked = listFileChecked;
        this.dialog=dialog;
        mIsRestore = isRestore;
        mServiceAutoBackup = serviceAutoBackup;
        mNameBackup = namebackup;
    }

    public  void setTotalCapacity(long totalCapacity){
        mTotalCapacity = totalCapacity;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backing,container,false);
        showTotalFileChecked = view.findViewById(R.id.capacity_backing);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mStatusLoad = view.findViewById(R.id.status_load);
        mPauseBackup = view.findViewById(R.id.pause_bachup);
        mStopBackup = view.findViewById(R.id.stop_bachup);
        //Log.d("Tiennvh", "onCreateView: "+ mServiceAutoBackup);
        mPauseBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPauseBackup.setVisibility(View.GONE);
                mStopBackup.setVisibility(View.VISIBLE);
                mStatusLoad.setText("Đang chuẩn bị...");
                LayoutInflater inflater = getLayoutInflater();
                String title ="Bạn có chắc muốn tiếp tục đồng bộ dữ liệu hay không ?";
                dialog.showDialog(getContext(), inflater, title, true, 1);
            }
        });
        mStopBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPauseBackup.setVisibility(View.VISIBLE);
                mStopBackup.setVisibility(View.GONE);
                mStatusLoad.setText("Tạm dừng ...");
                LayoutInflater inflater = getLayoutInflater();
                String title ="Bạn có chắc muốn dừng đồng bộ dữ liệu hay không ?";
                if(mServiceAutoBackup!= null)
                    mServiceAutoBackup.onStopBackup();
                dialog.showDialog(getContext(), inflater, title, true,2);
            }
        });

   /*     mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_BACKUP:
                        if (!mJsonData.equals("False")) {
                            if(mCountUpload==0)
                             mCallbackBackup.onCallbackBackup(mJsonData);
                            mStopBackup.setVisibility(View.INVISIBLE);
                            mPauseBackup.setVisibility(View.INVISIBLE);
                            mCallbackBackup.onFinishItem(0);
                            //Bkav TienNVh :comment
                           // mListFileChecked.clear();
                            mCountUpload++;
                        }
                }
            }
        };*/
        float capacity = 0;
        if(!mIsRestore)
            capacity = handleFile.KBToMB(handleFile.totalCapacity(mListFileChecked));
        else
            capacity = handleFile.KBToMB(mTotalCapacity);
        showTotalFileChecked.setText((Math.ceil(capacity* 10) / 10)+"MB");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO: Them dieu kien backup
        Log.d("Tiennvh", "onStart: "+mServiceAutoBackup);
        if(!mIsRestore) {
            if(mServiceAutoBackup!=null) {
                if(mServiceAutoBackup.isAsyncTaskRunning()){
                    int percen =mServiceAutoBackup.getPercenProgress();
                    mProgressBar.setProgress(percen);
                    if(percen == 100) {
                        mStatusLoad.setText("Đã xong");
                    }
                }else {
                    mServiceAutoBackup.onUploadAll(mListFileChecked ,mProgressBar, mStatusLoad , mNameBackup );
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int percen =mServiceAutoBackup.getPercenProgress();
                        mProgressBar.setProgress(percen);
                        mStatusLoad.setText("Đang Backuping : "+percen+"%");
                        if(percen == 100) {
                            mStatusLoad.setText("Đã xong");
                        }else
                            handler.postDelayed(this, 500);
                    }
                }, 100);

            }
        }else {
            AsyncTaskDownload asyncTaskDownload;
            for (int i = 0; i < mListFileChecked.size(); i++) {
                asyncTaskDownload = new AsyncTaskDownload( getContext(),mListFileChecked.get(i) ,mProgressBar, mStatusLoad);
                asyncTaskDownload.execute();
            }
        }
    }
}
