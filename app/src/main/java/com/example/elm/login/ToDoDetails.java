package com.example.elm.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.elm.login.adapter.MilestoneAdapter;
import com.example.elm.login.model.Milestones;
import com.example.elm.login.model.Note;
import com.example.elm.login.model.Todo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import layout.AddMilestone;
import layout.EventsFragment;

public class ToDoDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    public List<Milestones> milestones = new ArrayList<>();
    public static MilestoneAdapter milestoneAdapter;
    private newPercentage percentage;
    private newMilestoneReceiver milestoneReceiver;
    private milestoneRemover milestoneRemover;
    public TextView new_task;
    Long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getExtras().getLong("id");
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
            String newNote = bundle.getString("milestone");
            Gson gson = new Gson();
            Type type = new TypeToken<Milestones>(){
            }.getType();
            Milestones milestones = gson.fromJson(newNote, type);

            progress(Long.valueOf(milestones.getTodoid()));

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
