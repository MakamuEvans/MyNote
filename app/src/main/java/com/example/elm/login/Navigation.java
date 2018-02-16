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
import android.support.v4.content.ContextCompat;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.model.Note;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.model.Todo;
import com.example.elm.login.model.User;
import com.example.elm.login.preferences.BasicAuth;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TabLayout tabLayout;
    private TextView user_name;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Spinner spinner;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("myPref", 0);
        //Boolean fk = getSharedPreferences("myPref", 0).getBoolean("loggedIn", false);
        String theme = getSharedPreferences("myPref", 0).getString("theme", "none");
        if (theme.equals("none")){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("theme", "flamingo");
            editor.commit();
            recreate();
        }
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

        Boolean fk = true;
        if (!fk) {
            Intent intent = new Intent(Navigation.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("loggedIn", true);
            editor.commit();
            setContentView(R.layout.activity_navigation);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            Bundle bundle2 = new Bundle();
            //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle2.putString(FirebaseAnalytics.Param.ITEM_NAME, "Open");
            bundle2.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "App opened");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle2);

            mSectionsPagerAdapter = new Navigation.SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.getTabAt(0).setIcon(R.mipmap.ic_action_home_white);
            tabLayout.getTabAt(1).setIcon(R.mipmap.ic_action_assignment_white);
            tabLayout.getTabAt(2).setIcon(R.mipmap.ic_action_alarm_white);
            tabLayout.getTabAt(3).setIcon(R.mipmap.ic_action_todo_white);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                if (bundle.containsKey("page")) {
                    int page = intent.getExtras().getInt("page");
                    mViewPager.setCurrentItem(page);

                }

            }

            spinner = (Spinner) findViewById(R.id.fullSpinner);
              //TextView textView = (TextView) findViewById(R.id.huh);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
            navigationView.setNavigationItemSelectedListener(this);

            user_name = (TextView) header.findViewById(R.id.nav_username);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Log.e("Pref", String.valueOf(sp.getInt("theme_color", 1)));
            user_name.setText(sp.getString("user_name", "Buddy"));
        }
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        String topic = versionCode + versionName;
        Log.e("version", topic);
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public void newNote() {
        Intent intent = new Intent(Navigation.this, AddNote.class);

        startActivity(intent);
    }

    public void newReminder() {
        Intent intent = new Intent(Navigation.this, NewReminder.class);
        startActivity(intent);
    }

    public void newToDo() {
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

    private int mSelectedColor;

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
        if (id == R.id.note_setting) {
            newNote();
        }
        if (id == R.id.reminder_setting) {
            newReminder();
        }
        if (id == R.id.todo_setting) {
            newToDo();
        }
        if (id == R.id.theme_picker) {
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

                    SharedPreferences preferences = getSharedPreferences("myPref", 0);
                    SharedPreferences.Editor editor = preferences.edit();

                    //Navigation.this.setTitleColor(mSelectedColor);
                    user_name.setTextColor(mSelectedColor);
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
                    Navigation.this.recreate();

                }
            });

            dialog.show(getFragmentManager(), "color_test");
        }


        return super.onOptionsItemSelected(item);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //lazy to rename these things.....phew!!!!!!!
        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, Navigation.class);
            intent.putExtra("page", 0);
            startActivity(intent);
        } else if (id == R.id.nav_notes) {
            Intent intent = new Intent(this, Navigation.class);
            intent.putExtra("page", 1);
            startActivity(intent);
        } else if (id == R.id.nav_reminders) {
            Intent intent = new Intent(this, Navigation.class);
            intent.putExtra("page", 2);
            startActivity(intent);
        } else if (id == R.id.nav_todo) {
            Intent intent = new Intent(this, Navigation.class);
            intent.putExtra("page", 3);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(Navigation.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_credit){
            String url = "http://www.makamuevans.co.ke";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        } else if (id == R.id.nav_categories){
            Intent intent = new Intent(Navigation.this, ManageCategories.class);
            startActivity(intent);
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
            switch (position) {
                case 0:
                    if (homeVersion()) {
                        return Home1.newInstance("param1", "param2");
                    } else {
                        return HomeFragment.newInstance("param1", "param2");
                    }
                case 1:
                    return NotesFragment.newInstance("pram", "param");
                case 2:
                    return ReminderFragment.newInstance("pram", "param");
                case 3:
                    return EventsFragment.newInstance("pram", "param");
                default:
                    if (homeVersion()) {
                        return Home1.newInstance("param1", "param2");
                    } else {
                        return HomeFragment.newInstance("param1", "param2");
                    }

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
                    return null;
                case 1:
                    return null;
                case 2:
                    return null;
                case 3:
                    return null;

            }
            return null;
        }
    }

   /* public void logOut(){
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
    }*/
}
