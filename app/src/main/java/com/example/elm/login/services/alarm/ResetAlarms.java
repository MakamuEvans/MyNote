package com.example.elm.login.services.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.elm.login.NewReminder;
import com.example.elm.login.R;
import com.example.elm.login.model.AlarmReminder;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.utils.Constants;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResetAlarms extends BroadcastReceiver {
    private List<Reminder> oneTime = new ArrayList<>();
    private List<Reminder> repeating = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Calendar cal = Calendar.getInstance();

        //non-repeating
        oneTime = Select.from(Reminder.class)
                .where(Condition.prop("repeat").isNull())
                .where(Condition.prop("time").gt(formatter.format(cal.getTime())))
                .list();

        for (Reminder reminder : oneTime) {

            Calendar calendar = Calendar.getInstance();
            int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(reminder.getTime())) - 1;
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(reminder.getTime())));
            calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(reminder.getTime())));


            calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(reminder.getTime())));
            calendar.set(Calendar.MONTH, month0);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(reminder.getTime())));


            Intent intent1 = new Intent(context, AlarmCrud.class);
            intent.putExtra("calender", calendar.getTimeInMillis());
            intent.putExtra("nId", Constants.actualReminder);
            intent.putExtra("repeat", false);
            intent.putExtra("aId", reminder.getId());
            intent.putExtra("title", reminder.getTitle());
            intent.putExtra("content", reminder.getDescription());
            intent.putExtra("create", true);
            context.startService(intent);

            if (reminder.getPrior()) {
                AlarmReminder alarmReminder = AlarmReminder.findById(AlarmReminder.class, reminder.getId());
                String format = "MMM dd yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
                String date_string;
                Date date;

                try {
                    date = simpleDateFormat.parse(simpleDateFormat.format(reminder.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = null;
                }

                int value = Integer.parseInt(alarmReminder.getTime().replaceAll("[^0-9]", ""));


                Calendar calendar1 = Calendar.getInstance();
                int month1 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
                Log.e("month", String.valueOf(month1));
                calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
                calendar1.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));


                calendar1.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
                calendar1.set(Calendar.MONTH, month0);
                calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));

                String time = null;
                if (value == 5) {
                    calendar.add(calendar1.MINUTE, -5);
                    time = "Five Minutes";
                    Log.e("extra?", "yes");
                }
                if (value == 10) {
                    calendar.add(calendar1.MINUTE, -10);
                    time = "Ten Minutes";
                }
                if (value == 30) {
                    calendar.add(calendar1.MINUTE, -30);
                    time = "Thirty Minutes";
                }
                if (value == 1) {
                    calendar.add(calendar1.MINUTE, -60);
                    time = "One Hour";
                }
                if (value == 2) {
                    calendar.add(calendar1.MINUTE, -120);
                    time = "Two Hours";
                }
                Log.e("calendar", String.valueOf(calendar.get(Calendar.MINUTE)));


                //prepare reminder
                Intent intent3 = new Intent(context, AlarmCrud.class);
                intent.putExtra("calender", calendar1.getTimeInMillis());
                intent.putExtra("nId", Constants.earlyReminder);
                intent.putExtra("aId", alarmReminder.getId());
                intent.putExtra("repeat", false);
                intent.putExtra("title", reminder.getTitle());
                intent.putExtra("content", "Ready? You have  a Reminder in the next " + time);
                intent.putExtra("create", true);
                context.startService(intent3);
            }


        }


        //repeating
        repeating = Select.from(Reminder.class)
                .where(Condition.prop("repeat").isNotNull())
                .where(Condition.prop("time").gt(formatter.format(cal.getTime())))
                .list();

        for (Reminder reminder : repeating) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(reminder.getTime())));
            calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(reminder.getTime())));


            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));

            if (calendar1.getTimeInMillis() >= calendar.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Log.e("newTime", String.valueOf(calendar.getTime()));
            }

            Intent intent2 = new Intent(context, AlarmCrud.class);
            intent.putExtra("calender", calendar.getTimeInMillis());
            intent.putExtra("nId", Constants.actualReminder);
            intent.putExtra("repeat", true);
            intent.putExtra("aId", reminder.getId());
            intent.putExtra("title", reminder.getTitle());
            intent.putExtra("content", reminder.getDescription());
            intent.putExtra("create", true);
            context.startService(intent2);


            if (reminder.getPrior()) {
                AlarmReminder alarmReminder = AlarmReminder.findById(AlarmReminder.class, reminder.getId());
                String format = "MMM dd yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
                String date_string;
                Date date;

                try {
                    date = simpleDateFormat.parse(simpleDateFormat.format(reminder.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    date = null;
                }

                int value = Integer.parseInt(alarmReminder.getTime().replaceAll("[^0-9]", ""));


                Calendar calendar3 = Calendar.getInstance();
                int month2 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
                Log.e("month", String.valueOf(month2));
                calendar3.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
                calendar3.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));


                /*calendar3.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
                calendar3.set(Calendar.MONTH, month2);
                calendar3.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));
               *//* }else {*/
                Calendar calendar4 = Calendar.getInstance();
                Calendar calendar5 = Calendar.getInstance();
                calendar4.set(Calendar.HOUR_OF_DAY, calendar5.get(Calendar.HOUR_OF_DAY));
                calendar4.set(Calendar.MINUTE, calendar5.get(Calendar.MINUTE));

                if (calendar4.getTimeInMillis() >= calendar3.getTimeInMillis()) {
                    calendar3.add(Calendar.DAY_OF_YEAR, 1);
                }

                String time = null;
                if (value == 5) {
                    calendar.add(calendar3.MINUTE, -5);
                    time = "Five Minutes";
                    Log.e("extra?", "yes");
                }
                if (value == 10) {
                    calendar.add(calendar3.MINUTE, -10);
                    time = "Ten Minutes";
                }
                if (value == 30) {
                    calendar.add(calendar3.MINUTE, -30);
                    time = "Thirty Minutes";
                }
                if (value == 1) {
                    calendar.add(calendar3.MINUTE, -60);
                    time = "One Hour";
                }
                if (value == 2) {
                    calendar.add(calendar3.MINUTE, -120);
                    time = "Two Hours";
                }
                Log.e("calendar", String.valueOf(calendar.get(Calendar.MINUTE)));


                //prepare reminder
                Intent intent3 = new Intent(context, AlarmCrud.class);
                intent.putExtra("calender", calendar3.getTimeInMillis());
                intent.putExtra("nId", Constants.earlyReminder);
                intent.putExtra("aId", alarmReminder.getId());
                intent.putExtra("repeat", false);
                intent.putExtra("title", reminder.getTitle());
                intent.putExtra("content", "Ready? You have  a Reminder in the next " + time);
                intent.putExtra("create", true);
                context.startService(intent3);
            }

        }

    }
}
