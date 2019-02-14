package com.elm.mycheck.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.elm.mycheck.login.model.Note;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.model.Todo;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;
import layout.EventsFragment;
import layout.HomeFragment;
import layout.NotesFragment;
import layout.ReminderFragment;

public class Homev2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SpaceTabLayout tabLayout;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView app_menu;
    DrawerLayout navDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_NoActionBar_Primary);
        setContentView(R.layout.activity_homev2);

        List<Fragment> fragmentList = new ArrayList<>();
        if (homeVersion())
            fragmentList.add(new Home1());
        else
            fragmentList.add(new HomeFragment());
        fragmentList.add(new NotesFragment());
        fragmentList.add(new ReminderFragment());
        fragmentList.add(new EventsFragment());

        ViewPager viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.spaceTabLayout);

        tabLayout.initialize(viewPager, getSupportFragmentManager(), fragmentList);

         navDrawer = findViewById(R.id.manual_drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        TextView user_name = header.findViewById(R.id.nav_username);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Log.e("Pref", String.valueOf(sp.getInt("theme_color", 1)));
        user_name.setText(sp.getString("user_name", "Buddy"));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey("page")) {
                int page = intent.getExtras().getInt("page");
                //viewPager.setCurrentItem(2);
                tabLayout.setCurrentPosition(page);

            }

        }

        this.init();
    }

    /**
     * determine the fragment to be loaded in home screen.
     * depends if there is data in DB
     *
     * @return
     */
    private Boolean homeVersion() {
        if (Select.from(Note.class).where(Condition.prop("deleteflag").eq(0)).count() > 0 ||
                Select.from(Reminder.class).count() > 0 ||
                Select.from(Todo.class).count() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private ImageView plus;

    private void init() {
        plus = (ImageView) findViewById(R.id.add_new);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(Homev2.this, "WTF", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(Homev2.this, plus);
                popupMenu.getMenuInflater().inflate(R.menu.new_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.note_setting) {
                            newNote();
                        }
                        if (id == R.id.reminder_setting) {
                            newReminder();
                        }
                        if (id == R.id.todo_setting) {
                            newToDo();
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });


        app_menu = (ImageView) findViewById(R.id.app_menu);
        app_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If navigation drawer is not open yet open it else close it.
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(GravityCompat.START);
                else navDrawer.closeDrawer(GravityCompat.END);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //tabLayout.saveState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if (navDrawer.isDrawerOpen(GravityCompat.START)) {
            navDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void nN(View view) {
        newNote();
    }

    public void nR(View view) {
        newReminder();
    }

    public void nT(View view) {
        newToDo();
    }

    public void oS(View view) {
        openSettings();
    }

    public void tP(View view) {
        themePicker();
    }

    public void newNote() {
        Intent intent = new Intent(Homev2.this, AddNote.class);

        startActivity(intent);
    }

    public void newReminder() {
        Intent intent = new Intent(Homev2.this, NewReminder.class);
        startActivity(intent);
    }

    public void newToDo() {
        Intent intent = new Intent(Homev2.this, NewToDo.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(Homev2.this, SettingsActivity.class);
        startActivity(intent);
    }

    private int mSelectedColor;

    public void themePicker() {
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "none");

        if (theme.equalsIgnoreCase("tomato"))
            mSelectedColor = ContextCompat.getColor(this, R.color.tomato);
        if (theme.equalsIgnoreCase("tangarine"))
            mSelectedColor = ContextCompat.getColor(this, R.color.tangerine);
        if (theme.equalsIgnoreCase("banana"))
            mSelectedColor = ContextCompat.getColor(this, R.color.banana);
        if (theme.equalsIgnoreCase("basil"))
            mSelectedColor = ContextCompat.getColor(this, R.color.basil);
        if (theme.equalsIgnoreCase("sage"))
            mSelectedColor = ContextCompat.getColor(this, R.color.sage);
        if (theme.equalsIgnoreCase("peacock"))
            mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);
        if (theme.equalsIgnoreCase("blueberry"))
            mSelectedColor = ContextCompat.getColor(this, R.color.grape);
        if (theme.equalsIgnoreCase("lavender"))
            mSelectedColor = ContextCompat.getColor(this, R.color.lavender);
        if (theme.equalsIgnoreCase("grape"))
            mSelectedColor = ContextCompat.getColor(this, R.color.blueberry);
        if (theme.equalsIgnoreCase("flamingo"))
            mSelectedColor = ContextCompat.getColor(this, R.color.peacock);
        if (theme.equalsIgnoreCase("graphite"))
            mSelectedColor = ContextCompat.getColor(this, R.color.graphite);

        int[] mColors = getResources().getIntArray(R.array.default_rainbow);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                mColors,
                mSelectedColor,
                5,
                ColorPickerDialog.SIZE_SMALL,
                true);
        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;
                //Toast.makeText(getApplicationContext(), mSelectedColor, Toast.LENGTH_SHORT).show();
                System.out.println(mSelectedColor);
                Log.e("above", "above");

                SharedPreferences preferences = getSharedPreferences("myPref", 0);
                SharedPreferences.Editor editor = preferences.edit();

                //Navigation.this.setTitleColor(mSelectedColor);
                //user_name.setTextColor(mSelectedColor);
                if (mSelectedColor == -2818048)
                    editor.putString("theme", "tomato");
                editor.commit();
                if (mSelectedColor == -765666)
                    editor.putString("theme", "tangarine");
                if (mSelectedColor == -606426)
                    editor.putString("theme", "banana");
                if (mSelectedColor == -16023485)
                    editor.putString("theme", "basil");
                if (mSelectedColor == -13388167)
                    editor.putString("theme", "sage");
                if (mSelectedColor == -1672077)
                    editor.putString("theme", "peacock");
                if (mSelectedColor == -7461718)
                    editor.putString("theme", "blueberry");
                if (mSelectedColor == -8812853)
                    editor.putString("theme", "lavender");
                if (mSelectedColor == -12627531)
                    editor.putString("theme", "grape");
                if (mSelectedColor == -16540699)
                    editor.putString("theme", "flamingo");
                if (mSelectedColor == -10395295)
                    editor.putString("theme", "graphite");


                editor.commit();
                //Navigation.this.recreate();

            }
        });

        dialog.show(getFragmentManager(), "color_test");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //lazy to rename these things.....phew!!!!!!!

        if (id == R.id.nav_share) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "myCheck App");
            share.putExtra(Intent.EXTRA_TEXT, "I use myCheck App to save Notes and have Alarms with Puzzles! Download it now --> https://goo.gl/LNufqP");
            startActivity(Intent.createChooser(share, "Share via"));

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(Homev2.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_credit){
            String url = "http://www.makamuevans.co.ke";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        } else if (id == R.id.nav_categories){
            Intent intent = new Intent(Homev2.this, ManageCategories.class);
            startActivity(intent);
        }else if (id == R.id.nav_rate){
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.elm.mycheck.login"));
            startActivity(intent);
        }

        navDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
