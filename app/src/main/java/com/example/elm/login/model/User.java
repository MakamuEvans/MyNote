package com.example.elm.login.model;

import com.orm.SugarRecord;

/**
 * Created by elm on 7/10/17.
 */

public class User extends SugarRecord {
    private int userid;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    public User() {

    }

    public User(int userid, String firstname, String lastname, String email, String password) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
