package com.elm.mycheck.login.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    //notification types
    public static final int earlyReminder = 101;
    public static final int actualReminder = 100;
    public static final int todoReminder = 102;
    public static final int morning = 201;

    //retype Texts
    public static final String text1 = "Hey me! So am going to do myself a favour and GET UP. Owwww I just told myself to GetUp: Doing it in 5, 4, 3, 2, 1, 0.5, 0.1101.";
    public static final String text2 = "So am using this app called myCheck and its like forcing me to GETUP. Who DOES THAT!#* Nway, lemme get up and Share it to my FRiendS";

    public String uniqueCode(){
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMddHHmmss");
        return dateFormat.format(presentTime_Date);
    }

    //tabs
    final public static int HOME_TAB = 0;
    final public static int NOTES_TAB = 1;
    final public static int REMINDER_TAB = 2;
    final public static int TODO_TAB = 3;


}
