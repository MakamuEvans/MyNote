package com.elm.mycheck.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SelectPuzzle extends AppCompatActivity {

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

        setContentView(R.layout.activity_select_puzzle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select Puzzle");
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.cancel_puzzle:
                Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
                intent.putExtra("title", "None");
                sendBroadcast(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void demoRetype(View view){
        Intent intent = new Intent(SelectPuzzle.this, NotificationBase.class);
        intent.putExtra("demoRetype", 1);
        startActivity(intent);
    }

    public void demoPuzzle(View view){
        Intent intent = new Intent(SelectPuzzle.this, NotificationBase.class);
        intent.putExtra("demoPuzzle", 1);
        startActivity(intent);
    }

    public void demoSequence(View view){
        Intent intent = new Intent(SelectPuzzle.this, NotificationBase.class);
        intent.putExtra("demoSequence", 1);
        startActivity(intent);
    }

    public void Retype(View view){
        Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
        intent.putExtra("title", "retype");
        sendBroadcast(intent);
        finish();
    }

    public void Puzzle(View view){
        Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
        intent.putExtra("title", "puzzle");
        sendBroadcast(intent);
        finish();
    }

    public void Sequence(View view){
        Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
        intent.putExtra("title", "sequence");
        sendBroadcast(intent);
        finish();
    }

}
