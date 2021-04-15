package com.android.backup;

class ItemFile {
    private int id;

    private String name;

    private String path;

    private String icon;

    private String status;

    public ItemFile(int id, String name, String path, String icon, String status) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.icon = icon;
        this.status = status;
    }
}
