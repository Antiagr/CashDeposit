package com.example.anti.cashdeposits;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class DepositScheduleActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, DepositScheduleActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new DepositScheduleFragment();
    }
}
