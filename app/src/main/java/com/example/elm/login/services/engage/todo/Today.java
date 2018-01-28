package com.example.elm.login.services.engage.todo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Today extends Service {
    public Today() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        stopSelf(startId);
        return Service.START_STICKY;
    }
}
