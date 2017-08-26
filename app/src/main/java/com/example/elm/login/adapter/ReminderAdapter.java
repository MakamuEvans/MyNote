package com.example.elm.login.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
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
    Context context;

    public ReminderAdapter(List<Reminder> allReminders) {
        this.allReminders = allReminders;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title,time,dated, card_text,repeat_days;
        public ImageView reminderStatus, delete;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.reminder_title);
            time = (TextView) itemView.findViewById(R.id.reminder_time);
            repeat_days = (TextView) itemView.findViewById(R.id.repeat_days);
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
                    final int position = getLayoutPosition();
                    final Reminder reminder = allReminders.get(position);

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Are you sure?")
                            .setMessage("You are about to delete Alarm "+reminder.getTitle())
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reminder.delete();
                                    removeReminder(position);
                                }
                            })
                            .create()
                            .show();

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
        holder.repeat_days.setText(reminder.getRepeat());
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

        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view){
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        view.startAnimation(animation);
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
                notifyItemChanged(position, reminder);
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
