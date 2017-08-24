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
import android.widget.TextView;

import com.example.elm.login.adapter.MilestoneAdapter;
import com.example.elm.login.model.Milestones;
import com.example.elm.login.model.Todo;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import layout.EventsFragment;

public class ToDoDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    public List<Milestones> milestones = new ArrayList<>();
    private MilestoneAdapter milestoneAdapter;
    private newPercentage percentage;
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
