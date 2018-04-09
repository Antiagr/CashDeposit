package com.example.anti.cashdeposits;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

// TODO удалить
public class MainActivity extends SingleFragmentActivity{

    private static final String EXTRA_DEPOSIT_ID = "com.example.anti.cashdeposits.deposit_id";

    @Override
    protected Fragment createFragment() {
        UUID depositId = (UUID) getIntent().getSerializableExtra(EXTRA_DEPOSIT_ID);
        return DepositFragment.newInstance(depositId);
    }

    public static Intent newIntent(Context packageContext, UUID depositId) {
        Intent intent = new Intent(packageContext, MainActivity.class);
        intent.putExtra(EXTRA_DEPOSIT_ID, depositId);
        return intent;
    }

}
