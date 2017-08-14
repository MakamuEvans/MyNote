package com.example.elm.login.model;

import com.orm.SugarRecord;

/**
 * Created by elm on 8/13/17.
 */

public class Alarm extends SugarRecord {
    private int alarm;
    private String type;

    public Alarm() {
    }

    public Alarm(int alarm, String type) {
        this.alarm = alarm;
        this.type = type;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
