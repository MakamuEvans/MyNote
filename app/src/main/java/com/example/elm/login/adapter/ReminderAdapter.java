package com.example.elm.login.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.model.Reminder;

import java.util.List;

/**
 * Created by elm on 8/10/17.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.myViewHolder> {
    public List<Reminder> allReminders;

    public ReminderAdapter(List<Reminder> allReminders) {
        this.allReminders = allReminders;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title,time,dated;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.reminder_title);
            time = (TextView) itemView.findViewById(R.id.reminder_time);
            dated = (TextView) itemView.findViewById(R.id.reminder_dated);
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
        holder.time.setText(reminder.getStart().toString());
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
}
