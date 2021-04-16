package com.android.backup;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RestoreActivity extends AppCompatActivity {

    RecyclerView mRecyclerViewListRestore ,mRecyclerViewListRestoreOther;
    ArrayList <ItemListRestore> mListRestore;
    ArrayList <ItemListRestore> mListRestoreOther;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_activity);
        mRecyclerViewListRestore = findViewById(R.id.recyclerview_restore );
        mRecyclerViewListRestoreOther = findViewById(R.id.recyclerview_restore_other);





    }



}
