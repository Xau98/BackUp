package com.android.backup;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;

public class handleFile {
    public  static String PATH_ROOT = Environment.getExternalStorageDirectory().toString();
    public  static String PATH_FOLDER = PATH_ROOT+"/CompressionFile";
    // Bkav TienNVh :Load File
    //TODO: Lọc các thư mục cần thiết để backup (ko up lên cả)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<FileItem> loadFile(String path){
        ArrayList<FileItem> list= new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                FileItem fileItem= new FileItem(path+"/"+files[i].getName(), 0);
                if(fileItem.getSize()>0)
                     list.add(fileItem );
            }
            return  list;
        }
        else {
            // listAllFile.add(new FileItem(path, 0));
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long totalCapacity(ArrayList<FileItem> list){
        long total =0;
        for(int i=0;i<list.size();i++){
             FileItem file = new FileItem(list.get(i).getPath(),0);
             total = file.getSize();
        }
        return total;
    }

    //
    public static boolean duplicateFileItem(ArrayList<FileItem> list, FileItem fileItem){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getName().equals(fileItem.getName())){
                return false;
            }
        }
        return true;
    }

    public static ArrayList<FileItem> removeFileItem(ArrayList<FileItem> list, FileItem fileItem){
        for(int i=0;i<list.size();i++){
            if(list.get(i).getName().equals(fileItem.getName())){
                list.remove(i);
            }
        }
        return list;
    }

    public static void createFolderCompression(){
        File f=new File(PATH_FOLDER);
        if(!f.exists())
        {
            f.mkdir();
        }
        else{
            Log.d("tiennvh", "createFolderCompression: ");
        }
    }

    public static void fileToCompression(String path){
        try {
            File afile = new File(path);

            if (afile.renameTo(new File(PATH_FOLDER+"/" + afile.getName()))) {
                Log.d("Tiennvh", "File is moved successful: ");
            } else {
                Log.d("Tiennvh", "File is failed to move!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static void deleteFile(String path){
        File file= new File(path);
        if(file.exists()){
            file.delete();
        }
    }

}
