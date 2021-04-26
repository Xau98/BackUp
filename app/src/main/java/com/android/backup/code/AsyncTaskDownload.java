package com.android.backup.code;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.backup.CompressionFile;
import com.android.backup.FileItem;
import com.android.backup.RequestToServer;
import com.android.backup.activity.MainActivity;
import com.android.backup.handleFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AsyncTaskDownload extends AsyncTask<Void , String , String> {

   Context mContext;
    FileItem mFileItem;
    ProgressBar mProgressBar;
    TextView mStatusLoad;

    public AsyncTaskDownload(Context mContext, FileItem mFileItem, ProgressBar progressBar , TextView status) {
        this.mContext = mContext;
        this.mFileItem = mFileItem;
        mProgressBar = progressBar;
        mStatusLoad = status;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... voids) {
        Callback mCallback1= new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Tiennvh", "onResponse: ");
                    FileOutputStream fos = new FileOutputStream(handleFile.PATH_ROOT+"/CompressionFile/"+ mFileItem.getName()+".txt" );
                    fos.write(response.body().bytes());
                    fos.close();
                    new AsyncTaskdecrypt().execute();
                }
            }
        };
        SharedPreferences mSharedPref = mContext.getSharedPreferences(MainActivity.SHAREPREFENCE, mContext.MODE_PRIVATE);
        String id = mSharedPref.getString("id", null);
        String token = mSharedPref.getString("token", null) ;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("token", token);
            Log.d("Tiennvh", "onPreExecute: "+mFileItem.getPath());
            jsonObject.put("path", mFileItem.getPath());
            //mPathfile = handleFile.PATH_ROOT+"/CompressionFile/"+ mFileItem+".zip";
            RequestToServer.post("download", jsonObject,  mCallback1);
        } catch ( JSONException e) {
            e.printStackTrace();
        }

        return "Xong";
    }


    class AsyncTaskdecrypt extends AsyncTask<Void,String , String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String namFile = mFileItem.getName();
                Code.decrypt(mContext,handleFile.PATH_ROOT+"/CompressionFile/"+namFile+".txt",handleFile.PATH_ROOT+"/CompressionFile/"+namFile+".zip");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("Tiennvh", "onResponse: "+e);
            }
            return "OKE";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tiennvh", "onPostExecutedownload: "+s);
            String namFile = mFileItem.getName();
            String pathinput=handleFile.PATH_ROOT+"/CompressionFile/"+namFile ;
            CompressionFile.unZip(pathinput+".zip",pathinput);
        }
    }
}
