package com.elm.mycheck.login.model;

import com.orm.SugarRecord;

/**
 * Created by Beth on 28-Dec-17.
 */

public class AlarmReminder extends SugarRecord {
    private Long reminderid;
    private String time;
    private Integer identifier;
    private boolean active;

    public AlarmReminder() {
    }

    public AlarmReminder(Long reminderid, String time, int identifier, boolean active) {
        this.reminderid = reminderid;
        this.time = time;
        this.identifier = identifier;
        this.active = active;
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

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
