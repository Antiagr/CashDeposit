package com.example.anti.cashdeposits;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anti.cashdeposits.data.Bank;
import com.example.anti.cashdeposits.data.Currency;
import com.example.anti.cashdeposits.data.Deposit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class DepositEditFragment extends Fragment {

    private static final String TAG = "DepositEditFragment";

    private ArrayList<String> mCurrencyStrings = new ArrayList<>();
    private ArrayList<String> mBankStrings = new ArrayList<>();

    private Deposit mDeposit = new Deposit();
    private List<Currency> mCurrencies;
    private List<Bank> mBanks;
    private int currencySelected;
    private int bankSelected;
    private boolean isFill = false;

    private EditText mEditTitle;
    private Spinner mBankSpinner;
    private EditText mEditSumm;
    private EditText mEditPercent;
    private Spinner mCurrencySpinner;
    private TextView mCurrencyRate;
    private EditText mEditTime;
    private EditText mEditDate;
    private Button mSaveButton;

    private Calendar dateCalendar = Calendar.getInstance();
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    private DepositLab depositLab = DepositLab.get(getActivity());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrencies = depositLab.getCurrencies();
        for (Currency cur : mCurrencies){
            mCurrencyStrings.add(cur.getTitle());
        }
        mBanks = depositLab.getBanks();
        for (Bank bank : mBanks){
            mBankStrings.add(bank.getTitle());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (isFill){
            depositLab.updateDeposit(mDeposit);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_deposit, container, false);

        mEditTitle = v.findViewById(R.id.deposit_edit_title_field);

        ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mBankStrings);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBankSpinner = v.findViewById(R.id.deposit_edit_bank_spinner);
        mBankSpinner.setAdapter(bankAdapter);
        mBankSpinner.setSelection(0);
        mBankSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bankSelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEditSumm = v.findViewById(R.id.deposit_edit_summ_field);

        mCurrencyRate = v.findViewById(R.id.deposit_edit_currency_rate);
        mCurrencyRate.setText("");
        mCurrencyRate.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mCurrencyStrings);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCurrencySpinner = v.findViewById(R.id.deposit_edit_currency_spinner);
        mCurrencySpinner.setAdapter(currencyAdapter);
        mCurrencySpinner.setSelection(0);
        mCurrencySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currencySelected = i;
                if (i>0){
                    mCurrencyRate.setText(mCurrencies.get(i).getRate().toString());
                    mCurrencyRate.setVisibility(View.VISIBLE);
                } else {
                    mCurrencyRate.setText("");
                    mCurrencyRate.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mEditPercent = v.findViewById(R.id.deposit_edit_percentage_field);

        mEditTime = v.findViewById(R.id.deposit_edit_time_field);

        mEditDate = v.findViewById(R.id.deposit_edit_date_field);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mEditDate.setText(sdf.format(dateCalendar.getTime()));
            }

        };
        mEditDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, dateCalendar
                        .get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mSaveButton = v.findViewById(R.id.deposit_edit_save_button);
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isFill = true;
                if (mEditTitle.getText().toString().length() == 0) {
                    isFill = false;
                } else
                if (mEditSumm.getText().toString().length() == 0){
                    isFill = false;
                } else
                if (mEditPercent.getText().toString().length() == 0){
                    isFill = false;
                } else
                if (mEditTime.getText().toString().length() == 0){
                    isFill = false;
                } else
                if (mEditDate.getText().toString().length() == 0){
                    isFill = false;
                }
                if (isFill){
                    mDeposit.setTitle(mEditTitle.getText().toString());
                    mDeposit.setSumm(Long.valueOf(mEditSumm.getText().toString()));
                    mDeposit.setPercentage(Float.valueOf(mEditPercent.getText().toString()));
                    mDeposit.setTime(Integer.valueOf(mEditTime.getText().toString()));
                    try {
                        mDeposit.setDate(sdf.parse(mEditDate.getText().toString()));
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parse exception", e);
                    }
                    mDeposit.setInvestorId(UUID.randomUUID()); //TODO удалить
                    mDeposit.setCurrencyId(mCurrencies.get(currencySelected).getId());
                    mDeposit.setBankId(mBanks.get(bankSelected).getId());
                    depositLab.addDeposit(mDeposit);
                } else {
                    Toast.makeText(getContext(), "Заполните все данные",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }
}
