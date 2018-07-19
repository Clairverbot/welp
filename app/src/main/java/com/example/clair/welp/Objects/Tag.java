package com.example.clair.welp.Objects;

import android.media.Image;

import com.example.clair.welp.R;

public class Tag {
    private String[] YearsOfStudy=new String[]{"Secondary 1","Secondary 2","Secondary 3","Secondary 4","JC 1","JC 2","JC 3"};
    //Todo: add img icons to Subjects arr
    private String[] Subjects=new String[]{"English","Chinese","Math","Geography","History","Physics","Chemistry","Biology","A-Math"};
    private Integer[] img=new Integer[]{R.drawable.english,R.drawable.chinese,R.drawable.math,R.drawable.geography,R.drawable.history,R.drawable.physics,R.drawable.chemistry,R.drawable.biology,R.drawable.amath};
    private String YearOfStudy, Subject;
    private boolean isChecked=false;

    public Tag(){}
    public Tag(String yearOfStudy, String subject) {
        YearOfStudy = yearOfStudy;
        Subject = subject;
    }

    //region properties
    public String[] getYearsOfStudy() {
        return YearsOfStudy;
    }

    public void setYearsOfStudy(String[] yearsOfStudy) {
        YearsOfStudy = yearsOfStudy;
    }

    public String[] getSubjects() {
        return Subjects;
    }

    public void setSubjects(String[] subjects) {
        Subjects = subjects;
    }

    public String getYearOfStudy() {
        return YearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        YearOfStudy = yearOfStudy;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public Integer[] getImg() {
        return img;
    }

    public void setImg(Integer[] img) {
        this.img = img;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    //endregion
}
