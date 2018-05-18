package com.example.clair.welp;

import java.util.Date;
//TODO: add dimen resources
public class Note {
    private String Username,NoteTitle,NoteDescription,ResourceURL;
    private Date DatePosted,Deleted  ;
    private String[] Tags;
    private int Upvote,Downvote;
    private String[][] Comments;

    public Note(){}

    public Note(String username, String noteTitle, String noteDescription, String resourceURL, Date datePosted, Date deleted, String[] tags, String[][] comments, int upvote, int downvote) {
        Username = username;
        NoteTitle = noteTitle;
        NoteDescription = noteDescription;
        ResourceURL = resourceURL;
        DatePosted = datePosted;
        Deleted = deleted;
        Tags = tags;
        Comments = comments;
        Upvote = upvote;
        Downvote = downvote;
    }

    //region properties

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getNoteTitle() {
        return NoteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        NoteTitle = noteTitle;
    }

    public String getNoteDescription() {
        return NoteDescription;
    }

    public void setNoteDescription(String noteDescription) {
        NoteDescription = noteDescription;
    }

    public String getResourceURL() {
        return ResourceURL;
    }

    public void setResourceURL(String resourceURL) {
        ResourceURL = resourceURL;
    }

    public Date getDatePosted() {
        return DatePosted;
    }

    public void setDatePosted(Date datePosted) {
        DatePosted = datePosted;
    }

    public Date getDeleted() {
        return Deleted;
    }

    public void setDeleted(Date deleted) {
        Deleted = deleted;
    }

    public String[] getTags() {
        return Tags;
    }

    public void setTags(String[] tags) {
        Tags = tags;
    }

    public String[][] getComments() {
        return Comments;
    }

    public void setComments(String[][] comments) {
        Comments = comments;
    }

    public int getUpvote() {
        return Upvote;
    }

    public void setUpvote(int upvote) {
        Upvote = upvote;
    }

    public int getDownvote() {
        return Downvote;
    }

    public void setDownvote(int downvote) {
        Downvote = downvote;
    }

    //endregion
}
