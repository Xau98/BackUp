package com.android.backup;

import java.io.File;

class FileItem {
  //  private String ID;

    private String name;

   // private String displayName;

    private int type;
    // type = 0 ; File đã biết định dạng
    // type = 3 ; File chưa biết định dạng (unknow)
    // type = 2 ; Là thư mục
    private String path;

    private long date;

    private long size;

    public FileItem(  String name, int type, String path, long date, long size) {
        this.name = name;
        this.type = type;
        this.path = path;
        this.date = date;
        this.size = size;
    }

    public FileItem(String path , int type){
        File file=new File(path);
        name = file.getName();
        this.type = type;
        this.path = path;
        date = file.lastModified();
        size = file.length();
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public long getDate() {
        return date;
    }

    public long getSize() {
        return size;
    }
}
