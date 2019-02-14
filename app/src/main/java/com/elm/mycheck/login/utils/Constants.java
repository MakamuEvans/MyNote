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
    public static final String text1 = "Hey me! am doing a favour to me by Doing IT!! in 5, 4, 3, 1, 0.5, 0.1101";
    public static final String text2 = "Winning and losing is a part of life. When you win, doN't becOme arrogant; When you lose, don't give up or feel like you've failed";
    public static final String text3 = "When everything seems To be going against you; remEmber that the airplane takes off against the wind, not with it....";
    public static final String text4 = "I Don’t regret the things I’ve done, I regret the things I didn’t DO when I had the chAncE.";
    public static final String text5 = "Challenge yourself with something you know you could never do, and what you’ll find is that you can OverCOMe anyThing.";

    //morning texts
    public static final String text6 = "Some people dream of success, while other people get up every morning and make it happen! Make yours Happen!!!";
    public static final String text7 = "My GOAL is To simply wake up every morning a better perSon than when I went to bED";
    public static final String text8 = "Though no one can go back and make a brand new start: anyone can START from noW and make a brand new enDing!.";

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
