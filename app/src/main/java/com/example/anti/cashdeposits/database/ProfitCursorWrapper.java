package com.example.anti.cashdeposits.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.anti.cashdeposits.data.Profit;
import com.example.anti.cashdeposits.database.DepositDBSchema.DepositProfitsTable;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class ProfitCursorWrapper extends CursorWrapper{

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProfitCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Profit getProfits()  {
        String id = getString(getColumnIndex(DepositProfitsTable.Cols.UUID));
        String depositId = getString(getColumnIndex(DepositProfitsTable.Cols.DEPOSIT_UUID));
        String date = getString(getColumnIndex(DepositProfitsTable.Cols.DATE));
        Float value = getFloat(getColumnIndex(DepositProfitsTable.Cols.VALUE));
        Float prof = getFloat(getColumnIndex(DepositProfitsTable.Cols.PROFIT));
        Float month_prof = getFloat(getColumnIndex(DepositProfitsTable.Cols.MONTH_PROFIT));

        Profit profit = new Profit();
        profit.setId(UUID.fromString(id));
        profit.setDepositId(UUID.fromString(depositId));
        try {
            profit.setDate(sdf.parse(date));
        } catch (Exception e){
            e.printStackTrace();
        }
        profit.setValue(value);
        profit.setProfit(prof);
        profit.setMonthProfit(month_prof);

        return profit;
    }
}
