package com.example.elm.login.services.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.elm.login.LockScreenAlarm;
import com.example.elm.login.NotificationBase;
import com.example.elm.login.R;
import com.example.elm.login.model.Alarm;

/**
 * Created by elm on 8/14/17.
 */

public class ReminderAlarms extends BroadcastReceiver {
    String nTitle, nContent;
    int nId;
    Long alarmId;
    @Override
    public void onReceive(Context context, Intent intent) {
        //get data from calling intent
        Bundle bundle = intent.getExtras();
        nTitle = bundle.getString("title");
        nContent = bundle.getString("content");
        nId =bundle.getInt("notificationId");
        alarmId = bundle.getLong("alarmId");

        //raise notification flag
        Alarm alarm = Alarm.findById(Alarm.class, alarmId);
        alarm.setAlarm(1);
        alarm.save();

        //get type of notification -->early reminder n actual?
        if (nId == 100){
            //actual
            actualAlarms(context);
        }else {
            //early reminder
            reminderAlarms(nTitle, nContent, context);
        }
    }

    //handle reminders to alarms
    public  void reminderAlarms(String nTitle, String nContent, Context context){
        //check if there are other active reminders.
        int count;
        SharedPreferences sharedPreferences = null;
        count = context.getSharedPreferences("myPref", 0).getInt("alarmReminders", 0);

        //set action responses
        Intent resultIntent = new Intent(context, NotificationBase.class);
        Intent openReminder = new Intent(context, NotificationBase.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(context, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

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
        if (count==0){
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
        }else {
            SharedPreferences.Editor pref = context.getSharedPreferences("myPref", 0).edit();
            pref.putInt("alarmReminders", count+1);
            pref.apply();

            builder.setContentText("You have "+count+ " Reminders");
            mNotificationManager.notify(101, builder.build());
        }

    }
    //handle actual reminders
    public void actualAlarms(Context context){
        context.startActivity(new Intent(context, NotificationBase.class));
    }
}
