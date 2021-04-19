package com.android.backup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.backup.Dialog;
import com.android.backup.R;

public class FragmentRestoring extends Fragment {

    TextView mCapationRestore;
    ImageButton mBTRestore;
    Dialog dialog;
    long mCapacity = 0;
    CallBackConfirmRestore mCallBackConfirmRestore;

    public FragmentRestoring(Dialog dialog, long mCapacity) {
        this.dialog = dialog;
        this.mCapacity = mCapacity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restoreing,container,false);
        mCapationRestore = view.findViewById(R.id.show_capacity_restore);
        mBTRestore = view.findViewById(R.id.bt_confirm_restore);
        mCapationRestore.setText(mCapacity+"");
        mBTRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                String title ="Bạn có chắc muốn đồng bộ dữ liệu hay không ?";

                dialog.showDialog(getContext(), inflater, title, true,0);
            }
        });
        return view;
    }

interface  CallBackConfirmRestore{
        void onCallBackConfirmRestore();
}
}
