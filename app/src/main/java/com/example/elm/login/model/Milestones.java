package com.example.elm.login.model;

import com.orm.SugarRecord;

/**
 * Created by elm on 8/16/17.
 */

public class Milestones extends SugarRecord {
    private String todoid;
    private String description;
    private String time;
    private Boolean status;

    public Milestones() {
    }

    public Milestones(String todoid, String description, String time, Boolean status) {
        this.todoid = todoid;
        this.description = description;
        this.time = time;
        this.status = status;
    }

    public String getTodoid() {
        return todoid;
    }

    public void setTodoid(String todoid) {
        this.todoid = todoid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
