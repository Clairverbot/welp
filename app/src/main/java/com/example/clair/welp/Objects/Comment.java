package com.example.clair.welp.Objects;

import java.util.HashMap;
import java.util.Map;

public class Comment {

    private String Email,Username,Comment,DatePosted,NoteID;


    public Comment() {
    }
    public Comment(String email,String username,String comment,String datePosted,String noteID) {
        Email = email;
        Username = username;
        Comment = comment;
        DatePosted = datePosted;
        NoteID = noteID;

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDatePosted() {
        return DatePosted;
    }

    public void setDatePosted(String datePosted) {
        DatePosted = datePosted;
    }

    public String getNoteID() {
        return NoteID;
    }

    public void setNoteID(String noteID) {
        NoteID = noteID;
    }
}
