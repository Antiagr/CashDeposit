package com.example.anti.cashdeposits.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.anti.cashdeposits.data.Bank;
import com.example.anti.cashdeposits.database.DepositDBSchema.BankTable;

import java.util.UUID;

public class BankCursorWrapper extends CursorWrapper {

    public BankCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Bank getBank() {
        String uuidString = getString(getColumnIndex(BankTable.Cols.UUID));
        String title = getString(getColumnIndex(BankTable.Cols.TITLE));

        Bank bank = new Bank();
        bank.setId(UUID.fromString(uuidString));
        bank.setTitle(title);

        return bank;
    }
}