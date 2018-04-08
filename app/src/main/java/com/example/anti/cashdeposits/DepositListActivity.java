package com.example.anti.cashdeposits;

import android.support.v4.app.Fragment;

public class DepositListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DepositListFragment();
    }
}
