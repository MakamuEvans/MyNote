package com.elm.mycheck.login.services.alarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class SnoozeCounter extends Service {
    private final static String TAG = SnoozeCounter.class.getSimpleName();
    private CountDownTimer interval;
    Context context;

    public SnoozeCounter() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "counter started");

        stopSelf(startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
