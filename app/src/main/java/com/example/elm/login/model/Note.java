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
    private String updated_at;
    private Boolean favourite;
    private Boolean favaouriteflag;
    private Boolean uploadflag;
    private Boolean updateflag;
    private Boolean deleteflag;
    private String image;
    private String imageurl;


    public Note() {
    }

    public Note(String noteid, String title, String note, String status, String created_at, String updated_at, Boolean favourite, Boolean favaouriteflag, Boolean uploadflag, Boolean updateflag, Boolean deleteflag, String image, String imageurl) {
        this.noteid = noteid;
        this.title = title;
        this.note = note;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.favourite = favourite;
        this.favaouriteflag = favaouriteflag;
        this.uploadflag = uploadflag;
        this.updateflag = updateflag;
        this.deleteflag = deleteflag;
        this.image = image;
        this.imageurl = imageurl;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Boolean getFavaouriteflag() {
        return favaouriteflag;
    }

    public void setFavaouriteflag(Boolean favaouriteflag) {
        this.favaouriteflag = favaouriteflag;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteid='" + noteid + '\'' +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", status='" + status + '\'' +
                ", created_at='" + created_at + '\'' +
                ", favourite=" + favourite +
                ", favaouriteflag=" + favaouriteflag +
                ", uploadflag=" + uploadflag +
                ", updateflag=" + updateflag +
                ", deleteflag=" + deleteflag +
                ", image='" + image + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }
}
