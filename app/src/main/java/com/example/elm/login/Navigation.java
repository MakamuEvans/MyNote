package com.example.elm.login;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.model.Note;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.model.Todo;
import com.example.elm.login.model.User;
import com.example.elm.login.preferences.BasicAuth;
import com.github.clans.fab.FloatingActionMenu;
import com.orm.query.Select;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import layout.EventsFragment;
import layout.HomeFragment;
import layout.NotesFragment;
import layout.ReminderFragment;
import layout.SummaryFragment;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Navigation.SectionsPagerAdapter mSectionsPagerAdapter;
    com.github.clans.fab.FloatingActionButton note_btn,reminder_btn, todo_btn;
    FloatingActionMenu menu_btn;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("myPref",0);
        Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        //preferences.getBoolean();

        if (!fk){
            Intent intent = new Intent(Navigation.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("loggedIn", true);
            editor.commit();
            setContentView(R.layout.activity_navigation);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mSectionsPagerAdapter = new Navigation.SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                if (bundle.containsKey("page")){
                    int page = intent.getExtras().getInt("page");
                    mViewPager.setCurrentItem(page);

                }

            }

            note_btn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.note_fab);
            reminder_btn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.reminder_fab);
            todo_btn = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.todo_fab);
            final FloatingActionMenu menu_btn = (FloatingActionMenu) findViewById(R.id.menu_fab);

            todo_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu_btn.close(true);
                    newToDo();
                }
            });
            note_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu_btn.close(true);
                    newNote();
                }
            });

            reminder_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu_btn.close(true);
                    newReminder();
                }
            });


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            User user = Select.from(User.class)
                    .first();
            View v =navigationView.getHeaderView(0);
            TextView usernname = (TextView) v.findViewById(R.id.nav_username);
            usernname.setText(user.getLastname()+" , "+user.getFirstname());
        }
    }

    public void newNote(){

        Intent intent = new Intent(Navigation.this, AddNote.class);
        startActivity(intent);
    }

    public void newReminder(){
        Intent intent = new Intent(Navigation.this, NewReminder.class);
        startActivity(intent);
    }

    public void newToDo(){
        Intent intent = new Intent(Navigation.this, NewToDo.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Navigation.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.logout_settings){
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent(this, NotificationBase.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this, ToDoDetails.class));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(this, NewToDo.class));

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Navigation.PlaceholderFragment newInstance(int sectionNumber) {
            Navigation.PlaceholderFragment fragment = new Navigation.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home2, container, false);
            return rootView;
        }
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return HomeFragment.newInstance("param1", "param2");
                case 1:
                    return NotesFragment.newInstance("pram", "param");
                case 2:
                    return ReminderFragment.newInstance("pram", "param");
                case 3:
                    return EventsFragment.newInstance("pram", "param");
                default:
                    return HomeFragment.newInstance("param1", "param2");

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Notes";
                case 2:
                    return "Alarms";
                case 3:
                    return "To Do";

            }
            return null;
        }
    }

    public void logOut(){
        //reset basic auth
        Toast.makeText(getBaseContext(), "Logging you out...", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", false);
        editor.commit();
        User.deleteAll(User.class);
        Note.deleteAll(Note.class);
        Reminder.deleteAll(Reminder.class);

        Intent intent =new Intent(Navigation.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
