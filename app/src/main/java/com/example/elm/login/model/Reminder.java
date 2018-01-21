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
    private Boolean prior;
    private String description;
    private Boolean status;
    private Boolean active;
    private String repeat;
    private String created_at;
    private String updated_at;

    public Reminder() {
    }

    public Reminder(String title, String time, Boolean prior, String description, Boolean status, Boolean active, String repeat, String created_at, String updated_at) {
        this.title = title;
        this.time = time;
        this.prior = prior;
        this.description = description;
        this.status = status;
        this.active = active;
        this.repeat = repeat;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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

    public Boolean getPrior() {
        return prior;
    }

    public void setPrior(Boolean prior) {
        this.prior = prior;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", prior=" + prior +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", active=" + active +
                ", repeat='" + repeat + '\'' +
                '}';
    }
}

