package com.example.elm.login.services.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.LockScreenAlarm;
import com.example.elm.login.Navigation;
import com.example.elm.login.NewReminder;
import com.example.elm.login.NotificationBase;
import com.example.elm.login.R;
import com.example.elm.login.model.Alarm;
import com.example.elm.login.model.Reminder;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import layout.ReminderFragment;

/**
 * Created by elm on 8/14/17.
 */

public class ReminderAlarms extends BroadcastReceiver {
    String nTitle, nContent;
    int nId;
    Long alarmId;
    Boolean repeat;

    @Override
    public void onReceive(Context context, Intent intent) {
        //get data from calling intent
        Bundle bundle = intent.getExtras();
        nTitle = bundle.getString("title");
        nContent = bundle.getString("content");
        nId = bundle.getInt("notificationId");
        alarmId = bundle.getLong("alarmId");
        repeat = bundle.getBoolean("repeat");

        //get type of notification -->early reminder n actual?
        if (nId == 100) {
            //actual
            if (Build.VERSION.SDK_INT >= 19) {
                if (repeat) {
                    actualRepeats(alarmId, context);
                }
            }
            if (repeat) {
                Reminder reminder = Select.from(Reminder.class)
                        .where(Condition.prop("uniquecode").eq(alarmId))
                        .first();
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                Calendar calendar = Calendar.getInstance();
                String weekDay = dayFormat.format(calendar.getTime());
                if (reminder.getRepeat().contains(weekDay)) {
                    actualAlarms(context);
                    Alarm alarm = Alarm.findById(Alarm.class, alarmId);
                    alarm.setAlarm(1);
                    alarm.save();
                }
            } else {
                actualAlarms(context);
                //raise notification flag
                Alarm alarm = Alarm.findById(Alarm.class, alarmId);
                alarm.setAlarm(1);
                alarm.save();
            }
        } else {
            //early reminder
            reminderAlarms(nTitle, nContent, context);
        }
    }

    //handle reminders to alarms
    public void reminderAlarms(String nTitle, String nContent, Context context) {
        //check if there are other active reminders.
        int count;
        SharedPreferences sharedPreferences = null;
        count = context.getSharedPreferences("myPref", 0).getInt("alarmReminders", 0);

        //set action responses
        Intent resultIntent = new Intent(context, NotificationBase.class);
        Intent openReminder = new Intent(context, NotificationBase.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.logo)
                .setContentTitle("myNote: New Reminder(s)!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_action_check_free, "Open!", pendingIntent)
                .addAction(R.mipmap.ic_action_close, "Got it!", pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        System.out.println(count);
        System.out.println("haha");
        //handle notification putting in mind other active similar notifications
        if (count == 0) {
            SharedPreferences.Editor pref = context.getSharedPreferences("myPref", 0).edit();
            pref.putInt("alarmReminders", 1);
            pref.apply();

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(NotificationBase.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentText(nTitle);
            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(101, builder.build());
        } else {
            SharedPreferences.Editor pref = context.getSharedPreferences("myPref", 0).edit();
            pref.putInt("alarmReminders", count + 1);
            pref.apply();

            builder.setContentText("You have " + count + " Reminders");
            mNotificationManager.notify(101, builder.build());
        }

    }

    //handle actual reminders
    Context context;

    public void actualAlarms(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myPref", 0);
        Boolean isOpen = context.getSharedPreferences("myPref", 0).getBoolean("notificationOpen", false);
        if (isOpen) {
            Intent intent = new Intent(NotificationBase.newNotification.ACTION);
            context.sendBroadcast(intent);
        } else {
            context.startActivity(new Intent(context, NotificationBase.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void actualRepeats(Long alarmId, Context context) {
        //get alarm
        Reminder reminder = Select.from(Reminder.class)
                .where(Condition.prop("uniquecode").eq(alarmId.toString()))
                .first();
        //get dates right
        String time_format = "HH:mm";
        final SimpleDateFormat time_simpleDateFormat = new SimpleDateFormat(time_format, Locale.ENGLISH);

        Date date1 = null;
        try {
            date1 = time_simpleDateFormat.parse(reminder.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String format2 = "HH:mm";
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format2, Locale.ENGLISH);
        String description = null;

        if (reminder.getDescription().equals(null)) {
            description = "Its Time!. Hello from myNote";
        } else {
            description = reminder.getDescription();
        }

        if (!reminder.getPrior().equals("None")) {
            reminderRepeats(reminder.getPrior(), reminder.getTitle(), date1, alarmId, context);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date1)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date1)));

        Boolean repeated = true;

        //create alarm -->from a service
        Intent intent = new Intent(context, AlarmCrud.class);
        intent.putExtra("calender", calendar.getTimeInMillis());
        intent.putExtra("nId", 100);
        intent.putExtra("repeat", repeated);
        intent.putExtra("aId", alarmId);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("content", description);
        intent.putExtra("create", true);
        context.startService(intent);

    }

    public void reminderRepeats(String prior, String title, Date date, Long alarmId, Context context) {
        int value = Integer.parseInt(prior.replaceAll("[^0-9]", ""));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));
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

        Intent intent = new Intent(context, AlarmCrud.class);
        intent.putExtra("calender", calendar.getTimeInMillis());
        intent.putExtra("nId", 101);
        intent.putExtra("aId", alarmId);
        intent.putExtra("title", title);
        intent.putExtra("content", "Prepared? You have  a Reminder in the next " + time);
        intent.putExtra("create", true);
        context.startService(intent);
    }
}
