package com.example.elm.login.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by elm on 8/11/17.
 */

public class Constants {
    //for alarms
    final public static int five_minutes = 60*1000*5;
    final public static int ten_minutes = 60*1000*10;
    final public static int thirty_minutes = 60*1000*30;
    final public static int one_hour = 60*1000*60;
    final public static int two_hours = 60*1000*120;

    public String uniqueCode(){
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMddHHmmss");
        return dateFormat.format(presentTime_Date);
    }
}
