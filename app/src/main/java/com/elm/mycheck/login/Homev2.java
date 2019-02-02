package com.elm.mycheck.login;

import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;
import layout.EventsFragment;
import layout.HomeFragment;
import layout.NotesFragment;
import layout.ReminderFragment;

public class Homev2 extends AppCompatActivity {

    SpaceTabLayout tabLayout;
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
            setTheme(R.style.AppTheme_NoActionBar_Graphite);
        setContentView(R.layout.activity_homev2);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new NotesFragment());
        fragmentList.add(new ReminderFragment());
        fragmentList.add(new EventsFragment());

        ViewPager viewPager =(ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);

        tabLayout.initialize(viewPager, getSupportFragmentManager(), fragmentList, savedInstanceState);


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        tabLayout.saveState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
