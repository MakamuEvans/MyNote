package com.example.elm.login.model;

import com.orm.SugarRecord;

/**
 * Created by Beth on 28-Dec-17.
 */

public class AlarmReminder extends SugarRecord {
    private Long reminderid;
    private String time;
    private boolean active;

    public AlarmReminder() {
    }

    public AlarmReminder(Long reminderid, String time, boolean status) {
        this.reminderid = reminderid;
        this.time = time;
        this.active = status;
    }

    public Long getReminderid() {
        return reminderid;
    }

    public void setReminderid(Long reminderid) {
        this.reminderid = reminderid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean status) {
        this.active = status;
    }
}
