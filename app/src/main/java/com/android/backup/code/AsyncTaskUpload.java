package com.android.backup.code;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.backup.CompressionFile;
import com.android.backup.ConvertNameFile;
import com.android.backup.FileItem;
import com.android.backup.RequestToServer;
import com.android.backup.code.Code;
import com.android.backup.handleFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import okhttp3.Callback;

public class AsyncTaskUpload extends AsyncTask<Void, String , String> {

    FileItem mFileItem;
    Context mContext;
    String mConvertName;
    ProgressBar mProgressBar;
    TextView mStatusLoad;
    Callback mCallback;
    String mPathsave ;

    public AsyncTaskUpload(Context context , FileItem fileItem, String  pathSave , Callback callback, ProgressBar progressBar , TextView status) {
        this.mFileItem = fileItem;
        mContext = context;
        mProgressBar =progressBar;
        mStatusLoad = status;
        mCallback = callback;
        mPathsave = pathSave;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("Tiennvh", "onPreExecute: ");
        mConvertName = ConvertNameFile.NameFolderToFile(mFileItem.getName().toString());
        CompressionFile.zipDirectory(handleFile.PATH_ROOT+"/"+mFileItem.getName(),handleFile.PATH_ROOT+"/CompressionFile/"+ mConvertName+".zip");
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.d("Tiennvh", "doInBackground: ");
        try {
            Code.encrypt(mContext,handleFile.PATH_ROOT+"/CompressionFile/"+ mConvertName+".zip",handleFile.PATH_ROOT+"/CompressionFile/"+mConvertName+".txt");
            //Code.decrypt(getContext(),handleFile.PATH_ROOT+"/CompressionFile/Pictures.txt",handleFile.PATH_ROOT+"/CompressionFile/"+convertName+"2.zip");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "Xong";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("Tiennvh", "onPostExecute: "+s);
        String namePath = handleFile.PATH_ROOT+"/CompressionFile/"+ mConvertName +".txt";
        RequestToServer.upload(mContext,mPathsave ,"uploadfile",namePath, mCallback, mProgressBar, mStatusLoad );
        handleFile.deleteFile(handleFile.PATH_ROOT+"/CompressionFile/"+ mConvertName+".zip");
    }
}