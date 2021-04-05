package com.android.backup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterItemFile extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<ItemFile> mList;
    Boolean aBoolean= true;
    public AdapterItemFile(Context mContext, ArrayList<ItemFile> mList, Boolean b) {
        this.mContext = mContext;
        this.mList = mList;
        aBoolean =b;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(mContext);
        View file = inflater.inflate(R.layout.item_file,parent,false);
        ViewHolder viewHolder = new ViewHolder(file);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemFile fileItem = mList.get(position);



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View itemview;
        public TextView studentname;
        public TextView statusBackup;
        public Button detail_button;
        public  CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            statusBackup = itemView.findViewById(R.id.status_backup);
            if(aBoolean) {
                checkBox.setVisibility(View.VISIBLE);
                statusBackup.setVisibility(View.GONE);
            }
            else {
                checkBox.setVisibility(View.GONE);
                statusBackup.setVisibility(View.VISIBLE);
            }
        }
    }
}
