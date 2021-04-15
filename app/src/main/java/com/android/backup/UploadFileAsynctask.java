package com.android.backup;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UploadFileAsynctask extends AsyncTask<Void ,String , Void> {

    Activity mActivity;

    public UploadFileAsynctask(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //CompressionFile.zipDirectory(handleFile.PATH_FOLDER,CompressionFile.PATH_COMPRESSION);
        //handleFile.deleteFile(handleFile.PATH_FOLDER);

    }

    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }
}
