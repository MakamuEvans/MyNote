package com.elm.mycheck.login.model;

import com.orm.SugarRecord;

/**
 * Created by elm on 8/1/17.
 */

public class Reminder extends SugarRecord {
    private String title;
    private String time;
    private Boolean prior;
    private String description;
    private String puzzle;
    private Boolean status;
    private Boolean active;
    private String repeat;
    private String todo;
    private String puzzletype;
    private String puzzlelevel;
    private String poke;
    private String alternatepuzzle;
    private Integer identifier;
    private String created_at;
    private String updated_at;

    public Reminder() {
    }

    public Reminder(String title, String time, Boolean prior, String description, String puzzle, Boolean status, Boolean active, String repeat, String todo, String puzzletype, String puzzlelevel, String poke, String alternatepuzzle, Integer identifier, String created_at, String updated_at) {
        this.title = title;
        this.time = time;
        this.prior = prior;
        this.description = description;
        this.puzzle = puzzle;
        this.status = status;
        this.active = active;
        this.repeat = repeat;
        this.todo = todo;
        this.puzzletype = puzzletype;
        this.puzzlelevel = puzzlelevel;
        this.poke = poke;
        this.alternatepuzzle = alternatepuzzle;
        this.identifier = identifier;
        this.created_at = created_at;
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

    public String getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(String puzzle) {
        this.puzzle = puzzle;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getPuzzletype() {
        return puzzletype;
    }

    public void setPuzzletype(String puzzletype) {
        this.puzzletype = puzzletype;
    }

    public String getPuzzlelevel() {
        return puzzlelevel;
    }

    public void setPuzzlelevel(String puzzlelevel) {
        this.puzzlelevel = puzzlelevel;
    }

    public String getPoke() {
        return poke;
    }

    public void setPoke(String poke) {
        this.poke = poke;
    }

    public String getAlternatepuzzle() {
        return alternatepuzzle;
    }

    public void setAlternatepuzzle(String alternatepuzzle) {
        this.alternatepuzzle = alternatepuzzle;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
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

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", prior=" + prior +
                ", description='" + description + '\'' +
                ", puzzle='" + puzzle + '\'' +
                ", status=" + status +
                ", active=" + active +
                ", repeat='" + repeat + '\'' +
                ", todo='" + todo + '\'' +
                ", puzzletype='" + puzzletype + '\'' +
                ", puzzlelevel='" + puzzlelevel + '\'' +
                ", poke='" + poke + '\'' +
                ", alternatepuzzle='" + alternatepuzzle + '\'' +
                ", identifier=" + identifier +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}

