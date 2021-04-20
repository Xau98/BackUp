package com.android.backup.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.backup.Code;
import com.android.backup.CompressionFile;
import com.android.backup.Dialog;
import com.android.backup.FileItem;
import com.android.backup.R;
import com.android.backup.RequestToServer;
import com.android.backup.activity.HomePage;
import com.android.backup.activity.MainActivity;
import com.android.backup.handleFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;

public class FragmentBackuping extends Fragment {
    ArrayList <FileItem> mListFileChecked;
    ProgressBar mProgressBar;
    TextView showTotalFileChecked , mStatusLoad;
    Callback callback;
    callbackBackup mCallbackBackup;
    ImageButton mPauseBackup, mStopBackup;
    Dialog dialog;
    String mJsonData ;
    Handler mHandler;
    public FragmentBackuping(ArrayList<FileItem> listFileChecked, Dialog dialog) {
        mListFileChecked = listFileChecked;
        this.dialog=dialog;
    }

    public void setCallbackBackup(callbackBackup callbackBackup){
        mCallbackBackup = callbackBackup;
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
                dialog.showDialog(getContext(), inflater, title, true,2);
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 3:
                        if (!mJsonData.equals("False")) {
                            mCallbackBackup.onCallbackBackup(true);
                            mStopBackup.setVisibility(View.INVISIBLE);
                            mPauseBackup.setVisibility(View.INVISIBLE);
                            for (int i=0;i<mListFileChecked.size();i++){
                                handleFile.deleteFile(handleFile.PATH_ROOT+"/CompressionFile/"+ mListFileChecked.get(i).getName()+".txt");
                            }
                        }
                }
            }
        };

        callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mCallbackBackup.onCallbackBackup(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    mJsonData = response.body().string();
                    mHandler.sendEmptyMessage(3);
                }else
                    mCallbackBackup.onCallbackBackup(false);
            }
        };
        showTotalFileChecked.setText(handleFile.totalCapacity(mListFileChecked)+"");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i=0;i<mListFileChecked.size();i++){
            CompressionFile.zipDirectory(handleFile.PATH_ROOT+"/"+ mListFileChecked.get(i).getName(),handleFile.PATH_ROOT+"/CompressionFile/"+ mListFileChecked.get(i).getName()+".zip");
            try {
                Code.encrypt(getContext(),handleFile.PATH_ROOT+"/CompressionFile/"+ mListFileChecked.get(i).getName()+".zip",handleFile.PATH_ROOT+"/CompressionFile/"+ mListFileChecked.get(i).getName()+".txt");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

            RequestToServer.upload(getContext(),"uploadfile",handleFile.PATH_ROOT+"/CompressionFile/"+ mListFileChecked.get(i).getName()+".txt", callback, mProgressBar, mStatusLoad );
            handleFile.deleteFile(handleFile.PATH_ROOT+"/CompressionFile/"+ mListFileChecked.get(i).getName()+".zip");

        }
    }

   public interface  callbackBackup{
        void onCallbackBackup(boolean isSuccessful);
    }
}
