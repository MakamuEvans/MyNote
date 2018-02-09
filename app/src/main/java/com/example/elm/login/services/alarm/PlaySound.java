package com.example.elm.login.services.alarm;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.elm.login.NotificationBase;
import com.example.elm.login.model.AlarmReminder;
import com.example.elm.login.model.Reminder;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class PlaySound extends Service {
    public List<Reminder> activeReminders = new ArrayList<>();
    public List<AlarmReminder> activeNotifications = new ArrayList<>();
    private int alartTime, snoozeTime;
    CountDownTimer interval, muter;
    Boolean quit;
    Boolean n = false;

    public PlaySound() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Play", "Sound Called");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        alartTime = Integer.parseInt(sp.getString("ring_duration", "1"));
        snoozeTime = Integer.parseInt(sp.getString("snooze_duration", "5"));
        Boolean nType = false;

        Bundle bundle = intent.getExtras();
        quit = bundle.getBoolean("quit");
        nType = bundle.getBoolean("nType");

        if (quit) {
            stopAlarm(nType);
        } else {
            if (!nType)
                timer();
        }

        stopSelf(startId);
        return Service.START_STICKY;
    }

    public void stopAlarm(Boolean nType) {
        Log.e("Play", "Stop Called");

        if (!nType) {
            activeReminders = Select.from(Reminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();
            Log.e("count", String.valueOf(activeReminders.size()));

            for (Reminder alarm : activeReminders) {
                alarm.setActive(false);
                alarm.save();
            }
        } else {
            activeNotifications = Select.from(AlarmReminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();
            for (AlarmReminder reminder : activeNotifications
                    ) {
                reminder.setActive(false);
                reminder.save();
            }
        }
    }

    public void timer() {
        Log.e("Play", "timer Called");

        interval = new CountDownTimer(snoozeTime * 60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("remaininggg", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                getApplicationContext().startActivity(new Intent(getApplicationContext(), NotificationBase.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }.start();
    }
}
