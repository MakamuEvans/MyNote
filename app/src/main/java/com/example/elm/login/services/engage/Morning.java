package com.example.elm.login.services.engage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.elm.login.NotificationBase;
import com.example.elm.login.R;
import com.example.elm.login.model.AlarmReminder;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Morning extends Service {
    public Morning() {
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

    public void notification(){
        List<Reminder> reminders = new ArrayList<>();

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
       // cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());


        int count;
        SharedPreferences sharedPreferences = null;
        count = (int) Select.from(Reminder.class).where(Condition.prop("time").gt(formatter.format(cal.getTime().getTime()))).count();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());
        reminders = Select.from(Reminder.class).where(Condition.prop("repeat").isNotNull()).list();
        for (Reminder reminder: reminders){
            if (reminder.getRepeat().contains(weekDay)) {
               count++;
            }
        }

        if (count > 0){

        }
        //set action responses
        Intent resultIntent = new Intent(getApplicationContext(), NotificationBase.class);
        resultIntent.putExtra("notification", true);
        Intent openReminder = new Intent(getApplicationContext(), NotificationBase.class);
        openReminder.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.logo)
                .setContentTitle("myCheck: Morning Buddy!")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        System.out.println(count);
        //handle notification putting in mind other active similar notifications
        if (count == 0){
            mNotificationManager.cancel(Constants.morning);
        }else  {

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(NotificationBase.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.addAction(R.mipmap.ic_action_check_free, "Open!", pendingIntent);
            // builder.addAction(R.mipmap.ic_action_close, "Got it!", null);
            builder.setContentText("Check your Schedule today");
            builder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(Constants.morning, builder.build());
        }
    }
}
