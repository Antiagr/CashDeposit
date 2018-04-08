package com.example.anti.cashdeposits.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.anti.cashdeposits.data.Deposit;
import com.example.anti.cashdeposits.database.DepositDBSchema.BankTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyDynamicTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.DepositTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DepositCursorWrapper extends CursorWrapper{

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DepositCursorWrapper(Cursor cursor){
        super(cursor);
    }

    // С полями Валюта и Курс
    public Deposit getDeposit()  {
        Deposit deposit = getDeposits();

        String currencyTitle = getString(getColumnIndexOrThrow(CurrencyTable.Cols.TITLE));
        float currencyRate = getFloat(getColumnIndexOrThrow(CurrencyDynamicTable.Cols.RATE));
        String bankTitle = getString(getColumnIndexOrThrow(BankTable.Cols.TITLE));
        deposit.setCurrencyTitle(currencyTitle);
        deposit.setCurrencyRate(currencyRate);
        deposit.setBankTitle(bankTitle);

        return deposit;
    }

    // Без полей Валюта и Курс
    public Deposit getDeposits(){
        String uuidString = getString(getColumnIndex(DepositTable.Cols.UUID));
        String title = getString(getColumnIndex(DepositTable.Cols.TITLE));
        long summ = getLong(getColumnIndex(DepositTable.Cols.SUMM));
        Float percentage = getFloat(getColumnIndex(DepositTable.Cols.PERCENTAGE));
        String currencyId = getString(getColumnIndex(DepositTable.Cols.CURRENCY_ID));
        String date = getString(getColumnIndex(DepositTable.Cols.DATE));
        int time = getInt(getColumnIndex(DepositTable.Cols.TIME));
        String bankId = getString(getColumnIndex(DepositTable.Cols.BANK_ID));
        String investorId = getString(getColumnIndex(DepositTable.Cols.INVESTOR_ID));

        Deposit deposit = new Deposit(UUID.fromString(uuidString));
        deposit.setTitle(title);
        deposit.setSumm(summ);
        deposit.setPercentage(percentage);
        deposit.setCurrencyId(UUID.fromString(currencyId));
        try{
            deposit.setDate(sdf.parse(date));
        } catch (Exception e){
            e.printStackTrace();
        }
        deposit.setTime(time);
        deposit.setBankId(UUID.fromString(bankId));
        deposit.setInvestorId(UUID.fromString(investorId));

        return deposit;
    }
}
