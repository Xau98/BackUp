package com.android.backup;

public class ItemListRestore {
    private int ID;
    private String name;
    private String dateBackup;
    private String devices;

    public ItemListRestore(int ID, String name, String dateBackup, String devices) {
        this.ID = ID;
        this.name = name;
        this.dateBackup = dateBackup;
        this.devices = devices;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateBackup() {
        return dateBackup;
    }

    public void setDateBackup(String dateBackup) {
        this.dateBackup = dateBackup;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }


}
