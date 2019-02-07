package com.elm.mycheck.login;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v7.widget.Toolbar;
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
import com.elm.mycheck.login.model.Note;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.services.alarm.AlarmCrud;
import com.elm.mycheck.login.utils.Constants;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.irshulx.models.EditorContent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;
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
    private PokeReceiver pokeReceiver;
    private AlternateReceiver alternateReceiver;
    private Boolean repeating = false;
    private String puzzle = "None";
    private String puzzle_level = "[#]";
    private String poke = "No", alternate = "No";
    private View view;
    private AdView adView;
    private Spinner spinner;
    private TextView tsunday, tmonday, ttuesday, twednesday, tthursday, tfriday, tsaturday;
    private boolean ssunday = false, smonday = false, stuesday = false, swednesday = false, sthursday = false, sfriday = false, ssaturday = false, edit_mode = false;

    SimpleDateFormat simpleDateFormat, simpleDateFormat2;
    SingleDateAndTimePicker dateTime, time_picker;
    RelativeLayout repeat_mode;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "Default");
        Log.e("Theme", theme);
        setTheme(R.style.AppTheme_NoActionBar_Primary);


        setContentView(R.layout.activity_new_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  toolbar.setElevation(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New Reminder");
        //
        adView = (AdView) findViewById(R.id.ReminderadView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
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
        //reminderDescription.setFocusable(false);

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
        registerReceiver(earlyReceiver, intentFilter);

        IntentFilter filter = new IntentFilter(TitleReceiver.ACTIION_REP);
        titleReceiver = new TitleReceiver();
        registerReceiver(titleReceiver, filter);

        IntentFilter filter1 = new IntentFilter(PuzzleReceiver.ACTIION_REP);
        puzzleReceiver = new PuzzleReceiver();
        registerReceiver(puzzleReceiver, filter1);

        IntentFilter filter2 = new IntentFilter(PokeReceiver.ACTIION_REP);
        pokeReceiver = new PokeReceiver();
        registerReceiver(pokeReceiver, filter2);

        IntentFilter filter3 = new IntentFilter(AlternateReceiver.ACTIION_REP);
        alternateReceiver = new AlternateReceiver();
        registerReceiver(alternateReceiver, filter3);


        String format = "MMM dd yyyy HH:mm";
        simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String format2 = "HH:mm";
        simpleDateFormat2 = new SimpleDateFormat(format2, Locale.ENGLISH);

        selectedDate = findViewById(R.id.start_date);
        dateTime = findViewById(R.id.date_picker);
        time_picker = findViewById(R.id.time_picker);
        dateTime.setStepMinutes(1);
        dateTime.setIsAmPm(false);
        time_picker.setDisplayDays(false);
        time_picker.setMustBeOnFuture(false);
        time_picker.setStepMinutes(1);
        time_picker.setIsAmPm(false);
        dateTime.setMustBeOnFuture(true);

        repeat_mode = (RelativeLayout) findViewById(R.id.repeat_mode);
        spinner = (Spinner) findViewById(R.id.alarm_type);

        new Tooltip.Builder(spinner)
                .setText("Click here to modify Alarm Type")
                .setBackgroundColor(getResources().getColor(android.R.color.black))
                .setTextColor(getResources().getColor(R.color.colorWhite))
                .setCancelable(true)
                .show();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("position", String.valueOf(position));
                String selected_text = spinner.getSelectedItem().toString();
                if (selected_text.equals("One Time")) {
                    dateTime.setVisibility(View.VISIBLE);
                    time_picker.setVisibility(View.GONE);
                    repeating = false;
                    repeat_mode.setVisibility(View.GONE);
                } else {
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

        time_picker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectedDate.setText(simpleDateFormat2.format(date));
            }
        });

        dateTime.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectedDate.setText(simpleDateFormat.format(date));
            }
        });

        try {
            editingMode();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Long alarm_id;

    private void editingMode() throws ParseException {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("alarmId")) {
                edit_mode = true;
                alarm_id = intent.getExtras().getLong("alarmId");
                newReminder = Reminder.findById(Reminder.class, alarm_id);
                onComplete(newReminder.getTitle());
                puzzle = newReminder.getPuzzle();
                puzzle_level = newReminder.getPuzzlelevel();
                setPuzzleText();
                Log.e("reminder", newReminder.toString());
                //Log.e("reminder", newReminder.getRepeat());
                Date alarm_date = simpleDateFormat.parse(newReminder.getTime());

                if (newReminder.getRepeat() == null) {
                    Log.e("reminder", "not repeating");
                    repeating = false;
                    dateTime.setVisibility(View.VISIBLE);
                    dateTime.setDefaultDate(alarm_date);
                    time_picker.setVisibility(View.GONE);
                    repeat_mode.setVisibility(View.GONE);
                    selectedDate.setText(simpleDateFormat.format(alarm_date));
                } else {
                    Log.e("reminder", "repeating");

                    selectedDate.setText(simpleDateFormat2.format(alarm_date));
                    dateTime.setVisibility(View.GONE);
                    spinner.setSelection(1, true);
                    repeating = true;
                    time_picker.setDefaultDate(simpleDateFormat.parse(newReminder.getTime()));
                    dateTime.setVisibility(View.GONE);
                    time_picker.setVisibility(View.VISIBLE);
                    repeat_mode.setVisibility(View.VISIBLE);

                    String[] repeat_dates = newReminder.getRepeat().split(",");
                    for (String dt : repeat_dates
                    ) {
                        switch (dt) {
                            case "Sunday":
                                tsunday.callOnClick();
                                break;
                            case "Monday":
                                tmonday.callOnClick();
                                break;
                            case "Tuesday":
                                ttuesday.callOnClick();
                                break;
                            case "Wednesday":
                                twednesday.callOnClick();
                                break;
                            case "Thursday":
                                tthursday.callOnClick();
                                break;
                            case "Friday":
                                tfriday.callOnClick();
                                break;
                            case "Saturday":
                                tsaturday.callOnClick();
                                break;
                            default:

                        }
                    }
                }

                if (Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(newReminder.getId())).count() > 0 && newReminder.getPrior()) {
                    AlarmReminder alarmReminder = Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(newReminder.getId())).first();
                    reminderEarly.setText(alarmReminder.getTime());
                }

                reminderDescription.setText(newReminder.getDescription());
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (earlyReceiver != null)
            unregisterReceiver(earlyReceiver);
        if (titleReceiver != null)
            unregisterReceiver(titleReceiver);
        if (puzzleReceiver != null)
            unregisterReceiver(puzzleReceiver);
        if (pokeReceiver != null)
            unregisterReceiver(pokeReceiver);
        if (alternateReceiver != null)
            unregisterReceiver(alternateReceiver);
        super.onDestroy();
    }

    public static final int STORAGE = 1234;

    public void permission(View view) {
        if (ContextCompat.checkSelfPermission(NewReminder.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(NewReminder.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewReminder.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE);
            return;
        }

    }

    private void setPuzzleText() {
        String level_text;
        switch (puzzle_level) {
            case "1":
                level_text = "Easy";
                break;
            case "2":
                level_text = "Medium";
                break;
            case "3":
                level_text = "Hard";
                break;
            default:
                level_text = "None";
        }
        puzzle_name_activity.setText(puzzle + "(" + level_text + ")");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    MDToast.makeText(NewReminder.this, "Reminder requires read to external Storage", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
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
                smonday = smonday == false ? true : false;
                changeTextColor(tmonday, smonday);
            }
        });
        ttuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stuesday = stuesday == false ? true : false;
                changeTextColor(ttuesday, stuesday);
            }
        });
        twednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swednesday = swednesday == false ? true : false;
                changeTextColor(twednesday, swednesday);
            }
        });
        tthursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sthursday = sthursday == false ? true : false;
                changeTextColor(tthursday, sthursday);
            }
        });
        tfriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sfriday = sfriday == false ? true : false;
                changeTextColor(tfriday, sfriday);
            }
        });
        tsaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssaturday = ssaturday == false ? true : false;
                changeTextColor(tsaturday, ssaturday);
            }
        });
        tsunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssunday = ssunday == false ? true : false;
                changeTextColor(tsunday, ssunday);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void changeTextColor(TextView textView, Boolean status) {
        if (status) {
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
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
                Intent intent2 = new Intent(this, Homev2.class);
                intent2.putExtra("page", Constants.REMINDER_TAB);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
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
            Log.e("reminder", "Title Received");
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            onComplete(title);
        }
    }

    public class PokeReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "receive_poke";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            poke = bundle.getString("poke");
        }
    }

    public class AlternateReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "receive_alternate";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            alternate = bundle.getString("alternate");
        }
    }

    public class PuzzleReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "add_puzzle";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("reminder", "Puzzle Received");
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            String level = bundle.getString("level");
            puzzle = title;
            puzzle_level = level;
            setPuzzleText();
            //onComplete(title);
        }
    }

    public class EarlyReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "add_reminder";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("reminder", "Early Received");

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
     *
     * @return
     */
    public String repeatDates() {
        List<String> alarm_days = new ArrayList<>();
        if ((ssunday || smonday || stuesday || swednesday || sthursday || sfriday || ssaturday) && repeating) {
            if (ssunday) {
                alarm_days.add("Sunday");
            }
            if (smonday) {
                alarm_days.add("Monday");
            }
            if (stuesday) {
                alarm_days.add("Tuesday");
            }
            if (swednesday) {
                alarm_days.add("Wednesday");
            }
            if (sthursday) {
                alarm_days.add("Thursday");
            }
            if (sfriday) {
                alarm_days.add("Friday");
            }
            if (ssaturday) {
                alarm_days.add("Saturday");
            }

            String merged = android.text.TextUtils.join(",", alarm_days);
            return merged;
        } else {
            return null;
        }
    }

    Reminder newReminder;

    /**
     * set reminder
     *
     * @param view
     * @throws ParseException
     */
    public void setReminder(View view) throws ParseException {
        //initialize data
        SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        SingleDateAndTimePicker time_picker = (SingleDateAndTimePicker) findViewById(R.id.time_picker);

        //is title set?
        if (reminderName.getText().toString().isEmpty() || reminderName.getText().toString().equals("None")) {
            MDToast.makeText(getBaseContext(), "Alarm Title should be filled", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            return;
        }

        //which type of alarm?
        if (repeating) {
            if (repeatDates() == null) {
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
        if (!repeating) {
            date_string = simpleDateFormat.format(dateTime.getDate());
            // date = simpleDateFormat.parse(simpleDateFormat.format(dateTime.getDate()));
        } else {
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
        if (!edit_mode) {
            newReminder = new Reminder(
                    reminderName.getText().toString(),
                    date_string,
                    priorSet,
                    reminderDescription.getText().toString(),
                    puzzle,
                    true,
                    false,
                    repeatDates(),
                    "",
                    puzzle,
                    puzzle_level,
                    poke,
                    alternate,
                    0,
                    dateFormat.format(today),
                    dateFormat.format(today));
            newReminder.save();


            if (priorSet) {
                AlarmReminder alarmReminder = new AlarmReminder(
                        newReminder.getId(),
                        reminderEarly.getText().toString(),
                        0,
                        false
                );
                alarmReminder.save();
            }
        } else {
            newReminder.setTitle(reminderName.getText().toString());
            newReminder.setTime(date_string);
            newReminder.setPrior(priorSet);
            newReminder.setDescription(reminderDescription.getText().toString());
            newReminder.setPuzzle(puzzle);
            newReminder.setStatus(true);
            newReminder.setActive(false);
            newReminder.setRepeat(repeatDates());
            newReminder.setPuzzletype(puzzle);
            newReminder.setPuzzlelevel(puzzle_level);
            newReminder.setPoke(poke);
            newReminder.setAlternatepuzzle(alternate);
            newReminder.setUpdated_at(dateFormat.format(today));
            newReminder.save();


            AlarmReminder alarmReminder = Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(newReminder.getId())).first();
            if (alarmReminder != null)
                alarmReminder.delete();

            if (priorSet) {
                AlarmReminder alarm_reminder = new AlarmReminder(
                        newReminder.getId(),
                        reminderEarly.getText().toString(),
                        0,
                        false
                );
                alarm_reminder.save();
                //prior = priorReminder(reminderEarly.getText().toString(), reminderName.getText().toString(), newReminder.getId());
            }
        }

        //create alarm -->from a service
        Intent intent = new Intent(NewReminder.this, AlarmCrud.class);
        intent.putExtra("alarmId", newReminder.getId());
        if (edit_mode)
            intent.putExtra("edit", true);
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

        Intent intent2 = new Intent(this, Homev2.class);
        intent2.putExtra("page", Constants.REMINDER_TAB);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
        finish();
    }
}
