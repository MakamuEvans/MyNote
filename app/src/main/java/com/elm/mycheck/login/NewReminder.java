package com.elm.mycheck.login;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.elm.mycheck.login.model.AlarmReminder;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.services.alarm.AlarmCrud;
import com.elm.mycheck.login.utils.Constants;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.tooltip.Tooltip;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import layout.EarlyReminder;
import layout.ReminderFragment;

public class NewReminder extends AppCompatActivity {

    TextView selectedDate, reminderName, reminderEarly, reminderDescription, puzzle_name_activity;
    private EarlyReceiver earlyReceiver;
    private TitleReceiver titleReceiver;
    private PuzzleReceiver puzzleReceiver;
    private Boolean repeating =false;
    private String puzzle = "None";
    private View view;
    private AdView adView;
    private Spinner spinner;
    private TextView tsunday,tmonday,ttuesday,twednesday,tthursday,tfriday,tsaturday;
    private boolean ssunday=false,smonday=false,stuesday=false,swednesday=false,sthursday=false,sfriday=false,ssaturday=false;

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
            setTheme(R.style.AppTheme_Tangarine);
        if (theme.equalsIgnoreCase("banana"))
            setTheme(R.style.AppTheme_Banana);
        if (theme.equalsIgnoreCase("basil"))
            setTheme(R.style.AppTheme_Basil);
        if (theme.equalsIgnoreCase("sage"))
            setTheme(R.style.AppTheme_Sage);
        if (theme.equalsIgnoreCase("peacock"))
            setTheme(R.style.AppTheme_Peacock);
        if (theme.equalsIgnoreCase("blueberry"))
            setTheme(R.style.AppTheme_BlueBerry);
        if (theme.equalsIgnoreCase("lavender"))
            setTheme(R.style.AppTheme_Lavender);
        if (theme.equalsIgnoreCase("grape"))
            setTheme(R.style.AppTheme_Grape);
        if (theme.equalsIgnoreCase("flamingo"))
            setTheme(R.style.AppTheme_Flamingo);
        if (theme.equalsIgnoreCase("graphite"))
            setTheme(R.style.AppTheme_Graphite);


        setContentView(R.layout.activity_new_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        setTitle("New Reminder");
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

        permission(view);

        reminderName = (TextView) findViewById(R.id.reminder_name_activity);
        reminderEarly = (TextView) findViewById(R.id.reminder_early_activity);
        reminderDescription = (EditText) findViewById(R.id.reminder_description);

        reminderDescription.setFocusable(false);


        //initialize repeaters
        tmonday = (TextView) findViewById(R.id.repeat_monday);
        ttuesday = (TextView) findViewById(R.id.repeat_tuesday);
        twednesday = (TextView) findViewById(R.id.repeat_wednesday);
        tthursday = (TextView) findViewById(R.id.repeat_thursday);
        tfriday = (TextView) findViewById(R.id.repeat_friday);
        tsaturday = (TextView) findViewById(R.id.repeat_saturday);
        tsunday = (TextView) findViewById(R.id.repeat_sunday);
        repeatingAlarm();

        puzzle_name_activity = (TextView) findViewById(R.id.puzzle_name_activity);
        puzzle_name_activity.setText(puzzle);

        //register receivers
        IntentFilter intentFilter = new IntentFilter(earlyReceiver.ACTIION_REP);
        earlyReceiver = new EarlyReceiver();
        registerReceiver(earlyReceiver,intentFilter);

        IntentFilter filter = new IntentFilter(TitleReceiver.ACTIION_REP);
        titleReceiver = new TitleReceiver();
        registerReceiver(titleReceiver, filter);

        IntentFilter filter1 = new IntentFilter(PuzzleReceiver.ACTIION_REP);
        puzzleReceiver = new PuzzleReceiver();
        registerReceiver(puzzleReceiver, filter1);

        String format = "MMM dd yyyy HH:mm";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String format2 = "HH:mm";
        final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format2, Locale.ENGLISH);

        selectedDate = (TextView) findViewById(R.id.start_date);
        final SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        final SingleDateAndTimePicker time_picker = (SingleDateAndTimePicker) findViewById(R.id.time_picker);
        dateTime.setStepMinutes(1);
        dateTime.setIsAmPm(false);
        time_picker.setDisplayDays(false);
        time_picker.setMustBeOnFuture(false);
        time_picker.setStepMinutes(1);
        time_picker.setIsAmPm(false);
        dateTime.setMustBeOnFuture(true);

        final RelativeLayout repeat_mode = (RelativeLayout) findViewById(R.id.repeat_mode);
        spinner = (Spinner) findViewById(R.id.alarm_type);

        new Tooltip.Builder(spinner)
                .setText("Click here to modify Alarm Type")
                .setBackgroundColor(getResources().getColor(R.color.colorAccentTomato))
                .setTextColor(getResources().getColor(R.color.colorWhite))
                .setCancelable(true)
                .show();
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
        RelativeLayout puzzle_layout = (RelativeLayout) findViewById(R.id.puzzle_layout);
        puzzle_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewReminder.this, SelectPuzzle.class);
                startActivity(intent);
            }
        });
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

    @Override
    protected void onDestroy() {
        if (earlyReceiver != null)
            unregisterReceiver(earlyReceiver);
        if (titleReceiver != null)
            unregisterReceiver(titleReceiver);
        if (puzzleReceiver != null)
            unregisterReceiver(puzzleReceiver);
        super.onDestroy();
    }

    public static final int STORAGE = 1234;
    public void permission(View view){
        if (ContextCompat.checkSelfPermission(NewReminder.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(NewReminder.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewReminder.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE);
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MDToast.makeText(NewReminder.this,"Reminder requires read to external Storage",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
                 this.finish();
                }
                return;
            }
        }
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

    @Override
    protected void onPause() {
        super.onPause();
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

    public class PuzzleReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "add_puzzle";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            puzzle = title;
            puzzle_name_activity.setText(puzzle);
            //onComplete(title);
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
           // date = simpleDateFormat.parse(simpleDateFormat.format(dateTime.getDate()));
        }else {
            date_string = simpleDateFormat.format(time_picker.getDate());
           // date = simpleDateFormat.parse(simpleDateFormat.format(time_picker.getDate()));
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
                puzzle,
                true,
                false,
                repeatDates(),
                "",
                0,
                dateFormat.format(today),
                dateFormat.format(today));
        newReminder.save();

        if (priorSet){
            AlarmReminder alarmReminder = new AlarmReminder(
                    newReminder.getId(),
                    reminderEarly.getText().toString(),
                    0,
                    false
            );
            alarmReminder.save();
            //prior = priorReminder(reminderEarly.getText().toString(), reminderName.getText().toString(), newReminder.getId());
        }

        //create alarm -->from a service
        Intent intent = new Intent(NewReminder.this, AlarmCrud.class);
        intent.putExtra("alarmId", newReminder.getId());
        intent.putExtra("create", true);
        startService(intent);

        //Toast.makeText(getBaseContext(), "Reminder set to  " + date_string, Toast.LENGTH_LONG).show();
        MDToast.makeText(getBaseContext(), "Reminder set", MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();

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
}
