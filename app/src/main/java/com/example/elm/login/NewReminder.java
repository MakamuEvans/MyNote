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

import com.example.elm.login.model.Reminder;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import layout.EarlyReminder;
import layout.ReminderFragment;

public class NewReminder extends AppCompatActivity implements ReminderName.OnCompleteListener, EarlyReminder.OnCompleteListener {

    TextView selectedDate, reminderName, reminderEarly;
    private AlarmReceiver alarmReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    public void updateLabel(){

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

    @Override
    public void onComplete(String title) {
        TextView reminder_title = (TextView) findViewById(R.id.reminder_name_activity);
        reminder_title.setText(title);
    }

    @Override
    public void onCompleted(String duration) {
        TextView reminder_duration = (TextView) findViewById(R.id.reminder_early_activity);
        reminder_duration.setText(duration+" before");
    }

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("huh" , "alarm");
            Bundle bundle = intent.getExtras();
            String notificationTitle = bundle.getString("title");
            Intent resultIntent = new Intent(context, AddNote.class);
            Intent newReminder = new Intent(context, NewReminder.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,newReminder, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.logo)
                            .setContentTitle(notificationTitle)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .addAction(R.mipmap.ic_action_check_free, "View", pendingIntent)
                            .addAction(R.mipmap.ic_action_close, "Close", pendingIntent)
                            .setContentText("You have a new Reminder. Check it out!");
// Creates an explicit intent for an Activity in your app

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(AddNote.class);
// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
//
            mNotificationManager.notify(100, mBuilder.build());
        }
    }


    public void setReminder(View view){
        //save alarm
        reminderName = (TextView) findViewById(R.id.reminder_name_activity);
        reminderEarly = (TextView) findViewById(R.id.reminder_early_activity);
        SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);

        String format = "MMM dd yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String date_string = simpleDateFormat.format(dateTime.getDate());
        Reminder newReminder = new Reminder(reminderName.getText().toString(),
                date_string,
                date_string,
                reminderEarly.getText().toString(),
                "Active");
        newReminder.save();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent10 = new Intent("DISPLAY_NOTIFICATION");
        intent10.putExtra("title", reminderName.getText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, intent10,PendingIntent.FLAG_UPDATE_CURRENT);

        Log.e("huh", new SimpleDateFormat("d", Locale.ENGLISH).format(dateTime.getDate()));
        Calendar calendar = Calendar.getInstance();
        int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(dateTime.getDate())) - 1;
        Log.e("month", String.valueOf(month0));
        //calendar.add(Calendar.SECOND, 5);
        calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.MONTH, month0);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(dateTime.getDate())));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(dateTime.getDate())));
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);

        IntentFilter intentFilter = new IntentFilter("DISPLAY_NOTIFICATION");
        alarmReceiver = new NewReminder.AlarmReceiver();
        registerReceiver(alarmReceiver, intentFilter);
        Toast.makeText(getBaseContext(), "Reminder set to  "+ dateTime.getDate(), Toast.LENGTH_SHORT).show();

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
}
