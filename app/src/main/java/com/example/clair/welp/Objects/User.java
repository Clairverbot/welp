package com.example.clair.welp.Objects;

import java.util.ArrayList;
import java.util.Date;

public class User {
    public User(){}
    private String Email, YearOfStudy,Username;
    private ArrayList Subjects;

    public User(String email, String yearOfStudy, String username, ArrayList subjects) {
        Email = email;
        YearOfStudy = yearOfStudy;
        Username = username;
        Subjects = subjects;
    }

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

    public ArrayList getSubjects() {
        return Subjects;
    }

    public void setSubjects(ArrayList subjects) {
        Subjects = subjects;
    }
}
