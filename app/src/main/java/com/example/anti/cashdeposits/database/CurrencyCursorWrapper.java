package com.example.anti.cashdeposits.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.anti.cashdeposits.data.Currency;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyDynamicTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyTable;

import java.util.UUID;

public class CurrencyCursorWrapper extends CursorWrapper{

    public CurrencyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Currency getCurrency(){
        String uuidString = getString(getColumnIndex(CurrencyTable.Cols.UUID));
        String title = getString(getColumnIndex(CurrencyTable.Cols.TITLE));
        Float rate = getFloat(getColumnIndex(CurrencyDynamicTable.Cols.RATE));

        Currency currency = new Currency();
        currency.setId(UUID.fromString(uuidString));
        currency.setTitle(title);
        currency.setRate(rate);

        return currency;
    }
}
