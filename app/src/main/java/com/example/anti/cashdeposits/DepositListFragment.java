package com.example.anti.cashdeposits;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anti.cashdeposits.data.Currency;
import com.example.anti.cashdeposits.data.Deposit;
import com.example.anti.cashdeposits.database.AndroidDatabaseManager;
import com.example.anti.cashdeposits.service.RateService;

import java.text.SimpleDateFormat;
import java.util.List;

//Лист существующих вкладов
public class DepositListFragment extends Fragment {

    private RecyclerView mDepositRecyclerView;
    private DepositAdapter mAdapter;
    private EditText mUsdRate;
    private EditText mEurRate;
    private Button mUpdateRateButton;
    private Button mDBButton;

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        RateService.setServiceAlarm(getActivity(), true);
    }

    //Меню сверху
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.look_schedule : //Посмотреть график
                Intent intent1 = DepositScheduleActivity.newIntent(getActivity());
                startActivity(intent1);
                return true;
            case R.id.new_deposit : //Добавить новый вклад
                Intent intent2 = DepositActivity.newIntent(getActivity());
                startActivity(intent2);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_deposit_list, container, false);
        final DepositLab depositLab = DepositLab.get(getActivity());
        final List<Currency> currencies = depositLab.getCurrencies();
        mUsdRate = view.findViewById(R.id.list_usd_rate);
        mUsdRate.setText(currencies.get(1).getRate().toString());
        mEurRate = view.findViewById(R.id.list_eur_rate);
        mEurRate.setText(currencies.get(2).getRate().toString());
        mUpdateRateButton = view.findViewById(R.id.list_update_rate_button);
        mUpdateRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currencies.get(1).setRate(Float.valueOf(mUsdRate.getText().toString()));
                currencies.get(2).setRate(Float.valueOf(mEurRate.getText().toString()));
                depositLab.updateRate(currencies);
            }
        });
        mDepositRecyclerView = view.findViewById(R.id.deposit_recycler_view);
        mDepositRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //внещнаяя библиотека для работы с БД
        mDBButton = view.findViewById(R.id.db_button);
        mDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dbmanager = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });


        updateUI();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        DepositLab depositLab = DepositLab.get(getActivity());
        List<Deposit> deposits = depositLab.getDeposits();
        if (mAdapter == null){
            mAdapter = new DepositAdapter(deposits);
            mDepositRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDeposits(deposits);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class DepositHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Deposit mDeposit;

        public DepositHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_deposit, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.deposit_title);
            mDateTextView = itemView.findViewById(R.id.deposit_date);
        }

        public void bind(Deposit deposit){
            mDeposit = deposit;
            mTitleTextView.setText(mDeposit.getTitle());
            mDateTextView.setText(sdf.format(mDeposit.getDate()));
        }

        @Override
        public void onClick(View view){
            Intent intent = DepositPagerActivity.newIntent(getActivity(), mDeposit.getId());
            startActivity(intent);
        }
    }

    private class DepositAdapter extends RecyclerView.Adapter<DepositHolder> {

        private List<Deposit> mDeposits;

        public DepositAdapter(List<Deposit> deposits){
            mDeposits = deposits;
        }

        @Override
        public DepositHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DepositHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DepositHolder holder, int position) {
            Deposit deposit = mDeposits.get(position);
            holder.bind(deposit);
        }

        @Override
        public int getItemCount() {
            return mDeposits.size();
        }

        public void setDeposits(List<Deposit> deposits){
            mDeposits = deposits;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }
}
