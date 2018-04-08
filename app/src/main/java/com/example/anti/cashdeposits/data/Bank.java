package com.example.anti.cashdeposits.data;

import java.util.UUID;

public class Bank {

    private UUID mId;
    private String mTitle;

    public Bank(){
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
