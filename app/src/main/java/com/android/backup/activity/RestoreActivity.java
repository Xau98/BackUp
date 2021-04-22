package com.android.backup.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.backup.Dialog;
import com.android.backup.ItemListRestore;
import com.android.backup.OnSwipeTouchListener;
import com.android.backup.R;
import com.android.backup.RequestToServer;
import com.android.backup.adapter.AdapterListFileRestore;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RestoreActivity extends AppCompatActivity implements AdapterListFileRestore.onCallBackRestore , Dialog.onConfirmBackup {
    public static final  int MSG_LIST_RESTORE = 9;
    RecyclerView mRecyclerViewListRestore ,mRecyclerViewListRestoreOther;
    ArrayList <ItemListRestore> mListRestore;
    ArrayList <ItemListRestore> mListRestoreOther;
    LinearLayout mLinearLayout;
    CheckBox mCheckBoxAll;
    ImageButton mBTDelete;
    AdapterListFileRestore adapterItemFile;
    Dialog dialog= new Dialog();
    int mPositionDelete = -1 ;
    String mJsonData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_activity);
        mRecyclerViewListRestore = findViewById(R.id.recyclerview_restore );
        mRecyclerViewListRestoreOther = findViewById(R.id.recyclerview_restore_other);
        mListRestore = new ArrayList<>();
        mListRestoreOther =new ArrayList<>();
        mCheckBoxAll = findViewById(R.id.checkbox_all_restore);
        mBTDelete = findViewById(R.id.bt_delete_all_restore);
        init();

        adapterItemFile= new AdapterListFileRestore(this,mListRestore );
        adapterItemFile.setOnCallBackRestore(this);
        mLinearLayout = findViewById(R.id.linear_title);
        mLinearLayout.setOnTouchListener(new OnSwipeTouchListener(RestoreActivity.this) {
            public void onSwipeTop() {

                Log.d("Tiennvh", "onSwipeTop: ");
            }
            public void onSwipeRight() {
                Log.d("Tiennvh", "onSwipeRight: ");
                 for(int i=0;i<mListRestore.size();i++){
                     mListRestore.get(i).setType(0);
                     adapterItemFile.notifyDataSetChanged();
                 }
                 mBTDelete.setVisibility(View.GONE);
                mCheckBoxAll.setVisibility(View.GONE);
            }
            public void onSwipeLeft() {
                Log.d("Tiennvh", "onSwipeLeft: ");
                for(int i=0;i<mListRestore.size();i++){
                    mListRestore.get(i).setType(1);
                    adapterItemFile.notifyDataSetChanged();
                }
                mBTDelete.setVisibility(View.VISIBLE);
                mCheckBoxAll.setVisibility(View.VISIBLE);
            }
            public void onSwipeBottom() {
                Log.d("Tiennvh", "onSwipeBottom: ");
            }

        });
        mCheckBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    for(int i=0;i<mListRestore.size();i++){
                        mListRestore.get(i).setType(3);
                        adapterItemFile.notifyDataSetChanged();

                    }
                }else {
                    for(int i=0;i<mListRestore.size();i++){
                        mListRestore.get(i).setType(4);
                        adapterItemFile.notifyDataSetChanged();

                    }
                }
            }
        });

        mBTDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListRestore = adapterItemFile.getmList();

                    ArrayList<ItemListRestore> list2 = new ArrayList<>();
                    for (int i = 0; i < mListRestore.size(); i++) {
                        if (mListRestore.get(i).getType() != 3) {
                            mListRestore.get(i).setType(4);
                            ItemListRestore itemListRestore = mListRestore.get(i);
                            list2.add(itemListRestore);
                        }
                    }
                    mListRestore = (ArrayList<ItemListRestore>) list2.clone();
                    adapterItemFile.setmList(mListRestore);
                    adapterItemFile.notifyDataSetChanged();
                if (mListRestore.size() <= 0) {
                    mCheckBoxAll.setVisibility(View.GONE);
                    mBTDelete.setVisibility(View.GONE);
                }
            }
        });
        mRecyclerViewListRestore.setAdapter(adapterItemFile);
        mRecyclerViewListRestore.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper  itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int p = viewHolder.getAdapterPosition();
                mListRestore.get(p).setType(2);
                adapterItemFile.notifyDataSetChanged();
            }});

        itemTouchHelper.attachToRecyclerView(mRecyclerViewListRestore);
        dialog.setConfirmListener(this);
    }


  public void init(){
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_LIST_RESTORE:
                        if (!mJsonData.equals("False")) {
                            JSONObject Jobject = null;
                            try {
                                Jobject = new JSONObject(mJsonData);
                                JSONArray jsonArray = Jobject.getJSONArray("content");
                                for (int i=0;i<jsonArray.length();i++) {
                                    JSONObject Jobject1 = jsonArray.getJSONObject(i);
                                    String id_history = Jobject1.getString("id_history");
                                    String name_history = Jobject1.getString("name_history");
                                    String date_backup = Jobject1.getString("date_backup");
                                    String devices_backup = Jobject1.getString("devices_backup");

                                    ItemListRestore ir = new ItemListRestore(Integer.parseInt(id_history),name_history,date_backup ,devices_backup,0);
                                    mListRestore.add(ir);
                                    adapterItemFile.notifyDataSetChanged();
                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }


                            break;
                        }else {
                            Log.d("Tiennvh", "handleMessage: error");
                        }
                }
            }
        };
        Callback mCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Tiennvh", "onFailure: "+e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    mJsonData = response.body().string();
                    handler.sendEmptyMessage(MSG_LIST_RESTORE);
                }

            }
        };
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHAREPREFENCE, MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", sharedPref.getString("id", null));
            jsonObject.put("token", sharedPref.getString("token", null));
            String path = "getlistbackup";
            RequestToServer.post(path, jsonObject, mCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Tiennvh", "onCallbackBackup: "+e);
        }
}

    @Override
    public void onConfirmDeleteRestore(int position) {
        LayoutInflater inflater = getLayoutInflater();
        dialog.showDialog(this,inflater,"Bạn có chắc muốn xóa hay không ?", true,0);
        mPositionDelete = position;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mListRestore.clear();
//        Log.d("Tiennvh", "onResume: "+ mListRestore.size());
        //init();
    }

    @Override
    public void onClickItemRestore(ItemListRestore itemListRestore) {
        Intent intent=new Intent(this, BackupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("NameTab","Chi tiết");
        intent.putExtra("ItemListRestore",itemListRestore);
        intent.putExtra("restore",true );
        startActivity(intent);
    }

    @Override
    public void onConfirm(int type) {
       if(mPositionDelete !=-1){
           for(int i=0;i<mListRestore.size();i++){
               if(mListRestore.get(i).getID()==mPositionDelete){
                   mListRestore.remove(i);
                   adapterItemFile.notifyDataSetChanged();
               }
           }
       }
    }
}
