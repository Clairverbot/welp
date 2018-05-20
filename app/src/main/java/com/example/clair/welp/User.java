package com.example.clair.welp;

import java.util.Date;

public class User {
    public User(){}
    private String Email, YearOfStudy,Username;
    private Date RegisterDate;
    private Tag tag;

    public User(String email, String yearOfStudy, String username, Date registerDate, Tag tag) {
        Email = email;
        YearOfStudy = yearOfStudy;
        Username = username;
        RegisterDate = registerDate;
        this.tag = tag;
    }

    //region properties
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getYearOfStudy() {
        return YearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        YearOfStudy = yearOfStudy;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public Date getRegisterDate() {
        return RegisterDate;
    }

    public void setRegisterDate(Date registerDate) {
        RegisterDate = registerDate;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
    //endregion
}
