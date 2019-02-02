package com.elm.mycheck.login.services.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.elm.mycheck.login.NotificationBase;
import com.elm.mycheck.login.R;
import com.elm.mycheck.login.ToDoDetails;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SoundService extends Service {
    private MediaPlayer mMediaPlayer;
    private Boolean snoozeAlarm = false;
    private Boolean startAlarm = false;
    private Boolean stopAlarm = false;
    private CountDownTimer interval, self_stopper;
    private int alartTime, snoozeTime;
    Vibrator vibrator;
    long[] pattern = {0, 2000, 1000};

    public SoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("hai", "called");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        alartTime = Integer.parseInt(sp.getString("ring_duration", "1"));
        snoozeTime = Integer.parseInt(sp.getString("snooze_duration", "5"));

        Bundle bundle = intent.getExtras();
        if (bundle.containsKey("startAlarm")) {
            //start alarm
            playSound();
        }
        if (bundle.containsKey("snoozeAlarm")) {
            //snooze alarm
            Snoozetimer();
        }
        if (bundle.containsKey("fromSnooze")) {

        }
        //stopSelf(startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null)
            mMediaPlayer.stop();
        if (vibrator != null)
            vibrator.cancel();
        if (interval != null)
            interval.cancel();
    }


    AudioManager audioManager;
    int volume = 3;
    private void playSound() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        alartTime = Integer.parseInt(sp.getString("ring_duration", "1"));
        snoozeTime = Integer.parseInt(sp.getString("snooze_duration", "5"));
        Boolean vibrate = sp.getBoolean("notifications_new_message_vibrate", true);
        Uri ringtone = Uri.parse(sp.getString("notifications_new_message_ringtone", String.valueOf(Settings.System.DEFAULT_RINGTONE_URI)));


        if (vibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, 0);
        }

        mMediaPlayer = new MediaPlayer();
        try {
            if (ringtone.equals("default")) {
                mMediaPlayer.setDataSource(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
            } else {
                mMediaPlayer.setDataSource(getApplicationContext(), ringtone);
            }
             audioManager = (AudioManager) getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            //if ()
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
            //if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            Log.e("hai", "Playing Sound");
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            Timer timer = new Timer();
            //timer.schedule();
            timer.schedule(task, 1000, 4000);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            volume = volume + 1;
            while (volume < 20){
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
            }
        }
    };


    public void Snoozetimer() {
        //Log.e(TAG, "timer called");
        interval = new CountDownTimer(snoozeTime * 60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("remaining", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                getApplicationContext().startActivity(new Intent(getApplicationContext(), NotificationBase.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("snooze", true));
                stopSelf();
            }
        }.start();
    }


    private void createNotification(String message) {
        Intent resultIntent = new Intent(this, NotificationBase.class);
        resultIntent.putExtra("fromSnooze", true);
        Intent openReminder = new Intent(this, NotificationBase.class);
        resultIntent.putExtra("fromSnooze", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.my_checkv2)
                .setContentTitle("Missed Alarm")
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
        builder.setContentText(message);
        builder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(500, builder.build());
    }

}
