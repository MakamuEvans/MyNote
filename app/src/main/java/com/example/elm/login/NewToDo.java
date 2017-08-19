package com.example.elm.login;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.AddMilestone;
import layout.ToDo1;

public class NewToDo extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_do);

        android.support.v4.app.FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_frame, ToDo1.newInstance("param1", "param2")).commit();

    }

}
