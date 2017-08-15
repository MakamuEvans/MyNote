package com.example.elm.login.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.NewReminder;
import com.example.elm.login.R;
import com.example.elm.login.model.Reminder;
import com.example.elm.login.services.alarm.AlarmCrud;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by elm on 8/10/17.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.myViewHolder> {
    public List<Reminder> allReminders;

    public ReminderAdapter(List<Reminder> allReminders) {
        this.allReminders = allReminders;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title,time,dated, card_text;
        public ImageView reminderStatus, delete;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.reminder_title);
            time = (TextView) itemView.findViewById(R.id.reminder_time);
            dated = (TextView) itemView.findViewById(R.id.reminder_dated);
            reminderStatus = (ImageView) itemView.findViewById(R.id.reminderstatus);
            delete = (ImageView) itemView.findViewById(R.id.card_del);
            card_text = (TextView) itemView.findViewById(R.id.card_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (card_text.getVisibility() == View.VISIBLE){
                        card_text.setVisibility(View.GONE);
                    }else {
                        card_text.setVisibility(View.VISIBLE);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Reminder reminder = allReminders.get(position);
                    reminder.delete();
                    removeReminder(position);
                }
            });

            reminderStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Reminder reminder = allReminders.get(position);
                    Log.e("adapterstatus", reminder.getStatus());
                    if (reminder.getStatus().equals("0")){
                        //attempt to make the alarm active
                        Intent intent = new Intent(v.getContext(), AlarmCrud.class);
                        //handle date first
                        String convert = "MMM dd yyyy HH:mm";
                        final SimpleDateFormat convert_simpleDateFormat = new SimpleDateFormat(convert, Locale.ENGLISH);
                        Date date = null;
                        Calendar calendar = Calendar.getInstance();
                        try {
                            date = convert_simpleDateFormat.parse(reminder.getTime());
                            int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
                            calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
                            calendar.set(Calendar.MONTH, month0);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar now = Calendar.getInstance();
                        if (calendar.getTimeInMillis() > now.getTimeInMillis()){
                            intent.putExtra("calender", calendar.getTimeInMillis());
                            intent.putExtra("nId", 100);
                            intent.putExtra("aId", reminder.getUniquecode());
                            intent.putExtra("title", reminder.getTitle());
                            intent.putExtra("content", reminder.getDescription());
                            intent.putExtra("create", true);
                            v.getContext().startService(intent);
                            //update db
                            reminder.setStatus("1");
                            reminder.save();
                            //update ui
                            updateReminder(reminder);
                            Log.e("haha", "hahaha");
                        }else {
                            Toast.makeText(v.getContext(), "The Alarm is in a past date", Toast.LENGTH_SHORT).show();
                        }

                    }else if (reminder.getStatus().equals("1")){
                        //deactivate alarm
                        Intent intent = new Intent(v.getContext(), AlarmCrud.class);
                        intent.putExtra("create", false);
                        intent.putExtra("aId", reminder.getUniquecode());
                        v.getContext().startService(intent);
                        //update db
                        reminder.setStatus("0");
                        reminder.save();
                        //update ui
                        updateReminder(reminder);
                    }
                }
            });
        }
    }

    @Override
    public ReminderAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_reminder_card, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderAdapter.myViewHolder holder, int position) {
        Reminder reminder = allReminders.get(position);
        holder.title.setText(reminder.getTitle());
        holder.time.setText(reminder.getTime());
        holder.card_text.setVisibility(View.GONE);
        if (reminder.getDescription().equals("")){
            holder.card_text.setText("No description provided");
        }else {
            holder.card_text.setText(reminder.getDescription());
        }
        if (reminder.getStatus().equals("0")){
            holder.reminderStatus.setImageResource(R.mipmap.ic_action_access_time);
        }else {
            holder.reminderStatus.setImageResource(R.mipmap.ic_action_access_alarm);
        }
    }

    @Override
    public int getItemCount() {
        return allReminders.size();
    }

    public void newData(Reminder reminder){
        this.allReminders.add(0, reminder);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, allReminders.size());
    }

    public void updateReminder(Reminder reminder){
        Reminder data = null;
        for (Reminder r:allReminders){
            if (r.getId().equals(reminder.getId())){
                int position = allReminders.indexOf(r);
                removeReminder(position);

                allReminders.add(position,reminder);
                notifyItemInserted(position);
                notifyItemRangeChanged(position,allReminders.size());
                break;
            }
        }
    }

    public void removeReminder(int position){
        allReminders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allReminders.size());
    }
}
