package com.example.anti.cashdeposits;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anti.cashdeposits.data.Bank;
import com.example.anti.cashdeposits.data.Currency;
import com.example.anti.cashdeposits.data.Deposit;
import com.example.anti.cashdeposits.data.Profit;
import com.example.anti.cashdeposits.database.BankCursorWrapper;
import com.example.anti.cashdeposits.database.CurrencyCursorWrapper;
import com.example.anti.cashdeposits.database.DepositBaseHelper;
import com.example.anti.cashdeposits.database.DepositCursorWrapper;
import com.example.anti.cashdeposits.database.DepositDBSchema.BankTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyDynamicTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.DepositProfitsTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.DepositTable;
import com.example.anti.cashdeposits.database.ProfitCursorWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DepositLab {

    private static DepositLab sDepositLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static DepositLab get(Context context){
        if (sDepositLab == null) {
            sDepositLab = new DepositLab(context);
        }
        return sDepositLab;
    }

    private DepositLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new DepositBaseHelper(mContext).getWritableDatabase();
    }

    public Deposit getDeposit(UUID id){
        final String query ="select a.*, b." + CurrencyTable.Cols.TITLE +", c." + CurrencyDynamicTable.Cols.RATE + ", d." + BankTable.Cols.TITLE + " from " + DepositTable.NAME + " a " +
                "left outer join " + CurrencyTable.NAME + " b " +
                "on a." + DepositTable.Cols.CURRENCY_ID + "=b." + CurrencyTable.Cols.UUID +
                " left outer join " + CurrencyDynamicTable.NAME + " c " +
                "on a." + DepositTable.Cols.CURRENCY_ID + "=c." + CurrencyDynamicTable.Cols.CURRENCY_ID +
                " left outer join " + BankTable.NAME + " d " +
                "on a." + DepositTable.Cols.BANK_ID + "=d." + BankTable.Cols.UUID +
                " where a." + DepositTable.Cols.UUID + " = ? " +
                " order by c." + CurrencyDynamicTable.Cols.DATE + " desc limit 1";
        DepositCursorWrapper cursor = new DepositCursorWrapper(mDatabase.rawQuery(query, new String[] {id.toString()}));

        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getDeposit();
        } finally {
            cursor.close();
        }
    }

    public void updateDeposit(Deposit deposit){
        String uuidString = deposit.getId().toString();
        ContentValues values = getDepositContentValues(deposit);
        mDatabase.update(DepositTable.NAME, values,
                DepositTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    public void updateRate(){
        List<Currency> currencies = getCurrencies();
        updateRate(currencies);
    }

    public void updateRate(List<Currency> currencies){
        ContentValues values = getCurrencyValues(currencies.get(0));
        mDatabase.update(CurrencyDynamicTable.NAME, values,
                CurrencyDynamicTable.Cols.CURRENCY_ID + " = ?",
                new String[] {currencies.get(0).getId().toString()});
        values = getCurrencyValues(currencies.get(1));
        mDatabase.insert(CurrencyDynamicTable.NAME, null, values);
        values = getCurrencyValues(currencies.get(2));
        mDatabase.insert(CurrencyDynamicTable.NAME, null, values);
    }

    private DepositCursorWrapper queryDeposits(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                DepositTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DepositCursorWrapper(cursor);
    }

    private CurrencyCursorWrapper queryCurrency(String[] whereArgs){
        final String QUERY = "select * from " + CurrencyTable.NAME +
                " a left outer join " + CurrencyDynamicTable.NAME +
                " b on a." + CurrencyTable.Cols.UUID + "=b." + CurrencyDynamicTable.Cols.CURRENCY_ID +
                " order by b." + CurrencyDynamicTable.Cols.DATE + " desc limit 3";
        Cursor cursor = mDatabase.rawQuery(QUERY, whereArgs);
//        Cursor cursor = mDatabase.query(
//                CurrencyTable.NAME,
//                null,
//                whereClause,
//                whereArgs,
//                null,
//                null,
//                null
//        );
        return new CurrencyCursorWrapper(cursor);
    }

    private BankCursorWrapper queryBank(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                BankTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new BankCursorWrapper(cursor);
    }

    private ProfitCursorWrapper queryProfits(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                DepositProfitsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new ProfitCursorWrapper(cursor);
    }

    private static ContentValues getDepositContentValues(Deposit deposit){
        ContentValues values = new ContentValues();
        values.put(DepositTable.Cols.UUID, deposit.getId().toString());
        values.put(DepositTable.Cols.TITLE,  deposit.getTitle());
        values.put(DepositTable.Cols.SUMM, deposit.getSumm());
        values.put(DepositTable.Cols.CURRENCY_ID, deposit.getCurrencyId().toString());
        values.put(DepositTable.Cols.DATE, sdf.format(deposit.getDate().getTime()).toString());
        values.put(DepositTable.Cols.TIME, deposit.getTime());
        values.put(DepositTable.Cols.PERCENTAGE, deposit.getPercentage());
        values.put(DepositTable.Cols.BANK_ID, deposit.getBankId().toString());
        values.put(DepositTable.Cols.INVESTOR_ID, deposit.getInvestorId().toString());
        return values;
    }

    private static ContentValues getCurrencyValues(Currency currency){
        ContentValues values = new ContentValues();
        values.put(CurrencyDynamicTable.Cols.CURRENCY_ID, currency.getId().toString());
        values.put(CurrencyDynamicTable.Cols.DATE, sdf.format(new Date()).toString());
        values.put(CurrencyDynamicTable.Cols.RATE, currency.getRate());
        return values;
    }

    private static ContentValues getDepositProfitContentValues(Profit profit){
        ContentValues values = new ContentValues();
        values.put(DepositProfitsTable.Cols.UUID, profit.getId().toString());
        values.put(DepositProfitsTable.Cols.DEPOSIT_UUID, profit.getDepositId().toString());
        values.put(DepositProfitsTable.Cols.DATE, sdf.format(profit.getDate().getTime()).toString());
        values.put(DepositProfitsTable.Cols.VALUE, profit.getValue());
        values.put(DepositProfitsTable.Cols.PROFIT, profit.getProfit());
        values.put(DepositProfitsTable.Cols.MONTH_PROFIT, profit.getMonthProfit());
        return values;
    }

    public void addDeposit(Deposit deposit){
        ContentValues values = getDepositContentValues(deposit);
        int id  = (int) mDatabase.insertWithOnConflict(DepositTable.NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1){
            mDatabase.update(DepositTable.NAME, values,
                    DepositTable.Cols.UUID + " = ?", new String[] { deposit.getId().toString() });
        }
        //Генерируем строки начислений
        Date today = new Date();
        int months = today.getYear()*12 + today.getMonth() - deposit.getDate().getYear()*12 - deposit.getDate().getMonth();
        float summ = deposit.getSumm();
        float prof = 0F;
        int count = 12;
        Calendar cal = Calendar.getInstance();
        cal.setTime(deposit.getDate());
        for (int i = 1; i <= months; i++){
            Profit profit = new Profit();
            if (i <= deposit.getTime()){
                if (count == 12){
                    count = 0;
                    prof  = summ * (deposit.getPercentage()/100F) /12F;
                }
                count++;
                summ = summ + prof;
                profit.setDepositId(deposit.getId());
                cal.add(Calendar.MONTH, 1);
                profit.setDate(cal.getTime());
                profit.setValue(summ);
                profit.setProfit(summ-deposit.getSumm());
                profit.setMonthProfit(prof);
                ContentValues profitValues = getDepositProfitContentValues(profit);
                mDatabase.insert(DepositProfitsTable.NAME, null, profitValues);
            }else{
                break;
            }
        }
    }

    public void deleteDeposit(UUID id){
        mDatabase.delete(DepositTable.NAME,
                DepositTable.Cols.UUID + " = ?",
                new String[] {id.toString()});
        mDatabase.delete(DepositProfitsTable.NAME,
                DepositProfitsTable.Cols.DEPOSIT_UUID + " = ?",
                new String[] {id.toString()});
    }

    public List<Deposit> getDeposits(){
        List<Deposit> deposits = new ArrayList<>();
        DepositCursorWrapper cursor = queryDeposits(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                deposits.add(cursor.getDeposits());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return deposits;
    }
    // Я устал бороться с дженериками, а потому тут копипаста, бессмысленная и беспощадная.
    public List<Currency> getCurrencies(){
        List<Currency> currencies = new ArrayList<>();
        CurrencyCursorWrapper cursor = queryCurrency(null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                currencies.add(cursor.getCurrency());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return currencies;
    }

    public List<Bank> getBanks(){
        List<Bank> banks = new ArrayList<>();
        BankCursorWrapper cursor = queryBank(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                banks.add(cursor.getBank());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return banks;
    }

    public List<Profit> getProfits(UUID depositId){
        List<Profit> profits = new ArrayList<>();
        ProfitCursorWrapper cursor;
        if (depositId == null){
            cursor = queryProfits(null, null);
        }else{
            cursor = queryProfits(DepositProfitsTable.Cols.DEPOSIT_UUID + " = ?", new String[] { depositId.toString() });
        }

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                profits.add(cursor.getProfits());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return profits;
    }
}
