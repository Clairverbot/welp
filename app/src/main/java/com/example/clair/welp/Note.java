package com.example.clair.welp;

//TODO: add dimen resources
public class Note {
    private String Email,Username,UserIMG,NoteTitle,NoteDescription,ResourceURL;
    private String DatePosted,Deleted  ;
    private String[] Tags;
    private int Upvote,Downvote;
    private String[][] Comments;

    public Note(){}

    public Note(String email,String username, String userIMG, String noteTitle, String noteDescription, String resourceURL, String datePosted, String deleted, String[] tags, String[][] comments, int upvote, int downvote) {
        Email=email;
        Username = username;
        UserIMG=userIMG;
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

    public String getUserIMG() {
        return UserIMG;
    }

    public void setUserIMG(String userIMG) {
        UserIMG = userIMG;
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

    public String getDatePosted() {
        return DatePosted;
    }

    public void setDatePosted(String datePosted) {
        DatePosted = datePosted;
    }

    public String getDeleted() {
        return Deleted;
    }

    public void setDeleted(String deleted) {
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
