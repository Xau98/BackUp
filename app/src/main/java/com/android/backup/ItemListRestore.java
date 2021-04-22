package com.android.backup;

import java.io.Serializable;

public class ItemListRestore implements Serializable {
    private int ID;
    private String name;
    private String dateBackup;
    private String devices;
    private int type;
    public ItemListRestore(int ID, String name, String dateBackup, String devices, int type) {
        this.ID = ID;
        this.name = name;
        this.dateBackup = dateBackup;
        this.devices = devices;
        this.type =type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
