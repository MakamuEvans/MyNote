package com.elm.mycheck.login.services.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elm.mycheck.login.model.Reminder;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

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
