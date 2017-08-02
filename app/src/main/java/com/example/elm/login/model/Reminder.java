package com.example.elm.login.model;

import com.orm.SugarRecord;

import java.sql.Date;

/**
 * Created by elm on 8/1/17.
 */

public class Reminder extends SugarRecord {
    private String title;
    private Date start;
    private Date end;
    private String frequency;
    private String status;

    public Reminder() {
    }

    public Reminder(String title, Date start, Date end, String frequency, String status) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.frequency = frequency;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
