package com.example.elm.login.services.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.elm.login.model.Reminder;
import com.example.elm.login.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by elm on 8/15/17.
 */

public class AlarmCrud extends Service {
    private final static String TAG = AlarmCrud.class.getSimpleName();
    private String title, content;
    private int nId;
    private Long calender, aId;
    private boolean create, repeat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        aId = bundle.getLong("aId");
        create = bundle.getBoolean("create");
        repeat = bundle.getBoolean("repeat");

        //create or cancel
        if (create) { //create
            title = bundle.getString("title");
            content = bundle.getString("content");
            nId = bundle.getInt("nId");
            if (nId == Constants.earlyReminder)
                Log.e("AtherereCreate", String.valueOf(nId));
            calender = bundle.getLong("calender");
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(calender);
            Log.e("Timee",formatter.format(calendar.getTime()));
            createAlarm(title, content, nId, calender, aId, repeat);
        } else { //cancel
            cancelAlarm(aId);
        }
        stopSelf(startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy Called");
    }

    public void createAlarm(String title, String content, int nId, Long calender, Long aId, Boolean repeat) {
        Log.e("AtherereC", title+content+nId+aId);
        Intent intent = new Intent("DISPLAY_NOTIFICATION");
        intent.putExtra("title", title);
        intent.putExtra("repeat", repeat);
        intent.putExtra("content", content);
        intent.putExtra("notificationId", nId);
        intent.putExtra("alarmId", aId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(nId+aId.toString()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calender);
        if (repeat){
            Log.e("repeating", "yes");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            }else {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public void cancelAlarm(Long aId) {
        Log.e("service", "cancelstarted");

        Intent intent = new Intent("DISPLAY_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(),
                Integer.parseInt(aId.toString()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
