package com.elm.mycheck.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;

public class PuzzleDetails extends AppCompatActivity {
    private StickyScrollView puzzle,retype,sequence;
    private CheckBox touch_poke,retype_poke,sequence_poke,touch_alt,retype_alt,sequence_alt;

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

        setContentView(R.layout.activity_puzzle_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String type = null;
        if (bundle != null) {
            if (bundle.containsKey("type")) {
                type = bundle.getString("type");
            }
        }
        puzzle = (StickyScrollView) findViewById(R.id.puzzle_touch);
        retype = (StickyScrollView) findViewById(R.id.puzzle_retype);
        sequence = (StickyScrollView) findViewById(R.id.puzzle_sequence);
        if (type.equals("puzzle")){
            puzzle.setVisibility(View.VISIBLE);
            retype.setVisibility(View.GONE);
            sequence.setVisibility(View.GONE);
            setTitle("Active Touch");
        }else if (type.equals("retype")){
            puzzle.setVisibility(View.GONE);
            retype.setVisibility(View.VISIBLE);
            sequence.setVisibility(View.GONE);
            setTitle("Text Retype");
        }else {
            puzzle.setVisibility(View.GONE);
            retype.setVisibility(View.GONE);
            sequence.setVisibility(View.VISIBLE);
            setTitle("Match Sequence");
        }
        //setTitle("Select Puzzle");

        touch_poke = (CheckBox) findViewById(R.id.touch_poke);
        retype_poke = (CheckBox) findViewById(R.id.retype_poke);
        sequence_poke = (CheckBox) findViewById(R.id.sequence_poke);
        touch_poke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (touch_poke.isChecked()){
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("poke", "Yes");
                    sendBroadcast(intent1);
                }else {
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("poke", "No");
                    sendBroadcast(intent1);
                }
            }
        });
        retype_poke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (retype_poke.isChecked()){
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("poke", "Yes");
                    sendBroadcast(intent1);
                }else {
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("poke", "No");
                    sendBroadcast(intent1);
                }
            }
        });
        sequence_poke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sequence_poke.isChecked()){
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("poke", "Yes");
                    sendBroadcast(intent1);
                }else {
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("poke", "No");
                    sendBroadcast(intent1);
                }
            }
        });


        touch_alt = (CheckBox) findViewById(R.id.touch_alt);
        retype_alt = (CheckBox) findViewById(R.id.retype_alt);
        sequence_alt = (CheckBox) findViewById(R.id.sequence_alt);
        touch_alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (touch_alt.isChecked()){
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("alternate", "Yes");
                    sendBroadcast(intent1);
                }else {
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("alternate", "No");
                    sendBroadcast(intent1);
                }
            }
        });
        retype_alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (retype_alt.isChecked()){
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("alternate", "Yes");
                    sendBroadcast(intent1);
                }else {
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("alternate", "No");
                    sendBroadcast(intent1);
                }
            }
        });
        sequence_alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sequence_alt.isChecked()){
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("alternate", "Yes");
                    sendBroadcast(intent1);
                }else {
                    Intent intent1 = new Intent(NewReminder.PokeReceiver.ACTIION_REP);
                    intent1.putExtra("alternate", "No");
                    sendBroadcast(intent1);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void demo_Retype(View view){
        Intent intent = new Intent(PuzzleDetails.this, NotificationBase.class);
        intent.putExtra("demoRetype", 1);
        startActivity(intent);
    }

    public void demo_Puzzle(View view){
        Intent intent = new Intent(PuzzleDetails.this, NotificationBase.class);
        intent.putExtra("demoPuzzle", "puzzle");
        startActivity(intent);
    }

    public void demo_Sequence(View view){
        Intent intent = new Intent(PuzzleDetails.this, NotificationBase.class);
        intent.putExtra("demoSequence", 1);
        startActivity(intent);
    }

    public void Retype(View view){
        Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
        intent.putExtra("title", "retype");
        sendBroadcast(intent);
        finish();
    }

    public void puzzleEasy(View view){
        returnPuzzle("Active touch", "1");
    }
    public void puzzleMedium(View view){
        returnPuzzle("Active touch", "2");
    }
    public void puzzleDifficult(View view){
        returnPuzzle("Active touch", "3");
    }

    public void retypeEasy(View view){
        returnPuzzle("Retype", "1");
    }
    public void retypeMedium(View view){
        returnPuzzle("Retype", "2");
    }
    public void retypeDifficult(View view){
        returnPuzzle("Retype", "3");
    }

    public void sequenceEasy(View view){
        returnPuzzle("Sequence", "1");
    }
    public void sequenceMedium(View view){
        returnPuzzle("Sequence", "2");
    }
    public void sequenceDifficult(View view){
        returnPuzzle("Sequence", "3");
    }


    private void returnPuzzle(String title, String level){
        Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
        intent.putExtra("title", title);
        intent.putExtra("level", level);
        sendBroadcast(intent);

        Intent intent1 = new Intent(PuzzleDetails.this, NewReminder.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
        finish();
    }

    public void Sequence(View view){
        Intent intent = new Intent(NewReminder.PuzzleReceiver.ACTIION_REP);
        intent.putExtra("title", "sequence");
        sendBroadcast(intent);
        finish();
    }

    public void poke(View view){

    }
}
