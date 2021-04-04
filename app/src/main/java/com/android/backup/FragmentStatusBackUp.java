package com.android.backup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class FragmentStatusBackUp extends Fragment {
    ImageButton mBTBackup;
    FragmentBackuping mFragmentBackuping;
    boolean isStatus;
    public FragmentStatusBackUp(boolean a) {
        isStatus=a;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Tiennvh2", "FragmentStatusBackUp: ok" );
       View view= inflater.inflate(R.layout.fragment_choose,container,false);
        mBTBackup = view.findViewById(R.id.bt_confirm_save);

        mBTBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.dialog_confirm, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setView(alertLayout);
                alert.setCancelable(false);
                alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setPositiveButton("Xác Nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // code for matching password
                        mFragmentBackuping = new FragmentBackuping();
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.FagmentBackup, mFragmentBackuping).commit();

                    }
                });
                alert.show();
            }
        });

       return view;

    }
}
