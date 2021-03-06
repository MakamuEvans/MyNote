package com.elm.mycheck.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        /*if (theme == "tomato")
            setTheme(R.style.AppTheme_NoActionBar);
        if (theme == "tangarine")
            setTheme(R.style.AppTheme_NoActionBar_Tangarine);
        if (theme.equalsIgnoreCase("banana"))
            setTheme(R.style.AppTheme_NoActionBar_Banana);
        if (theme.equalsIgnoreCase("basil"))
            setTheme(R.style.AppTheme_NoActionBar_Basil);
        if (theme.equalsIgnoreCase("sage"))
            setTheme(R.style.AppTheme_NoActionBar_Sage);
        if (theme.equalsIgnoreCase("peacock"))
            setTheme(R.style.AppTheme_NoActionBar_Peacock);
        if (theme.equalsIgnoreCase("blueberry"))
            setTheme(R.style.AppTheme_NoActionBar_BlueBerry);
        if (theme.equalsIgnoreCase("lavender"))
            setTheme(R.style.AppTheme_NoActionBar_Lavender);
        if (theme.equalsIgnoreCase("grape"))
            setTheme(R.style.AppTheme_NoActionBar_Grape);
        if (theme.equalsIgnoreCase("flamingo"))
            setTheme(R.style.AppTheme_NoActionBar_Flamingo);
        if (theme.equalsIgnoreCase("graphite"))
            setTheme(R.style.AppTheme_NoActionBar_Graphite);*/
        setTheme(R.style.AppTheme_NoActionBar_Primary);

        setContentView(R.layout.activity_select_puzzle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select Puzzle");
        getSupportActionBar().setElevation(8);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.puzzle, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectPuzzle.this, NewReminder.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SelectPuzzle.this, NewReminder.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.cancel_puzzle:
                Intent intent2 = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
                intent2.putExtra("title", "None");
                sendBroadcast(intent2);

                Intent intent3 = new Intent(SelectPuzzle.this, NewReminder.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent3);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void demoRetype(View view){
        Intent intent = new Intent(SelectPuzzle.this, PuzzleDetails.class);
        intent.putExtra("demoRetype", 1);
        intent.putExtra("type", "retype");
        startActivity(intent);
    }

    public void demoPuzzle(View view){
        Intent intent = new Intent(SelectPuzzle.this, PuzzleDetails.class);
        intent.putExtra("type", "puzzle");
        startActivity(intent);
    }

    public void demoSequence(View view){
        Intent intent = new Intent(SelectPuzzle.this, PuzzleDetails.class);
        intent.putExtra("demoSequence", 1);
        intent.putExtra("type", "sequence");
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
