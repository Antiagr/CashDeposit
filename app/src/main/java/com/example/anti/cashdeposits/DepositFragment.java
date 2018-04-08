package com.example.anti.cashdeposits;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.anti.cashdeposits.data.Deposit;
import com.example.anti.cashdeposits.data.Profit;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class DepositFragment extends Fragment {

    private static final String ARG_DEPOSIT_ID = "deposit_id";

    private Deposit mDeposit;
    private Button mDeleteButton;
    private TextView mBankView;
    private TextView mTitleView;
    private TextView mSummView;
    private TextView mCurrencyView;
    private TextView mRateCaptionView;
    private TextView mRateValueView;
    private TextView mPercentageView;
    private TextView mTimeView;
    private TextView mDateView;
    private TableLayout mTable;

    private DepositLab depositLab = DepositLab.get(getActivity());
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
    private final static float RUB_RATE = 1f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID depositId = (UUID) getArguments().getSerializable(ARG_DEPOSIT_ID);
        mDeposit = depositLab.getDeposit(depositId);
    }

    @Override
    public void onPause(){
        super.onPause();
        depositLab.updateDeposit(mDeposit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deposit, container, false);

        mTitleView = v.findViewById(R.id.deposit_title_field);
        mTitleView.setText(mDeposit.getTitle());

        mSummView = v.findViewById(R.id.deposit_summ_field);
        mSummView.setText(mDeposit.getSumm().toString());

        mBankView = v.findViewById(R.id.deposit_detail_bank);
        mBankView.setText(mDeposit.getBankTitle());

        mCurrencyView = v.findViewById(R.id.deposit_currency_field);
        mCurrencyView.setText(mDeposit.getCurrencyTitle());

        if (!mDeposit.getCurrencyRate().equals(RUB_RATE)){
            mRateCaptionView = v.findViewById(R.id.deposit_currency_rate_caption_field);
            mRateCaptionView.setVisibility(View.VISIBLE);
            mRateValueView = v.findViewById(R.id.deposit_currency_rate_value_field);
            mRateValueView.setVisibility(View.VISIBLE);
            mRateValueView.setText(mDeposit.getCurrencyRate().toString());
        }

        mPercentageView = v.findViewById(R.id.deposit_percentage_field);
        mPercentageView.setText(mDeposit.getPercentage().toString());

        mTimeView = v.findViewById(R.id.deposit_time_field);
        mTimeView.setText(String.valueOf(mDeposit.getTime()));

        mDateView = v.findViewById(R.id.deposit_date_field);
        mDateView.setText(sdf.format(mDeposit.getDate()));

        mDeleteButton = v.findViewById(R.id.deposit_button_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depositLab.deleteDeposit(mDeposit.getId());
                getActivity().finish();
            }
        });

        mTable = v.findViewById(R.id.deposit_table);
        List<Profit> profits = depositLab.getProfits(mDeposit.getId());
        int i = 0;
        for (Profit profit : profits){
            TableRow row = (TableRow) inflater.inflate(R.layout.fragment_table_row, null);
            i++;
            TextView tv1 = row.findViewById(R.id.col1);
            tv1.setText(String.valueOf(i));
            TextView tv2 = row.findViewById(R.id.col2);
            tv2.setText(sdf.format(profit.getDate()));
            TextView tv3 = row.findViewById(R.id.col3);
            tv3.setText(String.valueOf(profit.getValue()));
            TextView tv4 = row.findViewById(R.id.col4);
            tv4.setText(String.valueOf(profit.getProfit()));
            mTable.addView(row);
        }

        return v;
    }

    public static DepositFragment newInstance(UUID depositId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEPOSIT_ID, depositId);

        DepositFragment fragment = new DepositFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
