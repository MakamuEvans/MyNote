package com.example.elm.login.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.model.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by elm on 8/11/17.
 */

public class Utils {
    public static void setupItem(final View view, final Reminder reminder) {
        final TextView txt = (TextView) view.findViewById(R.id.txt_item);
        final TextView content = (TextView) view.findViewById(R.id.txt_content);
        final TextView date = (TextView) view.findViewById(R.id.notification_date);
        final TextView time = (TextView) view.findViewById(R.id.notification_time);

        //get dates right
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

        //set data
        txt.setText(reminder.getTitle());
        content.setText(reminder.getDescription());
        if (reminder.getRepeat() == null){
            date.setText(date_simpleDateFormat.format(date1));
            time.setText(time_simpleDateFormat.format(date1));
        }else {
            date.setText(time_simpleDateFormat.format(date1));
            time.setText("");
        }

    }

    public static class LibraryObject {

        private String mTitle;
        private String mContent;
        private String mTime;
        private int mRes;

        public LibraryObject(final int res, final String title, final String content, final  String time) {
            mRes = res;
            mTitle = title;
            mContent = content;
            mTime= time;
        }

        public String getContent(){ return  mContent;}

        public void setContent(final String content){ mContent = content;}

        public String getTime(){ return  mTime;}

        public void setTime(final String time){ mTime = time;}

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public int getRes() {
            return mRes;
        }

        public void setRes(final int res) {
            mRes = res;
        }
    }
}
