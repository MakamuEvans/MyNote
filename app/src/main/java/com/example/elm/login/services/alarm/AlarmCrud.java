package com.example.elm.login.services.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by elm on 8/15/17.
 */

public class AlarmCrud extends Service {
    String title, content;
    int nId;
    Long calender,aId;
    boolean create;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service", "started");
        //get data
        Bundle bundle = intent.getExtras();
        title = bundle.getString("title");
        content = bundle.getString("content");
        nId = bundle.getInt("nId");
        calender = bundle.getLong("calender");
        aId = bundle.getLong("aId");
        create = bundle.getBoolean("create");

        //create or cancel
        if (create){ //create
            createAlarm(title,content,nId,calender,aId);
        }else { //cancel

        }
        return Service.START_STICKY;
    }

    public void createAlarm(String title,String content, int nId, Long calender, Long aId){
        Log.e("service", "startedyey");
        Intent intent = new Intent("DISPLAY_NOTIFICATION");
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("notificationId", nId);
        intent.putExtra("alarmId", aId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), Integer.parseInt(aId.toString()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calender);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

    }
    public void cancelAlarm(Long aId){

    }
}
