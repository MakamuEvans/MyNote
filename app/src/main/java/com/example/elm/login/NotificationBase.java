package com.example.elm.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elm.login.adapter.NotificationsAdapter;
import com.example.elm.login.model.Alarm;
import com.example.elm.login.model.Reminder;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class NotificationBase extends AppCompatActivity {
    public static boolean activityOpen = false;
    public List<Reminder> activeReminders = new ArrayList<>();
    public List<Alarm> activeAlarms = new ArrayList<>();
    TextView notificationsCount;
    ImageView close_button, pause_media;
    int count = 0;
    CountDownTimer interval,muter;
    private newNotification newNotificationn;
    Vibrator vibrator;
    long[] pattern = {0,2000,1000};


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("started", "yes");
        activityOpen = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //allow on lock screen
        Window window;
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor =preferences.edit();
        editor.putBoolean("notificationOpen", true);
        editor.commit();

        setContentView(R.layout.activity_notification_base);

        SharedPreferences preferences1 = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.putBoolean("soundAlarm", true);
        editor1.commit();
        //get active notifications
        activeAlarms = Select.from(Alarm.class)
                .where(Condition.prop("type").eq("actual"))
                .where(Condition.prop("alarm").eq(1))
                .list();
        for (Alarm alarm : activeAlarms) {
            Reminder reminder = Select.from(Reminder.class)
                    .where(Condition.prop("uniquecode").eq(alarm.getId()))
                    .first();
            activeReminders.add(reminder);
        }

        //register receiver
        IntentFilter intentFilter  = new IntentFilter(newNotificationn.ACTION);
        newNotificationn = new newNotification();
        registerReceiver(newNotificationn, intentFilter);


        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        horizontalInfiniteCycleViewPager.setAdapter(new NotificationsAdapter(getBaseContext(), false, activeReminders));

        notificationsCount = (TextView) findViewById(R.id.notifications_count);
        notificationsCount.setText("You have " + activeAlarms.size() + " Reminder(s)");
        close_button = (ImageView) findViewById(R.id.reminders_close);
        pause_media = (ImageView) findViewById(R.id.stop_alarm);

        pause_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();
            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeAlarms = Select.from(Alarm.class)
                        .where(Condition.prop("type").eq("actual"))
                        .where(Condition.prop("alarm").eq(1))
                        .list();
                for (Alarm alarm : activeAlarms) {
                    alarm.setAlarm(0);
                    alarm.save();
                }
                finish();
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Uri ringtone = Uri.parse(sp.getString("notifications_new_message_ringtone", "haha"));
        System.out.println(ringtone);
        playSound(this, ringtone);

    }

    private void soundController(final Uri uri) {
        interval = new CountDownTimer(300000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("remaininggg", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                if (count < 4) {
                    count = count + 1;
                    playSound(NotificationBase.this, uri);

                }
            }
        }.start();
    }

    private MediaPlayer mMediaPlayer;

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 20, 0);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, 0);
                muteSound(alert);
                Log.e("huh", "volume there");
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    public void muteSound(final Uri uri) {
        muter = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("remaining", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                mMediaPlayer.stop();
                vibrator.cancel();
                soundController(uri);
            }
        }.start();
    }

    private void stopSound() {
        Log.e("arrived", "yes");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            //mMediaPlayer.release();
        }
        if (vibrator!=null){
            vibrator.cancel();
        }
    }

    private void cancelCountDown(){
        if (interval != null){
            interval.cancel();
        }
        if (muter!=null){
            muter.cancel();
        }
        if (vibrator!=null){
            vibrator.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor =preferences.edit();
        editor.putBoolean("notificationOpen", false);
        editor.commit();
        Log.e("destroy", "yes");
        if (mMediaPlayer!=null){
            stopSound();
            //mMediaPlayer.release();
        }
        cancelCountDown();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor =preferences.edit();
        editor.putBoolean("notificationOpen", false);
        editor.commit();
        Log.e("destroy", "yes");
        if (mMediaPlayer!=null){
            stopSound();
            mMediaPlayer.release();
        }
        cancelCountDown();
    }

    public class newNotification extends BroadcastReceiver{
        public static final String ACTION="new_notification_base";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("check", "huuu");
            finish();
            startActivity(getIntent());
        }
    }
}
