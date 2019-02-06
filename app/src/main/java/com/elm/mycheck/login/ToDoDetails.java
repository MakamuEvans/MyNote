package com.elm.mycheck.login;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elm.mycheck.login.adapter.MilestoneAdapter;
import com.elm.mycheck.login.model.Milestones;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.model.Todo;
import com.elm.mycheck.login.services.alarm.AlarmCrud;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import layout.AddMilestone;
import layout.EventsFragment;

public class ToDoDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    public List<Milestones> milestones = new ArrayList<>();
    public static MilestoneAdapter milestoneAdapter;
    private newPercentage percentage;
    private newMilestoneReceiver milestoneReceiver;
    private milestoneRemover milestoneRemover;
    public TextView new_task, reminder_date;
    LinearLayout reminder_layout;
    Long id;
    Reminder reminder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "Default");
        setTheme(R.style.AppTheme_NoActionBar_Primary);


        setContentView(R.layout.activity_to_do_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getExtras().getLong("id");
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            if (bundle.containsKey("reminder")){
                int tracker = intent.getExtras().getInt("reminder");
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(tracker);
            }
        }

        reminder_date = (TextView) findViewById(R.id.reminder_date);
        reminder_layout = (LinearLayout) findViewById(R.id.reminder_layout);

        reminder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getTheme();
                theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
                @ColorInt int color = typedValue.data;
                new SingleDateAndTimePickerDialog.Builder(ToDoDetails.this)
                        //.bottomSheet()
                        //.curved()
                        //.minutesStep(15)

                        //.displayHours(false)
                        //.displayMinutes(false)

                        //.todayText("aujourd'hui")
                        .mainColor(color)
                        .mustBeOnFuture()

                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                //retrieve the SingleDateAndTimePicker
                            }
                        })

                        .title("Set Reminder")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                setReminder(date);
                            }
                        }).display();
            }
        });

        displayTime();

        Todo todo = Todo.findById(Todo.class, id);
        Log.e("Long", String.valueOf(id));
        IntentFilter intentFilter = new IntentFilter(newPercentage.SYNC_ACTION);
        percentage = new newPercentage();
        registerReceiver(percentage, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter(newMilestoneReceiver.ACTIION_REP);
        milestoneReceiver = new newMilestoneReceiver();
        registerReceiver(milestoneReceiver, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter(milestoneRemover.ACTIION_REP);
        milestoneRemover = new milestoneRemover();
        registerReceiver(milestoneRemover, intentFilter2);

        new_task = (TextView) findViewById(R.id.new_task);
        new_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMilestone addMilestone = new AddMilestone();
                MDToast.makeText(getApplicationContext(),"haha",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
                Bundle args = new Bundle();
                args.putString("task_id", id.toString());
                args.putBoolean("new_task", false);
                addMilestone.setArguments(args);

                addMilestone.show(getFragmentManager(), "Dialog");
            }
        });
        Todo todo1 = Todo.findById(Todo.class, id);
        TextView description = (TextView) findViewById(R.id.todo_title_details);
        if (todo1.getDescription().isEmpty()){
            description.setText("No description provided");
        }else {
            description.setText(todo1.getDescription());
        }
        setTitle(todo1.getTitle());
        recyclerView = (RecyclerView) findViewById(R.id.full_todo_recycler);
        //milestones = Milestones.listAll(Milestones.class);

        progress(id);
        milestones = Select.from(Milestones.class)
                .where(Condition.prop("todoid").eq(id.toString()))
                .list();

        milestoneAdapter = new MilestoneAdapter(milestones);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(milestoneAdapter);
    }

    @Override
    protected void onDestroy() {
        if (milestoneReceiver != null)
            unregisterReceiver(milestoneReceiver);
        if (milestoneRemover != null)
            unregisterReceiver(milestoneRemover);
        super.onDestroy();
    }

    private void displayTime() {
        int count = (int) Select.from(Reminder.class)
                .where(Condition.prop("todo").eq(id))
                .count();
        if (count > 0){
            reminder = Select.from(Reminder.class)
                    .where(Condition.prop("todo").eq(id))
                    .first();
            reminder_layout.setVisibility(View.VISIBLE);
            reminder_date.setText(reminder.getTime());
        }
    }

    private void setReminder(Date date) {
        //date format
        String format = "MMM dd yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        String date_string;
        date_string = simpleDateFormat.format(date);

        if (reminder != null){
            reminder.setTime(date_string);
            reminder.save();
        }

        //cancel previous alarm
        Intent intent2 = new Intent(ToDoDetails.this, AlarmCrud.class);
        intent2.putExtra("create", false);
        intent2.putExtra("alarmId", reminder.getId());
        startService(intent2);
       /* Reminder reminder = new Reminder(
                title,
                date_string,
                false,
                "",
                true,
                false,
                null,
                todoId,
                0,
                null,
                null
        );
        reminder.save();*/

        //create alarm -->from a service
        Intent intent = new Intent(ToDoDetails.this, AlarmCrud.class);
        intent.putExtra("alarmId", reminder.getId());
        intent.putExtra("create", true);
        startService(intent);

        displayTime();

        //Toast



        Log.e("dated", String.valueOf(date));
    }


    public void progress(Long id){
        Long total = Select.from(Milestones.class)
                .where(Condition.prop("todoid").eq(id.toString()))
                .count();
        Long done = Select.from(Milestones.class)
                .where(Condition.prop("todoid").eq(String.valueOf(id)))
                .where(Condition.prop("status").eq(1))
                .count();

        double percentage = ((double)done/total)*100;
        Log.e("count", String.valueOf(done));
        Log.e("count", String.valueOf(total));
        Log.e("count", String.valueOf(percentage));
        TextView mTitle = (TextView) findViewById(R.id.stat_title);
        DecimalFormat df = new DecimalFormat("0.00");
        mTitle.setText("Tasks   ("+ df.format(percentage)+" % done)");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class newMilestoneReceiver extends BroadcastReceiver {
        public static final String ACTIION_REP = "detailed_new_task";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Haa", "MilestoneReceiver Called");
            Bundle bundle = intent.getExtras();
            String title = bundle.getString("title");
            String todoID = bundle.getString("noteId");

            Milestones milestones = new Milestones(todoID, title, null, false);
            milestones.save();

            if (milestoneAdapter != null){
                milestoneAdapter.insert(milestones);
            }

            Intent intent1 = new Intent(EventsFragment.updateTodo.SYNC_ACTION);
            intent1.putExtra("id", Long.valueOf(milestones.getTodoid()));
            sendBroadcast(intent1);
        }
    }

    public class milestoneRemover extends BroadcastReceiver{
        public static final String ACTIION_REP = "remove_task";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Long id = bundle.getLong("id");
            progress(id);
        }
    }
    public void updateToDoList(Long id){
    }

    public class newPercentage extends BroadcastReceiver{
        public static final String SYNC_ACTION = "new_percentage";
        @Override
        public void onReceive(Context context, Intent intent) {
            progress(id);
            Intent intent1 = new Intent(EventsFragment.updateTodo.SYNC_ACTION);
            intent1.putExtra("id", id);
            sendBroadcast(intent1);
        }
    }
}
