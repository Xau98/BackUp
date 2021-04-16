package com.android.backup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterListFileRestore extends RecyclerView.Adapter<AdapterListFileRestore.ViewHolder> {
    private Context mContext;
    private ArrayList<ItemListRestore> mList;


    public AdapterListFileRestore(Context mContext, ArrayList<ItemListRestore> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_list_restore,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mNameBackup.setText(mList.get(position).getName());
        holder.mDateBackup.setText(mList.get(position).getDateBackup());

    }



    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mNameBackup, mDateBackup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameBackup = itemView.findViewById(R.id.name_restore);
            mDateBackup = itemView.findViewById(R.id.date_restore);

        }
    }
}
