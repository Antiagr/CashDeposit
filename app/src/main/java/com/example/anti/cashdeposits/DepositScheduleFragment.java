package com.example.anti.cashdeposits;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anti.cashdeposits.data.Bank;
import com.example.anti.cashdeposits.data.Currency;
import com.example.anti.cashdeposits.data.Deposit;
import com.example.anti.cashdeposits.data.Profit;
import com.example.anti.cashdeposits.widget.MultiSpinner;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DepositScheduleFragment extends Fragment{

    private static final String TAG = "DepositScheduleFragment";

    private static final Float ZERO_FLOAT = 0F;
    private static final Float TEXT_SIZE = 15F;

    private ArrayList<String> mCurrencyStrings = new ArrayList<>();
    private ArrayList<String> mBankStrings = new ArrayList<>();

    private PieChart mPieChart;
    private CheckBox mDateCheckbox;
    private EditText mDateFrom;
    private EditText mDateTo;
    private CheckBox mBankCheckbox;
    private MultiSpinner mBankSpinner;
    private CheckBox mCurrencyCheckbox;
    private MultiSpinner mCurrencySpinner;

    private DepositLab depositLab = DepositLab.get(getActivity());
    private List<Currency> mCurrencies = new ArrayList<>();
    private List<Bank> mBanks = new ArrayList<>();
    private List<Deposit> mDeposits = new ArrayList<>();
    private List<Profit> mProfits = new ArrayList<>();
    private List<Profit> mWorkingProfits = new ArrayList<>();
    private Set<UUID> selectedBanks = new HashSet<>();
    private Set<UUID> selectedCurrencies = new HashSet<>();
    private HashMap<UUID, Float> currencyRates = new HashMap<>();

    private boolean isBank = false;
    private boolean isCurrency = false;

    private Calendar dateCalendar = Calendar.getInstance();
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initialiseDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mDateCheckbox = view.findViewById(R.id.schedule_date_checkbox);
        mDateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mDateCheckbox.isChecked()){
                    if (mDateFrom.getText().toString().length() != 0 && mDateTo.getText().toString().length() != 0){
                        //Выборка по установленным датам
                        try{
                            setData(sdf.parse(mDateFrom.getText().toString()), sdf.parse(mDateTo.getText().toString()));
                        }catch(ParseException e){
                            Log.e(TAG, "Date parse exception", e);
                        }
                    } else {
                        mDateCheckbox.setChecked(false);
                        Toast.makeText(getContext(), "Введите обе даты",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mDateFrom.setText("");
                    mDateTo.setText("");
                    mWorkingProfits.clear();
                    mWorkingProfits.addAll(mProfits);
                    //Выборка по всем датам
                    setData();
                }
            }
        });
        mDateFrom = view.findViewById(R.id.schedule_from_date);
        mDateTo = view.findViewById(R.id.schedule_to_date);

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateFrom.setText(sdf.format(dateCalendar.getTime()));
            }

        };
        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateTo.setText(sdf.format(dateCalendar.getTime()));
            }

        };
        mDateFrom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), fromDate, dateCalendar
                        .get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mDateTo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), toDate, dateCalendar
                        .get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mBankCheckbox = view.findViewById(R.id.schedule_bank_checkbox);
        mBankCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Выборка по банкам
                if (isBank){
                    isBank = false;
                } else {
                    isBank = true;
                    isCurrency = false;
                    mCurrencyCheckbox.setChecked(false);
                }
                setData();
            }
        });

        mBankSpinner = view.findViewById(R.id.schedule_bank_spinner);
        mBankSpinner.setItems(mBankStrings, getString(R.string.all_banks), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedBanks.clear();
                int i = 0;
                for (boolean b : selected){
                    if (b){
                        selectedBanks.add(mBanks.get(i).getId());
                    }
                    i++;
                }
            }
        });

        mCurrencyCheckbox = view.findViewById(R.id.schedule_currency_checkbox);
        mCurrencyCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurrency){
                    isCurrency = false;
                } else {
                    isCurrency = true;
                    isBank = false;
                    mBankCheckbox.setChecked(false);
                }
                setData();
            }
        });
        mCurrencySpinner = view.findViewById(R.id.schedule_currency_spinner);
        mCurrencySpinner.setItems(mCurrencyStrings, getString(R.string.all_currencies), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                selectedCurrencies.clear();
                int i = 0;
                for (boolean b : selected){
                    if (b) {
                        selectedCurrencies.add(mCurrencies.get(i).getId());
                    }
                    i++;
                }
            }
        });

        mPieChart = view.findViewById(R.id.deposit_pie_chart);

        setData();

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setFormSize(TEXT_SIZE);
        l.setTextSize(TEXT_SIZE);
        l.setXEntrySpace(ZERO_FLOAT);
        l.setYEntrySpace(ZERO_FLOAT);
        l.setYOffset(ZERO_FLOAT);
        mPieChart.setEntryLabelColor(Color.BLACK);
        mPieChart.setEntryLabelTextSize(TEXT_SIZE);

        return view;
    }

    private void initialiseDate(){
        mBanks = depositLab.getBanks();
        mDeposits = depositLab.getDeposits();
        mCurrencies = depositLab.getCurrencies();
        mProfits = depositLab.getProfits(null);
        mWorkingProfits.addAll(mProfits);
        for (Currency cur : mCurrencies){
            currencyRates.put(cur.getId(), cur.getRate());
            mCurrencyStrings.add(cur.getTitle());
            selectedCurrencies.add(cur.getId());
        }
        mBanks = depositLab.getBanks();
        for (Bank bank : mBanks){
            mBankStrings.add(bank.getTitle());
            selectedBanks.add(bank.getId());
        }
    }

    // Получаем данные с учетом выбранных дат
    private void setData(Date from, Date to){
        mWorkingProfits.clear();
        for (Profit profit : mProfits){
            if (profit.getDate().after(from) && profit.getDate().before(to)){
                mWorkingProfits.add(profit);
            }
        }
        setData();
    }

    // Получаем данные для графика
    private void setData(){
        List<PieEntry> entries;
        if (isBank){
            entries = getDataBank();
        } else if (isCurrency){
            entries = getDataCurrency();
        } else {
            entries = getDataSimple();
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(TEXT_SIZE);
        data.setValueTextColor(Color.BLACK);

        mPieChart.setData(data);
        mPieChart.highlightValue(null);
        mPieChart.invalidate();
    }

    // Простая выборка по умолчанию. Включает в себя все существующие вклады
    private List<PieEntry> getDataSimple(){
        List<PieEntry> entries = new ArrayList<>();
        Map<UUID, Float> mSumms = new HashMap<>();
        for (Deposit deposit : mDeposits){
            mSumms.put(deposit.getId(), ZERO_FLOAT);
        }
        // Базовая выборка среди всех депозитов
        for (Profit profit : mWorkingProfits){
            mSumms.put(profit.getDepositId(), mSumms.get(profit.getDepositId()) + profit.getMonthProfit());
        }

        for (Deposit deposit : mDeposits){
            if (!mSumms.get(deposit.getId()).equals(ZERO_FLOAT)){
                entries.add(new PieEntry(mSumms.get(deposit.getId()) * currencyRates.get(deposit.getCurrencyId()), deposit.getTitle()));
            }
        }
        return entries;
    }

    // Получаем данные для выборке по банкам
    private List<PieEntry> getDataBank(){
        List<PieEntry> entries = new ArrayList<>();
        Map<UUID, Deposit> selectedDeposits = new HashMap<>();
        Map<UUID, Float> mSumms = new HashMap<>();
        for (UUID bankId : selectedBanks){
            mSumms.put(bankId, ZERO_FLOAT);
        }
        for (Deposit deposit : mDeposits){
            if (selectedBanks.contains(deposit.getBankId())){
                selectedDeposits.put(deposit.getId(), deposit);
            }
        }
        for (Profit profit : mWorkingProfits){
            if (selectedDeposits.containsKey(profit.getDepositId())){
                mSumms.put(selectedDeposits.get(profit.getDepositId()).getBankId(),
                        mSumms.get(selectedDeposits.get(profit.getDepositId()).getBankId()) + profit.getMonthProfit() * currencyRates.get(selectedDeposits.get(profit.getDepositId()).getCurrencyId()));
            }
        }
        for (Bank bank : mBanks){
            if (selectedBanks.contains(bank.getId())){
                if (!mSumms.get(bank.getId()).equals(ZERO_FLOAT)){
                    entries.add(new PieEntry(mSumms.get(bank.getId()), bank.getTitle()));
                }
            }
        }
        return entries;
    }

    // Получаем выборку для выборке по валюте
    private List<PieEntry> getDataCurrency(){
        List<PieEntry> entries = new ArrayList<>();
        Map<UUID, Deposit> selectedDeposits = new HashMap<>();
        Map<UUID, Float> mSumms = new HashMap<>();
        for (UUID currencyId : selectedCurrencies){
            mSumms.put(currencyId, ZERO_FLOAT);
        }
        for (Deposit deposit : mDeposits){
            if (selectedCurrencies.contains(deposit.getCurrencyId())){
                selectedDeposits.put(deposit.getId(), deposit);
            }
        }
        for (Profit profit : mWorkingProfits){
            if (selectedDeposits.containsKey(profit.getDepositId())){
                mSumms.put(selectedDeposits.get(profit.getDepositId()).getCurrencyId(),
                        mSumms.get(selectedDeposits.get(profit.getDepositId()).getCurrencyId()) + profit.getMonthProfit());
            }
        }
        for (Currency currency : mCurrencies){
            if (selectedCurrencies.contains(currency.getId())){
                if (!mSumms.get(currency.getId()).equals(ZERO_FLOAT)){
                    entries.add(new PieEntry(mSumms.get(currency.getId()) * currency.getRate(), currency.getTitle()));
                }
            }
        }

        return entries;
    }
}
