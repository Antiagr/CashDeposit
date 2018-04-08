package com.example.anti.cashdeposits.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.anti.cashdeposits.DepositLab;

import java.util.concurrent.TimeUnit;

public class RateService extends IntentService{

    private static final String TAG = "IntentService";
    private static final long RATE_INTERVAL_MS = TimeUnit.DAYS.toMillis(1);

    public static Intent newIntent(Context context){
        return new Intent(context, RateService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = RateService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    RATE_INTERVAL_MS, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public RateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        DepositLab depositLab = DepositLab.get(getApplicationContext());
//        depositLab.updateRate();
        Log.i(TAG, "currency rates updated");
    }
}
