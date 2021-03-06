package com.elm.mycheck.login.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.elm.mycheck.login.NewReminder;
import com.elm.mycheck.login.R;
import com.elm.mycheck.login.model.AlarmReminder;
import com.elm.mycheck.login.model.Reminder;
import com.elm.mycheck.login.services.alarm.AlarmCrud;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by elm on 8/10/17.
 */

public class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_VIEW = 1;
    private static final int DATA_VIEW = 2;
    public List<Reminder> allReminders;
    Context context;

    public ReminderAdapter(List<Reminder> allReminders) {
        this.allReminders = allReminders;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView title, time, dated, card_text, repeat_days;
        public ImageView reminderStatus, delete, puzzle;

        public myViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reminder_title);
            time = itemView.findViewById(R.id.reminder_time);
            repeat_days = itemView.findViewById(R.id.repeat_days);
            dated = itemView.findViewById(R.id.reminder_dated);
            reminderStatus = itemView.findViewById(R.id.reminderstatus);
            delete = itemView.findViewById(R.id.card_del);
            card_text =itemView.findViewById(R.id.card_text);
            puzzle = itemView.findViewById(R.id.reminder_puzzle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (card_text.getVisibility() == View.VISIBLE) {
                        card_text.setVisibility(View.GONE);
                    } else {
                        card_text.setVisibility(View.VISIBLE);
                    }*/
                    final int position = getLayoutPosition();
                    final Reminder reminder = allReminders.get(position);
                    Intent intent = new Intent(v.getContext(), NewReminder.class);
                    intent.putExtra("alarmId", reminder.getId());
                    v.getContext().startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getLayoutPosition();
                    final Reminder reminder = allReminders.get(position);

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Are you sure?")
                            .setMessage("You are about to delete Alarm " + reminder.getTitle())
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
                    if (!reminder.getStatus()) {
                        //attempt to make the alarm active
                        Intent intent = new Intent(v.getContext(), AlarmCrud.class);
                        //handle date first
                        String convert = "MMM dd yyyy HH:mm";
                        final SimpleDateFormat convert_simpleDateFormat = new SimpleDateFormat(convert, Locale.ENGLISH);
                        Date date = null;
                        Calendar calendar = Calendar.getInstance();
                        try {
                            date = convert_simpleDateFormat.parse(reminder.getTime());
                            if (reminder.getRepeat() == null) {
                                int month0 = Integer.parseInt(new SimpleDateFormat("M", Locale.ENGLISH).format(date)) - 1;
                                calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date)));
                                calendar.set(Calendar.MONTH, month0);
                                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(new SimpleDateFormat("d", Locale.ENGLISH).format(date)));
                            }
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new SimpleDateFormat("HH", Locale.ENGLISH).format(date)));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(new SimpleDateFormat("mm", Locale.ENGLISH).format(date)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar now = Calendar.getInstance();
                        if (reminder.getRepeat() == null) {
                            if (calendar.getTimeInMillis() > now.getTimeInMillis()) {
                                intent.putExtra("alarmId", reminder.getId());
                                intent.putExtra("create", true);
                                v.getContext().startService(intent);
                                //update db
                                reminder.setStatus(true);
                                reminder.save();
                                //update ui
                                updateReminder(reminder);
                            } else {
                                MDToast.makeText(v.getContext(), "The Alarm is in a past date", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
                                //Toast.makeText(v.getContext(), "The Alarm is in a past date", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            intent.putExtra("alarmId", reminder.getId());
                            intent.putExtra("create", true);
                            v.getContext().startService(intent);
                            //update db
                            reminder.setStatus(true);
                            reminder.save();
                            //update ui
                            updateReminder(reminder);
                        }


                    } else {
                        //deactivate alarm
                        Intent intent = new Intent(v.getContext(), AlarmCrud.class);
                        intent.putExtra("create", false);
                        intent.putExtra("alarmId", reminder.getId());
                        v.getContext().startService(intent);
                        //update db
                        reminder.setStatus(false);
                        reminder.save();
                        //update ui
                        updateReminder(reminder);
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType != EMPTY_VIEW){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_reminder_card, parent, false);
            holder = new myViewHolder(view);
            return holder;
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.alarm_zero, parent, false);
            holder = new emptyViewHolder(view);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType != EMPTY_VIEW) {
            myViewHolder holder = (myViewHolder) viewHolder;
            Reminder reminder = allReminders.get(position);
            holder.title.setText(reminder.getTitle());

            Log.e("time", reminder.getTime());

            String convert = "MMM dd yyyy HH:mm";
            String date_format = "dd MMM yyyy";
            String time_format = "HH:mm";
            final SimpleDateFormat date_simpleDateFormat = new SimpleDateFormat(date_format, Locale.ENGLISH);
            final SimpleDateFormat time_simpleDateFormat = new SimpleDateFormat(time_format, Locale.ENGLISH);
            final SimpleDateFormat convert_simpleDateFormat = new SimpleDateFormat(convert, Locale.ENGLISH);

            Date date1 = null;
            try {
                if (reminder.getRepeat() == null){
                    date1 = convert_simpleDateFormat.parse(reminder.getTime());
                }else {
                    date1 = convert_simpleDateFormat.parse(reminder.getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (reminder.getRepeat() == null){
              holder.time.setText(date_simpleDateFormat.format(date1)+" "+time_simpleDateFormat.format(date1));
            }else {
               holder.time.setText(time_simpleDateFormat.format(date1));
            }

            if (reminder.getPuzzle().equalsIgnoreCase("Active Touch")){
                holder.puzzle.setVisibility(View.VISIBLE);
                holder.puzzle.setImageResource(R.drawable.ic_touch);
            }
            if (reminder.getPuzzle().equalsIgnoreCase("Retype")){
                holder.puzzle.setVisibility(View.VISIBLE);
                holder.puzzle.setImageResource(R.drawable.ic_keyboard);
            }
            if (reminder.getPuzzle().equalsIgnoreCase("Sequence")){
                holder.puzzle.setVisibility(View.VISIBLE);
                holder.puzzle.setImageResource(R.drawable.ic_shuffle);
            }

            holder.card_text.setVisibility(View.GONE);
            holder.repeat_days.setText(reminder.getRepeat());
            if (Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(reminder.getId())).count() > 0 && reminder.getPrior()){
                AlarmReminder alarmReminder = Select.from(AlarmReminder.class).where(Condition.prop("reminderid").eq(reminder.getId())).first();
                int value = Integer.parseInt(alarmReminder.getTime().replaceAll("[^0-9]", ""));

                String before = null;
                if (value == 5) {
                    before = "-5 Mins";
                }
                if (value == 10) {
                    before = "-10 Mins";
                }
                if (value == 30) {
                    before = "-30 Mins";
                }
                if (value == 1) {
                    before = "-1 Hr";
                }
                if (value == 2) {
                    before = "-2 Hrs";
                }

                if (before != null)
                    holder.dated.setText(before);
            }

            if (reminder.getDescription().equals("") || reminder.getDescription() == null) {
                holder.card_text.setText("No description provided");
            } else {
                holder.card_text.setText(reminder.getDescription());
            }
            if (!reminder.getStatus()) {
                holder.reminderStatus.setImageResource(R.mipmap.ic_action_access_time);
            } else {
                holder.reminderStatus.setImageResource(R.mipmap.ic_action_access_alarm);
            }

            setFadeAnimation(holder.itemView);
        }else {

        }
    }

    private void setFadeAnimation(View view) {
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        if (allReminders.size() == 0)
            return 1;
        return allReminders.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (allReminders.size() == 0) {
            return EMPTY_VIEW;
        } else {
            return DATA_VIEW;
        }
    }

    public class emptyViewHolder extends RecyclerView.ViewHolder {

        public emptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void newData(Reminder reminder) {
        this.allReminders.add(0, reminder);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, allReminders.size());
    }

    public void updateReminder(Reminder reminder) {
        Reminder data = null;
        for (Reminder r : allReminders) {
            if (r.getId().equals(reminder.getId())) {
                int position = allReminders.indexOf(r);
                notifyItemChanged(position, reminder);
                break;
            }
        }
    }

    public void removeReminder(int position) {
        allReminders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allReminders.size());
    }
}
