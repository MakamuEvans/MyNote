package com.example.elm.login;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewReminder extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    TextView selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String format = "MMM dd yyyy HH:mm";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);

        selectedDate = (TextView) findViewById(R.id.start_date);
        final SingleDateAndTimePicker dateTime = (SingleDateAndTimePicker) findViewById(R.id.date_picker);
        dateTime.setStepMinutes(1);
        dateTime.setMustBeOnFuture(true);

        selectedDate.setText(simpleDateFormat.format(dateTime.getDate()));


        dateTime.setListener(new SingleDateAndTimePicker.Listener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                System.out.println(date);

                selectedDate.setText(simpleDateFormat.format(date));
            }
        });
    }

    public void updateLabel(){

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
