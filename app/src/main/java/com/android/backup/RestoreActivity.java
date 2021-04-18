package com.android.backup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RestoreActivity extends AppCompatActivity implements AdapterListFileRestore.onCallBackRestore , Dialog.onConfirmBackup{

    RecyclerView mRecyclerViewListRestore ,mRecyclerViewListRestoreOther;
    ArrayList <ItemListRestore> mListRestore;
    ArrayList <ItemListRestore> mListRestoreOther;
    LinearLayout mLinearLayout;
    CheckBox mCheckBoxAll;
    ImageButton mBTDelete;
    AdapterListFileRestore adapterItemFile;
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
        for(int i=0;i<5;i++) {
            ItemListRestore ir = new ItemListRestore(i,"backup "+i,"16/4/2021","Bphone",0);
            mListRestore.add(ir);
            mListRestoreOther.add(ir);
        }
}
    Dialog dialog= new Dialog();
    int mPositionDelete = -1 ;
    @Override
    public void onConfirmDeleteRestore(int position) {
        LayoutInflater inflater = getLayoutInflater();
        dialog.showDialog(this,inflater,"Bạn có chắc muốn xóa hay không ?", true,0);
        mPositionDelete = position;
    }

    @Override
    public void onClickItemRestore(int position) {
        Intent intent=new Intent(this, BackupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("NameTab","Chi tiết");
        intent.putExtra("Position",position);
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
