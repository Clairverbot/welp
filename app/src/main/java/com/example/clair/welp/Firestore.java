package com.example.clair.welp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Objects.Note;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Firestore {
    public Firestore() {
    }

    CollectionReference collectionref = FirebaseFirestore.getInstance().collection("Notes");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Note> notes;
    Map<String, Object> notas = new HashMap<>();
    MagicalNames magicalNames = new MagicalNames();
    String email, username,userIMG,noteTitle,noteDescription,resourceURL;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    String datePosted,deleted;
    List<String>tags;
    int upvote,downvote;

    public Firestore(MainActivity r) {
        final MainActivity reference = r;


        collectionref.get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot document : task.getResult()) {
                                email = document.getString(magicalNames.getNotes_Column_Email());
                                getUserInfo(email,reference);
                                userIMG = document.getString(magicalNames.getNotes_Column_UserIMG());
                                noteTitle = document.getString(magicalNames.getNotes_Column_NoteTitle());
                                noteDescription = document.getString(magicalNames.getNotes_Column_NoteDescription());
                                resourceURL = document.getString(magicalNames.getNotes_Column_ResourceURL());
                                Date DatePosted=document.getDate(magicalNames.getNotes_Column_DatePosted());
                                datePosted=DatePosted!=null?sdf.format(DatePosted):null;
                                Date Deleted=document.getDate(magicalNames.getNotes_Column_Deleted());
                                deleted =Deleted!=null?sdf.format(Deleted):null;

                                tags = (List<String>)document.get(magicalNames.getNotes_Column_Tags());
                                //String[][] comments = {{document.getString(magicalNames.getNotes_Column_CommentUsername())}, {document.getString(magicalNames.getNotes_Column_Comment())}};
                                upvote = Integer.parseInt(document.getLong(magicalNames.getNotes_Column_Upvote()).toString());
                                downvote = Integer.parseInt(document.getLong(magicalNames.getNotes_Column_Downvote()).toString());


                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    public void getUserInfo(String Email,MainActivity reference){
        final MainActivity ref=reference;
        db.collection("Users").document(Email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    username = task.getResult().getString(magicalNames.getNotes_Column_Username());
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                Note n=new Note(email,username,userIMG,noteTitle,noteDescription,resourceURL,datePosted,deleted,tags,upvote,downvote,null);
                notes.add(n);
                ref.UpdateList(notes);
            }
        });
    }

    public void add(Note n) {
        notas.put(magicalNames.getNotes_Column_Email(),n.getEmail());
        notas.put(magicalNames.getNotes_Column_Username(),n.getUsername());
        notas.put(magicalNames.getNotes_Column_UserIMG(),n.getUserIMG());
        notas.put(magicalNames.getNotes_Column_NoteTitle(),n.getNoteTitle());
        notas.put(magicalNames.getNotes_Column_NoteDescription(),n.getNoteDescription());
        notas.put(magicalNames.getNotes_Column_ResourceURL(),n.getResourceURL());
        notas.put(magicalNames.getNotes_Column_DatePosted(),n.getDatePosted());
        notas.put(magicalNames.getNotes_Column_Deleted(),n.getDeleted());
        notas.put(magicalNames.getNotes_Column_Tags(),n.getTags());
        notas.put(magicalNames.getNotes_Column_Comment(),n.getComments());
        notas.put(magicalNames.getNotes_Column_Upvote(),n.getUpvote());
        notas.put(magicalNames.getNotes_Column_Downvote(),n.getDownvote());

        collectionref.document().set(notas).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + collectionref.document().getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        Log.w(TAG, "Eroor adding document", e);
                    }
                });


    }
}
