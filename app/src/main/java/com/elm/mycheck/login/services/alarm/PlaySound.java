package com.elm.mycheck.login.services.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.elm.mycheck.login.NotificationBase;
import com.elm.mycheck.login.R;
import com.elm.mycheck.login.ToDoDetails;
import com.elm.mycheck.login.model.AlarmReminder;
import com.elm.mycheck.login.model.Reminder;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaySound extends Service {
    public List<Reminder> activeReminders = new ArrayList<>();
    public List<AlarmReminder> activeNotifications = new ArrayList<>();
    private int alartTime, snoozeTime;
    CountDownTimer interval, muter;
    Boolean quit, vibrateT;
    Boolean n = false;
    Boolean notif = false;
    Uri alert;
    Boolean snooze = false;
    Boolean nType = false;
    Boolean play = false;
    private MediaPlayer mMediaPlayer;
    private static final String TAG = "p_s";
    public Vibrator vibrator;
    long[] pattern = {0, 2000, 1000};

    public PlaySound() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.e("TF", String.valueOf(startId));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        alartTime = Integer.parseInt(sp.getString("ring_duration", "1"));
        snoozeTime = Integer.parseInt(sp.getString("snooze_duration", "5"));
        Bundle bundle = intent.getExtras();
        play = bundle.getBoolean("play");
        snooze = bundle.getBoolean("snooze");
        vibrateT = true;
        if (bundle.containsKey("url")) {
            alert = Uri.parse(bundle.getString("url"));
            vibrateT = bundle.getBoolean("vibrate");
        }
        if (bundle.containsKey("notif")) {
            notif = bundle.getBoolean("notif");
        }


        quit = bundle.getBoolean("quit");
        nType = bundle.getBoolean("nType");

        Log.e("quit", String.valueOf(quit));
        Log.e("ntype", String.valueOf(nType));
        Log.e("snooze", String.valueOf(snooze));
        Log.e("play", String.valueOf(play));
        Log.e("alert", String.valueOf(alert));
        Log.e("vibrates", String.valueOf(vibrateT));
        if (quit) {
            stopAlarm(nType);
        } else {
            if (!nType && snooze) {
                timer();
            }
        }

        if (play) {
            SharedPreferences preferences = getSharedPreferences("myPref", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("serviceId", startId);
            editor.commit();
            playSound(alert, vibrateT);

        } else {
            stopSound();
        }

       /* if (notif) {
            createNotification();
        }*/

        stopSelf(startId);
        return Service.START_NOT_STICKY;
    }

    private UpdateAlarm updateAlarm;
    @Override
    public void onCreate() {
       vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        IntentFilter intentFilter = new IntentFilter(UpdateAlarm.ACTION);
        updateAlarm = new UpdateAlarm();
        registerReceiver(updateAlarm, intentFilter);

        super.onCreate();
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

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(500);
    }

    public void timer() {
        Log.e(TAG, "timer called");
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

    public void playSound(Uri alert, Boolean vibrate) {
        Log.e(TAG, "playyyyyy " + alert);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), alert);
            final AudioManager audioManager = (AudioManager) getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            //if ()
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 20, 0);
            //if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            Log.e(TAG, "Playing Sound");
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            /*mMediaPlayer.prepare();
            mMediaPlayer.start();*/
            if (vibrateT)
                vibrator.vibrate(pattern, 0);
            //}
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void stopSound() {
        /*vibrator.cancel();
        vibrator.cancel();
        if (vibrator != null)
            Log.e(TAG, "playyy stopSound Called");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }*/

     /*   fk = true;
        onDestroy();*/
vibrator.cancel();
//        interval.cancel();
       /* if (vibrator != null) {
            vibrator.cancel();
        }*/
       /* int start = getSharedPreferences("myPref", 0).getInt("startId", 0);
        stopSelf(start);*/
    }

    @Override
    public void onDestroy() {
        Log.e("pray", "Destroying..");

        //    vibrator.cancel();
        super.onDestroy();
    }

    private void createNotification() {
        Intent resultIntent = new Intent(this, NotificationBase.class);
        resultIntent.putExtra("fromSnooze", true);
        Intent openReminder = new Intent(this, NotificationBase.class);
        resultIntent.putExtra("fromSnooze", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo)
                .setContentTitle("Active Alarm")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ToDoDetails.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.addAction(R.mipmap.ic_action_check_free, "Open!", pendingIntent);
        // builder.addAction(R.mipmap.ic_action_close, "Got it!", null);
        builder.setContentText("You Have an Active Alarm");
        builder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(500, builder.build());
    }

    public class UpdateAlarm extends BroadcastReceiver {
        public static final String ACTION = "update_alarm";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "newNotification Received");
            //finish();
            //startActivity(getIntent());
            vibrator.cancel();
        }
    }
}
