package com.example.elm.login.model;

import com.orm.SugarRecord;

/**
 * Created by elm on 7/16/17.
 */

public class Note extends SugarRecord {
    private String noteid;
    private String title;
    private String note;
    private String status;
    private String created_at;
    private Boolean uploadflag;
    private Boolean updateflag;
    private Boolean deleteflag;


    public Note() {
    }

    public Note(String noteid, String title, String note, String status, String created_at, Boolean uploadflag, Boolean updateflag, Boolean deleteflag) {
        this.noteid = noteid;
        this.title = title;
        this.note = note;
        this.status = status;
        this.created_at = created_at;
        this.uploadflag = uploadflag;
        this.updateflag = updateflag;
        this.deleteflag = deleteflag;
    }

    public String getNoteid() {
        return noteid;
    }

    public void setNoteid(String noteid) {
        this.noteid = noteid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Boolean getUploadflag() {
        return uploadflag;
    }

    public void setUploadflag(Boolean uploadflag) {
        this.uploadflag = uploadflag;
    }

    public Boolean getUpdateflag() {
        return updateflag;
    }

    public void setUpdateflag(Boolean updateflag) {
        this.updateflag = updateflag;
    }

    public Boolean getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(Boolean deleteflag) {
        this.deleteflag = deleteflag;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteid='" + noteid + '\'' +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", status='" + status + '\'' +
                ", created_at='" + created_at + '\'' +
                ", uploadflag=" + uploadflag +
                ", updateflag=" + updateflag +
                ", deleteflag=" + deleteflag +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;

        Note note1 = (Note) o;

        if (getNoteid() != null ? !getNoteid().equals(note1.getNoteid()) : note1.getNoteid() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(note1.getTitle()) : note1.getTitle() != null)
            return false;
        if (getNote() != null ? !getNote().equals(note1.getNote()) : note1.getNote() != null)
            return false;
        if (getStatus() != null ? !getStatus().equals(note1.getStatus()) : note1.getStatus() != null)
            return false;
        if (getCreated_at() != null ? !getCreated_at().equals(note1.getCreated_at()) : note1.getCreated_at() != null)
            return false;
        if (getUploadflag() != null ? !getUploadflag().equals(note1.getUploadflag()) : note1.getUploadflag() != null)
            return false;
        if (getUpdateflag() != null ? !getUpdateflag().equals(note1.getUpdateflag()) : note1.getUpdateflag() != null)
            return false;
        return getDeleteflag() != null ? getDeleteflag().equals(note1.getDeleteflag()) : note1.getDeleteflag() == null;

    }

    @Override
    public int hashCode() {
        int result = getNoteid() != null ? getNoteid().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getNote() != null ? getNote().hashCode() : 0);
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getCreated_at() != null ? getCreated_at().hashCode() : 0);
        result = 31 * result + (getUploadflag() != null ? getUploadflag().hashCode() : 0);
        result = 31 * result + (getUpdateflag() != null ? getUpdateflag().hashCode() : 0);
        result = 31 * result + (getDeleteflag() != null ? getDeleteflag().hashCode() : 0);
        return result;
    }
}
