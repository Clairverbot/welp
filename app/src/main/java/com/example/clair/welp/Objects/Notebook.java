package com.example.clair.welp.Objects;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Notebook {
    private String NotebookName;
    private String Email;
    private List<String> NotebookNotes;





    public String getNotebookName() {
        return NotebookName;
    }

    public void setNotebookName(String notebookName) {
        NotebookName = notebookName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public List<String> getNotebookNotes() {
        return NotebookNotes;
    }

    public void setNotebookNotes(List<String> notebookNotes) {
        NotebookNotes = notebookNotes;
    }
}
