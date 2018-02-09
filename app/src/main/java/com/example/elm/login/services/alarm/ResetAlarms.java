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
    private List<Reminder> alarms = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        alarms = Select.from(Reminder.class)
                .where(Condition.prop("status").eq(1))
                .list();

        for (Reminder reminder: alarms){
            Intent intent1 = new Intent(context, AlarmCrud.class);
            intent1.putExtra("alarmId", reminder.getId());
            intent1.putExtra("create", true);
            context.startService(intent1);
        }

    }
}
