package com.example.clair.welp.Objects;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Note {
    private String Email,Username,UserIMG,NoteTitle,NoteDescription,ResourceURL,DatePosted,Deleted,DocumentID,FileType  ;

    private HashMap<String, Boolean> Tags, Upvote,Downvote;

    private String[][] Comments;
    private Map<String, Boolean> Notebooks;

    public Note(){}
    public Note(String email, String username, String userIMG, String noteTitle, String noteDescription, String resourceURL,String  datePosted, String deleted, HashMap<String, Boolean> tags, HashMap<String, Boolean> upvote, HashMap<String, Boolean> downvote, String[][] comments, String fileType) {

        Email = email;
        Username = username;
        UserIMG = userIMG;
        NoteTitle = noteTitle;
        NoteDescription = noteDescription;
        ResourceURL = resourceURL;
        DatePosted = datePosted;
        Deleted = deleted;
        Tags = tags;
        Upvote = upvote;
        Downvote = downvote;
        Comments = comments;
        FileType=fileType;
        // Notebooks = notebooks;
    }

    public Note(String email, String username, String userIMG, String noteTitle, String noteDescription, String resourceURL,String  datePosted, String deleted, HashMap<String, Boolean> tags, HashMap<String, Boolean> upvote, HashMap<String, Boolean> downvote, String[][] comments, String documentID,String fileType) {
        Email = email;
        Username = username;
        UserIMG = userIMG;
        NoteTitle = noteTitle;
        NoteDescription = noteDescription;
        ResourceURL = resourceURL;
        DatePosted = datePosted;
        Deleted = deleted;
        Tags = tags;
        Upvote = upvote;
        Downvote = downvote;
        Comments = comments;
        DocumentID = documentID;
        FileType=fileType;
       // Notebooks = notebooks;
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



    public String[][] getComments() {
        return Comments;
    }

    public void setComments(String[][] comments) {
        Comments = comments;
    }



    public Map<String, Boolean> getNotebooks() {
        return Notebooks;
    }

    public void setNotebooks(Map<String, Boolean> notebooks) {
        Notebooks = notebooks;
    }

    public String getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(String documentID) {
        DocumentID = documentID;
    }

    public void getShortDateString(String datePosted){ }

    public HashMap<String, Boolean> getTags() { return Tags; }

    public void setTags(HashMap<String, Boolean> tags) { Tags = tags; }

    public HashMap<String, Boolean> getUpvote() {
        return Upvote;
    }

    public void setUpvote(HashMap<String, Boolean> upvote) {
        Upvote = upvote;
    }

    public HashMap<String, Boolean> getDownvote() {
        return Downvote;
    }

    public void setDownvote(HashMap<String, Boolean> downvote) {
        Downvote = downvote;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    //endregion


}
