package com.android.backup;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class handleFile {
    public  static String PATH_ROOT = Environment.getExternalStorageDirectory().toString();
    // Bkav TienNVh :Load File
    //TODO: Lọc các thư mục cần thiết để backup (ko up lên cả)
    private ArrayList<FileItem> loadFile(String path){
        ArrayList<FileItem> list= new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                list.add(new FileItem(path+"/"+files[i].getName(), 0));
                //listAllFile.add(new FileItem(list.get(i).getPath(), 3));
            }
            return  list;
        }
        else {
            // listAllFile.add(new FileItem(path, 0));
            return null;
        }
    }

}
