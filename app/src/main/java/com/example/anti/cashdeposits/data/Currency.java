package com.example.anti.cashdeposits.data;

import java.util.UUID;

public class Currency {

    private UUID mId;
    private String mTitle;
    private Float mRate;

    public Currency(){

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

    public Float getRate() {
        return mRate;
    }

    public void setRate(Float mRate) {
        this.mRate = mRate;
    }
}
