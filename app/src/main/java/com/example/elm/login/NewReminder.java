package com.example.elm.login;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.model.Alarm;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.services.alarm.AlarmCrud;
import com.example.elm.login.services.alarm.ReminderAlarms;
import com.example.elm.login.utils.Constants;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.gson.Gson;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import layout.EarlyReminder;
import layout.ReminderFragment;

public class NewReminder extends AppCompatActivity {

    TextView selectedDate, reminderName, reminderEarly, reminderDescription;
    private ReminderAlarms alarmReceiver;
    private EarlyReceiver earlyReceiver;
    private TitleReceiver titleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //register receivers
        IntentFilter intentFilter = new IntentFilter(earlyReceiver.ACTIION_REP);
        earlyReceiver = new EarlyReceiver();
        registerReceiver(earlyReceiver,intentFilter);

        IntentFilter filter = new IntentFilter(TitleReceiver.ACTIION_REP);
        titleReceiver = new TitleReceiver();
        registerReceiver(titleReceiver, filter);

        String format = "MMM dd yyyy HH:mm";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);

        selectedDate = (TextView) findViewById(R.id.start_date);
        final SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        dateTime.setStepMinutes(1);
        dateTime.setMustBeOnFuture(true);

        selectedDate.setText(simpleDateFormat.format(dateTime.getDate()));

        RelativeLayout title_layout = (RelativeLayout) findViewById(R.id.title_layout);
        RelativeLayout early_layout = (RelativeLayout) findViewById(R.id.early_layout);
        title_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReminderName reminderName = new ReminderName();
                reminderName.show(getFragmentManager(), "Dialog Fragment");
            }
        });

        early_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EarlyReminder().show(getFragmentManager(), "Dialog");
            }
        });


        dateTime.setListener(new SingleDateAndTimePicker.Listener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                System.out.println(date);

                selectedDate.setText(simpleDateFormat.format(date));
            }
        });
    }

    public void updateLabel() {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class TitleReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "add_title";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            onComplete(title);
        }
    }

    public class EarlyReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "add_reminder";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String duration = bundle.getString("duration");
            onCompleted(duration);
        }
    }

    public void onComplete(String title) {
        TextView reminder_title = (TextView) findViewById(R.id.reminder_name_activity);
        reminder_title.setText(title);
    }


    public void onCompleted(String duration) {
        TextView reminder_duration = (TextView) findViewById(R.id.reminder_early_activity);
        reminder_duration.setText(duration);
    }

    public void setReminder(View view) {
        //save alarm
        reminderName = (TextView) findViewById(R.id.reminder_name_activity);
        reminderEarly = (TextView) findViewById(R.id.reminder_early_activity);
        reminderDescription = (EditText) findViewById(R.id.reminder_description);
        SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);

        String format = "MMM dd yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String date_string = simpleDateFormat.format(dateTime.getDate());
        String description = null;

        if (reminderDescription.getText().toString().equals(null)) {
            description = "Its Time!. Hello from myNote";
        } else {
            description = reminderDescription.getText().toString();
        }
        String prior = "null";
        if (!reminderEarly.getText().toString().equals("None")) {
            prior = priorReminder(reminderEarly.getText().toString(), reminderName.getText().toString());
        }
        //save alarm blueprint
        Alarm alarm = new Alarm(0, "actual");
        alarm.save();

        Calendar calendar = Calendar.getInstance();
        int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(dateTime.getDate())) - 1;
        Log.e("month", String.valueOf(month0));
        //calendar.add(Calendar.SECOND, 5);
        calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.MONTH, month0);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(dateTime.getDate())));

        //create alarm -->from a service
        Intent intent = new Intent(NewReminder.this, AlarmCrud.class);
        intent.putExtra("calender", calendar.getTimeInMillis());
        intent.putExtra("nId", 100);
        intent.putExtra("aId", alarm.getId());
        intent.putExtra("title", reminderName.getText().toString());
        intent.putExtra("content", description);
        intent.putExtra("create", true);
        startService(intent);

        Reminder newReminder = new Reminder(
                reminderName.getText().toString(),
                date_string,
                prior,
                reminderDescription.getText().toString(),
                "1",
                Long.parseLong(alarm.getId().toString()));
        newReminder.save();

        //register receiver
        IntentFilter intentFilter = new IntentFilter("DISPLAY_NOTIFICATION");
        alarmReceiver = new ReminderAlarms();
        registerReceiver(alarmReceiver, intentFilter);
        Toast.makeText(getBaseContext(), "Reminder set to  " + dateTime.getDate(), Toast.LENGTH_SHORT).show();

        //update ui and launch relevant fragment
        String dt = new Gson().toJson(newReminder);
        Intent intent1 = new Intent();
        intent1.setAction(ReminderFragment.ReminderBroadcast.NEW_RECEIVER);
        intent1.putExtra("reminder", dt);
        sendBroadcast(intent1);
        int page = 2;

        Intent intent2 = new Intent(this, Navigation.class);
        intent2.putExtra("page", page);
        startActivity(intent2);
    }

    public String priorReminder(String prior, String title) {
        SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        int value = Integer.parseInt(prior.replaceAll("[^0-9]", ""));

        Log.e("check", String.valueOf(value));
        Calendar calendar = Calendar.getInstance();
        int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(dateTime.getDate())) - 1;

        //calendar.add(Calendar.SECOND, 5);
        calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.MONTH, month0);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(dateTime.getDate())));
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

        //save alarm blueprint
        Alarm alarm = new Alarm(0, "prior");
        alarm.save();

        Intent intent = new Intent(NewReminder.this, AlarmCrud.class);
        intent.putExtra("calender", calendar.getTimeInMillis());
        intent.putExtra("nId", 101);
        intent.putExtra("aId", alarm.getId());
        intent.putExtra("title", title);
        intent.putExtra("content", "Prepared? You have  a Reminder in the next " + time);
        intent.putExtra("create", true);
        startService(intent);

        return time;
    }
}
