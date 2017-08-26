package com.example.elm.login.model;

import android.util.Log;

import com.orm.SugarRecord;

import java.sql.Date;

/**
 * Created by elm on 8/1/17.
 */

public class Reminder extends SugarRecord {
    private String title;
    private String time;
    private String prior;
    private String description;
    private String status;
    private Long uniquecode;
    private String repeat;

    public Reminder() {
    }

    public Reminder(String title, String time, String prior, String description, String status, Long uniquecode, String repeat) {
        this.title = title;
        this.time = time;
        this.prior = prior;
        this.description = description;
        this.status = status;
        this.uniquecode = uniquecode;
        this.repeat = repeat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrior() {
        return prior;
    }

    public void setPrior(String prior) {
        this.prior = prior;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUniquecode() {
        return uniquecode;
    }

    public void setUniquecode(Long uniquecode) {
        this.uniquecode = uniquecode;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", prior='" + prior + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", uniquecode=" + uniquecode +
                ", repeat='" + repeat + '\'' +
                '}';
    }
}

