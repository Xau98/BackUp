package com.android.backup;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import org.json.JSONObject;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestToServer {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private Activity mActivity;
    private static final String AD_SERVER="http://172.36.68.238:2405/";
    public RequestToServer(Activity activity) {
        this.mActivity = activity;
    }

// Bkav TienNVh : method post
    public static void post(String path, JSONObject jsonObject, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = AD_SERVER + path;
        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

// Bkav TienNVh : Method GET
    public static void get(String path, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = AD_SERVER + path;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

// RequestToServer.upload("uploadfile",handleFile.PATH_ROOT+"/DCIM/Camera/Tien.jpg",callback);
    public static  void upload(String path, String namePath , Callback callback){
        OkHttpClient client = new OkHttpClient();
        String url = AD_SERVER + path;
        File file = new File(namePath);
        RequestBody postBodyImage = new MultipartBody.Builder()
              .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask2.jpg", RequestBody.create(MediaType.parse("image/*jpg"),file ))
              .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", postBodyImage)
                .build();
        client.newCall(request).enqueue(callback);
    }
    //

    // Bkav TienNVh : Check internet
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
