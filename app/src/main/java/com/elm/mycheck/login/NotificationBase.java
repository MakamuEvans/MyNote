package com.elm.mycheck.login;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elm.mycheck.login.adapter.NotificationsAdapter;
import com.elm.mycheck.login.model.AlarmReminder;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.services.alarm.PlaySound;
import com.elm.mycheck.login.services.alarm.SoundService;
import com.elm.mycheck.login.utils.Constants;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationBase extends AppCompatActivity {
    private static final String TAG = NotificationBase.class.getSimpleName();
    public static boolean activityOpen = false;
    public List<Reminder> activeReminders = new ArrayList<>();
    public List<AlarmReminder> activeNotifications = new ArrayList<>();
    TextView notificationsCount, box_message, box_counter_t, box_timer, text_test, failed_retype, sequence_message, sequence_counterr, s_c;
    EditText text_response;
    ImageView close_button;
    private Button close_b, pause_media, retype;
    int count = 0;
    CountDownTimer interval, muter, box_count_down, sequence_counter;
    private newNotification newNotificationn;
    Boolean notification = false;
    public Vibrator vibrator;
    public String demo_type;
    long[] pattern = {0, 2000, 1000};
    protected PowerManager.WakeLock wakeLock;
    private LinearLayout reminder_action;
    private int alartTime, snoozeTime;
    Boolean vibrate, demo;
    boolean snooze = false;
    int snooze_count;
    private NotificationManager mNotificationManager;

    MediaPlayer mMediaPlayer;
    Boolean snoozeActive = false;
    Boolean fromSnooze = false;
    String quit = "yes";
    private LinearLayout box_puzzle, info_layout, retype_puzzle, sequence_puzzle, sequence_quiz, sequence_ans;
    private CardView c_1, c_2, c_3, c_4, c_5, c_6, c_7, c_8, c_9, s_1, s_2, s_3, s_4;
    private int random;
    private int box_counter = 0;
    private String count_string = null;
    private Boolean isPuzzle = false;
    private String puzzleType = null;


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

        SharedPreferences preferences1 = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.putBoolean("soundAlarm", true);
        editor1.commit();
        //get active notifications
        Log.e("aisee", "Create");

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

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(500);
        init();
        close_button = (ImageView) findViewById(R.id.reminders_close);
        demo = false;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("notification")) {
                notification = intent.getExtras().getBoolean("notification");
                mNotificationManager.cancel(101);
            }

            if (bundle.containsKey("demoPuzzle")) {
                boxPuzzle();
                demo_type = "puzzle";
                demo = true;
            }

            if (bundle.containsKey("fromSnooze")) {
                fromSnooze = true;
            }

            if (bundle.containsKey("demoRetype")) {
                retypePuzzle();
                demo = true;
            }
            if (bundle.containsKey("demoSequence")) {
                sequencePuzzle();
                demo_type = "sequence";
                demo = true;
            }
            if (bundle.containsKey("snooze")) {
                snooze_count = getSharedPreferences("myPref", 0).getInt("snooze_count", 0);
                snooze_count++;
                snoozeActive = true;
                SharedPreferences.Editor editor2 = preferences.edit();
                editor2.putInt("snooze_count", snooze_count);
                editor2.commit();
                if (snooze_count > 4)
                    snooze = true;

            }
        }
        if (demo) {
            close_button.setVisibility(View.VISIBLE);
            isPuzzle = true;
        } else {
            close_button.setVisibility(View.INVISIBLE);
        }

        if (notification)
            close_button.setVisibility(View.VISIBLE);

        if (fromSnooze) {
            stopAlarm();
            Log.e("stoppin", "called");
        }

        if (!snoozeActive) {
            SharedPreferences.Editor editor2 = preferences.edit();
            editor2.putInt("snooze_count", 0);
            editor2.commit();
            snooze_count = 0;
        }


        reminder_action = (LinearLayout) findViewById(R.id.reminder_action);


        if (!notification) {
            reminder_action.setVisibility(View.VISIBLE);
            activeReminders = Select.from(Reminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();

            Log.e("oyole", String.valueOf(activeReminders.size()));
            if (activeReminders.size() == 0) {
                //return;
                //onDestroy();
               /* quit = true;
                finish();*/
            }

            for (Reminder reminder : activeReminders) {
                Reminder a_reminder = Reminder.findById(Reminder.class, reminder.getId());
                Log.e("haha", a_reminder.toString());
                if (!a_reminder.getPuzzle().equals("None")) {
                    isPuzzle = true;
                    puzzleType = a_reminder.getPuzzle();
                }
            }
        } else {
            activeNotifications = Select.from(AlarmReminder.class)
                    .where(Condition.prop("active").eq("1"))
                    .list();

            for (AlarmReminder reminder : activeNotifications
                    ) {
                activeReminders.add(Reminder.findById(Reminder.class, reminder.getReminderid()));
            }
        }


        //stopService(new Intent(NotificationBase.this, PlaySound.class));

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

        pause_media = (Button) findViewById(R.id.snooze_alarm);
        close_b = (Button) findViewById(R.id.close_alarm);

        close_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close(); //call service instead
                mNotificationManager.cancel(500);
                if (isPuzzle) {
                    close_button.setVisibility(View.GONE);
                    if (puzzleType.equals("puzzle"))
                        boxPuzzle();
                    if (puzzleType.equals("retype"))
                        retypePuzzle();
                    if (puzzleType.equals("sequence"))
                        sequencePuzzle();
                } else {
                    cancelCountDown();
                    stopAlarm();
                    clearActives();
                    quit = "no";
                    finish();
                }
            }
        });
        if (isPuzzle)
            pause_media.setVisibility(View.GONE);

        pause_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationManager.cancel(500);
                snoozeAlarm();
                quit = "no";
                finish();


                //mMediaPlayer.stop();
            }
        });

        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationManager.cancel(500);
                stopAlarm();
                quit = "no";
                finish();
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        alartTime = Integer.parseInt(sp.getString("ring_duration", "1"));
        snoozeTime = Integer.parseInt(sp.getString("snooze_duration", "5"));
        vibrate = sp.getBoolean("notifications_new_message_vibrate", true);

        Uri ringtone = Uri.parse(sp.getString("notifications_new_message_ringtone", "content://settings/system/ringtone"));
        System.out.println(ringtone);
        if (!notification) {
            Log.e("playSound", "Playing...");
            playSound(this, ringtone);
        } else {
            close_b.setVisibility(View.GONE);
            pause_media.setVisibility(View.GONE);
        }

    }

    private void clearActives() {
        if (!notification) {
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
    }

    private void startAlarm() {
        stopAlarm();
        Intent intent = new Intent(NotificationBase.this, SoundService.class);
        intent.putExtra("startAlarm", true);
        startService(intent);
    }

    private void snoozeAlarm() {
        cancelCountDown();
        if (wakeLock.isHeld())
            wakeLock.release();
        stopAlarm();
        //createNotification("Reminder in Snooze Mode");
        if (!snooze) {
            Intent intent = new Intent(NotificationBase.this, SoundService.class);
            intent.putExtra("snoozeAlarm", true);
            startService(intent);
        } else {
            createNotification("You Missed an Alarm. Click to View");
        }
    }

    private void stopAlarm() {
        stopService(new Intent(NotificationBase.this, SoundService.class));
    }


    private void init() {
        //initialize layout
        info_layout = (LinearLayout) findViewById(R.id.alarm_info);
        box_puzzle = (LinearLayout) findViewById(R.id.puzzle_box);
        sequence_puzzle = (LinearLayout) findViewById(R.id.puzzle_sequence);
        retype_puzzle = (LinearLayout) findViewById(R.id.puzzle_retype);
        sequence_quiz = (LinearLayout) findViewById(R.id.sequence_quiz);
        sequence_ans = (LinearLayout) findViewById(R.id.sequence_ans);

        box_counter_t = (TextView) findViewById(R.id.success_counter);
        box_message = (TextView) findViewById(R.id.box_message);
        box_timer = (TextView) findViewById(R.id.box_timer);
        sequence_message = (TextView) findViewById(R.id.sequence_message);
        sequence_counterr = (TextView) findViewById(R.id.sequence_counterr);

        sequence_message.setText("Touch on a Shape");

        Log.e("count", String.valueOf(box_counter));
        count_string = box_counter + " of 20";
        box_counter_t.setText(count_string);
        box_message.setText("Touch White Box");

        c_1 = (CardView) findViewById(R.id.p_1);
        c_2 = (CardView) findViewById(R.id.p_2);
        c_3 = (CardView) findViewById(R.id.p_3);
        c_4 = (CardView) findViewById(R.id.p_4);
        c_5 = (CardView) findViewById(R.id.p_5);
        c_6 = (CardView) findViewById(R.id.p_6);
        c_7 = (CardView) findViewById(R.id.p_7);
        c_8 = (CardView) findViewById(R.id.p_8);
        c_9 = (CardView) findViewById(R.id.p_9);

        s_1 = (CardView) findViewById(R.id.s_1);
        s_2 = (CardView) findViewById(R.id.s_2);
        s_3 = (CardView) findViewById(R.id.s_3);
        s_4 = (CardView) findViewById(R.id.s_4);

        text_test = (TextView) findViewById(R.id.text_test);
        failed_retype = (TextView) findViewById(R.id.failed_retype);
        text_response = (EditText) findViewById(R.id.text_response);
        retype = (Button) findViewById(R.id.retype_button);
        s_c = (TextView) findViewById(R.id.s_c);
        quit = "ok";
    }

    @Override
    protected void onPause() {
        Log.e("aisee", "pause");
        if (quit.equals("ok") && !notification) {
            createNotification("You have an Active Reminder.");
        }
        if (notification)
            finish();

        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e("aisee", "onResume");
        if (demo){

        }else if (demo){
            if (demo_type.equalsIgnoreCase("puzzle"))
                boxPuzzle();
            if (demo_type.equalsIgnoreCase("sequence"))
                sequencePuzzle();
        }else if (isPuzzle) {
           /* close_button.setVisibility(View.GONE);
            if (puzzleType.equals("puzzle"))
                boxPuzzle();
            if (puzzleType.equals("sequence"))
                sequencePuzzle();*/
        }
        super.onResume();
         //h
    }

    private void createNotification(String message) {
        Intent resultIntent = new Intent(this, NotificationBase.class);
        resultIntent.putExtra("fromSnooze", true);
        Intent openReminder = new Intent(this, NotificationBase.class);
        resultIntent.putExtra("fromSnooze", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openReminder, PendingIntent.FLAG_UPDATE_CURRENT);

        //prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo)
                .setContentTitle("Alarm")
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

    //public PowerManager.WakeLock wakeLock;

    private void playSound(Context context, Uri alert) {
        Log.e(TAG, "sn " + snooze_count);


        startAlarm();

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "elm"
        );

        if (alartTime == 30) {
            alartTime = 30000;
        } else {
            alartTime = alartTime * 60000;
        }

        if (isPuzzle) {
            alartTime = 60 * 60000;
            snooze=true;
        }

        wakeLock.acquire(alartTime);

        muteSound();
    }

    public void muteSound() {
        Log.e(TAG, "mute Sound Called");

        muter = new CountDownTimer(alartTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "Time remaining to mute sound " + String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {

                /*Intent intent = new Intent(NotificationBase.this, PlaySound.class);
                intent.putExtra("quit", false);
                intent.putExtra("play", false);
                intent.putExtra("snooze", true);
                intent.putExtra("notif", true);
                intent.putExtra("vibrate", vibrate);
                intent.putExtra("nType", notification);
                startService(intent);*/

                //stopService(new Intent(NotificationBase.this, PlaySound.class));

                snoozeAlarm();


            }
        }.start();
    }


    private void cancelCountDown() {
        Log.e(TAG, "cancelCountDown Called");
        if (interval != null) {
            interval.cancel();
        }
        if (muter != null) {
            muter.cancel();
        }
        if (box_count_down != null) {
            box_count_down.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        if (newNotificationn != null) {
            unregisterReceiver(newNotificationn);
        }
        if (muter != null)
            muter.cancel();
        super.onDestroy();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    private void boxPuzzle() {
        //initialize
        info_layout.setVisibility(View.GONE);
        box_puzzle.setVisibility(View.VISIBLE);
        retype_puzzle.setVisibility(View.GONE);
        sequence_puzzle.setVisibility(View.GONE);

        box_counter = 0;
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        boxRandomize();


        c_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_1);
                if (random == 1) {
                    c_1.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_2);
                if (random == 2) {
                    c_2.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);

            }
        });
        c_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_3);
                if (random == 3) {
                    c_3.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_4);
                if (random == 4) {
                    c_4.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_5);
                if (random == 5) {
                    c_5.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_6);
                if (random == 6) {
                    c_6.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_7);
                if (random == 7) {
                    c_7.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_8);
                if (random == 8) {
                    c_8.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });
        c_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(c_9);
                if (random == 9) {
                    c_9.setCardBackgroundColor(00555555);
                    box_counter++;
                    boxRandomize();
                    box_message.setText("Success!");
                    box_message.setTextColor(getResources().getColor(R.color.basil));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else {
                    box_counter = 0;
                    box_message.setText("Failed");
                    box_message.setTextColor(getResources().getColor(R.color.red));
                    box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    //warn
                }

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        });

    }

    public void boxRandomize() {
        Random r = new Random();
        random = (r.nextInt(9 - 1 + 1) + 1);

        if (box_count_down != null)
            box_count_down.cancel();

        if (box_counter == 20) {
            quit = "no";
            stopAlarm();
            if (!demo)
                clearActives();
            finish();
        } else {
            if (random == 1) {
                c_1.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_1.setCardBackgroundColor(00555555);
            }
            if (random == 2) {
                c_2.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_2.setCardBackgroundColor(00555555);
            }

            if (random == 3) {
                c_3.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_3.setCardBackgroundColor(00555555);
            }
            if (random == 4) {
                c_4.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_4.setCardBackgroundColor(00555555);
            }
            if (random == 5) {
                c_5.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_5.setCardBackgroundColor(00555555);
            }
            if (random == 6) {
                c_6.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_6.setCardBackgroundColor(00555555);
            }
            if (random == 7) {
                c_7.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_7.setCardBackgroundColor(00555555);
            }
            if (random == 8) {
                c_8.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_8.setCardBackgroundColor(00555555);
            }
            if (random == 9) {
                c_9.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
            } else {
                c_9.setCardBackgroundColor(00555555);
            }

            fiveSec();
        }

    }

    private void fiveSec() {
        box_count_down = new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int remaining = (int) (millisUntilFinished / 1000);
                box_timer.setText(remaining + " Sec");
            }

            @Override
            public void onFinish() {
                box_counter = 0;
                box_message.setText("Failed");
                box_message.setTextColor(getResources().getColor(R.color.red));
                box_message.setBackgroundColor(getResources().getColor(R.color.colorWhite));

                count_string = box_counter + " of 20";
                box_counter_t.setText(count_string);
            }
        }.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void retypePuzzle() {
        info_layout.setVisibility(View.GONE);
        box_puzzle.setVisibility(View.GONE);
        retype_puzzle.setVisibility(View.VISIBLE);
        failed_retype.setVisibility(View.GONE);
        sequence_puzzle.setVisibility(View.GONE);

        Random r = new Random();
        random = (r.nextInt(2 - 1 + 1) + 1);
        if (random == 1)
            text_test.setText(Constants.text1);
        if (random == 2)
            text_test.setText(Constants.text2);
        //set text

        //get response
        retype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failed_retype.setVisibility(View.GONE);
                Log.e("text1", text_test.getText().toString());
                if (text_test.getText().toString().equals(text_response.getText().toString())) {
                    stopService(new Intent(NotificationBase.this, PlaySound.class));
                    quit = "no";
                    stopAlarm();
                    if (!demo)
                        clearActives();
                    finish();
                } else {
                    //warn
                    failed_retype.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private ImageView image_sequence;
    private int imageCount = 0;
    private int ansCount = 0;
    private int[] active = new int[4];

    private void successSequence() {
        stopService(new Intent(NotificationBase.this, PlaySound.class));
        quit = "no";
        stopAlarm();
        if (!demo)
            clearActives();
        finish();
    }

    private void sequencePuzzle() {
        info_layout.setVisibility(View.GONE);
        box_puzzle.setVisibility(View.GONE);
        retype_puzzle.setVisibility(View.GONE);
        sequence_puzzle.setVisibility(View.VISIBLE);
        sequence_quiz.setVisibility(View.VISIBLE);
        sequence_ans.setVisibility(View.GONE);
        image_sequence = (ImageView) findViewById(R.id.image_sequence);

        String ans_message = ansCount + " of 4";
        sequence_counterr.setText(ans_message);

        s_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(s_1);
                if (active[ansCount] == 1) {
                    //success
                    ansCount++;
                    String ans_message = ansCount + " of 4";
                    sequence_counterr.setText(ans_message);
                    sequence_message.setText("Success!");
                    sequence_message.setTextColor(getResources().getColor(R.color.basil));
                    if (ansCount == 4)
                        successSequence();
                } else {
                    imageCount = 0;
                    ansCount = 0;
                    sequence_message.setText("Failed!...Restarting");
                    sequence_message.setTextColor(getResources().getColor(R.color.red));
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sequencePuzzle();
                        }
                    }, 2000);
                }
            }
        });

        s_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(s_2);
                if (active[ansCount] == 2) {
                    ansCount++;
                    String ans_message = ansCount + " of 4";
                    sequence_counterr.setText(ans_message);
                    sequence_message.setText("Success!");
                    sequence_message.setTextColor(getResources().getColor(R.color.basil));
                    if (ansCount == 4)
                        successSequence();
                    //success
                } else {
                    imageCount = 0;
                    ansCount = 0;
                    sequence_message.setText("Failed!...Restarting");
                    sequence_message.setTextColor(getResources().getColor(R.color.red));
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sequencePuzzle();
                        }
                    }, 2000);
                }
            }
        });

        s_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(s_3);
                if (active[ansCount] == 3) {
                    //success
                    ansCount++;
                    String ans_message = ansCount + " of 4";
                    sequence_counterr.setText(ans_message);
                    sequence_message.setText("Success!");
                    sequence_message.setTextColor(getResources().getColor(R.color.basil));
                    if (ansCount == 4)
                        successSequence();
                } else {
                    imageCount = 0;
                    ansCount = 0;
                    sequence_message.setText("Failed!...Restarting");
                    sequence_message.setTextColor(getResources().getColor(R.color.red));
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sequencePuzzle();
                        }
                    }, 2000);
                }
            }
        });

        s_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAnimation(s_4);
                if (active[ansCount] == 4) {
                    //success
                    ansCount++;
                    String ans_message = ansCount + " of 4";
                    sequence_counterr.setText(ans_message);
                    sequence_message.setText("Success!");
                    sequence_message.setTextColor(getResources().getColor(R.color.basil));
                    if (ansCount == 4)
                        successSequence();
                } else {
                    imageCount = 0;
                    ansCount = 0;
                    sequence_message.setText("Failed!...Restarting");
                    sequence_message.setTextColor(getResources().getColor(R.color.red));
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sequencePuzzle();
                        }
                    }, 2000);
                }
            }
        });

        //shiffle items
        shuffleImage();
    }

    private void shuffleImage() {
        Random r = new Random();
        random = (r.nextInt(4 - 1 + 1) + 1);

        imageCount++;
        Log.e("iczz", String.valueOf(imageCount));

        if (sequence_counter != null)
            sequence_counter.cancel();

        if (imageCount > 4) {
            sequence_quiz.setVisibility(View.GONE);
            sequence_ans.setVisibility(View.VISIBLE);
            s_c.setVisibility(View.GONE);
            return;
        } else {
            s_c.setVisibility(View.VISIBLE);
        }

        s_c.setText(imageCount + " of 4");


        //active.
        if (imageCount == 1) active[0] = random;
        if (imageCount == 2) active[1] = random;
        if (imageCount == 3) active[2] = random;
        if (imageCount == 4) active[3] = random;
        Log.e("random", String.valueOf(random));

        if (random == 1) {
            image_sequence.setImageResource(R.drawable.one);
        } else if (random == 2) {
            image_sequence.setImageResource(R.drawable.two);
        } else if (random == 3) {
            image_sequence.setImageResource(R.drawable.three);
        } else if (random == 4) {
            image_sequence.setImageResource(R.drawable.four);
        }
        setFadeAnimation(image_sequence);

        threeSec();
    }

    private void setFadeAnimation(ImageView imageView) {
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        imageView.startAnimation(animation);
    }

    private void setCardAnimation(CardView cardView) {
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        cardView.startAnimation(animation);
    }

    private void threeSec() {
        sequence_counter = new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("randomc", String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                shuffleImage();
            }
        }.start();
    }
}
