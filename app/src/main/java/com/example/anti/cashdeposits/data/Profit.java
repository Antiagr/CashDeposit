package com.example.anti.cashdeposits.data;

import java.util.Date;
import java.util.UUID;

public class Profit {

    private UUID mId;
    private UUID mDepositId;
    private Float mValue;
    private Float mProfit;
    private Date mDate;
    private Float mMonthProfit;

    public Profit(){
        this(UUID.randomUUID());
    }

    public Profit(UUID id){
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public UUID getDepositId() {
        return mDepositId;
    }

    public void setDepositId(UUID mDepositId) {
        this.mDepositId = mDepositId;
    }

    public Float getValue() {
        return mValue;
    }

    public void setValue(Float mValue) {
        this.mValue = mValue;
    }

    public Float getProfit() {
        return mProfit;
    }

    public void setProfit(Float mProfit) {
        this.mProfit = mProfit;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public Float getMonthProfit() {
        return mMonthProfit;
    }

    public void setMonthProfit(Float mMonthProfit) {
        this.mMonthProfit = mMonthProfit;
    }
}
