package com.elm.mycheck.login.services.alarm;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.elm.mycheck.login.NotificationBase;
import com.elm.mycheck.login.R;
import com.elm.mycheck.login.ToDoDetails;
import com.elm.mycheck.login.model.AlarmReminder;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by elm on 8/14/17.
 */

public class ReminderAlarms extends BroadcastReceiver {
    private static final String TAG = ReminderAlarms.class.getSimpleName();
    String nTitle, nContent;
    int nId, alarmTracker;
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
        alarmTracker = bundle.getInt("alarmTracker");

        Log.e("Atherere", String.valueOf(nId));

        //get type of notification -->early reminder n actual?
        if (nId == Constants.actualReminder) {
            //actual
            //handle alarms differently for api 19 and above due to OS changes
            if (Build.VERSION.SDK_INT >= 19) {
                if (repeat) {

                    Intent intent4 = new Intent(context, AlarmCrud.class);
                    intent4.putExtra("alarmId", alarmId);
                    intent4.putExtra("create", true);
                    intent4.putExtra("reset", true);
                    context.startService(intent4);
                    //actualRepeats(alarmId, context);
                }
            }
            Reminder reminder;
            //is alarm repeating?
            if (repeat) {
                //find the alarm
                reminder = Reminder.findById(Reminder.class, alarmId);
                if (reminder != null){
                    //set date format
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    Calendar calendar = Calendar.getInstance();
                    String weekDay = dayFormat.format(calendar.getTime());
                    if (reminder.getRepeat().contains(weekDay)) {
                        Log.e(TAG, "WeekFound");
                        //set reminder as active
                        reminder.setActive(true);
                        reminder.save();
                        if (reminder.getStatus())
                            actualAlarms(context);
                    }
                }else {
                    cancelAlarm(alarmTracker, context);
                }

            } else {
                reminder = Reminder.findById(Reminder.class, alarmId);
                if (reminder != null){
                    reminder.setActive(true);
                    reminder.save();
                    if (reminder.getStatus())
                        actualAlarms(context);

                    reminder.setStatus(false);
                    reminder.save();
                }
            }

            //clear from notification
            if (Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(alarmId)).count() > 0){
                AlarmReminder alarmReminder = Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(alarmId)).first();
                alarmReminder.setActive(false);
                alarmReminder.save();
                reminderAlarms(reminder.getTitle(), context);
            }
        } else if (nId == Constants.earlyReminder){
            //early reminder
            AlarmReminder alarmReminder = AlarmReminder.findById(AlarmReminder.class, alarmId);

            //get parent alarm
            Reminder reminder = Reminder.findById(Reminder.class, alarmReminder.getReminderid());
            if (reminder != null){
                if (reminder.getStatus()) {
                    alarmReminder.setActive(true);
                    alarmReminder.save();
                    reminderAlarms(nTitle, context);
                }
            }
        } else if (nId == Constants.todoReminder){
            Reminder reminder = Reminder.findById(Reminder.class, alarmId);
            reminder.setStatus(false);
            reminder.save();
            todoReminder(reminder.getTitle(), context, alarmId);
        }else {
            cancelAlarm(alarmTracker, context);
        }
    }

    //handle reminders to alarms
    public void reminderAlarms(String nTitle, Context context) {
        //check if there are other active reminders.
        int count;
        SharedPreferences sharedPreferences = null;
        count = (int) Select.from(AlarmReminder.class).where(Condition.prop("active").eq("1")).count();

        //set action responses
        Intent resultIntent = new Intent(context, NotificationBase.class);
        resultIntent.putExtra("notification", true);
        Intent openReminder = new Intent(context, NotificationBase.class);
        openReminder.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.logo)
                .setContentTitle("myCheck: New Reminder(s)!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        System.out.println(count);
        //handle notification putting in mind other active similar notifications
        if (count == 0){
            mNotificationManager.cancel(101);
        }else if (count == 1) {

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(NotificationBase.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.addAction(R.mipmap.ic_action_check_free, "Open!", pendingIntent);
            // builder.addAction(R.mipmap.ic_action_close, "Got it!", null);
            builder.setContentText(nTitle);
            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(101, builder.build());
        } else {
            builder.addAction(R.mipmap.ic_action_check_free, "Open!", pendingIntent);
            builder.setContentText("You have " + count + " Reminders");
            mNotificationManager.notify(101, builder.build());
        }

    }

    public void todoReminder(String nTitle, Context context, Long alarmId){
        Reminder reminder = Reminder.findById(Reminder.class, alarmId);
        Long id = Long.valueOf(reminder.getTodo());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddSSS");
        Random r = new Random();
        int random = (r.nextInt(999) + 9);
        String format = simpleDateFormat.format(new Date());
        int alarmTracker = Integer.parseInt(alarmId+format);
        alarmTracker = alarmTracker - random;
        //set action responses
        Intent resultIntent = new Intent(context, ToDoDetails.class);
        resultIntent.putExtra("id", id);
        resultIntent.putExtra("reminder", alarmTracker);
        Intent openReminder = new Intent(context, ToDoDetails.class);
        resultIntent.putExtra("id", id);
        resultIntent.putExtra("reminder", alarmTracker);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.logo)
                .setContentTitle(nTitle)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(ToDoDetails.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.addAction(R.mipmap.ic_action_check_free, "Open!", pendingIntent);
            // builder.addAction(R.mipmap.ic_action_close, "Got it!", null);
            builder.setContentText("Hey, Its time for your ToDo");
            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(alarmTracker, builder.build());
    }

    //handle actual reminders
    Context context;

    public void actualAlarms(Context context) {
        Log.e(TAG, "Actual Alarms called");
        Boolean isOpen = context.getSharedPreferences("myPref", 0).getBoolean("notificationOpen", false);
        Log.e(TAG, isOpen.toString());
        if (isOpen) {
            /*Intent intent = new Intent(NotificationBase.newNotification.ACTION);
            context.sendBroadcast(intent);*/
            context.startActivity(new Intent(context, NotificationBase.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(context, NotificationBase.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void actualRepeats(Long alarmId, Context context) {
        Log.e(TAG, "ActualRepeats Called");
        //get alarm
        Reminder reminder = Reminder.findById(Reminder.class, alarmId);
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

        if (reminder.getPrior()) {
            //AlarmReminder.find(AlarmReminder.class, "reminderid=")
            AlarmReminder alarmReminder = Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(reminder.getId())).first();
            reminderRepeats(alarmReminder.getTime(), reminder.getTitle(), date1, alarmId, context);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date1)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date1)));

        Boolean repeated = true;
        Log.e(TAG, calendar.toString());

        //create alarm -->from a service


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
        intent.putExtra("nId", Constants.earlyReminder);
        intent.putExtra("aId", alarmId);
        intent.putExtra("title", title);
        intent.putExtra("content", "Prepared? You have  a Reminder in the next " + time);
        intent.putExtra("create", true);
        context.startService(intent);
    }

    public void cancelAlarm(int aId, Context context) {
        //Log.e("service", "cancelstarted");

        Intent intent = new Intent("DISPLAY_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                aId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
