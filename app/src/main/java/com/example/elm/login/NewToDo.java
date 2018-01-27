package com.example.elm.login;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import layout.AddMilestone;
import layout.ToDo1;

public class NewToDo extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_to_do);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        android.support.v4.app.FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_frame, ToDo1.newInstance("param1", "param2")).commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
