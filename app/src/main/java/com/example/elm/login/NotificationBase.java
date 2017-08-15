package com.example.elm.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class NotificationBase extends AppCompatActivity {
    public List<Reminder> activeReminders = new ArrayList<>();
    public List<Alarm> activeAlarms = new ArrayList<>();
    TextView notificationsCount;
    ImageView close_button, pause_media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //allow on lock screen
        Window window;
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_notification_base);

        //get active notifications
        activeAlarms = Select.from(Alarm.class)
                .where(Condition.prop("type").eq("actual"))
                .where(Condition.prop("alarm").eq(1))
                .list();
        for (Alarm alarm: activeAlarms){
            Reminder reminder = Select.from(Reminder.class)
                    .where(Condition.prop("uniquecode").eq(alarm.getId()))
                    .first();
            activeReminders.add(reminder);
        }

        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        horizontalInfiniteCycleViewPager.setAdapter(new NotificationsAdapter(getBaseContext(), false, activeReminders));

        notificationsCount = (TextView) findViewById(R.id.notifications_count);
        notificationsCount.setText("You have "+activeAlarms.size()+" Reminder(s)");
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
                for (Alarm alarm: activeAlarms){
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

    private MediaPlayer mMediaPlayer;
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    private void stopSound(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }
}
