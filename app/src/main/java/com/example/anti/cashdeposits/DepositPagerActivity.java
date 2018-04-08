package com.example.anti.cashdeposits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.anti.cashdeposits.data.Deposit;

import java.util.List;
import java.util.UUID;

public class DepositPagerActivity extends AppCompatActivity{

    private static final String EXTRA_DEPOSIT_ID = "com.example.anti.cashdeposits.deposit_id";

    private ViewPager mViewPager;
    private List<Deposit> mDeposits;

    public static Intent newIntent(Context packageContext, UUID depositId){
        Intent intent = new Intent(packageContext, DepositPagerActivity.class);
        intent.putExtra(EXTRA_DEPOSIT_ID, depositId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_pager);

        UUID depositId = (UUID) getIntent().getSerializableExtra(EXTRA_DEPOSIT_ID);

        mViewPager = findViewById(R.id.deposit_view_pager);
        mDeposits = DepositLab.get(this).getDeposits();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Deposit deposit = mDeposits.get(position);
                return DepositFragment.newInstance(deposit.getId());
            }

            @Override
            public int getCount() {
                return mDeposits.size();
            }
        });

        for (int i = 0; i < mDeposits.size(); i++){
            if (mDeposits.get(i).getId().equals(depositId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
