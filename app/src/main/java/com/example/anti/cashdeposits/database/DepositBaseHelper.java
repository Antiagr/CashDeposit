package com.example.anti.cashdeposits.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.anti.cashdeposits.database.DepositDBSchema.BankTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyDynamicTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.CurrencyTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.DepositProfitsTable;
import com.example.anti.cashdeposits.database.DepositDBSchema.DepositTable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static java.util.UUID.*;

public class DepositBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "depositBase.db";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat floatFormat = new DecimalFormat("#0.00");

    public DepositBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DepositTable.NAME + "(" +
//                " _id integer primary key autoincrement, " +
                DepositTable.Cols.UUID + " primary key, " +
                DepositTable.Cols.TITLE + ", " +
                DepositTable.Cols.SUMM + ", " +
                DepositTable.Cols.CURRENCY_ID + ", " +
                DepositTable.Cols.DATE + ", " +
                DepositTable.Cols.TIME + ", " +
                DepositTable.Cols.PERCENTAGE + ", " +
                DepositTable.Cols.BANK_ID + ", " +
                DepositTable.Cols.INVESTOR_ID +
                ")"
        );

        db.execSQL("create table " + CurrencyTable.NAME + "(" +
                CurrencyTable.Cols.UUID + " primary key , " +
                CurrencyTable.Cols.TITLE +
                ")"
        );

        db.execSQL("create table " + BankTable.NAME + "(" +
                BankTable.Cols.UUID + " primary key , " +
                BankTable.Cols.TITLE +
                ")"
        );

        db.execSQL("create table " + DepositProfitsTable.NAME + "(" +
                DepositProfitsTable.Cols.UUID + " primary key , " +
                DepositProfitsTable.Cols.DEPOSIT_UUID + ", " +
                DepositProfitsTable.Cols.DATE + ", " +
                DepositProfitsTable.Cols.VALUE + ", " +
                DepositProfitsTable.Cols.PROFIT + ", " +
                DepositProfitsTable.Cols.MONTH_PROFIT +
                ")"
        );

        db.execSQL("create table " + CurrencyDynamicTable.NAME + "(" +
                CurrencyDynamicTable.Cols.ID + " integer primary key autoincrement, " +
                CurrencyDynamicTable.Cols.CURRENCY_ID + ", " +
                CurrencyDynamicTable.Cols.DATE + ", " +
                CurrencyDynamicTable.Cols.RATE +
                ")"
        );

        UUID rubUUID = randomUUID();
        UUID usdUUID = randomUUID();
        UUID eurUUID = randomUUID();

        // Тут захардкодены входные данные, потому что почему бы и нет
        db.execSQL("Insert or replace into " + CurrencyTable.NAME + "(" +
                CurrencyTable.Cols.UUID + ", " +
                CurrencyTable.Cols.TITLE + ") values" +
                " ('" + rubUUID.toString() + "', 'RUB')," +
                " ('" + usdUUID.toString() + "', 'USD')," +
                " ('" + eurUUID.toString() + "', 'EUR')"
        );

        db.execSQL("Insert or replace into " + BankTable.NAME + "(" +
                BankTable.Cols.UUID + ", " +
                BankTable.Cols.TITLE + ") values" +
                " ('" + randomUUID().toString() + "', 'Рога и Копыта')," +
                " ('" + randomUUID().toString() + "', 'TemplateOneDay Банк')," +
                " ('" + randomUUID().toString() + "', 'Дочка ВТБ')," +
                " ('" + randomUUID().toString() + "', 'ОбувайБанк')"
        );
        // Генерируем входные данные по курсу валют за 2016 - по нашем время
        StringBuilder sb = new StringBuilder("insert into " + CurrencyDynamicTable.NAME + "(" +
                CurrencyDynamicTable.Cols.CURRENCY_ID + ", " +
                CurrencyDynamicTable.Cols.DATE + ", " +
                CurrencyDynamicTable.Cols.RATE + ") values ('" + rubUUID.toString() + "', '"+
                sdf.format(new Date()) + "', '1'),");
        Calendar calFirst = Calendar.getInstance();
        calFirst.set(Calendar.YEAR, 2016);
        calFirst.set(Calendar.MONTH, Calendar.JANUARY);
        calFirst.set(Calendar.DAY_OF_MONTH, 1);
        Calendar calLast = Calendar.getInstance();
        calLast.setTime(new Date());
        float rateUsd = 72.9F;
        float rateEur = 79.6F;
        int today = calLast.get(Calendar.DAY_OF_YEAR);
        Random random = new Random();
        boolean plusOrMinus = random.nextBoolean();
        for (int i = 0; i < today + 731; i++){
            sb.append(" ('").append(usdUUID.toString()).append("', '")
                    .append(sdf.format(calFirst.getTime())).append("', '")
                    .append(String.valueOf(floatFormat.format(rateUsd))).append("'),");
            sb.append(" ('").append(eurUUID.toString()).append("', '")
                    .append(sdf.format(calFirst.getTime())).append("', '")
                    .append(String.valueOf(floatFormat.format(rateEur))).append("'),");
            calFirst.add(Calendar.DATE, 1);
            if (plusOrMinus){
                rateUsd = rateUsd + (float)Math.random();
            } else {
                rateUsd = rateUsd - (float)Math.random();
            }
            plusOrMinus = random.nextBoolean();
            if (plusOrMinus) {
                rateEur = rateEur + (float)Math.random();
            } else {
                rateEur = rateEur + (float)Math.random();
            }
            plusOrMinus = random.nextBoolean();
        }
        sb.deleteCharAt(sb.length()-1);
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Сторонная наработка для просмотра БД
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

}
