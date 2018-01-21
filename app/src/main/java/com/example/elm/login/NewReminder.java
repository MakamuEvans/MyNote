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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.model.AlarmReminder;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.services.alarm.AlarmCrud;
import com.example.elm.login.services.alarm.ReminderAlarms;
import com.example.elm.login.utils.Constants;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import layout.EarlyReminder;
import layout.ReminderFragment;

public class NewReminder extends AppCompatActivity {

    TextView selectedDate, reminderName, reminderEarly, reminderDescription;
    private EarlyReceiver earlyReceiver;
    private TitleReceiver titleReceiver;
    private Boolean repeating =false;
    private View view;
    private AdView adView;
    private Spinner spinner;
    private TextView tsunday,tmonday,ttuesday,twednesday,tthursday,tfriday,tsaturday;
    private boolean ssunday=false,smonday=false,stuesday=false,swednesday=false,sthursday=false,sfriday=false,ssaturday=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        adView = (AdView) findViewById(R.id.ReminderadView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.setVisibility(View.GONE);
            }
        });


        //initialize repeaters
        tmonday = (TextView) findViewById(R.id.repeat_monday);
        ttuesday = (TextView) findViewById(R.id.repeat_tuesday);
        twednesday = (TextView) findViewById(R.id.repeat_wednesday);
        tthursday = (TextView) findViewById(R.id.repeat_thursday);
        tfriday = (TextView) findViewById(R.id.repeat_friday);
        tsaturday = (TextView) findViewById(R.id.repeat_saturday);
        tsunday = (TextView) findViewById(R.id.repeat_sunday);
        repeatingAlarm();

        //register receivers
        IntentFilter intentFilter = new IntentFilter(earlyReceiver.ACTIION_REP);
        earlyReceiver = new EarlyReceiver();
        registerReceiver(earlyReceiver,intentFilter);

        IntentFilter filter = new IntentFilter(TitleReceiver.ACTIION_REP);
        titleReceiver = new TitleReceiver();
        registerReceiver(titleReceiver, filter);

        String format = "MMM dd yyyy HH:mm";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String format2 = "HH:mm";
        final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format2, Locale.ENGLISH);

        selectedDate = (TextView) findViewById(R.id.start_date);
        final SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        final SingleDateAndTimePicker time_picker = (SingleDateAndTimePicker) findViewById(R.id.time_picker);
        dateTime.setStepMinutes(1);
        time_picker.setDisplayDays(false);
        time_picker.setMustBeOnFuture(false);
        time_picker.setStepMinutes(1);
        dateTime.setMustBeOnFuture(true);

        final RelativeLayout repeat_mode = (RelativeLayout) findViewById(R.id.repeat_mode);
        spinner = (Spinner) findViewById(R.id.alarm_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_text = spinner.getSelectedItem().toString();
                if (selected_text.equals("One Time")){
                    dateTime.setVisibility(View.VISIBLE);
                    time_picker.setVisibility(View.GONE);
                    repeating = false;
                    repeat_mode.setVisibility(View.GONE);
                }else {
                    dateTime.setVisibility(View.GONE);
                    repeating = true;
                    time_picker.setVisibility(View.VISIBLE);
                    repeat_mode.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        time_picker.setListener(new SingleDateAndTimePicker.Listener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectedDate.setText(simpleDateFormat2.format(date));
            }
        });


        dateTime.setListener(new SingleDateAndTimePicker.Listener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectedDate.setText(simpleDateFormat.format(date));
            }
        });
    }

    public void repeatingAlarm() {
        tmonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smonday = smonday == false ? true:false;
                changeTextColor(tmonday, smonday);
            }
        });
        ttuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stuesday = stuesday == false ? true:false;
                changeTextColor(ttuesday, stuesday);
            }
        });
        twednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swednesday = swednesday ==false ? true:false;
                changeTextColor(twednesday, swednesday);
            }
        });
        tthursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sthursday = sthursday == false ? true:false;
                changeTextColor(tthursday, sthursday);
            }
        });
        tfriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sfriday = sfriday == false ? true:false;
                changeTextColor(tfriday, sfriday);
            }
        });
        tsaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssaturday = ssaturday ==false? true:false;
                changeTextColor(tsaturday, ssaturday);
            }
        });
        tsunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssunday = ssunday ==false? true:false;
                changeTextColor(tsunday,ssunday);
            }
        });
    }

    public void changeTextColor(TextView textView, Boolean status){
        if (status){
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else {
            textView.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.set_reminder_setting:
                try {
                    setReminder(view);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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

    /**
     * put all the dates of a repeating alarm into an array
     * @return
     */
    public String repeatDates(){
        List<String> alarm_days = new ArrayList<>();
        if (ssunday || smonday || stuesday || swednesday || sthursday || sfriday ||ssaturday){
            if (ssunday){
                alarm_days.add("Sunday");
            }
            if (smonday){
                alarm_days.add("Monday");
            }
            if (stuesday){
                alarm_days.add("Tuesday");
            }
            if (swednesday){
                alarm_days.add("Wednesday");
            }
            if (sthursday){
                alarm_days.add("Thursday");
            }
            if (sfriday){
                alarm_days.add("Friday");
            }
            if (ssaturday){
                alarm_days.add("Saturday");
            }

            String merged = android.text.TextUtils.join(",", alarm_days);
            return merged;
        }else {
            return null;
        }
    }

    /**
     * set reminder
     * @param view
     * @throws ParseException
     */
    public void setReminder(View view) throws ParseException {
        //initialize data
        reminderName = (TextView) findViewById(R.id.reminder_name_activity);
        reminderEarly = (TextView) findViewById(R.id.reminder_early_activity);
        reminderDescription = (EditText) findViewById(R.id.reminder_description);
        SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        SingleDateAndTimePicker time_picker = (SingleDateAndTimePicker) findViewById(R.id.time_picker);

        //is title set?
        if (reminderName.getText().toString().isEmpty() || reminderName.getText().toString().equals("None")){
            MDToast.makeText(getBaseContext(), "Alarm Title should be filled", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            return;
        }

        //which type of alarm?
        if (repeating){
            if (repeatDates() == null){
                MDToast.makeText(getBaseContext(), "No week days selected for repeating alarm!", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                return;
            }
        }

        //date format
        String format = "MMM dd yyyy HH:mm";
        String format2 = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format2, Locale.ENGLISH);
        String date_string;
        Date date;

        //get date depending on ReminderType
        if (!repeating){
            date_string = simpleDateFormat.format(dateTime.getDate());
            date = simpleDateFormat.parse(simpleDateFormat.format(dateTime.getDate()));
        }else {
            date_string = simpleDateFormat2.format(time_picker.getDate());
            date = simpleDateFormat.parse(simpleDateFormat.format(time_picker.getDate()));
        }
        String description = null;

        //Sort out description
        if (reminderDescription.getText().toString().equals(null)) {
            description = "Its Time!. Hello from myNote";
        } else {
            description = reminderDescription.getText().toString();
        }


        Calendar calendar = Calendar.getInstance();
        int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));

        if (!repeating){
            calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
            calendar.set(Calendar.MONTH, month0);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));
        }else {
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));

            if (calendar1.getTimeInMillis() >= calendar.getTimeInMillis()){
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Log.e("newTime", String.valueOf(calendar.getTime()));
            }
        }

        //early reminder requested?
        String prior = reminderEarly.getText().toString();
        Boolean priorSet = false;
        if (!reminderEarly.getText().toString().equals("None")) {
            priorSet = true;
        }

        //current_date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date today = new Date();

        //save alarm data to db
        Reminder newReminder = new Reminder(
                reminderName.getText().toString(),
                date_string,
                priorSet,
                reminderDescription.getText().toString(),
                true,
                false,
                repeatDates(),
                dateFormat.format(today),
                dateFormat.format(today));
        newReminder.save();

        if (priorSet){
            prior = priorReminder(reminderEarly.getText().toString(), reminderName.getText().toString(), newReminder.getId());
        }

        //create alarm -->from a service
        Intent intent = new Intent(NewReminder.this, AlarmCrud.class);
        intent.putExtra("calender", calendar.getTimeInMillis());
        intent.putExtra("nId", Constants.actualReminder);
        intent.putExtra("repeat", repeating);
        intent.putExtra("aId", newReminder.getId());
        intent.putExtra("title", reminderName.getText().toString());
        intent.putExtra("content", description);
        intent.putExtra("create", true);
        startService(intent);

        //Toast.makeText(getBaseContext(), "Reminder set to  " + date_string, Toast.LENGTH_LONG).show();
        MDToast.makeText(getBaseContext(), "Reminder set to  "+ date_string, MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();

        //update ui and launch relevant fragment
        String dt = new Gson().toJson(newReminder);
        Intent intent1 = new Intent();
        intent1.setAction(ReminderFragment.ReminderBroadcast.NEW_RECEIVER);
        intent1.putExtra("reminder", dt);
        sendBroadcast(intent1);

        Intent intent2 = new Intent(this, Navigation.class);
        intent2.putExtra("page", Constants.REMINDER_TAB);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
    }

    public String priorReminder(String prior, String title, Long id) throws ParseException {
        Log.e("Atherere", "Prior called");
        SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        SingleDateAndTimePicker time_picker = (SingleDateAndTimePicker) findViewById(R.id.time_picker);

        String format = "MMM dd yyyy HH:mm";
        String format2 = "HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format2, Locale.ENGLISH);
        String date_string;
        Date date;
        if (!repeating){
            date_string = simpleDateFormat.format(dateTime.getDate());
            date = simpleDateFormat.parse(simpleDateFormat.format(dateTime.getDate()));
        }else {
            date_string = simpleDateFormat2.format(time_picker.getDate());
            date = simpleDateFormat.parse(simpleDateFormat.format(time_picker.getDate()));
        }
        int value = Integer.parseInt(prior.replaceAll("[^0-9]", ""));


        Calendar calendar = Calendar.getInstance();
        int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
        Log.e("month", String.valueOf(month0));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));

        if (!repeating){
            calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
            calendar.set(Calendar.MONTH, month0);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));
        }else {
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));

            if (calendar1.getTimeInMillis() >= calendar.getTimeInMillis()){
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
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
        Log.e("calendar", String.valueOf(calendar.get(Calendar.MINUTE)));

        //save reminder
        AlarmReminder alarmReminder = new AlarmReminder(
                id,
                prior,
                false
        );
        alarmReminder.save();

        //prepare reminder
        Intent intent = new Intent(NewReminder.this, AlarmCrud.class);
        intent.putExtra("calender", calendar.getTimeInMillis());
        intent.putExtra("nId", Constants.earlyReminder);
        intent.putExtra("aId", alarmReminder.getId());
        intent.putExtra("repeat", repeating);
        intent.putExtra("title", title);
        intent.putExtra("content", "Ready? You have  a Reminder in the next " + time);
        intent.putExtra("create", true);
        startService(intent);

        Log.e("Atherere", "done");
        return prior;
    }
}
