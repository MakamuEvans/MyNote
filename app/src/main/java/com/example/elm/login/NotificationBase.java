package com.example.elm.login;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elm.login.adapter.NotificationsAdapter;
import com.example.elm.login.model.AlarmReminder;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.services.alarm.AlarmCrud;
import com.example.elm.login.services.alarm.SnoozeCounter;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class NotificationBase extends AppCompatActivity {
    private static final String TAG = NotificationBase.class.getSimpleName();
    public static boolean activityOpen = false;
    public List<Reminder> activeReminders = new ArrayList<>();
    public List<AlarmReminder> activeNotifications = new ArrayList<>();
    TextView notificationsCount;
    ImageView close_button;
    private Button close_b, pause_media;
    int count = 0;
    CountDownTimer interval, muter;
    private newNotification newNotificationn;
    Boolean notification = false;
    Vibrator vibrator;
    long[] pattern = {0, 2000, 1000};
    protected PowerManager.WakeLock wakeLock;
    private LinearLayout reminder_action;
    private int alartTime, snoozeTime;
    Boolean vibrate;

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart called");
        activityOpen = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "Default");
        Log.e("Theme", theme);
        if (theme == "tomato")
            setTheme(R.style.AppTheme_NoActionBar);
        if (theme == "tangarine")
            setTheme(R.style.AppTheme_NoActionBar_Tangarine);
        if (theme.equalsIgnoreCase("banana"))
            setTheme(R.style.AppTheme_NoActionBar_Banana);
        if (theme.equalsIgnoreCase("basil"))
            setTheme(R.style.AppTheme_NoActionBar_Basil);
        if (theme.equalsIgnoreCase("sage"))
            setTheme(R.style.AppTheme_NoActionBar_Sage);
        if (theme.equalsIgnoreCase("peacock"))
            setTheme(R.style.AppTheme_NoActionBar_Peacock);
        if (theme.equalsIgnoreCase("blueberry"))
            setTheme(R.style.AppTheme_NoActionBar_BlueBerry);
        if (theme.equalsIgnoreCase("lavender"))
            setTheme(R.style.AppTheme_NoActionBar_Lavender);
        if (theme.equalsIgnoreCase("grape"))
            setTheme(R.style.AppTheme_NoActionBar_Grape);
        if (theme.equalsIgnoreCase("flamingo"))
            setTheme(R.style.AppTheme_NoActionBar_Flamingo);
        if (theme.equalsIgnoreCase("graphite"))
            setTheme(R.style.AppTheme_NoActionBar_Graphite);


        //allow on lock screen
        Window window;
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //SharedPreferences preferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notificationOpen", true);
        editor.commit();

        setContentView(R.layout.activity_notification_base);
        reminder_action = (LinearLayout) findViewById(R.id.reminder_action);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("notification"))
                notification = intent.getExtras().getBoolean("notification");

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(101);
        }

        SharedPreferences preferences1 = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.putBoolean("soundAlarm", true);
        editor1.commit();
        //get active notifications
        if (!notification) {
            reminder_action.setVisibility(View.VISIBLE);
            activeReminders = Select.from(Reminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();
        } else {
            activeNotifications = Select.from(AlarmReminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();

            for (AlarmReminder reminder : activeNotifications
                    ) {
                activeReminders.add(Reminder.findById(Reminder.class, reminder.getReminderid()));
            }
        }


        //register receiver
        IntentFilter intentFilter = new IntentFilter(newNotificationn.ACTION);
        newNotificationn = new newNotification();
        registerReceiver(newNotificationn, intentFilter);


        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        horizontalInfiniteCycleViewPager.setAdapter(new NotificationsAdapter(getBaseContext(), false, activeReminders));

        notificationsCount = (TextView) findViewById(R.id.notifications_count);
        notificationsCount.setText("You have " + activeReminders.size() + " Reminder(s)");
        if (notification)
            notificationsCount.setText(activeReminders.size() + " Upcoming Reminder(s)");
        close_button = (ImageView) findViewById(R.id.reminders_close);

        pause_media = (Button) findViewById(R.id.snooze_alarm);
        close_b = (Button) findViewById(R.id.close_alarm);

        close_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
                destroy();
            }
        });

        pause_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();
            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                destroy();
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        alartTime = Integer.parseInt(sp.getString("ring_duration", "1"));
        snoozeTime = Integer.parseInt(sp.getString("snooze_duration", "5"));
        vibrate = sp.getBoolean("notifications_new_message_vibrate", true);

        Log.e("Yow", String.valueOf(alartTime) + "  " + String.valueOf(snoozeTime));
        Uri ringtone = Uri.parse(sp.getString("notifications_new_message_ringtone", "content://settings/system/ringtone"));
        System.out.println(ringtone);
        if (!notification)
            playSound(this, ringtone);

    }

    private void snooze() {
    }

    private void close() {
        if (!notification) {
            activeReminders = Select.from(Reminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();
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
    }

    private void soundController(final Uri uri) {
        Intent intent = new Intent(NotificationBase.this, SnoozeCounter.class);
        startService(intent);

        interval = new CountDownTimer(snoozeTime * 60000, 1000) {

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

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume Called");
        super.onResume();
    }

    private MediaPlayer mMediaPlayer;

    private void playSound(Context context, Uri alert) {
        Log.e(TAG, "play sound called from " + alert);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            //if ()
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 20, 0);
            //if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            Log.e(TAG, "Playing Sound");
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrate)
                vibrator.vibrate(pattern, 0);

            PowerManager.WakeLock wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "elm"
            );
            wakeLock.acquire(alartTime * 60000);

            muteSound(alert);
            //}
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void muteSound(final Uri uri) {
        muter = new CountDownTimer(alartTime * 60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "Time remaining to mite sound " + String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                if (mMediaPlayer != null) {
                    stopSound();
                    //mMediaPlayer.release();
                }
                soundController(uri);
            }
        }.start();
    }

    private void stopSound() {
        Log.e(TAG, "stopSound Called");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            //mMediaPlayer.release();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    private void cancelCountDown() {
        Log.e(TAG, "cancelCountDown Called");
        if (interval != null) {
            interval.cancel();
        }
        if (muter != null) {
            muter.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    protected void destroy() {
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notificationOpen", false);
        editor.commit();
        Log.e(TAG, "onDestroy called");
        if (mMediaPlayer != null) {
            stopSound();
            mMediaPlayer.release();
        }
        cancelCountDown();
        finish();
    }

    public class newNotification extends BroadcastReceiver {
        public static final String ACTION = "new_notification_base";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "newNotification Received");
            finish();
            startActivity(getIntent());
        }
    }
}
