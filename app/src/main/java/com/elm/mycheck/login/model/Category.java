package com.elm.mycheck.login.model;

import com.orm.SugarRecord;

/**
 * Created by Beth on 04-Feb-18.
 */

public class Category extends SugarRecord {
    private String title;
    private String description;

    public Category() {
    }

    public Category(String title, String description) {
        this.title = title;
        this.description = description;
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
}
