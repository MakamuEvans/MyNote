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

import com.example.elm.login.NewReminder;
import com.example.elm.login.model.AlarmReminder;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by elm on 8/15/17.
 */

public class AlarmCrud extends Service {
    private final static String TAG = AlarmCrud.class.getSimpleName();
    private String title, content, dated;
    private Long calender, reminderId;
    private boolean create, repeat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        reminderId = bundle.getLong("alarmId");
        create = bundle.getBoolean("create");

        Reminder reminder = Reminder.findById(Reminder.class, reminderId);
        title = reminder.getTitle();
        content = reminder.getDescription();
        dated = reminder.getTime();
        repeat = reminder.getRepeat() == null ? false : true;
        Log.e("Dated", dated);

        //create or cancel
        if (create) { //create
            String format = "MMM dd yyyy HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            Date date = null;

            //get date depending on ReminderType

               // date_string = simpleDateFormat2.format(time_picker.getDate());
                try {
                    if (reminder.getRepeat() == null){
                        // date_string = simpleDateFormat.format(dateTime.getDate());
                        date = simpleDateFormat.parse(dated);
                    }else {
                        date = simpleDateFormat.parse(dated);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            Calendar calendar = Calendar.getInstance();
            int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));

            if (reminder.getRepeat() == null){
                calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
                calendar.set(Calendar.MONTH, month0);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));
            }else {
                Calendar calendar1 = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
                calendar1.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));

                if (calendar1.getTimeInMillis() >= calendar.getTimeInMillis()){
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Log.e("newTime", String.valueOf(calendar.getTime()));
                }
            }

            createAlarm(
                    title,
                    content,
                    Constants.actualReminder,
                    calendar.getTimeInMillis(),
                    reminderId,
                    repeat
            );

            if (reminder.getPrior()){
                AlarmReminder earlyReminder = Select.from(AlarmReminder.class)
                        .where(Condition.prop("reminderid").eq(reminder.getId()))
                        .first();
                int value = Integer.parseInt(earlyReminder.getTime().replaceAll("[^0-9]", ""));
                String time = null;
                if (value == 5) {
                    calendar.add(calendar.MINUTE, -5);
                    time = "Five Minutes";
                    Log.e("extra?", "yes");
                }
                if (value == 10) {
                    calendar.add(calendar.MINUTE, -10);
                    time = "Ten Minutes";
                }
                if (value == 30) {
                    calendar.add(calendar.MINUTE, -30);
                    time = "Thirty Minutes";
                }
                if (value == 1) {
                    calendar.add(calendar.MINUTE, -60);
                    time = "One Hour";
                }
                if (value == 2) {
                    calendar.add(calendar.MINUTE, -120);
                    time = "Two Hours";
                }


                createAlarm(
                        title,
                        content,
                        Constants.earlyReminder,
                        calendar.getTimeInMillis(),
                        earlyReminder.getId(),
                        repeat
                );
            }
        } else { //cancel
            cancelAlarm(reminderId);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddSSS");
        Random r = new Random();
        int random = (r.nextInt(999) + 9);
        String format = simpleDateFormat.format(new Date());
        int alarmTracker = Integer.parseInt(nId+aId.toString()+format);
        alarmTracker = alarmTracker - random;

        Log.e("AtherereC", title+content+nId+aId);
        Intent intent = new Intent("DISPLAY_NOTIFICATION");
        intent.putExtra("title", title);
        intent.putExtra("repeat", repeat);
        intent.putExtra("content", content);
        intent.putExtra("notificationId", nId);
        intent.putExtra("alarmId", aId);
        intent.putExtra("alarmTracker", alarmTracker);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), alarmTracker, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

        if (nId == Constants.earlyReminder){
            AlarmReminder alarmReminder = AlarmReminder.findById(AlarmReminder.class, aId);
            alarmReminder.setIdentifier(alarmTracker);
            alarmReminder.save();
        }else {
            Reminder reminder = Reminder.findById(Reminder.class, aId);
            reminder.setIdentifier(alarmTracker);
            reminder.save();
        }
    }

    public void cancelAlarm(Long aId) {
        Reminder reminder = Reminder.findById(Reminder.class, aId);

        Intent intent = new Intent("DISPLAY_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(),
                reminder.getIdentifier(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        if (reminder.getPrior()){
            AlarmReminder alarmReminder = Select.from(AlarmReminder.class)
                    .where(Condition.prop("reminderid").eq(aId))
                    .first();

            Intent intent2 = new Intent("DISPLAY_NOTIFICATION");
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(
                    getBaseContext(),
                    alarmReminder.getIdentifier(),
                    intent2,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager2.cancel(pendingIntent2);
        }
    }
}
