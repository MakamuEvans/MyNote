package com.example.elm.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.elm.login.adapter.MilestoneAdapter;
import com.example.elm.login.model.Milestones;
import com.example.elm.login.model.Todo;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class ToDoDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    public List<Milestones> milestones = new ArrayList<>();
    private MilestoneAdapter milestoneAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Long id = intent.getExtras().getLong("id");
        Todo todo = Todo.findById(Todo.class, id);
        Log.e("Long", String.valueOf(id));

        Todo todo1 = Todo.findById(Todo.class, id);
        TextView description = (TextView) findViewById(R.id.todo_title_details);
        description.setText(todo1.getDescription());
        setTitle(todo1.getTitle());
        recyclerView = (RecyclerView) findViewById(R.id.full_todo_recycler);
        //milestones = Milestones.listAll(Milestones.class);
        milestones = Select.from(Milestones.class)
                .where(Condition.prop("todoid").eq(String.valueOf(id)))
                .list();
        milestoneAdapter = new MilestoneAdapter(milestones);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(milestoneAdapter);
    }
}
