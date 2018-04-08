package com.example.anti.cashdeposits.data;

import java.util.Date;
import java.util.UUID;

public class Deposit {

    private UUID mId;
    private String mTitle;
    private Long mSumm;
    private UUID mCurrencyId;
    private String mCurrencyTitle;
    private Float mCurrencyRate;
    private Float mPercentage;
    private int mTime;
    private Date mDate;
    private UUID mBankId;
    private String mBankTitle;
    private UUID mInvestorId;

    public Deposit(){
        this(UUID.randomUUID());
    }

    public Deposit(UUID id){
        mId = id;
        mDate = new Date();
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public Long getSumm() {
        return mSumm;
    }

    public void setSumm(Long mSumm) {
        this.mSumm = mSumm;
    }

    public String getCurrencyTitle() {
        return mCurrencyTitle;
    }

    public void setCurrencyTitle(String mCurrency) {
        this.mCurrencyTitle = mCurrency;
    }

    public Float getCurrencyRate() {
        return mCurrencyRate;
    }

    public void setCurrencyRate(Float mCurrencyRate) {
        this.mCurrencyRate = mCurrencyRate;
    }

    public Float getPercentage() {
        return mPercentage;
    }

    public void setPercentage(Float mPercentage) {
        this.mPercentage = mPercentage;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int mTime) {
        this.mTime = mTime;
    }

    public UUID getBankId() {
        return mBankId;
    }

    public void setBankId(UUID bankId) {
        this.mBankId = bankId;
    }

    public UUID getInvestorId() {
        return mInvestorId;
    }

    public void setInvestorId(UUID investorId) {
        this.mInvestorId = investorId;
    }

    public UUID getCurrencyId() {
        return mCurrencyId;
    }

    public void setCurrencyId(UUID mCurrencyId) {
        this.mCurrencyId = mCurrencyId;
    }

    public String getBankTitle() {
        return mBankTitle;
    }

    public void setBankTitle(String mBankTitle) {
        this.mBankTitle = mBankTitle;
    }
}
