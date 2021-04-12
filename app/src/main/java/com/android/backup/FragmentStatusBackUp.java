package com.android.backup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class FragmentStatusBackUp extends Fragment {
    ImageButton mBTBackup;
    TextView mShowCapacity;
    FragmentBackuping mFragmentBackuping;
    boolean isStatus;
    long mCapacity = 0;
    Dialog dialog;
    public FragmentStatusBackUp(Dialog dialog, long capacity) {
        this.dialog =dialog;
        mCapacity = capacity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Tiennvh2", "FragmentStatusBackUp: ok" );
       View view= inflater.inflate(R.layout.fragment_choose,container,false);
        mBTBackup = view.findViewById(R.id.bt_confirm_save);
        mShowCapacity = view.findViewById(R.id.show_capacity);
        mShowCapacity.setText( mCapacity+"");
        mBTBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                String title;
                boolean showconfirm;
                if(!mShowCapacity.getText().equals("0")) {
                    title = "Hãy xác nhận bạn muốn bắt đầu quá trình sao lưu dữ liệu";
                    showconfirm = true;
                }
                else {
                    title = "Vui lòng chọn thư mục backup ";
                    showconfirm = false;
                }

               dialog.showDialog(getContext(), inflater, title, showconfirm);
            }
        });

       return view;

    }


}
