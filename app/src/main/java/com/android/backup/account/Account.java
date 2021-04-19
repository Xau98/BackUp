package com.android.backup.account;

public class Account {
    private String mID , mName, mToken, mCreate ;

    public Account(String mID, String mName, String mToken, String mCreate) {
        this.mID = mID;
        this.mName = mName;
        this.mToken = mToken;
        this.mCreate = mCreate;
    }

    public String getmID() {
        return mID;
    }

    public String getmName() {
        return mName;
    }

    public String getmToken() {
        return mToken;
    }

    public String getmCreate() {
        return mCreate;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public void setmCreate(String mCreate) {
        this.mCreate = mCreate;
    }
}
