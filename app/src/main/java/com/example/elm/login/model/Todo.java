package com.example.elm.login.model;

import com.orm.SugarRecord;

/**
 * Created by elm on 8/16/17.
 */

public class Todo extends SugarRecord {
    private String title;
    private String description;
    private String dated;
    private String status;

    public Todo() {
    }

    public Todo(String title, String description, String dated, String status) {
        this.title = title;
        this.description = description;
        this.dated = dated;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDated() {
        return dated;
    }

    public void setDated(String dated) {
        this.dated = dated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}